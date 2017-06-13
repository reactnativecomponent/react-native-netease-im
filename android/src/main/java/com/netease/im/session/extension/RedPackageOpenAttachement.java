package com.netease.im.session.extension;

import com.alibaba.fastjson.JSONObject;

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
    public String getOpenId() {
        return openId;
    }

    public String getSendId() {
        return sendId;
    }
}