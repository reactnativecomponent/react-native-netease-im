package com.netease.im.recent;

import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.StatusCode;

/**
 * Created by dowin on 2017/4/28.
 */

public class RecentService {

    private RecentService() {

    }

    static class InstanceHolder {
        final static RecentService instance = new RecentService();
    }

    public static RecentService getInstance() {
        return InstanceHolder.instance;
    }
    Observer<StatusCode> userStatusObserver = new Observer<StatusCode>() {

        @Override
        public void onEvent(StatusCode code) {
        }
    };
    private void registerObservers(boolean register) {
//        MsgServiceObserve service = NIMClient.getService(MsgServiceObserve.class);
//        service.observeReceiveMessage(messageReceiverObserver, register);
//        service.observeRecentContact(messageObserver, register);
//        service.observeMsgStatus(statusObserver, register);
//        service.observeRecentContactDeleted(deleteObserver, register);
    }


}
