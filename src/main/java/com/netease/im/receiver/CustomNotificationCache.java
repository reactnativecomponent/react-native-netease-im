package com.netease.im.receiver;

import com.netease.nimlib.sdk.msg.model.CustomNotification;

import java.util.LinkedList;
import java.util.List;

/**
 * 自定义通知缓存
 * <p/>
 * Created by huangjun on 2015/5/29.
 */
public class CustomNotificationCache {

    public static CustomNotificationCache getInstance() {
        return InstanceHolder.instance;
    }

    private List<CustomNotification> notifications = new LinkedList<>();

    public void addCustomNotification(CustomNotification notification) {
        if (notification == null) {
            return;
        }

        if (!notifications.contains(notification)) {
            notifications.add(0, notification);
        }
    }

    public List<CustomNotification> getCustomNotification() {
        return notifications;
    }

    public static class InstanceHolder {
        public final static CustomNotificationCache instance = new CustomNotificationCache();
    }
}
