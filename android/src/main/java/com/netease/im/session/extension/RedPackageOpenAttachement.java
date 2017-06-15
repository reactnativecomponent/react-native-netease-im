package com.netease.im.session.extension;

import android.text.TextUtils;

import com.alibaba.fastjson.JSONObject;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableMap;
import com.netease.im.uikit.cache.NimUserInfoCache;

/**
 * Created by dowin on 2017/6/12.
 */

public class RedPackageOpenAttachement extends CustomAttachment {

    private final static String KEY_SEND_ID = "sendId";
    private final static String KEY_OPEN_ID = "openId";

    private String sendId;
    private String openId;

    public RedPackageOpenAttachement() {
        super(CustomAttachmentType.RedPackageOpen);
    }

    @Override
    protected void parseData(JSONObject data) {
        sendId = data.getString(KEY_SEND_ID);
        openId = data.getString(KEY_OPEN_ID);
    }

    @Override
    protected JSONObject packData() {
        JSONObject data = new JSONObject();
        data.put(KEY_SEND_ID,sendId);
        data.put(KEY_OPEN_ID,openId);
        return data;
    }
    public void setParams(String sendId,String openId){
        this.sendId = sendId;
        this.openId = openId;
    }
    @Override
    public WritableMap toReactNative(){
        WritableMap writableMap = Arguments.createMap();

        String sender = NimUserInfoCache.getInstance().getUserDisplayNameYou(sendId);
        String opener;
        if (TextUtils.equals(sendId, openId)) {
            opener = "自己";
        } else {
            opener = NimUserInfoCache.getInstance().getUserDisplayNameYou(openId);
        }
        writableMap.putString("tipMsg", sender + "打开了" + opener + "的红包");
        return writableMap;
    }
}