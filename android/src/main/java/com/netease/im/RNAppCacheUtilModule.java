package com.netease.im;

import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * Created by dowin on 2017/7/13.
 */

public class RNAppCacheUtilModule extends ReactContextBaseJavaModule {

    private final static String TAG = "AppCacheUtil";//AppCacheUtil.getCacheSize clearCache
    private final static String NAME = "AppCacheUtil";

    private ReactContext reactContext;
    public RNAppCacheUtilModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
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
    @ReactMethod
    public void saveImageToAlbum(String filePath, final Promise promise) {

        File imageFile = new File(filePath);
        if(imageFile.exists()){
            try {
                MediaStore.Images.Media.insertImage(reactContext.getContentResolver(), imageFile.getAbsolutePath(), "title", "description");
                reactContext.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + imageFile.getAbsolutePath())));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

        }


    }
}
