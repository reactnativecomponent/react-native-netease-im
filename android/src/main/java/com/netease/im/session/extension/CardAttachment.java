package com.netease.im.session.extension;

import com.alibaba.fastjson.JSONObject;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableMap;
import com.netease.im.MessageConstant;

/**
 * Created by dowin on 2017/10/23.
 */

public class CardAttachment extends CustomAttachment {

    private String cardType;
    private String name;
    private String imgPath;
    private String sessionId;

    public CardAttachment() {
        super(CustomAttachmentType.Card);
    }

    @Override
    protected void parseData(JSONObject data) {
        cardType = data.getString(MessageConstant.Card.type);
        name = data.getString(MessageConstant.Card.name);
        imgPath = data.getString(MessageConstant.Card.imgPath);
        sessionId = data.getString(MessageConstant.Card.sessionId);
    }

    public void setParams(String type, String name, String imgPath, String sessionId) {
        this.cardType = type;
        this.name = name;
        this.imgPath = imgPath;
        this.sessionId = sessionId;
    }

    public String getCardType() {
        return cardType;
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
        object.put(MessageConstant.Card.type, cardType);
        object.put(MessageConstant.Card.name, name);
        object.put(MessageConstant.Card.imgPath, imgPath);
        object.put(MessageConstant.Card.sessionId, sessionId);
        return object;
    }

    @Override
    public WritableMap toReactNative() {
        WritableMap writableMap = Arguments.createMap();
        writableMap.putString(MessageConstant.Card.type, cardType);
        writableMap.putString(MessageConstant.Card.name, name);
        writableMap.putString(MessageConstant.Card.imgPath, imgPath);
        writableMap.putString(MessageConstant.Card.sessionId, sessionId);
        return writableMap;
    }
}
