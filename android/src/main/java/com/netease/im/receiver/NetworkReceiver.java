package com.netease.im.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableMap;
import com.netease.im.ReactCache;
import com.netease.im.login.LoginService;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.StatusCode;

/**
 * Created by dowin on 2017/9/28.
 */

public class NetworkReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
//        NetworkInfo mobileInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
//        NetworkInfo wifiInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo activeInfo = manager.getActiveNetworkInfo();
        if (activeInfo != null && activeInfo.isAvailable()) {
            if (NIMClient.getStatus().shouldReLogin()) {
                LoginService.getInstance().autoLogin();
            }
        } else {
            WritableMap r = Arguments.createMap();
            r.putString("status", Integer.toString(StatusCode.NET_BROKEN.getValue()));
            ReactCache.emit(ReactCache.observeOnKick, r);
        }
    }
}
