package com.netease.im.session.extension;

import com.alibaba.fastjson.JSONObject;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableMap;

/**
 * Created by zhoujianghua on 2015/4/10.
 */
public class DefaultCustomAttachment extends CustomAttachment {

    private String content;

    public DefaultCustomAttachment(String type) {
        super(type);
    }

    @Override
    protected void parseData(JSONObject data) {
        content = data.toJSONString();
    }

    @Override
    protected JSONObject packData() {
        JSONObject data = null;
        try {
            data = JSONObject.parseObject(content);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public WritableMap toReactNative(){
        WritableMap writableMap = Arguments.createMap();
        writableMap.putString("content",content);
        return writableMap;
    }
}
