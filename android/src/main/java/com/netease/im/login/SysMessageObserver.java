package com.netease.im.login;

import android.text.TextUtils;

import com.netease.im.ReactCache;
import com.netease.im.uikit.cache.NimUserInfoCache;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.RequestCallbackWrapper;
import com.netease.nimlib.sdk.ResponseCode;
import com.netease.nimlib.sdk.friend.FriendService;
import com.netease.nimlib.sdk.friend.model.AddFriendNotify;
import com.netease.nimlib.sdk.msg.SystemMessageObserver;
import com.netease.nimlib.sdk.msg.SystemMessageService;
import com.netease.nimlib.sdk.msg.constant.SystemMessageStatus;
import com.netease.nimlib.sdk.msg.constant.SystemMessageType;
import com.netease.nimlib.sdk.msg.model.SystemMessage;
import com.netease.nimlib.sdk.team.TeamService;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by dowin on 2017/5/3.
 */

public class SysMessageObserver {

    final static String TAG = "SysMessageObserver";
    /*******************************系统消息********************************/

    private static final boolean MERGE_ADD_FRIEND_VERIFY = true; // 是否要合并好友申请，同一个用户仅保留最近一条申请内容（默认不合并）

    private static final int LOAD_MESSAGE_COUNT = 10;

    private List<SystemMessage> sysItems = new ArrayList<>();
    private int loadOffset = 0;
    private Set<String> addFriendVerifyRequestAccounts = new HashSet<>(); // 发送过好友申请的账号（好友申请合并用）

    private Set<Long> itemIds = new HashSet<>();

    // db
    private boolean firstLoad = true;

    /**
     * 加载历史消息
     */
    public void loadMessages() {
        boolean loadCompleted; // 是否已经加载完成，后续没有数据了or已经满足本次请求数量
        int validMessageCount = 0; // 实际加载的数量（排除被过滤被合并的条目）
        List<String> messageFromAccounts = new ArrayList<>(LOAD_MESSAGE_COUNT);

        while (true) {
            List<SystemMessage> temps = NIMClient.getService(SystemMessageService.class)
                    .querySystemMessagesBlock(loadOffset, LOAD_MESSAGE_COUNT);

            loadOffset += temps.size();
            loadCompleted = temps.size() < LOAD_MESSAGE_COUNT;

            int tempValidCount = 0;

            for (SystemMessage m : temps) {
                // 去重
                if (duplicateFilter(m)) {
                    continue;
                }

                // 同一个账号的好友申请仅保留最近一条
                if (addFriendVerifyFilter(m)) {
                    continue;
                }

                // 保存有效消息
                sysItems.add(m);
                tempValidCount++;
                if (!messageFromAccounts.contains(m.getFromAccount())) {
                    messageFromAccounts.add(m.getFromAccount());
                }

                // 判断是否达到请求数
                if (++validMessageCount >= LOAD_MESSAGE_COUNT) {
                    loadCompleted = true;
                    // 已经满足要求，此时需要修正loadOffset
                    loadOffset -= temps.size();
                    loadOffset += tempValidCount;

                    break;
                }
            }

            if (loadCompleted) {
                break;
            }
        }
        firstLoad = false;
        // 更新数据源，刷新界面
        refresh();

        // 收集未知用户资料的账号集合并从远程获取
        collectAndRequestUnknownUserInfo(messageFromAccounts);
    }

    boolean hasRegister;

    public void registerSystemObserver(boolean register) {
        if (hasRegister && register) {
            return;
        }
        hasRegister = register;
        NIMClient.getService(SystemMessageObserver.class).observeReceiveSystemMsg(systemMessageObserver, register);
    }

    private Observer<SystemMessage> systemMessageObserver = new Observer<SystemMessage>() {
        @Override
        public void onEvent(SystemMessage systemMessage) {
            onIncomingMessage(systemMessage);
        }
    };


