package com.netease.im.session.extension;

import com.alibaba.fastjson.JSONObject;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableMap;
import com.netease.im.MessageConstant;

/**
 * Created by dowin on 2017/5/2.
 */

public class BankTransferAttachment extends CustomAttachment {

    final static String KEY_AMOUNT= "amount";
    final static String KEY_COMMENTS= "comments";
    final static String KEY_SERIAL_NO = "serialNo";

    private String amount;
    private String comments;
    private String serialNo;

    public BankTransferAttachment() {
        super(CustomAttachmentType.BankTransfer);
    }

    @Override
    protected void parseData(JSONObject data) {
        amount = data.getString(KEY_AMOUNT);
        comments = data.getString(KEY_COMMENTS);
        serialNo = data.getString(KEY_SERIAL_NO);
    }

    @Override
    protected JSONObject packData() {
        JSONObject object = new JSONObject();
        object.put(KEY_AMOUNT, amount);
        object.put(KEY_COMMENTS, comments);
        object.put(KEY_SERIAL_NO, serialNo);
        return object;
    }

    public void setParams(String amount, String comments, String serialNo) {
        this.amount = amount;
        this.comments = comments;
        this.serialNo = serialNo;
    }

    public String getComments() {
        return comments;
    }

    @Override
    public WritableMap toReactNative() {
        WritableMap writableMap = Arguments.createMap();
        writableMap.putString(MessageConstant.BankTransfer.AMOUNT,amount);
        writableMap.putString(MessageConstant.BankTransfer.COMMENTS,comments);
        writableMap.putString(MessageConstant.BankTransfer.SERIA_NO,serialNo);
        return writableMap;
    }
}
