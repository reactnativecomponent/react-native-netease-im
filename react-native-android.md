

###自动链接
react-native link react-native-netease-im

###`MainActivity.java` 

```

import com.netease.im.uikit.permission.MPermission;

public class MainActivity extends ReactActivity {

 ......

 @Override
 public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        MPermission.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
 }
 }

###`MainApplication.java`

import com.netease.im.IMApplication;


 @Override
public void onCreate() {

    ......
    //小米推送设置 R.drawable.ic_stat_notify_msg这个是 通知图标
    /初始化方法appId以及appKey在小米开放平台获取，小米推送证书名称在网易云信后台设置
    IMApplication.init(this, MainActivity.class,R.drawable.ic_stat_notify_msg,
    new IMApplication.MiPushConfig("小米推送证书名称","小米推送appId","小米推送的appKey"));


}
```

###`AndroidManifest.xml`


< manifest

    ......

    <!-- SDK 权限申明, 第三方 APP 接入时，请将 com.im.demo 替换为自己的包名 -->
    <!-- 和下面的 uses-permission 一起加入到你的 AndroidManifest 文件中。 -->
    <permission
        android:name="com.im.demo.permission.RECEIVE_MSG"
        android:protectionLevel="signature"/>
    <!-- 接收 SDK 消息广播权限， 第三方 APP 接入时，请将 com.netease.nim.demo 替换为自己的包名 -->
    <uses-permission android:name="com.im.demo.permission.RECEIVE_MSG"/>
    <!-- 小米推送 -->
    <permission
        android:name="com.im.demo.permission.MIPUSH_RECEIVE"
        android:protectionLevel="signature"/>
    <uses-permission android:name="com.im.demo.permission.MIPUSH_RECEIVE"/>

    ......
    < application

            ......
            <!-- 设置你的网易聊天App Key -->
             <meta-data
                        android:name="com.netease.nim.appKey"
                        android:value="App Key" />


                        