package com.netease.im.session;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.netease.im.IMApplication;
import com.netease.im.session.extension.RedPacketOpenAttachement;
import com.netease.im.uikit.common.util.log.LogUtil;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.msg.MessageBuilder;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.constant.MsgStatusEnum;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.CustomMessageConfig;
import com.netease.nimlib.sdk.msg.model.CustomNotification;
import com.netease.nimlib.sdk.msg.model.CustomNotificationConfig;
import com.netease.nimlib.sdk.msg.model.IMMessage;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by dowin on 2017/5/2.
 */

public class SessionUtil {

    public final static String CUSTOM_Notification = "1";
    public final static String CUSTOM_Notification_redpacket_open = "2";

    public static SessionTypeEnum getSessionType(String sessionType) {
        SessionTypeEnum sessionTypeE = SessionTypeEnum.None;
        try {
            sessionTypeE = SessionTypeEnum.typeOfValue(Integer.parseInt(sessionType));
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return sessionTypeE;
    }

    private static void appendPushConfig(IMMessage message) {
//        CustomPushContentProvider customConfig = NimUIKit.getCustomPushContentProvider();
//        if (customConfig != null) {
//            String content = customConfig.getPushContent(message);
//            Map<String, Object> payload = customConfig.getPushPayload(message);
//            message.setPushContent(content);
//            message.setPushPayload(payload);
//        }
    }

    /**
     * 设置最近联系人的消息为已读
     *
     * @param enable
     */
    private void enableMsgNotification(boolean enable) {
        if (enable) {
            /**
             * 设置最近联系人的消息为已读
             *
             * @param account,    聊天对象帐号，或者以下两个值：
             *                    {@link #MSG_CHATTING_ACCOUNT_ALL} 目前没有与任何人对话，但能看到消息提醒（比如在消息列表界面），不需要在状态栏做消息通知
             *                    {@link #MSG_CHATTING_ACCOUNT_NONE} 目前没有与任何人对话，需要状态栏消息通知
             */
            NIMClient.getService(MsgService.class).setChattingAccount(MsgService.MSG_CHATTING_ACCOUNT_NONE, SessionTypeEnum.None);
        } else {
            NIMClient.getService(MsgService.class).setChattingAccount(MsgService.MSG_CHATTING_ACCOUNT_ALL, SessionTypeEnum.None);
        }
    }

    public static void sendMessage(IMMessage message) {

        appendPushConfig(message);
        NIMClient.getService(MsgService.class).sendMessage(message, false);
    }


    /**
     * 添加好友通知
     *
     * @param account
     * @param content
     */
    public static void sendAddFriendNotification(String account, String content) {
        sendCustomNotification(account, SessionTypeEnum.P2P, CUSTOM_Notification, content);
    }

    public static void receiver(NotificationManager manager, CustomNotification customNotification) {
        LogUtil.i("SessionUtil", customNotification.getFromAccount());
        Map<String, Object> map = customNotification.getPushPayload();
        if (map != null && map.containsKey("type")) {
            String type = (String) map.get("type");
            if (SessionUtil.CUSTOM_Notification.equals(type)) {
                NotificationCompat.Builder builder = new NotificationCompat.Builder(IMApplication.getContext());
                builder.setContentTitle("请求加为好友");
                builder.setContentText(customNotification.getApnsText());
                builder.setAutoCancel(true);
                PendingIntent contentIntent = PendingIntent.getActivity(
                        IMApplication.getContext(), 0, new Intent(IMApplication.getContext(), IMApplication.getMainActivityClass()), 0);
                builder.setContentIntent(contentIntent);
                builder.setSmallIcon(IMApplication.getNotify_msg_drawable_id());
                manager.notify((int) System.currentTimeMillis(), builder.build());
            } else {
                String content = customNotification.getContent();
                if (!TextUtils.isEmpty(content)) {
                    JSONObject object = JSON.parseObject(content);
                    JSONObject data = object.getJSONObject("data");

                    JSONObject dict = data.getJSONObject("dict");
                    String sendId = dict.getString("sendId");
                    String openId = dict.getString("openId");
                    String hasRedPacket = dict.getString("hasRedPacket");
                    String serialNo = dict.getString("serialNo");

                    String timestamp = data.getString("timestamp");
                    long t = 0L;
                    try {
                        t = Long.parseLong(timestamp);
                    } catch (NumberFormatException e) {
                        t = System.currentTimeMillis() / 1000;
                        e.printStackTrace();
                    }
//                LogUtil.i("timestamp","timestamp:"+timestamp);
//                LogUtil.i("timestamp","t:"+t);
//                LogUtil.i("timestamp",""+data);
                    String sessionId = (String) data.get("sessionId");
                    String sessionType = (String) data.get("sessionType");
                    final String id = getSessionType(sessionType) == SessionTypeEnum.P2P ? openId : sessionId;
                    sendRedPacketOpenLocal(id, getSessionType(sessionType), sendId, openId, hasRedPacket, serialNo, t);
                }
            }
        }

    }

    /**
     * @param account
     * @param sessionType
     * @param type
     * @param content
     */
    public static void sendCustomNotification(String account, SessionTypeEnum sessionType, String type, String content) {
        CustomNotification notification = new CustomNotification();
        notification.setSessionId(account);
        notification.setSessionType(sessionType);

        notification.setContent(content);
        notification.setSendToOnlineUserOnly(false);
        notification.setApnsText(content);

        Map<String, Object> pushPayload = new HashMap<>();
        pushPayload.put("type", type);
        pushPayload.put("content", content);
        notification.setPushPayload(pushPayload);

        NIMClient.getService(MsgService.class).sendCustomNotification(notification);
    }

    public static void sendRedPacketOpenLocal(String sessionId, SessionTypeEnum sessionType,
                                              String sendId, String openId, String hasRedPacket, String serialNo, long timestamp) {

        CustomMessageConfig config = new CustomMessageConfig();
        config.enableUnreadCount = false;
        config.enablePush = false;
        RedPacketOpenAttachement attachment = new RedPacketOpenAttachement();
        attachment.setParams(sendId, openId, hasRedPacket, serialNo);
        IMMessage message = MessageBuilder.createCustomMessage(sessionId, sessionType, attachment.getTipMsg(true), attachment, config);
        message.setStatus(MsgStatusEnum.success);

        message.setConfig(config);
        NIMClient.getService(MsgService.class).saveMessageToLocalEx(message, true, timestamp * 1000);
    }

    public static void sendRedPacketOpenNotification(String sessionId, SessionTypeEnum sessionType,
                                                     String sendId, String openId, String hasRedPacket, String serialNo, long timestamp) {

        if (TextUtils.equals(sendId, openId)) {
            return;
        }
        Map<String, Object> data = new HashMap<>();
        Map<String, String> dict = new HashMap<>();
        dict.put("sendId", sendId);
        dict.put("openId", openId);
        dict.put("hasRedPacket", hasRedPacket);
        dict.put("serialNo", serialNo);

        data.put("dict", dict);
        data.put("timestamp", Long.toString(timestamp));
        data.put("sessionId", sessionId);
        data.put("sessionType", Integer.toString(sessionType.getValue()));

        CustomNotification notification = new CustomNotification();
        notification.setSessionId(sendId);
        notification.setSessionType(SessionTypeEnum.P2P);
        CustomNotificationConfig config = new CustomNotificationConfig();
        config.enablePush = false;
        config.enableUnreadCount = false;
        notification.setConfig(config);

        notification.setSendToOnlineUserOnly(false);

        Map<String, Object> pushPayload = new HashMap<>();
        pushPayload.put("type", CUSTOM_Notification_redpacket_open);
        pushPayload.put("data", data);
        notification.setPushPayload(pushPayload);

        NIMClient.getService(MsgService.class).sendCustomNotification(notification);
    }
}
