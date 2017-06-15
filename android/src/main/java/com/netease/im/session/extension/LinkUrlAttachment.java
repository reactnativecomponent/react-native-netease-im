package com.netease.im.session.extension;

import com.alibaba.fastjson.JSONObject;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableMap;

/**
 * Created by dowin on 2017/6/14.
 */

public class LinkUrlAttachment extends CustomAttachment{


    final String KEY_TITLE = "title";
    final String KEY_DESCRIBE = "describe";
    final String KEY_IMAGE = "image";
    final String KEY_LINK_URL = "linkUrl";
    private String title;
    private String describe;
    private String image;
    private String linkUrl;
    public LinkUrlAttachment() {
        super(CustomAttachmentType.LinkUrl);
    }

    @Override
    protected void parseData(JSONObject data) {
        title = data.getString(KEY_TITLE);
        describe = data.getString(KEY_DESCRIBE);
        image = data.getString(KEY_IMAGE);
        linkUrl = data.getString(KEY_LINK_URL);
    }

    @Override
    protected JSONObject packData() {
        JSONObject object = new JSONObject();
        object.put(KEY_TITLE,title);
        object.put(KEY_DESCRIBE,describe);
        object.put(KEY_IMAGE,image);
        object.put(KEY_LINK_URL,linkUrl);
        return object;
    }

    @Override
    protected WritableMap toReactNative() {
        WritableMap writableMap = Arguments.createMap();
        writableMap.putString("title",title);
        writableMap.putString("describe",describe);
        writableMap.putString("image",image);
        writableMap.putString("linkUrl",linkUrl);
        return writableMap;
    }

}