    private void onIncomingMessage(final SystemMessage message) {
        // 同一个账号的好友申请仅保留最近一条
        if (addFriendVerifyFilter(message)) {
            SystemMessage del = null;
            for (SystemMessage m : sysItems) {
                if (m.getFromAccount().equals(message.getFromAccount()) && m.getType() == SystemMessageType.AddFriend) {
                    AddFriendNotify attachData = (AddFriendNotify) m.getAttachObject();
                    if (attachData != null && attachData.getEvent() == AddFriendNotify.Event.RECV_ADD_FRIEND_VERIFY_REQUEST) {
                        del = m;
                        break;
                    }
                }
            }

            if (del != null) {
                sysItems.remove(del);
            }
        }

        loadOffset++;
        sysItems.add(0, message);

        refresh();

        // 收集未知用户资料的账号集合并从远程获取
        List<String> messageFromAccounts = new ArrayList<>(1);
        messageFromAccounts.add(message.getFromAccount());
        collectAndRequestUnknownUserInfo(messageFromAccounts);
    }

    // 同一个账号的好友申请仅保留最近一条
    private boolean addFriendVerifyFilter(final SystemMessage msg) {
        if (!MERGE_ADD_FRIEND_VERIFY) {
            return false; // 不需要MERGE，不过滤
        }

        if (msg.getType() != SystemMessageType.AddFriend) {
            return false; // 不过滤
        }

        AddFriendNotify attachData = (AddFriendNotify) msg.getAttachObject();
        if (attachData == null) {
            return true; // 过滤
        }

        if (attachData.getEvent() != AddFriendNotify.Event.RECV_ADD_FRIEND_VERIFY_REQUEST) {
            return false; // 不过滤
        }

        if (addFriendVerifyRequestAccounts.contains(msg.getFromAccount())) {
            return true; // 过滤
        }

        addFriendVerifyRequestAccounts.add(msg.getFromAccount());
        return false; // 不过滤
    }


    // 请求未知的用户资料
    private void collectAndRequestUnknownUserInfo(List<String> messageFromAccounts) {
        List<String> unknownAccounts = new ArrayList<>();
        for (String account : messageFromAccounts) {
            if (!NimUserInfoCache.getInstance().hasUser(account)) {
                unknownAccounts.add(account);
            }
        }

        if (!unknownAccounts.isEmpty()) {
            requestUnknownUser(unknownAccounts);
        }
    }

    private void requestUnknownUser(List<String> accounts) {
        NimUserInfoCache.getInstance().getUserInfoFromRemote(accounts, new RequestCallbackWrapper<List<NimUserInfo>>() {
            @Override
            public void onResult(int code, List<NimUserInfo> users, Throwable exception) {
                if (code == ResponseCode.RES_SUCCESS) {
                    if (users != null && !users.isEmpty()) {
                        refresh();
                    }
                }
            }
        });
    }

    // 去重
    private boolean duplicateFilter(final SystemMessage msg) {
        if (itemIds.contains(msg.getMessageId())) {
            return true;
        }

        itemIds.add(msg.getMessageId());
        return false;
    }

    private void refresh() {
        ReactCache.emit(ReactCache.observeReceiveSystemMsg, ReactCache.createSystemMsg(sysItems));
    }

    public void startSystemMsg() {
        loadMessages();
        registerSystemObserver(true);
    }

    public void stopSystemMsg() {
        registerSystemObserver(false);
    }

    public void deleteSystemMessageById(String contactId) {

        for (int i = sysItems.size() - 1; i >= 0; i--) {
            SystemMessage msg = sysItems.get(i);
            if (TextUtils.equals(contactId, msg.getFromAccount())) {
                NIMClient.getService(SystemMessageService.class).deleteSystemMessage(msg.getMessageId());
                sysItems.remove(i);
            }
        }
        refresh();
    }

    public void deleteSystemMessage(long messageId) {
        if(firstLoad){
            loadMessages();
        }
        NIMClient.getService(SystemMessageService.class).deleteSystemMessage(messageId);
        for (int i = sysItems.size() - 1; i >= 0; i--) {
            SystemMessage msg = sysItems.get(i);
            if (messageId == msg.getMessageId()) {
                sysItems.remove(i);
                break;
            }
        }
    }

