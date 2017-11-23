package com.netease.im.contact;

import com.netease.im.ReactCache;
import com.netease.im.uikit.cache.NimUserInfoCache;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallbackWrapper;
import com.netease.nimlib.sdk.ResponseCode;
import com.netease.nimlib.sdk.friend.FriendService;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;
import com.netease.nimlib.sdk.uinfo.model.UserInfo;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by dowin on 2017/5/13.
 */

public class BlackListObserver {

    List<UserInfo> items = new ArrayList<>();

    public void startBlackList() {
        items.clear();
        final List<String> accounts = NIMClient.getService(FriendService.class).getBlackList();
        List<String> unknownAccounts = new ArrayList<>();
        if (accounts != null) {
            for (String contactId : accounts) {
                if (!NimUserInfoCache.getInstance().hasUser(contactId)) {
                    unknownAccounts.add(contactId);
                } else {
                    items.add(NimUserInfoCache.getInstance().getUserInfo(contactId));
                }
            }
        }

        if (!unknownAccounts.isEmpty()) {
            NimUserInfoCache.getInstance().getUserInfoFromRemote(unknownAccounts, new RequestCallbackWrapper<List<NimUserInfo>>() {
                @Override
                public void onResult(int code, List<NimUserInfo> users, Throwable exception) {
                    if (code == ResponseCode.RES_SUCCESS) {
                        items.addAll(users);
                    }
                    refresh();
                }
            });
        } else {
            refresh();
        }
    }

    public void removeFromBlackList(final String contactId, final RequestCallbackWrapper<Void> callbackWrapper) {
        NIMClient.getService(FriendService.class).removeFromBlackList(contactId)
                .setCallback(new RequestCallbackWrapper<Void>() {
                    @Override
                    public void onResult(int code, Void aVoid, Throwable throwable) {
                        if (callbackWrapper != null) {
                            callbackWrapper.onResult(code, aVoid, throwable);
                        }
                        if (code == ResponseCode.RES_SUCCESS) {
                            removeBlackList(contactId);
                        }
                    }
                });
    }

    public void addToBlackList(final String contactId, final RequestCallbackWrapper<Void> callbackWrapper) {
        NIMClient.getService(FriendService.class).addToBlackList(contactId)
                .setCallback(new RequestCallbackWrapper<Void>() {
                    @Override
                    public void onResult(int code, Void aVoid, Throwable throwable) {
                        if (callbackWrapper != null) {
                            callbackWrapper.onResult(code, aVoid, throwable);
                        }
                        if (code == ResponseCode.RES_SUCCESS) {
                            addBlackList(contactId);
                        }
                    }
                });
    }

    void addBlackList(String contactId) {
        items.add(NimUserInfoCache.getInstance().getUserInfo(contactId));
        refresh();
    }

    private void removeBlackList(String contactId) {

        int index = -1;
        for (int i = 0; i < items.size(); i++) {
            UserInfo item = items.get(i);
            if (contactId.equals(item.getAccount())) {
                index = i;
                break;
            }
        }

        if (index < 0) {
            return;
        }
        items.remove(index);

        refresh();
    }

    void refresh() {
        ReactCache.emit(ReactCache.observeBlackList, ReactCache.createBlackList(items));
    }

    public void stopBlackList() {
        items.clear();
    }
}
