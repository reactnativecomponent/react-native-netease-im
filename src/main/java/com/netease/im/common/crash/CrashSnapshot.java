package com.netease.im.common.crash;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.provider.Settings.Secure;
import android.text.TextUtils;

import com.netease.im.IMApplication;
import com.netease.im.common.sys.InstallUtil;
import com.netease.im.common.sys.SysInfoUtil;
import com.netease.im.uikit.common.util.sys.NetworkUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;


public class CrashSnapshot {

	private static long mTotalMemory = -1;

	/**
	 * 检测手机是否Rooted
	 * 
	 * @return
	 */
	private static boolean isRooted() {
		boolean isSdk = isGoogleSdk();
		Object tags = Build.TAGS;
		if ((!isSdk) && (tags != null)
				&& (((String) tags).contains("test-keys"))) {
			return true;
		}
		if (new File("/system/app/Superuser.apk").exists()) {
			return true;
		}
		if ((!isSdk) && (new File("/system/xbin/su").exists())) {
			return true;
		}
		return false;
	}

	private static boolean isGoogleSdk() {
		String str = Secure.getString(IMApplication.getContext()
				.getContentResolver(), Secure.ANDROID_ID);
		return ("sdk".equals(Build.PRODUCT))
				|| ("google_sdk".equals(Build.PRODUCT)) || (str == null);
	}

	/**
	 * 获取手机剩余电量
	 * 
	 * @return
	 */
	private static String battery() {
		IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
		Intent intent = IMApplication.getContext().registerReceiver(null, filter);
		int level = intent.getIntExtra("level", -1);
		int scale = intent.getIntExtra("scale", -1);
		if(scale == -1) {
			return "--";
		} else {
			return String.format(Locale.US, "%d %%", (level*100) / scale);
		}
	}

	private static long getAvailMemory() {
		ActivityManager am = (ActivityManager) IMApplication.getContext()
				.getSystemService(Context.ACTIVITY_SERVICE);
		ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
		am.getMemoryInfo(mi);
		return mi.availMem;
	}

	private static String parseFile(File file, String filter) {
		String str = null;
		if (file.exists()) {
			BufferedReader br = null;
			try {
				br = new BufferedReader(new FileReader(file), 1024);
				String line;
				while ((line = br.readLine()) != null) {
					Pattern pattern = Pattern.compile("\\s*:\\s*");
					String[] ret = pattern.split(line, 2);
					if (ret != null && ret.length > 1 && ret[0].equals(filter)) {
						str = ret[1];
						break;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return str;
	}

	private static long getSize(String size, String uint, int factor) {
		return Long.parseLong(size.split(uint)[0].trim()) * factor;
	}

	private static synchronized long getTotalMemory() {
		if (mTotalMemory == -1) {
			long total = 0L;
			String str;
			try {
				if (!TextUtils.isEmpty(str = parseFile(
						new File("/proc/meminfo"), "MemTotal"))) {
					str = str.toUpperCase(Locale.US);
					if (str.endsWith("KB")) {
						total = getSize(str, "KB", 1024);
					} else if (str.endsWith("MB")) {
						total = getSize(str, "MB", 1048576);
					} else if (str.endsWith("GB")) {
						total = getSize(str, "GB", 1073741824);
					} else {
						total = -1;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			mTotalMemory = total;
		}
		return mTotalMemory;
	}

	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	private static long[] getSdCardMemory() {
		long[] sdCardInfo = new long[2];
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			File sdcardDir = Environment.getExternalStorageDirectory();
			StatFs sf = new StatFs(sdcardDir.getPath());
			if (Build.VERSION.SDK_INT >= 18) {
				long bSize = sf.getBlockSizeLong();
				long bCount = sf.getBlockCountLong();
				long availBlocks = sf.getAvailableBlocksLong();
				sdCardInfo[0] = bSize * bCount;
				sdCardInfo[1] = bSize * availBlocks;
			} else {
				long bSize = sf.getBlockSize();
				long bCount = sf.getBlockCount();
				long availBlocks = sf.getAvailableBlocks();
				sdCardInfo[0] = bSize * bCount;
				sdCardInfo[1] = bSize * availBlocks;
			}
		}
		return sdCardInfo;
	}
	
	private static String disk() {
		long[] info = getSdCardMemory();
		long total = info[0];
		long avail = info[1];
		if(total <= 0) {
			return "--";
		} else {
			float ratio = (float) ((avail * 100) / total); 
			return String.format(Locale.US, "%.01f%% [%s]", ratio, getSizeWithUnit(total));
		}
	}
	
	private static String ram() {
		long total = getTotalMemory();
		long avail = getAvailMemory();
		if(total <= 0) {
			return "--";
		} else {
			float ratio = (float) ((avail * 100) / total); 
			return String.format(Locale.US, "%.01f%% [%s]", ratio, getSizeWithUnit(total));
		}
	}
	
	private static String getSizeWithUnit(long size) {
		if(size >= 1073741824) {
			float i = (float) (size / 1073741824);
			return String.format(Locale.US, "%.02f GB", i);
		} else if(size >= 1048576 ) {
			float i = (float) (size / 1048576);
			return String.format(Locale.US, "%.02f MB", i);
		} else {
			float i = (float) (size / 1024);
			return String.format(Locale.US, "%.02f KB", i);
		}
	}

	
	public static String snapshot(Context context, boolean uncaught, String timestamp, String trace, int count) {
		Map<String, String> info = new LinkedHashMap<String, String>();
        info.put("count: ", String.valueOf(count));
		info.put("time: ", timestamp);
		info.put("device: ", SysInfoUtil.getPhoneModelWithManufacturer());
		info.put("android: ", SysInfoUtil.getOsInfo());
		info.put("system: ", Build.DISPLAY);
		info.put("battery: ", battery());
		info.put("rooted: ", isRooted() ? "yes" : "no");
		info.put("ram: ", ram());
		info.put("disk: ", disk());
		info.put("ver: ", String.format("%d", InstallUtil.getVersionCode(context)));
		info.put("caught: ", uncaught ? "no" : "yes");
		info.put("network: ", NetworkUtil.getNetworkInfo(context));
		Iterator<Map.Entry<String, String>> iterator = info.entrySet().iterator();	
		StringBuilder sb = new StringBuilder();
        while (iterator.hasNext()) {
            Map.Entry<String, String> entry = iterator.next();
            if(entry != null) {
            	sb.append(entry.getKey()).append(entry.getValue());
            	sb.append(System.getProperty("line.separator"));
            }
        }
        sb.append(System.getProperty("line.separator"));
        sb.append(trace);
    	sb.append(System.getProperty("line.separator"));
		sb.append(System.getProperty("line.separator"));
		sb.append(System.getProperty("line.separator"));
		return sb.toString();
	}

}
