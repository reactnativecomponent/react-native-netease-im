package com.netease.im.common;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.text.TextUtils;
import android.widget.Toast;

import com.netease.im.R;
import com.netease.im.login.LoginService;
import com.netease.im.uikit.common.util.sys.NetworkUtil;

import java.io.File;

/**
 * Created by dowin on 2017/5/8.
 */

public class ToolUtil {

    public static boolean checkNetwork(Context context) {
        if (!NetworkUtil.isNetAvailable(context)) {
            Toast.makeText(context, R.string.network_is_not_available, Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }

    public static boolean checkAddFriend(Context context,String account) {
        if (!TextUtils.isEmpty(account) && account.equals(LoginService.getInstance().getAccount())) {
            Toast.makeText(context, "不能加自己为好友", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
    private MediaPlayer getVideoMediaPlayer(Context context,File file) {
        try {
            return MediaPlayer.create(context, Uri.fromFile(file));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