    public void acceptInvite(final long messageId, String targetId, String fromAccount, final boolean pass, String timestamp, final RequestCallbackWrapper<Void> callbackWrapper) {
        RequestCallbackWrapper<Void> callback = new RequestCallbackWrapper<Void>() {
            @Override
            public void onResult(int code, Void aVoid, Throwable throwable) {
                if (callbackWrapper != null) {
                    callbackWrapper.onResult(code, aVoid, throwable);
                }
                if (code == ResponseCode.RES_SUCCESS) {
                    onProcessSuccess(pass, messageId);
                } else {
                    onProcessFailed(code, messageId);
                }
            }
        };
        if (pass) {
            NIMClient.getService(TeamService.class).acceptInvite(targetId, fromAccount).setCallback(callback);
        } else {
            NIMClient.getService(TeamService.class).declineInvite(targetId, fromAccount, "").setCallback(callback);
        }
    }

    public void passApply(final long messageId, String targetId, String fromAccount, final boolean pass, String timestamp, final RequestCallbackWrapper<Void> callbackWrapper) {
        RequestCallbackWrapper<Void> callback = new RequestCallbackWrapper<Void>() {
            @Override
            public void onResult(int code, Void aVoid, Throwable throwable) {
                if (callbackWrapper != null) {
                    callbackWrapper.onResult(code, aVoid, throwable);
                }
                if (code == ResponseCode.RES_SUCCESS) {
                    onProcessSuccess(pass, messageId);
                } else {
                    onProcessFailed(code, messageId);
                }
            }
        };
        if (pass) {
            NIMClient.getService(TeamService.class).passApply(targetId, fromAccount).setCallback(callback);
        } else {
            NIMClient.getService(TeamService.class).rejectApply(targetId, fromAccount, "").setCallback(callback);
        }
    }

    public void ackAddFriendRequest(final long messageId, final String contactId, final boolean pass, String timestamp, final RequestCallbackWrapper<Void> callbackWrapper) {
        NIMClient.getService(FriendService.class).ackAddFriendRequest(contactId, pass)
                .setCallback(new RequestCallbackWrapper<Void>() {
                    @Override
                    public void onResult(int code, Void aVoid, Throwable throwable) {
                        if (callbackWrapper != null) {
                            callbackWrapper.onResult(code, aVoid, throwable);
                        }
                        if (code == ResponseCode.RES_SUCCESS) {
                            onProcessSuccess(pass, messageId);
                        } else {
                            onProcessFailed(code, messageId);
                        }
                    }
                });
    }

    private void onProcessSuccess(final boolean pass, long messageId) {
        SystemMessageStatus status = pass ? SystemMessageStatus.passed : SystemMessageStatus.declined;
        NIMClient.getService(SystemMessageService.class).setSystemMessageStatus(messageId, status);
        refreshViewHolder(messageId, status);
    }

    private void onProcessFailed(final int code, long messageId) {
        if (code == 408) {
            return;
        }

        SystemMessageStatus status = SystemMessageStatus.expired;
        NIMClient.getService(SystemMessageService.class).setSystemMessageRead(messageId);
        NIMClient.getService(SystemMessageService.class).setSystemMessageStatus(messageId, status);
        refreshViewHolder(messageId, status);
    }

    private void refreshViewHolder(long messageId, SystemMessageStatus status) {

        int index = -1;
        for (int i = 0; i < sysItems.size(); i++) {
            SystemMessage item = sysItems.get(i);
            if (messageId == item.getMessageId()) {
                item.setStatus(status);
                index = i;
                break;
            }
        }

        if (index < 0) {
            return;
        }

        refresh();
    }

    public void clearSystemMessages() {
        NIMClient.getService(SystemMessageService.class).clearSystemMessages();
        NIMClient.getService(SystemMessageService.class).resetSystemMessageUnreadCount();
        refresh();
    }
}
