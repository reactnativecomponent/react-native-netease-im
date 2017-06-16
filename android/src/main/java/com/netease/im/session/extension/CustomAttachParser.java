package com.netease.im.session.extension;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.netease.nimlib.sdk.msg.attachment.MsgAttachment;
import com.netease.nimlib.sdk.msg.attachment.MsgAttachmentParser;

/**
 * Created by zhoujianghua on 2015/4/9.
 */
public class CustomAttachParser implements MsgAttachmentParser {

    private static final String KEY_TYPE = "msgtype";
    private static final String KEY_DATA = "data";

    @Override
    public MsgAttachment parse(String json) {
        CustomAttachment attachment = null;
        try {
            JSONObject object = JSON.parseObject(json);
            String type = object.getString(KEY_TYPE);
            JSONObject data = object.getJSONObject(KEY_DATA);
            switch (type) {
                case CustomAttachmentType.RedPacket:
                    attachment = new RedPacketAttachement();
                    break;
                case CustomAttachmentType.BankTransfer:
                    attachment = new BankTransferAttachment();
                    break;
                case CustomAttachmentType.BankTransferSystem:
                    attachment = new BankTransferSystemAttachment();
                    break;
                case CustomAttachmentType.RedPacketOpen:
                    attachment = new RedPacketOpenAttachement();
                    break;
                case CustomAttachmentType.LinkUrl:
                    attachment = new LinkUrlAttachment();
                    break;
                case CustomAttachmentType.AccountNotice:
                    attachment = new AccountNoticeAttachment();
                    break;
                default:
                    attachment = new DefaultCustomAttachment(type);
                    break;
            }

            if (attachment != null) {
                attachment.fromJson(data);
            }
        } catch (Exception e) {

        }

        return attachment;
    }

    public static String packData(String type, JSONObject data) {
        JSONObject object = new JSONObject();
        object.put(KEY_TYPE, type);
        if (data != null) {
            object.put(KEY_DATA, data);
        }

        return object.toJSONString();
    }
}
