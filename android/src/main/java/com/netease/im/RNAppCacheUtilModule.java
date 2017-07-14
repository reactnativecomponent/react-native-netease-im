package com.netease.im;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

/**
 * Created by dowin on 2017/7/13.
 */

public class RNAppCacheUtilModule extends ReactContextBaseJavaModule {

    private final static String TAG = "AppCacheUtil";//AppCacheUtil.getCacheSize clearCache
    private final static String NAME = "AppCacheUtil";

    public RNAppCacheUtilModule(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    @Override
    public String getName() {
        return NAME;
    }

    @ReactMethod
    public void getCacheSize(final Promise promise) {
        FileCacheUtil.getCacheSie(new FileCacheUtil.OnObserverGet() {
            @Override
            public void onGetCacheSize(String size) {
                promise.resolve(size);
            }
        });
    }

    @ReactMethod
    public void cleanCache(final Promise promise) {
        FileCacheUtil.cleanCache(new FileCacheUtil.OnObserverClean() {

            @Override
            public void onCleanCache(boolean succeeded) {
                promise.resolve("" + succeeded);
            }
        });
    }
}
