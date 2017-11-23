package com.netease.im.contact;

import com.netease.im.uikit.cache.FriendDataCache;
import com.netease.im.uikit.cache.NimUserInfoCache;
import com.netease.im.uikit.contact.core.ContactProvider;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;
import com.netease.nimlib.sdk.uinfo.model.UserInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * UIKit默认的通讯录（联系人）数据源提供者，
 * Created by hzchenkang on 2016/12/19.
 */

public class DefaultContactProvider implements ContactProvider {

    @Override
    public List<UserInfo> getUserInfoOfMyFriends() {
        List<NimUserInfo> nimUsers = NimUserInfoCache.getInstance().getAllUsersOfMyFriend();
        List<UserInfo> users = new ArrayList<>(nimUsers.size());
        if (!nimUsers.isEmpty()) {
            users.addAll(nimUsers);
        }

        return users;
    }

    @Override
    public int getMyFriendsCount() {
        return FriendDataCache.getInstance().getMyFriendCounts();
    }

    @Override
    public String getUserDisplayName(String account) {
        return NimUserInfoCache.getInstance().getUserDisplayName(account);
    }
}
