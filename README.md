
React Native的网易云信插件
#### 注意: react-native版本需要0.40.0及以上

## 如何安装

### 1.首先安装npm包

```bash
npm install react-native-netease-im --save
```

### 2.link
#### 自动link方法~rnpm requires node version 4.1 or higher

```bash
rnpm link react-native-netease-im
```

#### 手动link~（如果不能够自动link）
#####ios
a.打开XCode's工程中, 右键点击Libraries文件夹 ➜ Add Files to <...>
b.去node_modules ➜ react-native-netease-im ➜ ios ➜ 选择 RNNeteaseIm.xcodeproj
c.在工程Build Phases ➜ Link Binary With Libraries中添加libRNNeteaseIm.a

#####Android

```
// file: android/settings.gradle
...

include ':react-native-netease-im'
project(':react-native-netease-im').projectDir = new File(settingsDir, '../node_modules/react-native-netease-im/android')
```

```
// file: android/app/build.gradle
...

dependencies {
    ...
    compile project(':react-native-netease-im')
}
```

`android/app/src/main/java/<你的包名>/MainApplication.java`中添加如下两行：

```java
...
import com.netease.im.RNNeteaseImPackage;  // 在public class MainApplication之前import
import com.netease.im.IMApplication;

public class MainApplication extends Application implements ReactApplication {

  private final ReactNativeHost mReactNativeHost = new ReactNativeHost(this) {
    @Override
    protected boolean getUseDeveloperSupport() {
      return BuildConfig.DEBUG;
    }

    @Override
    protected List<ReactPackage> getPackages() {
      return Arrays.<ReactPackage>asList(
          new RNNeteaseImPackage(), // 然后添加这一行
          new MainReactPackage()
      );
    }
  };

  @Override
  public ReactNativeHost getReactNativeHost() {
      return mReactNativeHost;
  }
   @Override
  public void onCreate() {
    //初始化方法appId以及appKey在小米开放平台获取，小米推送证书名称在网易云信后台设置
    IMApplication.init(this, MainActivity.class,R.drawable.ic_stat_notify_msg,new    IMApplication.MiPushConfig("小米推送appId","小米推送的appKey","小米推送证书名称"));
   ...
  }
}
```


### 3.工程配置
#### iOS配置

在工程target的`Build Phases->Link Binary with Libraries`中加入`、libsqlite3.tbd、libc++、libz.tbd、CoreTelephony.framework`



在你工程的`AppDelegate.m`文件中添加如下代码：

```
#import "../Libraries/LinkingIOS/RCTLinkingManager.h"

- (BOOL)application:(UIApplication *)application openURL:(NSURL *)url sourceApplication:(NSString *)sourceApplication annotation:(id)annotation {
  return [RCTLinkingManager application:application openURL:url sourceApplication:sourceApplication annotation:annotation];
}
```

#### Android配置

在`android/app/build.gradle`里，defaultConfig栏目下添加如下代码：

```
manifestPlaceholders = [
	// 如果有多项，每一项之间需要用逗号分隔
    NIM_KEY: "网易云信APPID"		//在此修改网易云信APPID
]
```

## 如何使用

### 引入包

```
import NIM from 'react-native-netease-im';
```

### API
#### NIM.login()
```javascript
// 登录参数 
{
	accid: "", //云信注册帐号
    token:"" //登录的token
}
```
返回一个`Promise`对象

#### 监听会话
```
NativeAppEventEmitter.addListener("observeRecentContact",(data)=>{
  console.log(data); //返回内容android和ios有区别
})；
```


