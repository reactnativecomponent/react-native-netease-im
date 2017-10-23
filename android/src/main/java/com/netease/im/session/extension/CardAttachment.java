package com.netease.im.session.extension;

import com.alibaba.fastjson.JSONObject;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableMap;
import com.netease.im.MessageConstant;

/**
 * Created by dowin on 2017/10/23.
 */

public class CardAttachment extends CustomAttachment {

    private String type;
    private String name;
    private String imgPath;
    private String sessionId;

    public CardAttachment() {
        super(CustomAttachmentType.Card);
    }

    @Override
    protected void parseData(JSONObject data) {
        type = data.getString(MessageConstant.Card.type);
        name = data.getString(MessageConstant.Card.name);
        imgPath = data.getString(MessageConstant.Card.imgPath);
        sessionId = data.getString(MessageConstant.Card.sessionId);
    }

    public void setParams(String type, String name, String imgPath, String sessionId) {
        this.type = type;
        this.name = name;
        this.imgPath = imgPath;
        this.sessionId = sessionId;
    }

    @Override
    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String getImgPath() {
        return imgPath;
    }

    @Override
    protected JSONObject packData() {
        JSONObject object = new JSONObject();
        object.put(MessageConstant.Card.type, type);
        object.put(MessageConstant.Card.name, name);
        object.put(MessageConstant.Card.imgPath, imgPath);
        object.put(MessageConstant.Card.sessionId, sessionId);
        return object;
    }

    @Override
    public WritableMap toReactNative() {
        WritableMap writableMap = Arguments.createMap();
        writableMap.putString(MessageConstant.Card.type, type);
        writableMap.putString(MessageConstant.Card.name, name);
        writableMap.putString(MessageConstant.Card.imgPath, imgPath);
        writableMap.putString(MessageConstant.Card.sessionId, sessionId);
        return writableMap;
    }
}
