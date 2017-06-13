package com.netease.im.session.extension;

import com.alibaba.fastjson.JSONObject;

/**
 * Created by dowin on 2017/6/8.
 */

public class BankTransferSystemAttachment extends CustomAttachment {

    final static String KEY_VALUE = "value";
    final static String KEY_EXPLAIN = "explain";
    final static String KEY_TYPE_TEXT = "typeText";
    final static String KEY_ID = "id";

    final static String KEY_TIME = "time";
    final static String KEY_RECEIVER = "receiver";
    final static String KEY_PAY_TYPE = "payType";
    final static String KEY_PAY_STATUS = "payStatus";

    private String id;
    private String explain;
    private String typeText;
    private String value;

    private String time;
    private String receiver;
    private String payType;
    private String payStatus;

    public BankTransferSystemAttachment() {
        super(CustomAttachmentType.BankTransferSystem);
    }

    @Override
    protected void parseData(JSONObject data) {
        id = data.getString(KEY_ID);
        explain = data.getString(KEY_EXPLAIN);
        typeText = data.getString(KEY_TYPE_TEXT);
        value = data.getString(KEY_VALUE);
        time = data.getString(KEY_TIME);
        receiver = data.getString(KEY_RECEIVER);
        payType = data.getString(KEY_PAY_TYPE);
        payStatus = data.getString(KEY_PAY_STATUS);
    }

    @Override
    protected JSONObject packData() {
        JSONObject object = new JSONObject();
        object.put(KEY_ID, id);
        object.put(KEY_EXPLAIN, explain);
        object.put(KEY_TYPE_TEXT, typeText);
        object.put(KEY_VALUE, value);
        object.put(KEY_TIME, time);
        object.put(KEY_RECEIVER, receiver);
        object.put(KEY_PAY_TYPE, payType);
        object.put(KEY_PAY_STATUS, payStatus);
        return object;
    }

    public void setParams(String id, String explain, String typeText, String value, String time, String receiver, String payType, String payStatus) {
        this.id = id;
        this.explain = explain;
        this.typeText = typeText;
        this.value = value;
        this.time = time;
        this.receiver = receiver;
        this.payType = payType;
        this.payStatus = payStatus;
    }

    public String getId() {
        return id;
    }

    public String getExplain() {
        return explain;
    }

    public String getValue() {
        return value;
    }

    public String getTime() {
        return time;
    }

    public String getReceiver() {
        return receiver;
    }

    public String getPayType() {
        return payType;
    }

    public String getPayStatus() {
        return payStatus;
    }

    public String getTypeText() {
        return typeText;
    }
}
