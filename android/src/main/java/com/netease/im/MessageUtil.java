package com.netease.im;

import com.netease.im.session.extension.BankTransferAttachment;
import com.netease.im.session.extension.RedPacketAttachement;
import com.netease.nimlib.sdk.msg.constant.AttachStatusEnum;
import com.netease.nimlib.sdk.msg.constant.MsgDirectionEnum;
import com.netease.nimlib.sdk.msg.constant.MsgTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;

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
}
