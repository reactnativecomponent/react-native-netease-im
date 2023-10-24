package com.netease.im.login;

import android.text.TextUtils;
import android.util.Log;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableMap;
import com.netease.im.RNNeteaseImModule;
import com.netease.im.ReactCache;
import com.netease.im.session.extension.AccountNoticeAttachment;
import com.netease.im.session.extension.CustomAttachment;
import com.netease.im.session.extension.CustomAttachmentType;
import com.netease.im.uikit.cache.SimpleCallback;
import com.netease.im.uikit.cache.TeamDataCache;
import com.netease.im.uikit.common.util.log.LogUtil;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.RequestCallbackWrapper;
import com.netease.nimlib.sdk.StatusCode;
import com.netease.nimlib.sdk.auth.AuthServiceObserver;
import com.netease.nimlib.sdk.auth.constant.LoginSyncStatus;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.MsgServiceObserve;
import com.netease.nimlib.sdk.msg.attachment.NotificationAttachment;
import com.netease.nimlib.sdk.msg.constant.MsgTypeEnum;
import com.netease.nimlib.sdk.msg.constant.NotificationType;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.netease.nimlib.sdk.msg.model.RecentContact;
import com.netease.nimlib.sdk.team.TeamService;
import com.netease.nimlib.sdk.team.model.Team;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.netease.nimlib.sdk.StatusCode.PWD_ERROR;

/**
 * Created by dowin on 2017/5/3.
 */

public class RecentContactObserver {
    final static String TAG = "RecentContactObserver";

    private RecentContactObserver() {

    }

    static class InstanceHolder {
        final static RecentContactObserver instance = new RecentContactObserver();
    }

    public static RecentContactObserver getInstance() {
        return InstanceHolder.instance;
    }

    /*******************************最近会话********************************/

    private List<RecentContact> items = new ArrayList<>();

    public void queryRecentContacts() {
        NIMClient.getService(MsgService.class).queryRecentContacts().setCallback(new RequestCallbackWrapper<List<RecentContact>>() {

            @Override
            public void onResult(int code, List<RecentContact> recentContacts, Throwable throwable) {

                items.clear();
                if (recentContacts != null) {
                    for (RecentContact c : recentContacts) {
                        doAddDeleteQuitTeamMessage(c, false);
                    }
                }
//                items.addAll(recentContacts);
                refreshMessages(true);
            }
        });
    }


    public void registerRecentContactObserver(boolean register) {

        NIMClient.getService(AuthServiceObserver.class).observeOnlineStatus(userStatusObserver, register);
        NIMClient.getService(AuthServiceObserver.class).observeLoginSyncDataStatus(loginSyncDataStatus, register);
        MsgServiceObserve service = NIMClient.getService(MsgServiceObserve.class);
        service.observeReceiveMessage(messageReceiverObserver, register);
        service.observeRecentContact(messageObserver, register);
        service.observeMsgStatus(statusObserver, register);
        service.observeRecentContactDeleted(deleteObserver, register);

    }

    private void deleteRecentContactCallback(RecentContact recent, boolean callback) {

        String contactId = recent.getContactId();
        SessionTypeEnum sessionTypeEnum = recent.getSessionType();
        // Log.d("deleteRecentContact-", "---" + contactId);
        // Log.d("deleteRecentContact-", "---" + recent.getContent());
        if (callback) {
            NIMClient.getService(MsgService.class).deleteRecentContact2(contactId, sessionTypeEnum);
        } else {
            NIMClient.getService(MsgService.class).deleteRecentContact(recent);
        }
        NIMClient.getService(MsgService.class).clearChattingHistory(contactId, sessionTypeEnum);
    }

    public boolean deleteRecentContact(String rContactId) {
        if (TextUtils.isEmpty(rContactId)) {
            return false;
        }
        boolean result = false;
        for (RecentContact recent : items) {
            String contactId = recent.getContactId();
            if (rContactId.equals(contactId)) {
                deleteRecentContactCallback(recent, true);
                result = true;
                break;
            }
        }
        return result;
    }

