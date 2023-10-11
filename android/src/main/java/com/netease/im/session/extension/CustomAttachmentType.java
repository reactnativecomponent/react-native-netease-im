package com.netease.im.session.extension;

/**
 * Created by zhoujianghua on 2015/4/9.
 */
public interface CustomAttachmentType {

    String ForwardMultipleText = "forwardMultipleText";
    String RedPacket = "redpacket";//红包
    String BankTransfer = "transfer";//转账

    String BankTransferSystem= "system";//系统消息
    String RedPacketOpen = "redpacketOpen";//拆红包提醒

    String ProfileCard = "ProfileCard";//个人名片
    String Collection = "Collection";//收藏
    String SystemImageText = "SystemImageText";//系统富文本消息


    String LinkUrl = "url";//链接
    String AccountNotice = "account_notice";//账户变动通知
    String Card = "card";//账户变动通知

}
