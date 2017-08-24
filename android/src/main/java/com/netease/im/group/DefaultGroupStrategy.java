package com.netease.im.group;

/**
 * Created by dowin on 2017/8/16.
 */

public class DefaultGroupStrategy extends GroupStrategy {

    public DefaultGroupStrategy() {
        add(GroupStrategy.GROUP_NULL, -1, "");
        addABC(0);
    }

    @Override
    public String belongs(AbstractItem item) {
        return super.belongs(item);
    }
}
