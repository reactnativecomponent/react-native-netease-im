package com.netease.im.session;

import android.media.AudioManager;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableMap;
import com.netease.im.MessageUtil;
import com.netease.im.ReactCache;
import com.netease.im.login.LoginService;
import com.netease.im.session.extension.BankTransferAttachment;
import com.netease.im.session.extension.CustomAttachment;
import com.netease.im.session.extension.CustomAttachmentType;
import com.netease.im.session.extension.DefaultCustomAttachment;
import com.netease.im.session.extension.RedPacketAttachement;
import com.netease.im.session.extension.RedPacketOpenAttachement;
import com.netease.im.uikit.cache.TeamDataCache;
import com.netease.im.uikit.common.util.log.LogUtil;
import com.netease.im.uikit.common.util.string.MD5;
import com.netease.im.uikit.session.helper.MessageHelper;
import com.netease.im.uikit.session.helper.MessageListPanelHelper;
import com.netease.im.uikit.uinfo.UserInfoHelper;
import com.netease.im.uikit.uinfo.UserInfoObservable;
import com.netease.nimlib.sdk.AbortableFuture;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.RequestCallbackWrapper;
import com.netease.nimlib.sdk.ResponseCode;
import com.netease.nimlib.sdk.friend.FriendService;
import com.netease.nimlib.sdk.msg.MessageBuilder;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.MsgServiceObserve;
import com.netease.nimlib.sdk.msg.attachment.FileAttachment;
import com.netease.nimlib.sdk.msg.attachment.MsgAttachment;
import com.netease.nimlib.sdk.msg.constant.AttachStatusEnum;
import com.netease.nimlib.sdk.msg.constant.MsgDirectionEnum;
import com.netease.nimlib.sdk.msg.constant.MsgStatusEnum;
import com.netease.nimlib.sdk.msg.constant.MsgTypeEnum;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.AttachmentProgress;
import com.netease.nimlib.sdk.msg.model.CustomMessageConfig;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.netease.nimlib.sdk.msg.model.MemberPushOption;
import com.netease.nimlib.sdk.msg.model.MessageReceipt;
import com.netease.nimlib.sdk.msg.model.QueryDirectionEnum;
import com.netease.nimlib.sdk.team.model.Team;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by dowin on 2017/5/10.
 */

public class SessionService {

    final static String TAG = "SessionService2";

    private static final int LOAD_MESSAGE_COUNT = 20;


    private SessionTypeEnum sessionTypeEnum = SessionTypeEnum.None;
    private String sessionId;

    private IMMessage fistMessage;
    private IMMessage lastMessage;
    /************************* 时间显示处理 ************************/

    private Set<String> timedItems = new HashSet<>(); // 需要显示消息时间的消息ID
    private IMMessage lastShowTimeItem; // 用于消息时间显示,判断和上条消息间的时间间隔

    private Handler handler;
    private boolean mute = false;

    private SessionService() {
    }

    static class InstanceHolder {
        final static SessionService instance = new SessionService();
    }

    public static SessionService getInstance() {
        return InstanceHolder.instance;
    }


    public String getSessionId() {
        return sessionId;
    }

    public SessionTypeEnum getSessionTypeEnum() {
        return sessionTypeEnum;
    }


    private IMMessage anchorMessage(QueryDirectionEnum direction) {

        IMMessage message = direction == QueryDirectionEnum.QUERY_NEW ? lastMessage : fistMessage;
        if (message == null) {
            message = MessageBuilder.createEmptyMessage(sessionId, sessionTypeEnum, 0);
        }
        return message;
    }


