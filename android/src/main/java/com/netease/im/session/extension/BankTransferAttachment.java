package com.netease.im.session.extension;

import com.alibaba.fastjson.JSONObject;

/**
 * Created by dowin on 2017/5/2.
 */

public class BankTransferAttachment extends CustomAttachment {

    final static String KEY_VALUE = "value";
    final static String KEY_EXPLAIN = "explain";
    final static String KEY_TYPE_TEXT = "typeText";
    final static String KEY_ID = "id";

    private String typeText;
    private String value;
    private String explain;
    private String bankTransferId;

    public BankTransferAttachment() {
        super(CustomAttachmentType.BankTransfer);
    }

    @Override
    protected void parseData(JSONObject data) {
        value = data.getString(KEY_VALUE);
        explain = data.getString(KEY_EXPLAIN);
        typeText = data.getString(KEY_TYPE_TEXT);
        bankTransferId = data.getString(KEY_ID);
    }

    @Override
    protected JSONObject packData() {
        return null;
    }

    public void setParams(String typeText, String value, String explain, String bankTransferId) {
        this.typeText = typeText;
        this.value = value;
        this.explain = explain;
        this.bankTransferId = bankTransferId;
    }

    public String getTypeText() {
        return typeText;
    }

    public String getExplain() {
        return explain;
    }

    public String getValue() {
        return value;
    }

    public String getBankTransferId() {
        return bankTransferId;
    }
}