    private void onRecentContactChanged(List<RecentContact> recentContacts) {
        // Log.d("onRecentContactChanged", recentContacts.toString());
        int index;
        for (RecentContact r : recentContacts) {
            index = -1;
            for (int i = 0; i < items.size(); i++) {
                if (r.getContactId().equals(items.get(i).getContactId())
                        && r.getSessionType() == (items.get(i).getSessionType())) {
                    index = i;
                    break;
                }
            }

            if (index >= 0) {
                items.remove(index);
            }

            doAddDeleteQuitTeamMessage(r, false);

        }

        refreshMessages(true);
    }

    void doAddDeleteQuitTeamMessage(final RecentContact r, boolean isDelete) {
        if (isDelete && r.getSessionType() == SessionTypeEnum.Team) {

            final String contactId = r.getContactId();
            final Team t = NIMClient.getService(TeamService.class).queryTeamBlock(contactId);
            if (t != null) {
                TeamDataCache.getInstance().addOrUpdateTeam(t);
                if (t.isMyTeam()) {
                    items.add(r);
                } else {
                    deleteRecentContactCallback(r, false);
                }
            } else {
                TeamDataCache.getInstance().fetchTeamById(contactId, new SimpleCallback<Team>() {
                    @Override
                    public void onResult(boolean success, Team result) {
                        if (success) {
                            if (result != null) {
                                if (result.isMyTeam()) {
                                    items.add(r);
                                    refreshMessages(true);
                                    return;
                                } else {
                                    deleteRecentContactCallback(r, true);
                                }
                            } else {
                                deleteRecentContactCallback(r, true);
                            }
                        }
                    }
                });

            }
        } else {
            items.add(r);
        }
    }

    int unreadNum = 0;

    public void refreshMessages(boolean unreadChanged) {
        sortRecentContacts(items);


        if (unreadChanged) {

            // 方式一：累加每个最近联系人的未读（快）


//            for (RecentContact r : items) {
//                unreadNum += r.getUnreadCount();
//            }

            // 方式二：直接从SDK读取（相对慢）
//            unreadNum = NIMClient.getService(MsgService.class).getTotalUnreadCount();

//            if (callback != null) {
//                callback.onUnreadCountChange(unreadNum);
//            }
        }
        ReactCache.emit(ReactCache.observeRecentContact, ReactCache.createRecentList(items, unreadNum));
    }

    /**
     * **************************** 排序 ***********************************
     */
    private void sortRecentContacts(List<RecentContact> list) {
        if (list.size() == 0) {
            return;
        }
        Collections.sort(list, comp);
    }

