package com.netease.im.session.extension;

import android.text.TextUtils;

import com.alibaba.fastjson.JSONObject;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableMap;
import com.netease.im.login.LoginService;
import com.netease.im.uikit.cache.NimUserInfoCache;

/**
 * Created by dowin on 2017/6/12.
 */

public class RedPacketOpenAttachement extends CustomAttachment {

    private final static String KEY_SEND_ID = "sendId";
    private final static String KEY_OPEN_ID = "openId";
    private final static String KEY_HAS_REDPACKET = "hasRedPacket";
    private final static String KEY_SERIAL_NO = "serialNo";

    private String sendId;
    private String openId;
    private String hasRedPacket;
    private String serialNo;

    public RedPacketOpenAttachement() {
        super(CustomAttachmentType.RedPacketOpen);
    }

    @Override
    protected void parseData(JSONObject data) {
        sendId = data.getString(KEY_SEND_ID);
        openId = data.getString(KEY_OPEN_ID);
        hasRedPacket = data.getString(KEY_HAS_REDPACKET);
        serialNo = data.getString(KEY_SERIAL_NO);
    }

    @Override
    protected JSONObject packData() {
        JSONObject data = new JSONObject();
        data.put(KEY_SEND_ID, sendId);
        data.put(KEY_OPEN_ID, openId);
        data.put(KEY_HAS_REDPACKET, hasRedPacket);
        data.put(KEY_SERIAL_NO, serialNo);
        return data;
    }

    public void setParams(String sendId, String openId, String hasRedPacket, String serialNo) {
        this.sendId = sendId;
        this.openId = openId;
        this.hasRedPacket = hasRedPacket;
        this.serialNo = serialNo;
    }

    public boolean isSelf() {
        String self = LoginService.getInstance().getAccount();
        return TextUtils.equals(self, sendId) || TextUtils.equals(self, openId);
    }

    public String getTipMsg() {
        String openName = NimUserInfoCache.getInstance().getUserDisplayNameYou(openId);
        String sendName;
        if (TextUtils.equals(sendId, openId)) {
            sendName = "自己发";
        } else {
            sendName = NimUserInfoCache.getInstance().getUserDisplayNameYou(sendId);
        }
        String end = "";
        if ("1".equals(hasRedPacket) && TextUtils.equals(LoginService.getInstance().getAccount(), sendId)) {
            end = "，你的红包已被领完";
        }
        return openName + "领取了" + sendName + "的红包" + end;
    }

    @Override
    public WritableMap toReactNative() {
        WritableMap writableMap = Arguments.createMap();
//        writableMap.putString("sendId", sendId);
//        writableMap.putString("sendName", sendName);
//        writableMap.putString("openId", openId);
//        writableMap.putString("openName", openName);
        writableMap.putString("hasRedPacket", hasRedPacket);
        writableMap.putString("serialNo", serialNo);
        writableMap.putString("tipMsg", getTipMsg());
        return writableMap;
    }
}