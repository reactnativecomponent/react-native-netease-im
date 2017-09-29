/**
 * Created by dowin on 2017/8/2.
 */
'use strict'
import { NativeModules,Platform } from 'react-native';

const { RNNeteaseIm } = NativeModules;
class SystemMsg {
    /** ******************************systemMsg 系统通知****************************************** **/

    /**
     * 进入系统通知消息
     * @returns {*} @see observeReceiveSystemMsg
     */
    startSystemMsg(){
        return RNNeteaseIm.startSystemMsg();
    }

    /**
     * 退出系统通知消息
     * @returns {*}
     */
    stopSystemMsg(){
        return RNNeteaseIm.stopSystemMsg();
    }

    /**
     * 开始系统通知计数监听
     * @returns {*} @see observeUnreadCountChange
     */
    startSystemMsgUnreadCount(){
        return RNNeteaseIm.startSystemMsgUnreadCount();
    }

    /**
     * 停止系统通知计数监听
     * @returns {*}
     */
    stopSystemMsgUnreadCount(){
        return RNNeteaseIm.stopSystemMsgUnreadCount();
    }
    /**
     * 查询系统通知列表
     * @returns {*}
     */
    querySystemMessagesBlock(offset,limit){
        return RNNeteaseIm.querySystemMessagesBlock(offset,limit);
    }

    /**
     * 通过/拒绝 系统通知
     * @param type
     * @param messageId
     * @param targetId
     * @param contactId
     * @param pass 通过/拒绝
     * @param timestamp
     * @returns {*}
     */
    onSystemNotificationDeal(type,messageId,targetId,fromAccount,pass,timestamp){
        if(type==='0'){
            return RNNeteaseIm.passApply(messageId, targetId,fromAccount,pass,timestamp);
        }else if(type==='2'){
            return RNNeteaseIm.acceptInvite(messageId,targetId,fromAccount,pass,timestamp);
        }else if(type==='5'){
            return RNNeteaseIm.ackAddFriendRequest(messageId,fromAccount,pass,timestamp);
        }
    }

    /**
     * 通过/拒绝对方好友请求
     * @param messageId Android 使用
     * @param contactId
     * @param pass 通过/拒绝
     * @param timestamp ios使用
     * @returns {*}
     */
    ackAddFriendRequest(messageId,contactId,pass,timestamp){
        if(Platform.OS === 'ios'){
            return RNNeteaseIm.ackAddFriendRequest(contactId,pass,timestamp);
        }
        return RNNeteaseIm.ackAddFriendRequest(messageId,contactId,pass,timestamp);
    }

    /**
     * 通过/拒绝申请(仅限高级群)
     * @param messageId Android 使用
     * @param targetId
     * @param fromAccount
     * @param pass 通过/拒绝
     * @param timestamp ios使用
     * @returns {*}
     */
    passApply(messageId, targetId,fromAccount,pass,timestamp){
        return RNNeteaseIm.passApply(messageId, targetId,fromAccount,pass,timestamp);
    }

    /**
     * 同意/拒绝群邀请(仅限高级群)
     * @param messageId Android 使用
     * @param targetId
     * @param fromAccount
     * @param pass 通过/拒绝
     * @param timestamp ios使用
     * @returns {*}
     */
    acceptInvite(messageId,targetId,fromAccount,pass,timestamp){
        return RNNeteaseIm.acceptInvite(messageId,targetId,fromAccount,pass,timestamp);
    }
    /**
     * 删除系统通知
     * @param fromAccount
     * @param timestamp
     * @returns {*}
     */
    deleteSystemMessage(fromAccount,timestamp){
        return RNNeteaseIm.deleteSystemMessage(fromAccount,timestamp);
    }

    /**
     * 删除所有系统通知
     * @returns {*}
     */
    clearSystemMessages(){
        return RNNeteaseIm.clearSystemMessages();
    }

    /**
     * 将所有系统通知设为已读
     * @returns {*}
     */
    resetSystemMessageUnreadCount(){
        return RNNeteaseIm.resetSystemMessageUnreadCount();
    }
}
export default new SystemMsg();
