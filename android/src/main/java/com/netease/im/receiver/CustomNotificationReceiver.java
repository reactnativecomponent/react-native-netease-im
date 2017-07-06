package com.netease.im.receiver;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.netease.im.IMApplication;
import com.netease.im.uikit.common.util.log.LogUtil;
import com.netease.nimlib.sdk.NimIntent;

/**
 * 自定义通知消息广播接收器
 */
public class CustomNotificationReceiver extends BroadcastReceiver {

    private NotificationManager notificationManager;

    @Override
    public void onReceive(Context context, Intent intent) {

        String action = context.getPackageName() + NimIntent.ACTION_RECEIVE_CUSTOM_NOTIFICATION;
//        printIntent(action, intent);
        if (action.equals(intent.getAction())) {

            // 从intent中取出自定义通知
//            try {
//                CustomNotification notification = (CustomNotification) intent.getSerializableExtra(NimIntent.EXTRA_BROADCAST_MSG);
//                SessionUtil.receiver(getNotificationManager(), notification);
//            } catch (Exception e) {
//                LogUtil.e("CustomNotificationReceiver", e.getMessage());
//            }
        }
    }

    public NotificationManager getNotificationManager() {
        if (notificationManager == null) {
            notificationManager = (NotificationManager) IMApplication.getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return notificationManager;
    }

    void printIntent(String action, Intent intent) {
        LogUtil.d("NimNetease", "--------------------------------------------");
        LogUtil.d("NimNetease", "action" + action);
        LogUtil.d("NimNetease", intent.getAction());
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            LogUtil.d("NimNetease", "+" + intent);
        } else {
            LogUtil.d("NimNetease", "+null");
        }
        LogUtil.d("NimNetease", "--------------------------------------------");
    }
}
