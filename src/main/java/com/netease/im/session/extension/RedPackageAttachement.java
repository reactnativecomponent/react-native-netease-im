package com.netease.im.session.extension;

import com.alibaba.fastjson.JSONObject;

/**
 * Created by dowin on 2017/5/2.
 */

public class RedPackageAttachement extends CustomAttachment {

    final static String KEY_TYPE = "type";
    final static String KEY_WISH_TEXT = "wishText";
    final static String KEY_TYPE_TEXT = "typeText";
    final static String KEY_ID = "id";
    private String redPackageType;
    private String typeText;
    private String wishText;
    private String redPackageId;

    public RedPackageAttachement() {
        super(CustomAttachmentType.RedPackage);

    }

    public void setParams(String redPackageType, String typeText, String wishText, String redPackageId) {
        this.redPackageType = redPackageType;
        this.typeText = typeText;
        this.wishText = wishText;
        this.redPackageId = redPackageId;
    }

    @Override
    protected void parseData(JSONObject data) {
        redPackageType = data.getString(KEY_TYPE);
        typeText = data.getString(KEY_TYPE_TEXT);
        wishText = data.getString(KEY_WISH_TEXT);
        redPackageId = data.getString(KEY_ID);
    }

    @Override
    protected JSONObject packData() {
        JSONObject object = new JSONObject();
        object.put(KEY_TYPE, redPackageType);
        object.put(KEY_TYPE_TEXT, typeText);
        object.put(KEY_WISH_TEXT, wishText);
        object.put(KEY_ID, redPackageId);
        return object;
    }

    public String getRedPackageId() {
        return redPackageId;
    }

    public String getRedPackageType() {
        return redPackageType;
    }

    public String getTypeText() {
        return typeText;
    }

    public String getWishText() {
        return wishText;
    }
}
