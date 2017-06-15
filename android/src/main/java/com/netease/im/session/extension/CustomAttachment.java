package com.netease.im.session.extension;

import com.alibaba.fastjson.JSONObject;
import com.facebook.react.bridge.WritableMap;
import com.netease.nimlib.sdk.msg.attachment.MsgAttachment;

/**
 * Created by zhoujianghua on 2015/4/9.
 */
public abstract class CustomAttachment implements MsgAttachment {

    protected String type;

    CustomAttachment(String type) {
        this.type = type;
    }

    public void fromJson(JSONObject data) {
        if (data != null) {
            parseData(data);
        }
    }

    @Override
    public String toJson(boolean send) {
        return CustomAttachParser.packData(type, packData());
    }

    public String getType() {
        return type;
    }

    protected abstract void parseData(JSONObject data);
    protected abstract JSONObject packData();
    protected abstract WritableMap toReactNative();

//    protected abstract WritableMap packReactNative();
//    protected abstract void packReactNative(ReadableMap map);

}
