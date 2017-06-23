package com.netease.im.session.extension;

import com.alibaba.fastjson.JSONObject;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableMap;

/**
 * Created by zhoujianghua on 2015/4/10.
 */
public class DefaultCustomAttachment extends CustomAttachment {

    final static String KEY_DIGST = "digst";
    private String content;
    private String digst;

    public DefaultCustomAttachment(String type) {
        super(type);
    }

    @Override
    protected void parseData(JSONObject data) {
        digst = data.getString(KEY_DIGST);
        content = data.toJSONString();
    }

    @Override
    protected JSONObject packData() {
        JSONObject data = null;
        try {
            data = JSONObject.parseObject(content);
            if(data == null){
                data = new JSONObject();
            }
            data.put(KEY_DIGST, digst);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setDigst(String digst) {
        this.digst = digst;
    }

    public String getDigst() {
        return digst;
    }

    @Override
    public WritableMap toReactNative() {
        WritableMap writableMap = Arguments.createMap();
        writableMap.putString("digst", digst);
        writableMap.putString("content", content);
        return writableMap;
    }
}
