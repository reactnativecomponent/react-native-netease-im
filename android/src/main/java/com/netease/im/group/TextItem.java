package com.netease.im.group;

import android.text.TextUtils;

/**
 * Created by dowin on 2017/8/16.
 */

public class TextItem extends AbstractItem implements Comparable<TextItem> {
    private final String text;

    public TextItem(String text) {
        this.text = text != null ? text : "";
    }

    public final String getText() {
        return text;
    }

    @Override
    public int getItemType() {
        return 0;
    }

    @Override
    public String belongsGroup() {
        String group = TextComparator.getLeadingUp(text);

        return !TextUtils.isEmpty(group) ? group : GroupStrategy.GROUP_SHARP;
    }

    @Override
    public int compareTo(TextItem item) {
        return TextComparator.compareIgnoreCase(text, item.text);
    }
}
