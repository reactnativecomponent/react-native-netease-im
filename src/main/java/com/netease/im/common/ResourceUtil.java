package com.netease.im.common;

import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

import com.netease.im.IMApplication;

/**
 * Created by dowin on 2017/4/28.
 */

public class ResourceUtil {

    @NonNull
    public final static String getString(@StringRes int resId) {

        return IMApplication.getContext().getResources().getString(resId);
    }
    @NonNull
    public final static String getString(@StringRes int resId, Object... formatArgs) {
        return IMApplication.getContext().getString(resId, formatArgs);
    }

}
