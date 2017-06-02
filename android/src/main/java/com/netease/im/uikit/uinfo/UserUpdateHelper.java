package com.netease.im.uikit.uinfo;

import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallbackWrapper;
import com.netease.nimlib.sdk.uinfo.UserService;
import com.netease.nimlib.sdk.uinfo.constant.UserInfoFieldEnum;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by hzxuwen on 2015/9/17.
 */
public class UserUpdateHelper {
    private static final String TAG = UserUpdateHelper.class.getSimpleName();

    /**
     * 更新用户资料
     */
    public static void update(final UserInfoFieldEnum field, final Object value, RequestCallbackWrapper<Void> callback) {
        Map<UserInfoFieldEnum, Object> fields = new HashMap<>(1);
        fields.put(field, value);
        update(fields, callback);
    }

    public static void update(final Map<UserInfoFieldEnum, Object> fields, final RequestCallbackWrapper<Void> callback) {
        NIMClient.getService(UserService.class).updateUserInfo(fields).setCallback(callback);
    }
}
