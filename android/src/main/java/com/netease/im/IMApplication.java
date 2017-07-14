package com.netease.im;

import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.location.LocationProvider;
import android.os.Environment;
import android.support.annotation.DrawableRes;
import android.text.TextUtils;
import android.util.Log;

import com.netease.im.common.ImageLoaderKit;
import com.netease.im.common.crash.AppCrashHandler;
import com.netease.im.common.sys.SystemUtil;
import com.netease.im.contact.DefalutUserInfoProvider;
import com.netease.im.contact.DefaultContactProvider;
import com.netease.im.login.LoginService;
import com.netease.im.session.SessionUtil;
import com.netease.im.session.extension.CustomAttachParser;
import com.netease.im.uikit.LoginSyncDataStatusObserver;
import com.netease.im.uikit.cache.DataCacheManager;
import com.netease.im.uikit.common.util.log.LogUtil;
import com.netease.im.uikit.common.util.media.ImageUtil;
import com.netease.im.uikit.common.util.storage.StorageType;
import com.netease.im.uikit.common.util.storage.StorageUtil;
import com.netease.im.uikit.common.util.sys.ScreenUtil;
import com.netease.im.uikit.contact.core.ContactProvider;
import com.netease.im.uikit.contact.core.query.PinYin;
import com.netease.im.uikit.session.helper.MessageHelper;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.SDKOptions;
import com.netease.nimlib.sdk.StatusBarNotificationConfig;
import com.netease.nimlib.sdk.auth.LoginInfo;
import com.netease.nimlib.sdk.mixpush.NIMPushClient;
import com.netease.nimlib.sdk.msg.MessageNotifierCustomization;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.MsgServiceObserve;
import com.netease.nimlib.sdk.msg.model.CustomNotification;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.netease.nimlib.sdk.uinfo.UserInfoProvider;

/**
 * Created by dowin on 2017/4/28.
 */

public class IMApplication {


    public static class MiPushConfig {

        public String certificate;
        public String appID;
        public String appKey;

        /**
         * 注册小米推送证书名称 、推送appID 以及在云信管理后台添加的appKey
         */
        public MiPushConfig(String certificate, String appID, String appKey) {
            this.certificate = certificate;
            this.appID = appID;
            this.appKey = appKey;
        }
    }

    // context
    private static Context context;

    private static Class mainActivityClass;
    @DrawableRes
    private static int notify_msg_drawable_id;
    // 用户信息提供者
    private static UserInfoProvider userInfoProvider;

    // 通讯录信息提供者
    private static ContactProvider contactProvider;

    // 地理位置信息提供者
    private static LocationProvider locationProvider;

    // 图片加载、缓存与管理组件
    private static ImageLoaderKit imageLoaderKit;
    private static StatusBarNotificationConfig statusBarNotificationConfig;

    public static void init(Context context, Class mainActivityClass, @DrawableRes int notify_msg_drawable_id, MiPushConfig miPushConfig) {
        IMApplication.context = context.getApplicationContext();
        IMApplication.mainActivityClass = mainActivityClass;
        IMApplication.notify_msg_drawable_id = notify_msg_drawable_id;

        // 注册小米推送appID 、appKey 以及在云信管理后台添加的小米推送证书名称，该逻辑放在 NIMClient init 之前
        if (miPushConfig != null)
            NIMPushClient.registerMiPush(context, miPushConfig.certificate, miPushConfig.appID, miPushConfig.appKey);

        NIMClient.init(context, getLoginInfo(), getOptions(context));
        // crash handler
        AppCrashHandler.getInstance(context);
        if (inMainProcess(IMApplication.context)) {


            // init pinyin
            PinYin.init(context);
            PinYin.validate();

            // 初始化Kit模块
            initKit();

        }

    }

    private static Observer<CustomNotification> notificationObserver = new Observer<CustomNotification>() {
        @Override
        public void onEvent(CustomNotification customNotification) {
            NotificationManager notificationManager = (NotificationManager) IMApplication.getContext().getSystemService(Context.NOTIFICATION_SERVICE);
            SessionUtil.receiver(notificationManager, customNotification);
        }
    };

    private static boolean inMainProcess(Context context) {
        String packageName = context.getPackageName();
        String processName = SystemUtil.getProcessName(context);
        return packageName.equals(processName);
    }


    public static Context getContext() {
        return context;
    }

    public static int getNotify_msg_drawable_id() {
        return notify_msg_drawable_id;
    }

    public static Class getMainActivityClass() {
        return mainActivityClass;
    }

    private static LoginInfo getLoginInfo() {
        return LoginService.getInstance().getLoginInfo(context);
    }

    public static String getSdkStorageRooPath() {
        return Environment.getExternalStorageDirectory() + "/" + context.getPackageName() + "/nim";
    }

