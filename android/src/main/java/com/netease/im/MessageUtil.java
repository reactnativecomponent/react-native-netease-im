package com.netease.im;

import android.text.TextUtils;

import com.netease.im.login.LoginService;
import com.netease.im.session.extension.BankTransferAttachment;
import com.netease.im.session.extension.RedPacketAttachement;
import com.netease.im.uikit.cache.TeamDataCache;
import com.netease.nimlib.sdk.msg.constant.AttachStatusEnum;
import com.netease.nimlib.sdk.msg.constant.MsgDirectionEnum;
import com.netease.nimlib.sdk.msg.constant.MsgTypeEnum;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.netease.nimlib.sdk.robot.model.RobotAttachment;
import com.netease.nimlib.sdk.team.constant.TeamMemberType;
import com.netease.nimlib.sdk.team.model.TeamMember;

/**
 * Created by dowin on 2017/6/14.
 */

public class MessageUtil {


    public static boolean shouldIgnore(IMMessage message) {//TODO;
        if (message.getDirect() == MsgDirectionEnum.In
                && (message.getAttachStatus() == AttachStatusEnum.transferring
                || message.getAttachStatus() == AttachStatusEnum.fail)) {
            // 接收到的消息，附件没有下载成功，不允许转发
            return true;
        } else if (message.getMsgType() == MsgTypeEnum.custom && message.getAttachment() != null
                && (message.getAttachment() instanceof RedPacketAttachement
                || message.getAttachment() instanceof BankTransferAttachment)) {
            // 红包 转账  不允许转发
            return true;
        }
        return false;
    }

    public static boolean shouldIgnoreRevoke(IMMessage message) {//TODO;
        if (message.getMsgType() == MsgTypeEnum.custom && message.getAttachment() != null
                && (message.getAttachment() instanceof RedPacketAttachement
                || message.getAttachment() instanceof BankTransferAttachment)) {
            // 红包 转账  不允许转发
            return true;
        }
        return false;
    }

    public static String getRevokeTipContent(IMMessage item, String revokeAccount) {

        String fromAccount = item.getFromAccount();
        if (item.getMsgType() == MsgTypeEnum.robot) {
            RobotAttachment robotAttachment = (RobotAttachment) item.getAttachment();
            if (robotAttachment.isRobotSend()) {
                fromAccount = robotAttachment.getFromRobotAccount();
            }
        }

        if (!TextUtils.isEmpty(
                revokeAccount) && !revokeAccount.equals(fromAccount)) {
            return getRevokeTipOfOther(item.getSessionId(), item.getSessionType(), revokeAccount);
        } else {
            String revokeNick = ""; // 撤回者
            if (item.getSessionType() == SessionTypeEnum.Team) {
                revokeNick = TeamDataCache.getInstance().getTeamMemberDisplayNameYou(item.getSessionId(), item.getFromAccount());
            } else if (item.getSessionType() == SessionTypeEnum.P2P) {
                revokeNick = item.getFromAccount().equals(LoginService.getInstance().getAccount()) ? "你" : "对方";
            }
            return revokeNick + "撤回了一条消息";
        }
    }

    // 撤回其他人的消息时，获取tip
    public static String getRevokeTipOfOther(String sessionID, SessionTypeEnum sessionType, String revokeAccount) {
        if (sessionType == SessionTypeEnum.Team) {
            String revokeNick = ""; // 撤回者

            if (LoginService.getInstance().getAccount().equals(revokeAccount)) {
                revokeNick = "你";
            } else {
                TeamMember member = TeamDataCache.getInstance().getTeamMember(sessionID, revokeAccount);

                String revoker = TeamDataCache.getInstance().getDisplayNameWithoutMe(sessionID, revokeAccount);

                if (member == null || member.getType() == TeamMemberType.Manager) {
                    revokeNick = "管理员 " + revoker + " ";
                } else if (member.getType() == TeamMemberType.Owner) {
                    revokeNick = "群主 " + revoker + " ";
                }
            }
            return revokeNick + "撤回了一条成员消息";
        } else {
            return "撤回了一条消息";
        }
    }
}
