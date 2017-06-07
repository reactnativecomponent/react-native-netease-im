package com.netease.im.login;

import android.text.TextUtils;

import com.netease.im.ReactCache;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.RequestCallbackWrapper;
import com.netease.nimlib.sdk.StatusCode;
import com.netease.nimlib.sdk.auth.AuthServiceObserver;
import com.netease.nimlib.sdk.auth.constant.LoginSyncStatus;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.MsgServiceObserve;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.netease.nimlib.sdk.msg.model.RecentContact;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by dowin on 2017/5/3.
 */

public class RecentContactObserver {
    final static String TAG = "RecentContactObserver";
    /*******************************最近会话********************************/

    private List<RecentContact> items = new ArrayList<>();

    public void queryRecentContacts() {
        NIMClient.getService(MsgService.class).queryRecentContacts().setCallback(new RequestCallbackWrapper<List<RecentContact>>() {

            @Override
            public void onResult(int code, List<RecentContact> recentContacts, Throwable throwable) {

                items.clear();
                items.addAll(recentContacts);
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

    public boolean deleteRecentContact(String rContactId) {
        if(TextUtils.isEmpty(rContactId)){
            return false;
        }
        boolean result = false;
        for(RecentContact recent:items) {
            String contactId = recent.getContactId();
            if(rContactId.equals(contactId)) {
                NIMClient.getService(MsgService.class).deleteRecentContact2(contactId, recent.getSessionType());
                NIMClient.getService(MsgService.class).clearChattingHistory(contactId, recent.getSessionType());
                result = true;
                break;
            }
        }
        return result;
    }

    private void onRecentContactChanged(List<RecentContact> recentContacts) {
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

            items.add(r);
        }

        refreshMessages(true);
    }
    int unreadNum = 0;
    private void refreshMessages(boolean unreadChanged) {
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

        }
    };
    Observer<List<RecentContact>> messageObserver = new Observer<List<RecentContact>>() {

        @Override
        public void onEvent(List<RecentContact> recentContacts) {
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
            if(loginSyncStatus==LoginSyncStatus.SYNC_COMPLETED) {
                refreshMessages(true);
            }
            ReactCache.emit(ReactCache.observeOnlineStatus, Integer.toString(StatusCode.values().length + loginSyncStatus.ordinal()));
        }
    };
    Observer<StatusCode> userStatusObserver = new Observer<StatusCode>() {

        @Override
        public void onEvent(StatusCode code) {
            ReactCache.emit(ReactCache.observeOnlineStatus, Integer.toString(code.getValue()));
        }
    };
}
