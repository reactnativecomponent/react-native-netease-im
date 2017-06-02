package com.netease.im.uikit.contact.core.provider;

import com.netease.im.uikit.cache.FriendDataCache;
import com.netease.im.uikit.common.util.log.LogUtil;
import com.netease.im.uikit.contact.core.item.AbsContactItem;
import com.netease.im.uikit.contact.core.item.ContactItem;
import com.netease.im.uikit.contact.core.item.ItemTypes;
import com.netease.im.uikit.contact.core.query.TextQuery;
import com.netease.im.uikit.contact.core.util.ContactHelper;
import com.netease.im.IMApplication;
import com.netease.nimlib.sdk.friend.model.Friend;
import com.netease.nimlib.sdk.uinfo.UserInfoProvider;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class UserDataProvider {

    final static String TAG = "UserDataProvider";
    public static final List<AbsContactItem> provide(TextQuery query) {
        List<UserInfoProvider.UserInfo> sources = query(query);
        List<AbsContactItem> items = new ArrayList<>(sources.size());
        for (UserInfoProvider.UserInfo u : sources) {
            items.add(new ContactItem(ContactHelper.makeContactFromUserInfo(u), ItemTypes.FRIEND));
        }

        LogUtil.i(TAG, "contact provide data size =" + items.size());
        return items;
    }

    private static final List<UserInfoProvider.UserInfo> query(TextQuery query) {
        if (query != null) {
            List<UserInfoProvider.UserInfo> users = IMApplication.getContactProvider().getUserInfoOfMyFriends();
            UserInfoProvider.UserInfo user;
            for (Iterator<UserInfoProvider.UserInfo> iter = users.iterator(); iter.hasNext(); ) {
                user = iter.next();
                Friend friend = FriendDataCache.getInstance().getFriendByAccount(user.getAccount());
                boolean hit = ContactSearch.hitUser(user, query) || (friend != null && ContactSearch.hitFriend(friend, query));
                if (!hit) {
                    iter.remove();
                }
            }
            return users;
        } else {
            return IMApplication.getContactProvider().getUserInfoOfMyFriends();
        }
    }
}