    private static SDKOptions getOptions(Context context) {
        SDKOptions options = new SDKOptions();

        // 如果将新消息通知提醒托管给SDK完成，需要添加以下配置。
        initStatusBarNotificationConfig(options, context);

        // 配置保存图片，文件，log等数据的目录

        options.sdkStorageRootPath = getSdkStorageRooPath();

        // 配置数据库加密秘钥
        options.databaseEncryptKey = "NETEASE";

        // 配置是否需要预下载附件缩略图
        options.preloadAttach = true;

        // 配置附件缩略图的尺寸大小，
        options.thumbnailSize = ImageUtil.getImageMaxEdge();

        // 用户信息提供者
        options.userInfoProvider = new DefalutUserInfoProvider(context);

        // 定制通知栏提醒文案（可选，如果不定制将采用SDK默认文案）
        options.messageNotifierCustomization = messageNotifierCustomization;

        // 在线多端同步未读数
        options.sessionReadAck = true;

        return options;
    }

    // 这里开发者可以自定义该应用初始的 StatusBarNotificationConfig
    private static StatusBarNotificationConfig loadStatusBarNotificationConfig(Context context) {
        StatusBarNotificationConfig config = new StatusBarNotificationConfig();
        // 点击通知需要跳转到的界面
        config.notificationEntrance = mainActivityClass;
        config.notificationSmallIconId = notify_msg_drawable_id;

        // 通知铃声的uri字符串
        config.notificationSound = "android.resource://" + context.getPackageName() + "/raw/msg";

        // 呼吸灯配置
        config.ledARGB = Color.GREEN;
        config.ledOnMs = 1000;
        config.ledOffMs = 1500;

        // save cache，留做切换账号备用
        setStatusBarNotificationConfig(config);
        return config;
    }

    private static void initStatusBarNotificationConfig(SDKOptions options, Context context) {
        // load 应用的状态栏配置
        StatusBarNotificationConfig config = loadStatusBarNotificationConfig(context);

        // load 用户的 StatusBarNotificationConfig 设置项
        StatusBarNotificationConfig userConfig = null;//UserPreferences.getStatusConfig();
        if (userConfig == null) {
            userConfig = config;
        } else {
            // 新增的 UserPreferences 存储项更新，兼容 3.4 及以前版本
            // APP默认 StatusBarNotificationConfig 配置修改后，使其生效
            userConfig.notificationEntrance = config.notificationEntrance;
            userConfig.notificationFolded = config.notificationFolded;
        }
        // 持久化生效
//        UserPreferences.setStatusConfig(config);
        // SDK statusBarNotificationConfig 生效
        options.statusBarNotificationConfig = userConfig;
    }

    private static MessageNotifierCustomization messageNotifierCustomization = new MessageNotifierCustomization() {
        @Override
        public String makeNotifyContent(String nick, IMMessage message) {
            return null; // 采用SDK默认文案
        }

        @Override
        public String makeTicker(String nick, IMMessage message) {
            return null; // 采用SDK默认文案
        }
    };


    /*********************/
    public static void initKit() {
        NIMClient.getService(MsgService.class).registerCustomAttachmentParser(new CustomAttachParser());
        initUserInfoProvider(userInfoProvider);
        initContactProvider(contactProvider);
//        initDefalutSessionCustomization();
//        initDefalutContactEventListener();

        imageLoaderKit = new ImageLoaderKit(context, null);

        // init data cache
        LoginSyncDataStatusObserver.getInstance().registerLoginSyncDataStatus(true);  // 监听登录同步数据完成通知
        DataCacheManager.observeSDKDataChanged(true);
        if (!TextUtils.isEmpty(getLoginInfo().getAccount())) {
            DataCacheManager.buildDataCache(); // build data cache on auto login
        }

        // init tools
        StorageUtil.init(context, null);
        ScreenUtil.init(context);

        // 注册消息撤回监听器
        registerMsgRevokeObserver();

//        NIMClient.getService(MsgServiceObserve.class).observeCustomNotification(notificationObserver, true);

        // init log
        String path = StorageUtil.getDirectoryByDirType(StorageType.TYPE_LOG);
        LogUtil.init(path, Log.DEBUG);


    }

    private static void registerMsgRevokeObserver() {
        NIMClient.getService(MsgServiceObserve.class).observeRevokeMessage(new Observer<IMMessage>() {
            @Override
            public void onEvent(IMMessage message) {
                if (message == null) {
                    return;
                }

                MessageHelper.getInstance().onRevokeMessage(message);
            }
        }, true);
    }

    // 初始化用户信息提供者
    private static void initUserInfoProvider(UserInfoProvider userInfoProvider) {

        if (userInfoProvider == null) {
            userInfoProvider = new DefalutUserInfoProvider(context);
        }

        IMApplication.userInfoProvider = userInfoProvider;
    }

    // 初始化联系人信息提供者
    private static void initContactProvider(ContactProvider contactProvider) {

        if (contactProvider == null) {
            contactProvider = new DefaultContactProvider();
        }

        IMApplication.contactProvider = contactProvider;
    }

    public static UserInfoProvider getUserInfoProvider() {
        return userInfoProvider;
    }

    public static ContactProvider getContactProvider() {
        return contactProvider;
    }

    public static ImageLoaderKit getImageLoaderKit() {
        return imageLoaderKit;
    }

    public static void setStatusBarNotificationConfig(StatusBarNotificationConfig statusBarNotificationConfig) {
        IMApplication.statusBarNotificationConfig = statusBarNotificationConfig;
    }

    public static StatusBarNotificationConfig getNotificationConfig() {
        return statusBarNotificationConfig;
    }
}