    /**
     * 接收消息
     *
     * @param messages
     */
    public void onIncomingMessage(@NonNull List<IMMessage> messages) {
        boolean needRefresh = false;
        List<IMMessage> addedListItems = new ArrayList<>(messages.size());
        for (IMMessage message : messages) {
            if (isMyMessage(message)) {
                addedListItems.add(message);
                needRefresh = true;
            }
        }
        if (needRefresh) {
            sortMessages(addedListItems);
        }
        if (addedListItems.size() > 0) {
            updateShowTimeItem(addedListItems, false);
        }
        List<IMMessage> r = onQuery(addedListItems);
        if (r.size() > 0) {
            IMMessage m = messages.get(0);
            if (!this.mute && m.getDirect() == MsgDirectionEnum.In) {
                if (showMsg(m)) {
                    if (m.getAttachment() != null && (m.getAttachment() instanceof RedPacketAttachement)) {
                        AudioPlayService.getInstance().playAudio(handler, ReactCache.getReactContext(), AudioManager.STREAM_RING, "raw", "rp");
                    } else {
                        AudioPlayService.getInstance().playAudio(handler, ReactCache.getReactContext(), AudioManager.STREAM_RING, "raw", "msg");
                    }
                }

            }
        }
        refreshMessageList(r);

    }

