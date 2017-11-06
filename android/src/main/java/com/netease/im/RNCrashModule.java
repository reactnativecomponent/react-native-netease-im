package com.netease.im;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.netease.im.uikit.common.util.storage.StorageType;
import com.netease.im.uikit.common.util.storage.StorageUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by dowin on 2017/11/3.
 */

public class RNCrashModule extends ReactContextBaseJavaModule implements Thread.UncaughtExceptionHandler {

    private final static String TAG = "CrashHandler";
    private final static String NAME = "CrashHandler";

    public RNCrashModule(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    @Override
    public String getName() {
        return NAME;
    }

    //    object.put("count",String.valueOf(count));
//		object.put("time",timestamp);
//		object.put("device",SysInfoUtil.getPhoneModelWithManufacturer());
//		object.put("android",SysInfoUtil.getOsInfo());
//		object.put("system",Build.DISPLAY);
//		object.put("battery",battery());
//		object.put("rooted: ", isRooted() ? "yes" : "no");
//		object.put("ram",ram());
//		object.put("disk",disk());
//		object.put("ver", String.format("%d", InstallUtil.getVersionCode(context)));
//		object.put("caught", uncaught ? "no" : "yes");
//		object.put("network",NetworkUtil.getNetworkInfo(context));
//
//		object.put("errorInfo",trace);
    @ReactMethod
    public void getErrorMessage(Promise promise) {

        File mFile = new File(StorageUtil.getWritePath("jsonUpLoad.crashlog", StorageType.TYPE_LOG));

        if (!mFile.exists()) {
            return;
        }
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(mFile));
            StringBuffer s = new StringBuffer();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                s.append(line).append(System.getProperty("line.separator"));
            }
            JSONObject object = new JSONObject(s.toString());
            promise.resolve(object.optString("errorInfo"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @ReactMethod
    public void deleteErrorMessage(Promise promise) {
        File mFile = new File(StorageUtil.getWritePath("jsonUpLoad.crashlog", StorageType.TYPE_LOG));
        if (mFile.exists()) {
            mFile.delete();
        }
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {

    }
}
