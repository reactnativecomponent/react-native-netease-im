package com.netease.im.common.push;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import com.netease.im.IMApplication;
import com.netease.im.uikit.common.util.log.LogUtil;
import com.netease.nimlib.sdk.NimIntent;
import com.netease.nimlib.sdk.StatusBarNotificationConfig;
import com.netease.nimlib.sdk.mixpush.MixPushMessageHandler;
import com.netease.nimlib.sdk.msg.MessageBuilder;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by hzchenkang on 2016/11/10.
 */

public class NimMixPushMessageHandler implements MixPushMessageHandler {

    @Override
    public boolean onNotificationClicked(Context context, Map<String, String> payload) {

        LogUtil.w(NimMixPushMessageHandler.class.getSimpleName(), "rev miPushMessage payload " + payload);

        String sessionId = payload.get("sessionID");
        String type = payload.get("sessionType");
        //
        if (sessionId != null && type != null) {
            int typeValue = Integer.valueOf(type);
            ArrayList<IMMessage> imMessages = new ArrayList<>();
            IMMessage imMessage = MessageBuilder.createEmptyMessage(sessionId, SessionTypeEnum.typeOfValue(typeValue), 0);
            imMessages.add(imMessage);
            Intent notifyIntent = new Intent();
            notifyIntent.setComponent(initLaunchComponent(context));
            notifyIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            notifyIntent.setAction(Intent.ACTION_VIEW);
            notifyIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // 必须
            notifyIntent.putExtra(NimIntent.EXTRA_NOTIFY_CONTENT, imMessages);

            context.startActivity(notifyIntent);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean cleanMixPushNotifications(int pushType) {
        return true;
    }

    private ComponentName initLaunchComponent(Context context) {
        ComponentName launchComponent;
        StatusBarNotificationConfig config = IMApplication.getNotificationConfig();
        Class<? extends Activity> entrance = config.notificationEntrance;
        if (entrance == null) {
            launchComponent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName()).getComponent();
        } else {
            launchComponent = new ComponentName(context, entrance);
        }
        return launchComponent;
    }
}
