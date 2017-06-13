package com.netease.im.session.extension;

/**
 * Created by zhoujianghua on 2015/4/9.
 */
public interface CustomAttachmentType {
    // 多端统一
    int Default = 0;
    int Guess = 1;
    int SnapChat = 2;
    int Sticker = 3;
    int RTS = 4;
    int RedPackage = 5;//红包
    int BankTransfer = 6;//转账
    int BankTransferSystem= 7;//系统消息
    int RedPackageOpen = 8;//拆红包提醒

    int ProfileCard = 9;
    int Collection = 10;
    int SystemImageText = 11;//系统富文本消息

}
