package com.netease.im.group;

/**
 * Created by dowin on 2017/8/16.
 */

public class LabeItem extends AbstractItem{
    private final String text;

    public LabeItem(String text) {
        this.text = text;
    }

    @Override
    public int getItemType() {
        return -1;
    }

    @Override
    public String belongsGroup() {
        return null;
    }

    public final String getText() {
        return text;
    }
}
