package com.netease.im.session.extension;

import com.alibaba.fastjson.JSONObject;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableMap;

/**
 * Created by dowin on 2017/6/14.
 */

public class AccountNoticeAttachment extends CustomAttachment {

    final String KEY_TITLE = "title";
    final String KEY_DATE = "date";
    final String KEY_TIME = "time";
    final String KEY_AMOUNT = "amount";
    final String KEY_BODY = "body";
    final String KEY_SERIAL_NO = "serialNo";

    private String title;
    private String date;
    private String time;
    private String amount;
    private String body;
    private String serialNo;

    public AccountNoticeAttachment() {
        super(CustomAttachmentType.AccountNotice);
    }

    @Override
    protected void parseData(JSONObject data) {
        title = data.getString(KEY_TITLE);
        time = data.getString(KEY_TIME);
        date = data.getString(KEY_DATE);
        amount = data.getString(KEY_AMOUNT);

        body = data.getJSONObject(KEY_BODY).toJSONString();
        serialNo = data.getString(KEY_SERIAL_NO);
    }

    @Override
    protected JSONObject packData() {

        JSONObject object = new JSONObject();
        object.put(KEY_TITLE, title);
        object.put(KEY_TIME, time);
        object.put(KEY_DATE, date);
        object.put(KEY_AMOUNT, amount);
        object.put(KEY_SERIAL_NO, serialNo);
        try {
            object.put(KEY_BODY, JSONObject.parseObject(body));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return object;
    }

    public String getTitle() {
        return title;
    }

    @Override
    public WritableMap toReactNative() {
        WritableMap writableMap = Arguments.createMap();
        writableMap.putString("title", title);
        writableMap.putString("time", time);
        writableMap.putString("date", date);
        writableMap.putString("amount", amount);
        writableMap.putString("serialNo", serialNo);
        writableMap.putString("body", body);
        return writableMap;
    }
}

