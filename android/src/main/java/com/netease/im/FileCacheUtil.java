package com.netease.im;

import android.content.Context;
import android.content.pm.IPackageDataObserver;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.RemoteException;
import android.os.StatFs;

import com.netease.im.uikit.common.util.file.FileUtil;
import com.netease.im.uikit.common.util.log.LogUtil;
import com.netease.im.uikit.common.util.storage.StorageType;
import com.netease.im.uikit.common.util.storage.StorageUtil;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.msg.MsgService;

import java.io.File;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by dowin on 2017/7/6.
 * <p>
 * log: SDK日志
 * file: 文件消息文件
 * image: 图片消息文件
 * audio：语音消息文件
 * video：视频消息文件
 * thumb：图片/视频缩略图文件
 */

public class FileCacheUtil {

    final static String TAG = "FileCacheUtil";

    interface OnObserverGet {
        void onGetCacheSize(String size);
    }

    interface OnObserverClean {
        void onCleanCache(boolean succeeded);
    }

    public static void getCacheSie(final OnObserverGet observer) {

        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                Set<String> pathList = getCacheDir();
                long allLength = 0;
                for (String s : pathList) {
                    long t = makeDirSize(new File(s));
                    LogUtil.i(TAG, s + ":" + FileUtil.formatFileSize(t));
                    allLength += t;
                }
                LogUtil.i(TAG, "allFile" + ":" + FileUtil.formatFileSize(allLength));
                final long finalAllLength = allLength;
                getCacheSize(new IPackageStatsObserver.Stub() {
                    @Override
                    public void onGetStatsCompleted(PackageStats pStats, boolean succeeded) throws RemoteException {

                        LogUtil.i(TAG, "cacheSize" + ":" + FileUtil.formatFileSize(pStats.cacheSize));
                        LogUtil.i(TAG, "externalCacheSize" + ":" + FileUtil.formatFileSize(pStats.externalCacheSize));

//                        LogUtil.i(TAG, "codeSize" + ":" + FileUtil.formatFileSize(pStats.codeSize));
//                        LogUtil.i(TAG, "dataSize" + ":" + FileUtil.formatFileSize(pStats.dataSize));
//                        LogUtil.i(TAG, "externalCodeSize" + ":" + FileUtil.formatFileSize(pStats.externalCodeSize));
//                        LogUtil.i(TAG, "externalDataSize" + ":" + FileUtil.formatFileSize(pStats.externalDataSize));
//                        LogUtil.i(TAG, "externalMediaSize" + ":" + FileUtil.formatFileSize(pStats.externalMediaSize));
//                        LogUtil.i(TAG, "externalObbSize" + ":" + FileUtil.formatFileSize(pStats.externalObbSize));
                        long result = finalAllLength;
                        result += pStats.cacheSize;
                        result += pStats.externalCacheSize;
//                        LogUtil.i(TAG, "result" + ":" + FileUtil.formatFileSize(result));
                        if (observer != null) {
                            observer.onGetCacheSize(FileUtil.formatFileSize(result));
                        }
                    }
                });
                return null;
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public static void cleanCache(final OnObserverClean observer) {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                IMApplication.getImageLoaderKit().clearCache();
                Set<String> pathList = getCacheDir();
                for (String s : pathList) {
                    deleteDir(new File(s));
                }
                NIMClient.getService(MsgService.class).clearMsgDatabase(true);
                freeStorageAndNotify(new IPackageDataObserver.Stub() {

                    @Override
                    public void onRemoveCompleted(String packageName, boolean succeeded) throws RemoteException {
                        LogUtil.i(TAG, "result" + ":" + packageName);
                        LogUtil.i(TAG, "result" + ":" + succeeded);
                        if (observer != null) {
                            observer.onCleanCache(succeeded);
                        }
                    }
                });
                return null;
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private static void deleteDir(File file) {
        if (file == null || !file.exists()) {
            return;
        }
        if (file.isFile()) {
            file.delete();
        }
        File[] list = file.listFiles();
        if (list != null && list.length > 0) {
            for (File f : list) {
                if (f.isDirectory()) {
                    deleteDir(f);
                } else {
                    f.delete();
                }
            }
        }
    }

    private static long makeDirSize(File file) {

        if (file == null || !file.exists()) {
            return 0L;
        }
        if (file.isFile()) {
            return file.length();
        }
        long all = 0L;
        File[] list = file.listFiles();
        if (list != null && list.length > 0) {
            for (File f : list) {
                if (f.isDirectory()) {
                    all += makeDirSize(f);
                } else {
                    all += f.length();
                }
            }
        }
        return all;
    }

    private static void getCacheSize(IPackageStatsObserver.Stub observer) {
        Context context = IMApplication.getContext();
        String pkg = context.getPackageName();
        PackageManager pm = context.getPackageManager();
        try {
            LogUtil.i(TAG, "name:" + pm.getClass().getName());
            Method getPackageSizeInfo = pm.getClass().getMethod("getPackageSizeInfo", String.class, IPackageStatsObserver.class);
            getPackageSizeInfo.invoke(pm, pkg, observer);
        } catch (Exception ex) {
            LogUtil.e("", "NoSuchMethodException");
            ex.printStackTrace();
        }
    }

    private static Set<String> getCacheDir() {

        StorageType[] storageTypes = StorageType.values();
        String[] sdkFileName = {"log/", "file/", "image/", "audio/", "video/", "thumb/"};
        Set<String> path = new HashSet<>();
        for (StorageType type : storageTypes) {
            path.add(StorageUtil.getDirectoryByDirType(type));
        }
        for (String sdk : sdkFileName) {
            path.add(IMApplication.getSdkStorageRooPath() + "/" + sdk);
        }
        File imageCacheDir = IMApplication.getImageLoaderKit().getChacheDir();
        if (imageCacheDir.exists()) {
            path.add(imageCacheDir.getAbsolutePath());
        }

        Context context = IMApplication.getContext();
        path.add(context.getCacheDir().getAbsolutePath());
        path.add(context.getExternalCacheDir().getAbsolutePath());

        return path;
    }


    private static long getEnvironmentSize() {
        File localFile = Environment.getDataDirectory();
        if (localFile == null)
            return 0L;

        StatFs statFs = new StatFs(localFile.getPath());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            return statFs.getBlockCountLong() * statFs.getBlockSizeLong();
        }
        return statFs.getBlockCount() * statFs.getBlockSize();
    }

    private static void freeStorageAndNotify(IPackageDataObserver.Stub observer) {

        try {
            Context context = IMApplication.getContext();
            PackageManager pm = context.getPackageManager();
            LogUtil.i(TAG, "name:" + pm.getClass().getName());
            Method localMethod = pm.getClass().getMethod("freeStorageAndNotify", Long.TYPE,
                    IPackageDataObserver.class);
            long localLong = Long.valueOf(getEnvironmentSize() - 1L);

            localMethod.invoke(pm, localLong, observer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
