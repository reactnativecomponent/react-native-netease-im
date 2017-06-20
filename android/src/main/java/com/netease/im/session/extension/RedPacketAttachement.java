package com.netease.im.session.extension;

import com.alibaba.fastjson.JSONObject;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableMap;

/**
 * Created by dowin on 2017/5/2.
 */

public class RedPacketAttachement extends CustomAttachment {

    final static String KEY_TYPE = "type";
    final static String KEY_COMMENTS= "comments";
    final static String KEY_SERIAL_NO = "serialNo";

    private String redPacketType;
    private String comments;
    private String serialNo;

    public RedPacketAttachement() {
        super(CustomAttachmentType.RedPacket);

    }

    public void setParams(String redPacketType, String comments, String serialNo) {
        this.redPacketType = redPacketType;
        this.comments = comments;
        this.serialNo = serialNo;
    }

    @Override
    protected void parseData(JSONObject data) {
        redPacketType = data.getString(KEY_TYPE);
        comments = data.getString(KEY_COMMENTS);
        serialNo = data.getString(KEY_SERIAL_NO);
    }

    public String getComments() {
        return comments;
    }

    @Override
    protected JSONObject packData() {
        JSONObject object = new JSONObject();
        object.put(KEY_TYPE, redPacketType);
        object.put(KEY_COMMENTS, comments);
        object.put(KEY_SERIAL_NO, serialNo);
        return object;
    }
    @Override
    public WritableMap toReactNative(){
        WritableMap writableMap = Arguments.createMap();
        writableMap.putString("type",redPacketType);
        writableMap.putString("comments",comments);
        writableMap.putString("serialNo",serialNo);
        return writableMap;
    }

}