    public static final long RECENT_TAG_STICKY = 1; // 联系人置顶tag
    private static Comparator<RecentContact> comp = new Comparator<RecentContact>() {

        @Override
        public int compare(RecentContact o1, RecentContact o2) {
            // 先比较置顶tag
            long sticky = (o1.getTag() & RECENT_TAG_STICKY) - (o2.getTag() & RECENT_TAG_STICKY);
            if (sticky != 0) {
                return sticky > 0 ? -1 : 1;
            } else {
                long time = o1.getTime() - o2.getTime();
                return time == 0 ? 0 : (time > 0 ? -1 : 1);
            }
        }
    };
    Observer<List<IMMessage>> messageReceiverObserver = new Observer<List<IMMessage>>() {

        @Override
        public void onEvent(List<IMMessage> imMessages) {
            if (imMessages == null || imMessages.isEmpty()) {
                return;
            }
            for (IMMessage m : imMessages) {
                if (m.getMsgType() == MsgTypeEnum.custom) {
                    CustomAttachment attachment = null;
                    try {
                        attachment = (CustomAttachment) m.getAttachment();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (attachment != null && attachment.getType() == CustomAttachmentType.AccountNotice) {
                        AccountNoticeAttachment noticeAttachment = null;
                        try {
                            noticeAttachment = (AccountNoticeAttachment) attachment;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        ReactCache.emit(ReactCache.observeAccountNotice, noticeAttachment == null ? null : noticeAttachment.toReactNative());
                        break;
                    }
                }
                if (m.getMsgType() == MsgTypeEnum.notification) {
                    NotificationAttachment notiAttachment = (NotificationAttachment) m.getAttachment();
                    NotificationType operationType = notiAttachment.getType();

                    // Log.d("imMessages", String.valueOf(operationType.getValue()));
                    switch (operationType) {
                        case DismissTeam:
                        case KickMember:
                        case InviteMember:
                        case AcceptInvite:
                            List<RecentContact> newItems = new ArrayList<RecentContact>();

                            for (int i=0; i < items.size(); i++) {
                                RecentContact newRecentContact = items.get(i);

                                if (items.get(i).getContactId().equals(m.getSessionId())) {
                                    newRecentContact.setLastMsg(m);
                                }

                                newItems.add(items.get(i));
                            }
//                            NIMClient.getService(MsgService.class).insertLocalMessage(m, m.getFromAccount());
                            items.clear();
                            items.addAll(newItems);
                            onRecentContactChanged(newItems);
                            break;
                        default:
                            break;
                    }
                }
            }
        }
    };
    Observer<List<RecentContact>> messageObserver = new Observer<List<RecentContact>>() {

        @Override
        public void onEvent(List<RecentContact> recentContacts) {//TODO
            onRecentContactChanged(recentContacts);
        }
    };
    Observer<IMMessage> statusObserver = new Observer<IMMessage>() {


        @Override
        public void onEvent(IMMessage imMessage) {

        }
    };
    Observer<RecentContact> deleteObserver = new Observer<RecentContact>() {

        @Override
        public void onEvent(RecentContact recentContact) {
            if (recentContact != null) {
                for (RecentContact item : items) {
                    if (TextUtils.equals(item.getContactId(), recentContact.getContactId())
                            && item.getSessionType() == recentContact.getSessionType()) {
                        items.remove(item);
                        refreshMessages(true);
                        break;
                    }
                }
            } else {
                items.clear();
                refreshMessages(true);
            }
        }
    };
    Observer<LoginSyncStatus> loginSyncDataStatus = new Observer<LoginSyncStatus>() {

        @Override
        public void onEvent(LoginSyncStatus loginSyncStatus) {
            if (loginSyncStatus == LoginSyncStatus.SYNC_COMPLETED) {
                refreshMessages(true);
            }
            WritableMap r = Arguments.createMap();
            r.putString("status", Integer.toString(StatusCode.values().length + loginSyncStatus.ordinal()));
            ReactCache.emit(ReactCache.observeOnlineStatus, r);
        }
    };
    Observer<StatusCode> userStatusObserver = new Observer<StatusCode>() {

        @Override
        public void onEvent(StatusCode code) {
            if (code != PWD_ERROR && code.wontAutoLogin()) {
                WritableMap r = Arguments.createMap();
                String status = "";
                switch (code) {
                    case KICKOUT:
                        status = "1";
                        break;
                    case KICK_BY_OTHER_CLIENT:
                        status = "3";
                        break;
                    case FORBIDDEN:
                        status = "2";
                        break;
                }
                if ("onHostPause".equals(RNNeteaseImModule.status)) {
                    RNNeteaseImModule.status = status;
                }else {
                    RNNeteaseImModule.status = "";
                }
                r.putString("status", status);
                ReactCache.emit(ReactCache.observeOnKick, r);
            } else {
                RNNeteaseImModule.status = "";
            }

            WritableMap r = Arguments.createMap();
            String codeValue;
            switch (code) {
                case PWD_ERROR:
                    codeValue = "10";
                    break;
                default:
                    codeValue = Integer.toString(code.getValue());
                    break;
            }
            r.putString("status", codeValue);
            LogUtil.w(TAG, "onHostStatus1:" + RNNeteaseImModule.status);
            LogUtil.w(TAG, "onHostStatus2:" + codeValue);
            ReactCache.emit(ReactCache.observeOnlineStatus, r);
        }
    };
}
