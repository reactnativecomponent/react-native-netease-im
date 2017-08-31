package com.netease.im.session.extension;

import com.alibaba.fastjson.JSONObject;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableMap;
import com.netease.im.MessageConstant;

import java.util.Map;
import java.util.Set;

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
    private Map<String, Object> body;
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

        body = data.getJSONObject(KEY_BODY);
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
        JSONObject bodyJson = new JSONObject();
        if (body != null && !body.isEmpty()) {

            Set<Map.Entry<String, Object>> entrySet = body.entrySet();
            for (Map.Entry<String, Object> entry : entrySet) {
                bodyJson.put(entry.getKey(), entry.getValue());
            }

        }
        object.put(KEY_BODY, bodyJson);
        return object;
    }

    public String getTitle() {
        return title;
    }

    @Override
    public WritableMap toReactNative() {
        WritableMap writableMap = Arguments.createMap();
        writableMap.putString(MessageConstant.AccountNotice.TITLE, title);
        writableMap.putString(MessageConstant.AccountNotice.TIME, time);
        writableMap.putString(MessageConstant.AccountNotice.DATE, date);
        writableMap.putString(MessageConstant.AccountNotice.AMOUNT, amount);
        writableMap.putString(MessageConstant.AccountNotice.SERIAL_NO, serialNo);
        WritableMap bodyMap = Arguments.createMap();
        if (body != null && !body.isEmpty()) {
            Set<Map.Entry<String, Object>> entrySet = body.entrySet();
            for (Map.Entry<String, Object> entry : entrySet) {
                bodyMap.putString(entry.getKey(), String.valueOf(entry.getValue()));
            }
        }
        writableMap.putMap(MessageConstant.AccountNotice.BODY, bodyMap);
        return writableMap;
    }
}

