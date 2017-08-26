package com.netease.im.group;

import android.text.TextUtils;

/**
 * Created by dowin on 2017/8/16.
 */

public class DefaultItem<T> extends AbstractItem implements Comparable<DefaultItem> ,IData{

    private final int dataItemType;
    private final String displayName;
    private T data;
    public DefaultItem(String displayName) {
        this.displayName = displayName;
        this.dataItemType = 1;
    }
    public DefaultItem(String displayName, int type) {
        this.displayName = displayName;
        this.dataItemType = type;
    }

    public void setData(T data) {
        this.data = data;
    }

    public T getData() {
        return data;
    }

    @Override
    public int getItemType() {
        return dataItemType;
    }

    @Override
    public int compareTo(DefaultItem item) {
        // TYPE
        int compare = compareType(item);
        if (compare != 0) {
            return compare;
        } else {
            return TextComparator.compareIgnoreCase(getCompare(), item.getCompare());
        }
    }

    @Override
    public String belongsGroup() {
        if (displayName == null) {
            return GroupStrategy.GROUP_NULL;
        }

        String group = TextComparator.getLeadingUp(getCompare());
        return !TextUtils.isEmpty(group) ? group : GroupStrategy.GROUP_SHARP;
    }

    private String getCompare() {
        return displayName;
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }
}
