package com.netease.im.session.extension;

import com.alibaba.fastjson.JSONObject;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.WritableMap;
import com.netease.im.MessageConstant;



public class ForwardMultipleTextAttachment extends CustomAttachment {

    private String messages;

    public ForwardMultipleTextAttachment() {
        super(CustomAttachmentType.ForwardMultipleText);
    }

    @Override
    protected void parseData(JSONObject data) {
        messages = data.getString(MessageConstant.ForwardMultipleText.messages);
    }

    public void setParams(ReadableArray messages) {
        this.messages = messages.toString();
    }

    @Override
    protected JSONObject packData() {
        JSONObject object = new JSONObject();

        object.put(MessageConstant.ForwardMultipleText.messages, messages);

        return object;
    }

    @Override
    public WritableMap toReactNative() {
        WritableMap writableMap = Arguments.createMap();

        return writableMap;
    }

    public String toReactNativeCustom() {
        return messages;
    }
}
