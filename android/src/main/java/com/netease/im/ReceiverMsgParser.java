package com.netease.im;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableMap;
import com.netease.im.common.push.Extras;
import com.netease.im.uikit.common.util.log.LogUtil;
import com.netease.nimlib.sdk.NimIntent;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;

import java.util.ArrayList;

/**
 * <h1>接收到推送消息通知启动</h1>
 * <br/>type 启动类型：1. 会话聊天(session)
 * <br/>sessionType 聊天类型，单聊或群组
 * <br/>sessionId 聊天对象的 ID，如果是单聊，为用户帐号，如果是群聊，为群组 ID
 * <br/>Created by dowin on 2017/5/2.
 */

public class ReceiverMsgParser {

    public static boolean checkOpen(Intent intent) {
        if (intent != null && canAutoLogin()) {
            if (intent.hasExtra(NimIntent.EXTRA_NOTIFY_CONTENT) || intent.hasExtra(Extras.EXTRA_JUMP_P2P)) {
                return true;
            }
        }
        return false;
    }

    private static Intent result = new Intent();

    public static void setIntent(Intent intent) {
        result = intent;
    }

    public static Intent getIntent() {
        return result;
    }

    public static Bundle openIntent(Intent intent) {
        Bundle result = new Bundle();
        if (intent != null && canAutoLogin()) {
            if (intent.hasExtra(NimIntent.EXTRA_NOTIFY_CONTENT)) {
                ArrayList<IMMessage> messages = (ArrayList<IMMessage>) intent.getSerializableExtra(NimIntent.EXTRA_NOTIFY_CONTENT);
                if (messages == null || messages.size() > 1) {
                    result.putString("type", "sessionList");
                } else {
                    IMMessage message = messages.get(0);
                    result.putString("type", "session");
                    result.putString("sessionType", Integer.toString(message.getSessionType().getValue()));
                    result.putString("sessionId", message.getSessionId());
                }
            } else if (intent.hasExtra(Extras.EXTRA_JUMP_P2P)) {
                Intent data = intent.getParcelableExtra(Extras.EXTRA_DATA);
                String account = data.getStringExtra(Extras.EXTRA_ACCOUNT);
                if (!TextUtils.isEmpty(account)) {
                    result.putString("type", "session");
                    result.putString("sessionType", Integer.toString(SessionTypeEnum.P2P.getValue()));
                    result.putString("sessionId", account);
                }
            }

            LogUtil.w("ReceiverMsgParser", intent + "");
        }

        LogUtil.w("ReceiverMsgParser", result + "");
        return result;
    }

    public static WritableMap getWritableMap(Intent intent) {
        WritableMap r = Arguments.createMap();
        if (intent != null && canAutoLogin()) {
            if (intent.hasExtra(NimIntent.EXTRA_NOTIFY_CONTENT)) {
                ArrayList<IMMessage> messages = (ArrayList<IMMessage>) intent.getSerializableExtra(NimIntent.EXTRA_NOTIFY_CONTENT);
                if (messages == null || messages.size() > 1) {
                    r.putString("type", "sessionList");
                } else {
                    IMMessage message = messages.get(0);
                    r.putString("type", "session");
                    r.putString("sessionType", Integer.toString(message.getSessionType().getValue()));
                    r.putString("sessionId", message.getSessionId());
                }
            } else if (intent.hasExtra(Extras.EXTRA_JUMP_P2P)) {
                Intent data = intent.getParcelableExtra(Extras.EXTRA_DATA);
                String account = data.getStringExtra(Extras.EXTRA_ACCOUNT);
                if (!TextUtils.isEmpty(account)) {
                    r.putString("type", "session");
                    r.putString("sessionType", Integer.toString(SessionTypeEnum.P2P.getValue()));
                    r.putString("sessionId", account);
                }
            }

            LogUtil.w("ReceiverMsgParser", intent + "");
        }

        LogUtil.w("ReceiverMsgParser", result + "");
        return r;
    }

    /**
     * 已经登陆过，自动登陆
     */
    private static boolean canAutoLogin() {

        return true;//!TextUtils.isEmpty(account) && !TextUtils.isEmpty(token);
    }
}
