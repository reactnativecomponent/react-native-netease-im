package com.netease.im.common.sys;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Context;
import android.os.Build;
import android.os.PowerManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;

public class SysInfoUtil {
	public static final String getOsInfo() {
		return Build.VERSION.RELEASE;
	}

	public static final String getPhoneModelWithManufacturer() {
		return Build.MANUFACTURER + " " + Build.MODEL;
	}

    public static final String getPhoneMode() {
        return Build.MODEL;
    }

	public static final boolean isAppOnForeground(Context context) {
		ActivityManager manager = (ActivityManager) context
				.getApplicationContext().getSystemService(
						Context.ACTIVITY_SERVICE);
		String packageName = context.getApplicationContext().getPackageName();
		List<ActivityManager.RunningAppProcessInfo> list = manager
				.getRunningAppProcesses();
		if (list == null)
			return false;
		boolean ret = false;
		Iterator<ActivityManager.RunningAppProcessInfo> it = list.iterator();
		while (it.hasNext()) {
			ActivityManager.RunningAppProcessInfo appInfo = it.next();
			if (appInfo.processName.equals(packageName)
					&& appInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
				ret = true;
				break;
			}
		}
		return ret;
	}

	public static final boolean isScreenOn(Context context) {
		PowerManager powerManager = (PowerManager) context
				.getSystemService(Context.POWER_SERVICE);
		return powerManager.isScreenOn();
	}
	
	public static boolean stackResumed(Context context) {
		ActivityManager manager = (ActivityManager) context
				.getApplicationContext().getSystemService(
						Context.ACTIVITY_SERVICE);
		String packageName = context.getApplicationContext().getPackageName();
		List<RunningTaskInfo> recentTaskInfos = manager.getRunningTasks(1);
		if (recentTaskInfos != null && recentTaskInfos.size() > 0) {
			RunningTaskInfo taskInfo = recentTaskInfos.get(0);
			if (taskInfo.baseActivity.getPackageName().equals(packageName) && taskInfo.numActivities > 1) {
				return true;
			}
		}
		
		return false;
	}
	
	public static final boolean mayOnEmulator(Context context) {
		if (mayOnEmulatorViaBuild()) {
			return true;
		}
		
		if (mayOnEmulatorViaTelephonyDeviceId(context)) {
			return true;
		}

		if (mayOnEmulatorViaQEMU(context)) {
			return true;
		}

		return false;
	}
	
	private static final boolean mayOnEmulatorViaBuild() {
		/**
		 * ro.product.model likes sdk
		 */
		if (!TextUtils.isEmpty(Build.MODEL) && Build.MODEL.toLowerCase().contains("sdk")) {
			return true;
		}
		
		/**
		 * ro.product.manufacturer likes unknown
		 */
		if (!TextUtils.isEmpty(Build.MANUFACTURER) && Build.MANUFACTURER.toLowerCase().contains("unknown")) {
			return true;
		}

		/**
		 * ro.product.device likes generic
		 */
		if (!TextUtils.isEmpty(Build.DEVICE) && Build.DEVICE.toLowerCase().contains("generic")) {
			return true;
		}
	
		return false;
	}
	
	private static final boolean mayOnEmulatorViaTelephonyDeviceId(Context context) {		
		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		if (tm == null) {
			return false;
		}
		
		String deviceId = tm.getDeviceId();
		if (TextUtils.isEmpty(deviceId)) {
			return false;
		}
		
		/**
		 * device id of telephony likes '0*'
		 */
		for (int i = 0; i < deviceId.length(); i++) {
			if (deviceId.charAt(i) != '0') {
				return false;
			}
		}
		
		return true;
	}
	
	private static final boolean mayOnEmulatorViaQEMU(Context context) {
		String qemu = getProp(context, "ro.kernel.qemu");
		return "1".equals(qemu);
	}
	
	private static final String getProp(Context context, String property) {
    	try {
    		ClassLoader cl = context.getClassLoader();
    		Class<?> SystemProperties = cl.loadClass("android.os.SystemProperties");
    		Method method = SystemProperties.getMethod("get", String.class);
    		Object[] params = new Object[1];
    		params[0] = property;
    		return (String)method.invoke(SystemProperties, params);
    	} catch (Exception e) {
    		return null;
    	}
    }
}
