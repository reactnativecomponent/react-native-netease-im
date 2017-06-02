package com.netease.im.uikit.session.helper;

import com.netease.im.login.LoginService;
import com.netease.im.uikit.cache.TeamDataCache;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.msg.MessageBuilder;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.constant.MsgStatusEnum;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.CustomMessageConfig;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.netease.nimlib.sdk.team.constant.TeamTypeEnum;
import com.netease.nimlib.sdk.team.model.Team;

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
    public void onRevokeMessage(IMMessage item) {
        if (item == null) {
            return;
        }

        IMMessage message = MessageBuilder.createTipMessage(item.getSessionId(), item.getSessionType());
        String nick = "";
        if (item.getSessionType() == SessionTypeEnum.Team) {
            nick = TeamDataCache.getInstance().getTeamMemberDisplayNameYou(item.getSessionId(), item.getFromAccount());
        } else if (item.getSessionType() == SessionTypeEnum.P2P) {
            nick = item.getFromAccount().equals(LoginService.getInstance().getAccount()) ? "你" : "对方";
        }
        message.setContent(nick + "撤回了一条消息");
        message.setStatus(MsgStatusEnum.success);
        CustomMessageConfig config = new CustomMessageConfig();
        config.enableUnreadCount = false;
        message.setConfig(config);
        NIMClient.getService(MsgService.class).saveMessageToLocalEx(message, true, item.getTime());
    }
    public void onCreateTeamMessage(Team team){
        if(team==null||team.getType()== TeamTypeEnum.Normal){
            return;
        }
        Map<String, Object> content = new HashMap<>(1);
        content.put("content", "成功创建高级群");
        IMMessage msg = MessageBuilder.createTipMessage(team.getId(), SessionTypeEnum.Team);
        msg.setRemoteExtension(content);
        CustomMessageConfig config = new CustomMessageConfig();
        config.enableUnreadCount = false;
        msg.setConfig(config);
        msg.setStatus(MsgStatusEnum.success);
        NIMClient.getService(MsgService.class).saveMessageToLocal(msg, true);
    }
}
