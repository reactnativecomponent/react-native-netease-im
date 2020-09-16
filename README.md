
React Native的网易云信插件
欢迎加入QQ群交流`153174456`
## Demo
[react-native-chat-demo](https://github.com/reactnativecomponent/react-native-chat-demo)

### 注意事项: 
##### 2.普通帐号不要使用5位数，因为5位数设定是系统帐号，尽量使用6位或者6位以上


## 1.安装 
- ###  0.60以下请使用2.1.0版本
```bash
npm install react-native-netease-im 或者 yarn add react-native-netease-im 
cd ios
pod install
```

## 2.配置
### 2.1 android配置

在`android/app/build.gradle`里，defaultConfig栏目下添加如下代码：
```
multiDexEnabled true
manifestPlaceholders = [
	// 如果有多项，每一项之间需要用逗号分隔
    NIM_KEY: "云信的APPID"    //在此修改云信APPID
]
```
在`AndroidManifest.xml`里，添加如下代码：
```
< manifest

    ......

    <!-- SDK 权限申明 -->
    <permission
        android:name="${applicationId}.permission.RECEIVE_MSG"
        android:protectionLevel="signature"/>
    <!-- 接收 SDK 消息广播权限 -->
    <uses-permission android:name="${applicationId}.permission.RECEIVE_MSG"/>

    ......
    < application
            ......
            <!-- 设置你的网易聊天App Key -->
     <meta-data android:name="com.netease.nim.appKey" android:value="${NIM_KEY}" />


```
`android/app/src/main/java/<你的包名>/MainActivity.java`
```
import com.netease.im.uikit.permission.MPermission;
import com.netease.im.RNNeteaseImModule;
import com.netease.im.ReceiverMsgParser;

public class MainActivity extends ReactActivity {

 ......

  @Override
     protected void onCreate(Bundle savedInstanceState) {
         super.onCreate(savedInstanceState);
         if(ReceiverMsgParser.checkOpen(getIntent())){//在后台时处理点击推送消息
             RNNeteaseImModule.launch = getIntent();
         }
     }

 @Override
 public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        MPermission.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
 }
 ```

`android/app/src/main/java/<你的包名>/MainApplication.java`中添加如下两行：

```
...
import com.netease.im.RNNeteaseImPackage;
import com.netease.im.IMApplication;
import com.netease.im.ImPushConfig;

public class MainApplication extends Application implements ReactApplication {

  @Override
  public void onCreate() {
    // IMApplication.setDebugAble(BuildConfig.DEBUG);
    // 推送配置，没有可传null
     ImPushConfig config = new ImPushConfig();
    // 小米证书配置，没有可不填
    config.xmAppId = "";
    config.xmAppKey = "";
    config.xmCertificateName = "";
    // 华为推送配置，没有可不填
    config.hwCertificateName = "";
    IMApplication.init(this, MainActivity.class,R.drawable.ic_stat_notify_msg, config);
   ...
  }
}
```

#### 2.2 ios配置


在你工程的`AppDelegate.m`文件中添加如下代码：

```
...
#import <NIMSDK/NIMSDK.h>
#import "NTESSDKConfigDelegate.h"
@interface AppDelegate ()
@property (nonatomic,strong) NTESSDKConfigDelegate *sdkConfigDelegate;
@end
@implementation AppDelegate

- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions
{
...
[self setupNIMSDK];
[self registerAPNs];
if (launchOptions) {//未启动时，点击推送消息
    NSDictionary * remoteNotification = [launchOptions objectForKey:UIApplicationLaunchOptionsRemoteNotificationKey];
    if (remoteNotification) {
      [self performSelector:@selector(clickSendObserve:) withObject:remoteNotification afterDelay:0.5];
    }
  }
...
return YES;
}
- (void)clickSendObserve:(NSDictionary *)dict{
  [[NSNotificationCenter defaultCenter]postNotificationName:@"ObservePushNotification" object:@{@"dict":dict,@"type":@"launch"}];
}
- (void)setupNIMSDK
{
//在注册 NIMSDK appKey 之前先进行配置信息的注册，如是否使用新路径,是否要忽略某些通知，是否需要多端同步未读数
self.sdkConfigDelegate = [[NTESSDKConfigDelegate alloc] init];
[[NIMSDKConfig sharedConfig] setDelegate:self.sdkConfigDelegate];
[[NIMSDKConfig sharedConfig] setShouldSyncUnreadCount:YES];
//appkey 是应用的标识，不同应用之间的数据（用户、消息、群组等）是完全隔离的。
//注册APP，请将 NIMSDKAppKey 换成您自己申请的App Key
[[NIMSDK sharedSDK] registerWithAppID:@"appkey" cerName:@"证书名称"];
}

#pragma mark - misc
- (void)registerAPNs
{
[[UIApplication sharedApplication] registerForRemoteNotifications];
UIUserNotificationType types = UIUserNotificationTypeBadge | UIUserNotificationTypeSound | UIUserNotificationTypeAlert;
UIUserNotificationSettings *settings = [UIUserNotificationSettings settingsForTypes:types categories:nil];
[[UIApplication sharedApplication] registerUserNotificationSettings:settings];
}

- (void)application:(UIApplication *)app didRegisterForRemoteNotificationsWithDeviceToken:(NSData *)deviceToken
{
  [[NIMSDK sharedSDK] updateApnsToken:deviceToken];
}
//在后台时处理点击推送消息
- (void)application:(UIApplication *)application didReceiveRemoteNotification:(NSDictionary *)userInfo{

  [[NSNotificationCenter defaultCenter]postNotificationName:@"ObservePushNotification" object:@{@"dict":userInfo,@"type":@"background"}];
}
- (void)application:(UIApplication *)app didFailToRegisterForRemoteNotificationsWithError:(NSError *)error
{
  NSLog(@"fail to get apns token :%@",error);
}
- (void)applicationDidEnterBackground:(UIApplication *)application {
  NSInteger count = [[[NIMSDK sharedSDK] conversationManager] allUnreadCount];
  [[UIApplication sharedApplication] setApplicationIconBadgeNumber:count];
}
```



## 如何使用
### 引入包

```
import {NimSession} from 'react-native-netease-im';
```

### API

参考[index.js](https://github.com/reactnativecomponent/react-native-netease-im/blob/master/index.js)

#### 监听会话
```
NativeAppEventEmitter.addListener("observeRecentContact",(data)=>{
  console.log(data); //返回会话列表和未读数
})；
```
#### 推送(推送配置参考官方文档即可)
```
//程序运行时获取的推送点击事件
NativeAppEventEmitter.addListener("observeLaunchPushEvent",(data)=>{
  console.log(data);
})；
//程序后台时获取的推送点击事件
NativeAppEventEmitter.addListener("observeBackgroundPushEvent",(data)=>{
  console.log(data); 
})；
//推送数据格式
{
    ...
    sessionBody：{
        sessionId:"",
        sessionType:"",
        sessionName:""
    }
}

```