    boolean showMsg(IMMessage m) {
        return !(m.getMsgType() == MsgTypeEnum.notification || m.getMsgType() == MsgTypeEnum.tip
                || (m.getAttachment() != null && (m.getAttachment() instanceof RedPacketOpenAttachement);
    }

    public boolean isMyMessage(IMMessage message) {
        return message.getSessionType() == sessionTypeEnum
                && message.getSessionId() != null
                && message.getSessionId().equals(sessionId);
    }

    /**
     * 列表加入新消息时，更新时间显示
     *
     * @param items
     * @param isQuery
     */
    public void updateShowTimeItem(List<IMMessage> items, boolean isQuery) {
//        IMMessage anchor = isQuery ? items.get(0) : lastMessage;
//
//        for (IMMessage message : items) {
//            if (setShowTimeFlag(message, anchor)) {
//                anchor = message;
//            }
//        }

        if (!isQuery && fistMessage != null) {
            fistMessage = items.get(0);
        }

        if (isQuery && lastMessage != null) {
            lastMessage = items.get(items.size() - 1);
        }
    }

    /**
     * 是否显示时间item
     *
     * @param message
     * @param anchor
     * @return
     */
    private boolean setShowTimeFlag(IMMessage message, IMMessage anchor) {
        boolean update = false;

        if (hideTimeAlways(message)) {
            setShowTime(message, false);
        } else {
            if (anchor == null) {
                setShowTime(message, true);
                update = true;
            } else {
                long time = anchor.getTime();
                long now = message.getTime();

                if (now - time == 0) {
                    // 消息撤回时使用
                    setShowTime(message, true);
                    lastShowTimeItem = message;
                    update = true;
                } else if (now - time < (long) (5 * 60 * 1000)) {
                    setShowTime(message, false);
                } else {
                    setShowTime(message, true);
                    update = true;
                }
            }
        }

        return update;
    }

    private void setShowTime(IMMessage message, boolean show) {
        if (show) {
            timedItems.add(message.getUuid());
        } else {
            timedItems.remove(message.getUuid());
        }
    }

    private boolean hideTimeAlways(IMMessage message) {
        switch (message.getMsgType()) {
            case notification:
                return true;
            default:
                return false;
        }
    }


    /**
     * 发送消息后，更新本地消息列表
     *
     * @param message
     */
    public void onMsgSend(IMMessage message) {
        List<IMMessage> addedListItems = new ArrayList<>(1);
        addedListItems.add(message);
        updateShowTimeItem(addedListItems, false);
    }

    /**
     * 删除消息
     *
     * @param messageItem
     * @param isRelocateTime
     */
    public void deleteItem(IMMessage messageItem, boolean isRelocateTime) {
        if (messageItem == null) {
            return;
        }
        getMsgService().deleteChattingHistory(messageItem);
    }

    /**
     * @return
     */
    private IMMessage getLastReceiptMessage(List<IMMessage> messageList) {
        IMMessage lastMessage = null;
        for (int i = messageList.size() - 1; i >= 0; i--) {
            if (sendReceiptCheck(messageList.get(i))) {
                lastMessage = messageList.get(i);
                break;
            }
        }

        return lastMessage;
    }

    private boolean sendReceiptCheck(final IMMessage msg) {
        if (msg == null || msg.getDirect() != MsgDirectionEnum.In ||
                msg.getMsgType() == MsgTypeEnum.tip || msg.getMsgType() == MsgTypeEnum.notification) {
            return false; // 非收到的消息，Tip消息和通知类消息，不要发已读回执
        }

        return true;
    }

    /**
     * 发送已读回执（需要过滤）
     *
     * @param messageList
     */

    public void sendMsgReceipt(@NonNull List<IMMessage> messageList) {
        if (sessionId == null || sessionTypeEnum != SessionTypeEnum.P2P) {
            return;
        }

        IMMessage message = getLastReceiptMessage(messageList);
        if (!sendReceiptCheck(message)) {
            return;
        }

        getMsgService().sendMessageReceipt(sessionId, message);
    }

    /**
     * 消息接收观察者
     */
    Observer<List<IMMessage>> incomingMessageObserver = new Observer<List<IMMessage>>() {
        @Override
        public void onEvent(List<IMMessage> messages) {
            if (messages == null || messages.isEmpty()) {
                return;
            }
            sendMsgReceipt(messages); // 发送已读回执
            onIncomingMessage(messages);

        }
    };

    /**
     * 收到已读回执（更新VH的已读label）
     */

    private void receiveReceipt() {//TODO

    }

    private void onMessageStatusChange(IMMessage message, boolean isSend) {
        if (isMyMessage(message)) {
            List<IMMessage> list = new ArrayList<>(1);
            list.add(message);
            Object a = ReactCache.createMessageList(list);
            ReactCache.emit(ReactCache.observeMsgStatus, a);
        }
    }

    /**
     * 收到已读回执
     */
    private Observer<List<MessageReceipt>> messageReceiptObserver = new Observer<List<MessageReceipt>>() {
        @Override
        public void onEvent(List<MessageReceipt> messageReceipts) {
            receiveReceipt();
        }
    };


    /**
     * 消息状态变化观察者
     */
    Observer<IMMessage> messageStatusObserver = new Observer<IMMessage>() {
        @Override
        public void onEvent(IMMessage message) {
            onMessageStatusChange(message, false);
        }
    };
    /**
     * 消息附件上传/下载进度观察者
     */
    Observer<AttachmentProgress> attachmentProgressObserver = new Observer<AttachmentProgress>() {
        @Override
        public void onEvent(AttachmentProgress progress) {
//            onAttachmentProgressChange(progress);
        }
    };

    /**
     * 本地消息接收观察者
     */
    MessageListPanelHelper.LocalMessageObserver incomingLocalMessageObserver = new MessageListPanelHelper.LocalMessageObserver() {
        @Override
        public void onAddMessage(IMMessage message) {
            if (message == null || !sessionId.equals(message.getSessionId())) {
                return;
            }

            onMsgSend(message);
        }

        @Override
        public void onClearMessages(String account) {
            refreshMessageList(null);
        }
    };

    /**
     * 消息撤回观察者
     */
    Observer<IMMessage> revokeMessageObserver = new Observer<IMMessage>() {
        @Override
        public void onEvent(IMMessage message) {
            if (message == null || sessionId == null || !sessionId.equals(message.getSessionId())) {
                return;
            }

            deleteItem(message, false);
            revokMessage(message);
//            MessageHelper.getInstance().onRevokeMessage(message);
        }
    };
    private UserInfoObservable.UserInfoObserver uinfoObserver;

    private void registerUserInfoObserver() {
        if (uinfoObserver == null) {
            uinfoObserver = new UserInfoObservable.UserInfoObserver() {
                @Override
                public void onUserInfoChanged(List<String> accounts) {
                    if (sessionTypeEnum == SessionTypeEnum.P2P) {
                        if (accounts.contains(sessionId) || accounts.contains(LoginService.getInstance().getAccount())) {
                            //TODO 刷新
                        }
                    } else { // 群的，简单的全部重刷
                        //TODO 刷新
                    }
                }
            };
        }

        UserInfoHelper.registerObserver(uinfoObserver);
    }

    private void unregisterUserInfoObserver() {
        if (uinfoObserver != null) {
            UserInfoHelper.unregisterObserver(uinfoObserver);
        }
    }

    /**
     * anchor 查询锚点
     *
     * @param anchor
     * @param limit  查询结果的条数限制
     */
    public void queryMessageListEx(IMMessage anchor, final QueryDirectionEnum direction, final int limit, final OnMessageQueryListListener onMessageQueryListListener) {

        if (anchor == null) {
            anchor = MessageBuilder.createEmptyMessage(sessionId, sessionTypeEnum, 0);
        }
        getMsgService().queryMessageListEx(anchor, direction, limit, true)
                .setCallback(new RequestCallbackWrapper<List<IMMessage>>() {

                    @Override
                    public void onResult(int code, List<IMMessage> result, Throwable exception) {
                        if (code == ResponseCode.RES_SUCCESS) {
                            if (result != null && result.size() > 0) {
                                fistMessage = result.get(0);
                                updateShowTimeItem(result, true);

                                final int size = result.size();
                                boolean isLimit = size >= limit;
                                List<IMMessage> r = onQuery(result);

                                if (r.size() == 0) {
                                    queryMessageListEx(fistMessage, direction, size - r.size(), onMessageQueryListListener);
                                } else {
                                    onMessageQueryListListener.onResult(code, r, timedItems);

                                    if (r.size() < size && isLimit) {
                                        fistMessage = result.get(0);
//                                    queryMessageListEx(fistMessage, direction, size - r.size(), onMessageQueryListListener);
                                    }
                                }

                                return;
                            }
                        }
                        onMessageQueryListListener.onResult(code, null, null);
                    }
                });
    }

    List<IMMessage> onQuery(List<IMMessage> result) {//TODO


        for (int i = result.size() - 1; i >= 0; i--) {
            IMMessage message = result.get(i);
            if (message == null) {
                result.remove(i);
            }
            MsgAttachment attachment = message.getAttachment();
            if (attachment != null) {
                if (message.getMsgType() == MsgTypeEnum.custom) {
                    CustomAttachment customAttachment = (CustomAttachment) attachment;
                    if (customAttachment.getType() == CustomAttachmentType.RedPacketOpen) {
                        RedPacketOpenAttachement rpOpen = (RedPacketOpenAttachement) attachment;
                        if (!rpOpen.isSelf()) {
                            result.remove(i);
                        }
                    }
                }
            }
        }
        return result;
    }

    boolean hasRegister;

    private void registerObservers(boolean register) {
        if (hasRegister && register) {
            return;
        }
        hasRegister = register;
        MsgServiceObserve service = NIMClient.getService(MsgServiceObserve.class);
        service.observeReceiveMessage(incomingMessageObserver, register);
        service.observeMessageReceipt(messageReceiptObserver, register);

        service.observeMsgStatus(messageStatusObserver, register);
//        service.observeAttachmentProgress(attachmentProgressObserver, register);
        service.observeRevokeMessage(revokeMessageObserver, register);
        observerAttachProgress(register);
        if (register) {
            registerUserInfoObserver();
        } else {
            unregisterUserInfoObserver();
        }

        MessageListPanelHelper.getInstance().registerObserver(incomingLocalMessageObserver, register);
    }

    /****************************** 排序 ***********************************/
    private void sortMessages(List<IMMessage> list) {
        if (list.size() == 0) {
            return;
        }
        Collections.sort(list, comp);
    }

    private static Comparator<IMMessage> comp = new Comparator<IMMessage>() {

        @Override
        public int compare(IMMessage o1, IMMessage o2) {
            long time = o1.getTime() - o2.getTime();
            return time == 0 ? 0 : (time < 0 ? -1 : 1);
        }
    };

    /****************************** 消息处理 ***********************************/

    public void startSession(Handler handler, String sessionId, String type) {
        clear();
        this.handler = handler;
        this.sessionId = sessionId;

        sessionTypeEnum = SessionUtil.getSessionType(type);

        if (sessionTypeEnum == SessionTypeEnum.P2P) {
            this.mute = !NIMClient.getService(FriendService.class).isNeedMessageNotify(sessionId);
        } else {
            Team t = TeamDataCache.getInstance().getTeamById(sessionId);
            this.mute = t.mute();
        }
        registerObservers(true);
        getMsgService().setChattingAccount(sessionId, sessionTypeEnum);
    }

    void clear() {
        sessionId = null;
        timedItems.clear();
        fistMessage = null;
        lastMessage = null;
        lastShowTimeItem = null;
    }

    public void stopSession() {
        clear();
        registerObservers(false);
        getMsgService().setChattingAccount(MsgService.MSG_CHATTING_ACCOUNT_NONE,
                SessionTypeEnum.None);
    }

    private void refreshMessageList(List<IMMessage> messageList) {
        if (messageList == null || messageList.isEmpty()) {
            return;
        }
        Object a = ReactCache.createMessageList(messageList);
        ReactCache.emit(ReactCache.observeReceiveMessage, a);
    }

    /**
     * 重发消息到服务器
     *
     * @param messageId
     */
    public void resendMessage(String messageId) {
        // 重置状态为unsent
        queryMessage(messageId, new OnMessageQueryListener() {
            @Override
            public int onResult(int code, IMMessage message) {
                IMMessage item = message;
                item.setStatus(MsgStatusEnum.sending);
                deleteItem(item, true);
                onMsgSend(item);
                getMsgService().sendMessage(item, true);
                return 0;
            }
        });
    }

    /**
     * @param content
     */
    public void sendTextMessage(String content, List<String> selectedMembers, OnSendMessageListener onSendMessageListener) {

        IMMessage message = MessageBuilder.createTextMessage(sessionId, sessionTypeEnum, content);

        if (selectedMembers != null && !selectedMembers.isEmpty()) {
            MemberPushOption option = createMemPushOption(selectedMembers, message);
            message.setPushContent("有人@了你");
            message.setMemberPushOption(option);
        }
        sendMessage(message, onSendMessageListener);
    }

    /**
     * @param content
     */
    public void sendTipMessage(String content, OnSendMessageListener onSendMessageListener) {
        CustomMessageConfig config = new CustomMessageConfig();
        config.enablePush = false; // 不推送
        IMMessage message = MessageBuilder.createTipMessage(sessionId, sessionTypeEnum);
        if (sessionTypeEnum == SessionTypeEnum.Team) {
            Map<String, Object> contentMap = new HashMap<>(1);
            contentMap.put("content", content);
            message.setRemoteExtension(contentMap);
            message.setConfig(config);
            message.setStatus(MsgStatusEnum.success);
            getMsgService().saveMessageToLocal(message, true);
        } else {

            message.setContent(content);
            message.setConfig(config);
            sendMessage(message, onSendMessageListener);
        }
    }

    public void sendImageMessage(String file, String displayName, OnSendMessageListener onSendMessageListener) {
        file = Uri.parse(file).getPath();
        File f = new File(file);
        IMMessage message = MessageBuilder.createImageMessage(sessionId, sessionTypeEnum, f, TextUtils.isEmpty(displayName) ? f.getName() : displayName);
        sendMessage(message, onSendMessageListener);
    }

    public void sendAudioMessage(String file, long duration, OnSendMessageListener onSendMessageListener) {
        file = Uri.parse(file).getPath();
        File f = new File(file);

        IMMessage message = MessageBuilder.createAudioMessage(sessionId, sessionTypeEnum, f, duration);
        sendMessage(message, onSendMessageListener);
    }

    //        String md5Path = StorageUtil.getWritePath(filename, StorageType.TYPE_VIDEO);
//        MediaPlayer mediaPlayer = getVideoMediaPlayer(f);
//        long duration = mediaPlayer == null ? 0 : mediaPlayer.getDuration();
//        int height = mediaPlayer == null ? 0 : mediaPlayer.getVideoHeight();
//        int width = mediaPlayer == null ? 0 : mediaPlayer.getVideoWidth();
    public void sendVideoMessage(String file, String duration, int width, int height, String displayName, OnSendMessageListener onSendMessageListener) {


//        String filename = md5 + "." + FileUtil.getExtensionName(file);
        file = Uri.parse(file).getPath();
        String md5 = TextUtils.isEmpty(displayName) ? MD5.getStreamMD5(file) : displayName;
        File f = new File(file);
        long durationL = 0;
        try {
            durationL = Long.parseLong(duration);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        IMMessage message = MessageBuilder.createVideoMessage(sessionId, sessionTypeEnum, f, durationL, width, height, md5);
        sendMessage(message, onSendMessageListener);
    }

    public void sendLocationMessage(String latitude, String longitude, String address, OnSendMessageListener onSendMessageListener) {
        double lat = 23.12504;
        try {
            lat = Double.parseDouble(latitude);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        double lon = 113.327474;
        try {
            lon = Double.parseDouble(longitude);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        IMMessage message = MessageBuilder.createLocationMessage(sessionId, sessionTypeEnum, lat, lon, address);
        sendMessage(message, onSendMessageListener);
    }

    public void sendDefaultMessage(String type, String digst, String content, OnSendMessageListener onSendMessageListener) {
        CustomMessageConfig config = new CustomMessageConfig();
        DefaultCustomAttachment attachment = new DefaultCustomAttachment(type);
        attachment.setDigst(digst);
        attachment.setContent(content);
        IMMessage message = MessageBuilder.createCustomMessage(sessionId, sessionTypeEnum, digst, attachment, config);
        sendMessage(message, onSendMessageListener);
    }

    public void sendRedPacketOpenMessage(String sendId, String openId, String hasRedPacket, String serialNo, OnSendMessageListener onSendMessageListener) {
//        CustomMessageConfig config = new CustomMessageConfig();
//        config.enableUnreadCount = false;
//        config.enablePush = false;
//        RedPacketOpenAttachement attachment = new RedPacketOpenAttachement();
//        attachment.setParams(sendId, openId, hasRedPacket, serialNo);
//        IMMessage message = MessageBuilder.createCustomMessage(sessionId, sessionTypeEnum, sendId + ";" + openId, attachment, config);
//
////        message.
//        sendMessage(message, onSendMessageListener);
        long timestamp = new Date().getTime() / 1000;
        SessionUtil.sendRedPacketOpenNotification(sessionId, sessionTypeEnum, sendId, openId, hasRedPacket, serialNo, timestamp);
        SessionUtil.sendRedPacketOpenLocal(sessionId, sessionTypeEnum, sendId, openId, hasRedPacket, serialNo, timestamp);
    }

    public void sendRedPacketMessage(String type, String comments, String serialNo, OnSendMessageListener onSendMessageListener) {
        CustomMessageConfig config = new CustomMessageConfig();
        RedPacketAttachement attachment = new RedPacketAttachement();
        attachment.setParams(type, comments, serialNo);
        IMMessage message = MessageBuilder.createCustomMessage(sessionId, sessionTypeEnum, comments, attachment, config);
        sendMessage(message, onSendMessageListener);
    }

    public void sendBankTransferMessage(String amount, String comments, String serialNo, OnSendMessageListener onSendMessageListener) {
        CustomMessageConfig config = new CustomMessageConfig();
        BankTransferAttachment attachment = new BankTransferAttachment();
        attachment.setParams(amount, comments, serialNo);
        IMMessage message = MessageBuilder.createCustomMessage(sessionId, sessionTypeEnum, comments, attachment, config);
        sendMessage(message, onSendMessageListener);
    }

    public int sendForwardMessage(IMMessage selectMessage, final String sessionId, final String sessionType, String content, OnSendMessageListener onSendMessageListener) {
        if (selectMessage == null) {
            return 0;
        }
        if (MessageUtil.shouldIgnore(selectMessage)) {
            return 1;
        }
        SessionTypeEnum sessionTypeE = SessionUtil.getSessionType(sessionType);
        IMMessage message = MessageBuilder.createForwardMessage(selectMessage, sessionId, sessionTypeE);
        if (message == null) {
            return 1;
        }
        sendMessage(message, onSendMessageListener);
        return 2;
    }

    void revokMessage(IMMessage message) {
        WritableMap msg = Arguments.createMap();
        msg.putString("_id", message.getUuid());
        ReactCache.emit(ReactCache.observeDeleteMessage, msg);
    }

    public int revokeMessage(final IMMessage selectMessage, final OnSendMessageListener onSendMessageListener) {
        if (selectMessage == null) {
            return 0;
        }
        if (MessageUtil.shouldIgnoreRevoke(selectMessage)) {
            return 1;
        }
        getMsgService().revokeMessage(selectMessage).setCallback(new RequestCallbackWrapper<Void>() {
            @Override
            public void onResult(int code, Void aVoid, Throwable throwable) {
                if (code == ResponseCode.RES_SUCCESS) {
                    deleteItem(selectMessage, false);
                    revokMessage(selectMessage);
                    MessageHelper.getInstance().onRevokeMessage(selectMessage);
                }
                if (onSendMessageListener != null) {
                    onSendMessageListener.onResult(code, selectMessage);
                }
            }
        });
        return 2;
    }

    public void queryMessage(String selectMessageId, final OnMessageQueryListener messageQueryListener) {
        if (messageQueryListener == null) {
            return;
        }
        if (TextUtils.isEmpty(selectMessageId)) {
            messageQueryListener.onResult(-1, null);
            return;
        }
        List<String> uuids = new ArrayList<>();
        uuids.add(selectMessageId);
        getMsgService().queryMessageListByUuid(uuids).setCallback(new RequestCallbackWrapper<List<IMMessage>>() {
            @Override
            public void onResult(int code, List<IMMessage> messageList, Throwable throwable) {

                if (messageList == null || messageList.isEmpty()) {
                    messageQueryListener.onResult(code, null);
                    return;
                }
                LogUtil.i(TAG, messageList.get(0).getUuid() + "::" + messageList.get(0).getContent());
                messageQueryListener.onResult(code, messageList.get(0));
            }
        });
        return;
    }

    MsgService msgService;

    public MsgService getMsgService() {
        if (msgService == null) {
            synchronized (SessionService.class) {
                if (msgService == null) {
                    msgService = NIMClient.getService(MsgService.class);
                }
            }
        }
        return msgService;
    }

    public void sendMessage(final IMMessage message, final OnSendMessageListener onSendMessageListener) {


        appendPushConfig(message);

        getMsgService().sendMessage(message, false);
        onMessageStatusChange(message, true);

    }

    private void appendPushConfig(IMMessage message) {
//        CustomPushContentProvider customConfig = NimUIKit.getCustomPushContentProvider();
//        if (customConfig != null) {
//            String content = customConfig.getPushContent(message);
//            Map<String, Object> payload = customConfig.getPushPayload(message);
//            message.setPushContent(content);
//            message.setPushPayload(payload);
//        }
    }

    private MemberPushOption createMemPushOption(List<String> selectedMembers, IMMessage message) {

        if (selectedMembers.isEmpty()) {
            return null;
        }

        MemberPushOption memberPushOption = new MemberPushOption();
        memberPushOption.setForcePush(true);
        memberPushOption.setForcePushContent(message.getContent());
        memberPushOption.setForcePushList(selectedMembers);
        return memberPushOption;
    }

    private boolean isOriginImageHasDownloaded(final IMMessage message) {

        if (message.getAttachStatus() == AttachStatusEnum.transferred) {
            FileAttachment attachment = null;
            try {
                attachment = (FileAttachment) message.getAttachment();
//                AudioAttachment audioAttachment;
//                VideoAttachment videoAttachment;
//                ImageAttachment imageAttachment;
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (attachment != null && !TextUtils.isEmpty(attachment.getPath())) {
                LogUtil.i(TAG, "attachmentPath:" + attachment.getPath());
                return true;
            }
        }
        return false;
    }

    void observerAttachProgress(boolean register) {
        NIMClient.getService(MsgServiceObserve.class).observeAttachmentProgress(new Observer<AttachmentProgress>() {
            @Override
            public void onEvent(AttachmentProgress attachmentProgress) {
                ReactCache.emit(ReactCache.observeAttachmentProgress, ReactCache.createAttachmentProgress(attachmentProgress));
            }
        }, register);
    }
    // 下载附件，参数1位消息对象，参数2为是下载缩略图还是下载原图。
// 因为下载的文件可能会很大，这个接口返回类型为 AbortableFuture ，允许用户中途取消下载。

    public void downloadAttachment(IMMessage message, boolean isThumb) {
        if (isOriginImageHasDownloaded(message)) {
            return;
        }
        AbortableFuture future = NIMClient.getService(MsgService.class).downloadAttachment(message, isThumb);
    }

    public interface OnSendMessageListener {
        int onResult(int code, IMMessage message);
    }

    public interface OnMessageQueryListListener {
        public int onResult(int code, List<IMMessage> messageList, Set<String> timedItems);
    }

    public interface OnMessageQueryListener {
        public int onResult(int code, IMMessage message);
    }
}
