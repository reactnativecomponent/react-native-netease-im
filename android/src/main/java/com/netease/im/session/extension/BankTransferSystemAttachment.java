package com.netease.im.session.extension;

import com.alibaba.fastjson.JSONObject;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableMap;

/**
 * Created by dowin on 2017/6/8.
 */

public class BankTransferSystemAttachment extends CustomAttachment {

    final static String KEY_FROM = "from";
    final static String KEY_OPE = "ope";
    final static String KEY_TO = "to";
    final static String KEY_TYPE = "type";
    final static String KEY_BODY = "body";


    final static String KEY_BODY_MSG_TYPE = "msgtype";
    final static String KEY_BODY_AMOUNT = "amount";
    final static String KEY_BODY_COMMENTS = "comments";
    final static String KEY_BODY_SERIAL_NO = "serialNo";

    private String from;
    private String ope;
    private String to;
    private String attachType;

    private String msgtype;
    private String amount;
    private String comments;
    private String serialNo;

    public BankTransferSystemAttachment() {
        super(CustomAttachmentType.BankTransferSystem);
    }

    @Override
    protected void parseData(JSONObject data) {
        from = data.getString(KEY_FROM);
        ope = data.getString(KEY_OPE);
        to = data.getString(KEY_TO);
        attachType = data.getString(KEY_TYPE);

        JSONObject body = data.getJSONObject(KEY_BODY);
        msgtype = body.getString(KEY_BODY_MSG_TYPE);
        amount = body.getString(KEY_BODY_AMOUNT);
        comments = body.getString(KEY_BODY_COMMENTS);
        serialNo = body.getString(KEY_BODY_SERIAL_NO);
    }

    @Override
    protected JSONObject packData() {
        JSONObject object = new JSONObject();
        object.put(KEY_FROM,from);
        object.put(KEY_OPE,ope);
        object.put(KEY_TO,to);
        object.put(KEY_TYPE,type);

        JSONObject body = new JSONObject();
        body.put(KEY_BODY_MSG_TYPE,msgtype);
        body.put(KEY_BODY_AMOUNT,amount);
        body.put(KEY_BODY_COMMENTS,comments);
        body.put(KEY_BODY_SERIAL_NO,serialNo);
        object.put(KEY_BODY,body);

        return object;
    }

    public void setParams(String from, String ope, String to, String type, String msgtype, String amount, String comments, String serialNo) {
        this.from = from;
        this.ope = ope;
        this.to = to;
        this.attachType = type;
        this.msgtype = msgtype;
        this.amount = amount;
        this.comments = comments;
        this.serialNo = serialNo;
    }
    @Override
    public WritableMap toReactNative(){
        WritableMap writableMap = Arguments.createMap();
        writableMap.putString("from",from);
        writableMap.putString("ope",ope);
        writableMap.putString("to",to);
        writableMap.putString("type",attachType);

        writableMap.putString("msgtype",msgtype);
        writableMap.putString("amount",amount);
        writableMap.putString("comments",comments);
        writableMap.putString("serialNo",serialNo);
        return writableMap;
    }
}
