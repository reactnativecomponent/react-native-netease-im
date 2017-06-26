package com.netease.im.login;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.netease.im.IMApplication;
import com.netease.im.session.SessionUtil;
import com.netease.im.team.TeamListService;
import com.netease.im.uikit.LoginSyncDataStatusObserver;
import com.netease.im.uikit.cache.DataCacheManager;
import com.netease.nimlib.sdk.AbortableFuture;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.auth.AuthService;
import com.netease.nimlib.sdk.auth.LoginInfo;
import com.netease.nimlib.sdk.friend.model.AddFriendNotify;
import com.netease.nimlib.sdk.msg.MsgServiceObserve;
import com.netease.nimlib.sdk.msg.constant.SystemMessageType;
import com.netease.nimlib.sdk.msg.model.CustomNotification;
import com.netease.nimlib.sdk.msg.model.SystemMessage;

import java.util.Map;

/**
 * Created by dowin on 2017/4/28.
 */

public class LoginService {


    final static String TAG = "LoginService";
    // 自己的用户帐号
    private String account;
    private AbortableFuture<LoginInfo> loginInfoFuture;

    private LoginService() {

    }

    static class InstanceHolder {
        final static LoginService instance = new LoginService();
    }

    public static LoginService getInstance() {
        return InstanceHolder.instance;
    }

    /**
     * 设置当前登录用户的帐号
     *
     * @param account 帐号
     */
    public void setAccount(String account) {
        this.account = account;
    }

    public String getAccount() {
        return account;
    }

    public LoginInfo getLoginInfo(Context context) {
        LoginInfo info = new LoginInfo(null, null);
        return info;
    }

    void initLogin(LoginInfo loginInfo) {
        DataCacheManager.buildDataCacheAsync();
    }

    public void login(LoginInfo loginInfo, final RequestCallback<LoginInfo> callback) {
        loginInfoFuture = NIMClient.getService(AuthService.class).login(loginInfo);
        loginInfoFuture.setCallback(new RequestCallback<LoginInfo>() {
            @Override
            public void onSuccess(LoginInfo loginInfo) {
                account = loginInfo.getAccount();
                initLogin(loginInfo);
                callback.onSuccess(loginInfo);

                registerObserver(true);
                queryRecentContacts();
                loginInfoFuture = null;
            }


            @Override
            public void onFailed(int code) {
                callback.onFailed(code);
                registerObserver(false);
                loginInfoFuture = null;
            }

            @Override
            public void onException(Throwable exception) {
                callback.onException(exception);
                registerObserver(false);
                loginInfoFuture = null;
            }
        });

    }


    private void queryRecentContacts() {
        recentContactObserver.queryRecentContacts();
    }

    volatile boolean hasRegister;

    RecentContactObserver recentContactObserver = new RecentContactObserver();
//    SysMessageObserver sysMessageObserver = new SysMessageObserver();

    synchronized void registerObserver(boolean register) {
        if (hasRegister && register) {
            return;
        }
        hasRegister = register;

        recentContactObserver.registerRecentContactObserver(register);
//        sysMessageObserver.registerSystemObserver(register);
//        NIMClient.getService(SystemMessageObserver.class).observeReceiveSystemMsg(systemMessageObserver, register);
        NIMClient.getService(MsgServiceObserve.class).observeCustomNotification(notificationObserver, register);
    }

    private NotificationManager notificationManager;
    private Observer<CustomNotification> notificationObserver = new Observer<CustomNotification>() {
        @Override
        public void onEvent(CustomNotification customNotification) {

            Map<String, Object> map = customNotification.getPushPayload();
            if (map != null && map.containsKey("type")) {
                String type = (String) map.get("type");
                if (SessionUtil.CUSTOM_Notification.equals(type)) {
                    NotificationCompat.Builder builder = new NotificationCompat.Builder(IMApplication.getContext());
                    builder.setContentTitle("请求加为好友");
                    builder.setContentText(customNotification.getApnsText());
                    builder.setAutoCancel(true);
                    PendingIntent contentIntent = PendingIntent.getActivity(
                            IMApplication.getContext(), 0, new Intent(IMApplication.getContext(), IMApplication.getMainActivityClass()), 0);
                    builder.setContentIntent(contentIntent);
                    builder.setSmallIcon(IMApplication.getNotify_msg_drawable_id());
                    getNotificationManager().notify((int) System.currentTimeMillis(),builder.build());
                }
            }
        }
    };

    public NotificationManager getNotificationManager() {
        if (notificationManager == null) {
            notificationManager = (NotificationManager) IMApplication.getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return notificationManager;
    }

    private Observer<SystemMessage> systemMessageObserver = new Observer<SystemMessage>() {
        @Override
        public void onEvent(SystemMessage systemMessage) {
            if (systemMessage.getType() == SystemMessageType.AddFriend) {
                AddFriendNotify attachData = (AddFriendNotify) systemMessage.getAttachObject();
                if (attachData != null && attachData.getEvent() == AddFriendNotify.Event.RECV_ADD_FRIEND_VERIFY_REQUEST) {//TODO

                }
            }
        }
    };

    public boolean deleteRecentContact(String rContactId) {
        return recentContactObserver.deleteRecentContact(rContactId);
    }

    public void logout() {

        NIMClient.getService(AuthService.class).logout();//退出服务

        registerObserver(false);//取消登录注册
        TeamListService.getInstance().clear();//群清理
        // 清理缓存&注销监听&清除状态
        DataCacheManager.clearDataCache();
        account = null;
        LoginSyncDataStatusObserver.getInstance().reset();
    }


}
