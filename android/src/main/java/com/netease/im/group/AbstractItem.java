package com.netease.im.group;

/**
 * Created by dowin on 2017/8/16.
 */

public abstract class AbstractItem {
    /**
     * 所属的类型
     *
     */
    public abstract int getItemType();

    /**
     * 所属的分组
     */
    public abstract String belongsGroup();

    protected final int compareType(AbstractItem item) {
        return compareType(getItemType(), item.getItemType());
    }

    public static int compareType(int lhs, int rhs) {
        return lhs - rhs;
    }
}
