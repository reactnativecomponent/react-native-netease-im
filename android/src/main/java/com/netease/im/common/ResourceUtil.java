package com.netease.im.common;

import com.netease.im.IMApplication;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

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
