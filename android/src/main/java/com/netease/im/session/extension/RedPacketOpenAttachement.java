package com.netease.im.session.extension;

import android.text.TextUtils;

import com.alibaba.fastjson.JSONObject;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableMap;
import com.netease.im.uikit.cache.NimUserInfoCache;

/**
 * Created by dowin on 2017/6/12.
 */

public class RedPacketOpenAttachement extends CustomAttachment {

    private final static String KEY_SEND_ID = "sendId";
    private final static String KEY_OPEN_ID = "openId";
    private final static String KEY_HAS_REDPACKET = "hasRedPacket";

    private String sendId;
    private String openId;
    private String hasRedPacket;

    public RedPacketOpenAttachement() {
        super(CustomAttachmentType.RedPacketOpen);
    }

    @Override
    protected void parseData(JSONObject data) {
        sendId = data.getString(KEY_SEND_ID);
        openId = data.getString(KEY_OPEN_ID);
        hasRedPacket = data.getString(KEY_HAS_REDPACKET);
    }

    @Override
    protected JSONObject packData() {
        JSONObject data = new JSONObject();
        data.put(KEY_SEND_ID, sendId);
        data.put(KEY_OPEN_ID, openId);
        data.put(KEY_HAS_REDPACKET, hasRedPacket);
        return data;
    }

    public void setParams(String sendId, String openId, String hasRedPacket) {
        this.sendId = sendId;
        this.openId = openId;
        this.hasRedPacket = hasRedPacket;
    }

    @Override
    public WritableMap toReactNative() {
        WritableMap writableMap = Arguments.createMap();

        String sendName = NimUserInfoCache.getInstance().getUserDisplayNameYou(sendId);
        String openName;
        if (TextUtils.equals(sendId, openId)) {
            openName = "自己";
        } else {
            openName = NimUserInfoCache.getInstance().getUserDisplayNameYou(openId);
        }
        writableMap.putString("sendId", sendId);
        writableMap.putString("sendName", sendName);
        writableMap.putString("openId", openId);
        writableMap.putString("openName", openName);
        writableMap.putString("hasRedPacket", hasRedPacket);
        writableMap.putString("tipMsg", sendName + "打开了" + openName + "的红包");
        return writableMap;
    }
}