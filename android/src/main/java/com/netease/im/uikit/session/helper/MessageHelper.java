package com.netease.im.uikit.session.helper;

import com.netease.im.login.LoginService;
import com.netease.im.uikit.cache.NimUserInfoCache;
import com.netease.im.uikit.cache.TeamDataCache;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallbackWrapper;
import com.netease.nimlib.sdk.msg.MessageBuilder;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.constant.MsgStatusEnum;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.CustomMessageConfig;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.netease.nimlib.sdk.team.constant.TeamTypeEnum;
import com.netease.nimlib.sdk.team.model.Team;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by hzxuwen on 2016/8/19.
 */
public class MessageHelper {

    public static MessageHelper getInstance() {
        return InstanceHolder.instance;
    }

    static class InstanceHolder {
        final static MessageHelper instance = new MessageHelper();
    }

    // 消息撤回
    public void onRevokeMessage(final IMMessage item) {
        if (item == null) {
            return;
        }
        if (item.getSessionType() == SessionTypeEnum.Team) {
            Team t = TeamDataCache.getInstance().getTeamById(item.getSessionId());
            if (t == null || !t.isMyTeam()) {
                return;
            }
        }

        final IMMessage message = MessageBuilder.createTipMessage(item.getSessionId(), item.getSessionType());
        String nick = "";
        if (item.getSessionType() == SessionTypeEnum.Team) {
            nick = TeamDataCache.getInstance().getTeamMemberDisplayNameYou(item.getSessionId(), item.getFromAccount());
        } else if (item.getSessionType() == SessionTypeEnum.P2P) {
            nick = item.getFromAccount().equals(LoginService.getInstance().getAccount()) ? "你" : "对方";
        }

        final String nickName = nick;
        if (item.getFromAccount().equals(nick)) {
            NimUserInfoCache.getInstance().getUserInfoFromRemote(item.getFromAccount(), new RequestCallbackWrapper<NimUserInfo>() {
                @Override
                public void onResult(int code, NimUserInfo result, Throwable exception) {
                    if (result != null) {
                        message.setContent(result.getName() + "撤回了一条消息");
                    } else {
                        message.setContent(nickName + "撤回了一条消息");
                    }
//                    message.setPushContent(nick + "撤回了一条消息");
                    message.setStatus(MsgStatusEnum.success);
                    CustomMessageConfig config = new CustomMessageConfig();
                    config.enableUnreadCount = false;
                    config.enablePush = false;
                    message.setConfig(config);
                    NIMClient.getService(MsgService.class).saveMessageToLocalEx(message, true, item.getTime());
                }
            });
        }
    }

    public void onCreateTeamMessage(Team team) {
        if (team == null || team.getType() == TeamTypeEnum.Normal) {
            return;
        }
        Map<String, Object> content = new HashMap<>(1);
        content.put("content", "成功创建群");
        IMMessage msg = MessageBuilder.createTipMessage(team.getId(), SessionTypeEnum.Team);
        msg.setRemoteExtension(content);
        CustomMessageConfig config = new CustomMessageConfig();
        config.enableUnreadCount = false;
        config.enablePush = false;
        msg.setConfig(config);
        msg.setStatus(MsgStatusEnum.success);
        NIMClient.getService(MsgService.class).saveMessageToLocal(msg, true);
    }
}
