'use strict'
import { NativeModules,Platform } from 'react-native';

const { RNNeteaseIm } = NativeModules;

export default class NIM{

    // static init(){
    //     return RNNeteaseIm.init();
    // }
    static addListener =
        [{'observeRecentContact':'最近会话'},{'observeOnlineStatus':'在线状态'},{'observeFriend':'联系人'},{'observeTeam':'群组'},{'observeBlackList':'黑名单'},
            {'observeReceiveMessage':'接收消息'},{'observeReceiveSystemMsg':'系统通知'},{'observeUnreadCountChange':'未读数变化'},
            {'observeMsgStatus':'发送消息状态变化'},{'observeAudioRecord':'录音状态'},
        ];
    /**
     * 登陆
     * @param account
     * @param token
     * @returns {*} @see observeRecentContact observeOnlineStatus
     */
    static login(contactId, token){
        return RNNeteaseIm.login(contactId, token);
    }

    /**
     * 退出
     * @returns {*}
     */
    static logout(){
        return RNNeteaseIm.logout();
    }
    /** ******************************好友用户资料****************************************** **/
    /**
     * 进入好友
     * @returns {*} @see observeFriend
     */
    static startFriendList(){
        return RNNeteaseIm.startFriendList();
    }

    /**
     * 退出好友
     * @returns {*}
     */
    static stopFriendList(){
        return RNNeteaseIm.stopFriendList();
    }

    /**
     * 获取本地用户资料
     * @param contactId
     * @returns {*}
     */
    static getUserInfo(contactId){
        return RNNeteaseIm.getUserInfo(contactId);
    }

    /**
     * 获取服务器用户资料
     * @param contactId
     * @returns {*}
     */
    static fetchUserInfo(contactId){
        return RNNeteaseIm.fetchUserInfo(contactId);
    }
    /**
     * 保存用户资料
     * @param contactId {'NIMUserInfoUpdateTagNick':'昵称'}
     * @returns {*}
     */
    static updateMyUserInfo(userInFo){
        return RNNeteaseIm.updateMyUserInfo(userInFo);
    }
    /**
     * 保存好友备注
     * @param contactId
     * @returns {*}
     */
    static updateUserInfo(contactId,alias){
        return RNNeteaseIm.updateUserInfo(contactId,alias);
    }
    /**
     * 好友列表
     * @param keyword
     * @returns {*}
     */
    static getFriendList(keyword){
        return RNNeteaseIm.getFriendList(keyword);
    }
    /**
     * 添加好友
     * @param contactId
     * @param msg 备注
     * @returns {*}
     */
    static addFriend(contactId,msg){
        return RNNeteaseIm.addFriend(contactId,msg);
    }

    /**
     * 添加好友
     * @param contactId
     * @param verifyType "1" 直接添加 其他 验证添加
     * @param msg 备注
     * @returns {*}
     */
    static addFriendWithType(contactId,verifyType,msg){
        return RNNeteaseIm.addFriendWithType(contactId,verifyType,msg);
    }

    /**
     * 删除好友
     * @param contactId
     * @returns {*}
     */
    static deleteFriend(contactId){
        return RNNeteaseIm.deleteFriend(contactId);
    }
    /** ******************************录音 播放 ****************************************** **/

    /**
    * 开启录音权限
    * @returns {*}
    */
    static onTouchVoice(){
        return RNNeteaseIm.onTouchVoice();
    }
    /**
     * 开始录音
     * @returns {*}
     */
    static startAudioRecord(){
        return RNNeteaseIm.startAudioRecord();
    }

    /**
     * 结束录音,自动发送
     * @returns {*} @see observeAudioRecord
     */
    static endAudioRecord(){
        return RNNeteaseIm.endAudioRecord();
    }
    /**
     * 播放录音
     * @returns {*}
     */
    static play(filepath){
        return RNNeteaseIm.play(filepath);
    }
    /**
     * 停止播放录音
     * @returns {*}
     */
    static stopPlay(){
        return RNNeteaseIm.stopPlay();
    }
    /**
     * 取消播放录音
     * @returns {*}
     */
    static cancelAudioRecord(){
        return RNNeteaseIm.cancelAudioRecord();
    }


    /** ********************获取图片/视频 拍照/录像 获取定位 显示图片/视频 显示定位****************/


    /** ******************************会话  聊天****************************************** **/


    /**
     * 最近会话列表
     * @returns {*}
     */
    static getRecentContactList(){
        return RNNeteaseIm.getRecentContactList();
    }
    /**
     * 删除最近会话
     * @param recentContactId
     * @returns {*}
     */
    static deleteRecentContact(recentContactId){
        return RNNeteaseIm.deleteRecentContact(recentContactId);
    }
    /**
     * 进入聊天会话
     * @param sessionId
     * @param type
     * @returns {*} @see observeReceiveMessage 接收最近20消息记录
     */
    static startSession(sessionId,type){
        return RNNeteaseIm.startSession(sessionId,type);
    }

    /**
     * 退出聊天会话
     * @returns {*}
     */
    static stopSession(){
        return RNNeteaseIm.stopSession();
    }

    /**
     * 获取最近聊天内容
     * @param messageId
     * @param limit 查询结果的条数限制
     * @returns {*}  @see 回调返回最近所有消息记录
     */
    static queryMessageListEx(messageId,limit){
        return RNNeteaseIm.queryMessageListEx(messageId,limit);
    }
    /**
     * 获取最近聊天内容
     * @param sessionId 聊天会话ID,
     * @param sessionType 聊天类型 0 单聊，1 群聊
     * @param timeLong 消息时间点 0为最新
     * @param direction 查询方向：'new' 新的; 'old' 旧的
     * @param limit 查询结果的条数限制
     * @param asc 查询结果的排序规则，如果为 true，结果按照时间升级排列，如果为 false，按照时间降序排列
     * @returns {*}  @see 回调返回最近所有消息记录
     */
    static queryMessageListHistory(sessionId, sessionType, timeLong, direction, limit:Number, asc){
        return RNNeteaseIm.queryMessageListHistory(sessionId, sessionType, timeLong, direction, limit, asc);
    }

    /**
     * 转发消息操作
     * @param messageId
     * @param sessionId
     * @param sessionType
     * @param content
     * @returns {*}
     */
    static sendForwardMessage(messageId, sessionId, sessionType,content){
        return RNNeteaseIm.sendForwardMessage(messageId, sessionId, sessionType,content);
    }

    /**
     * 消息撤回
     * @param messageId
     * @returns {*}
     */
    static revokeMessage(messageId){
        return RNNeteaseIm.revokeMessage(messageId);
    }

    /**
     * 重发消息到服务器
     * @param messageId
     * @returns {*}
     */
    static resendMessage(messageId){
        return RNNeteaseIm.resendMessage(messageId);
    }
    /**
     * 消息删除
     * @param messageId
     * @returns {*}
     */
    static deleteMessage(messageId){
        return RNNeteaseIm.deleteMessage(messageId);
    }
    /**
     * 清空聊天记录
     * @param messageId
     * @returns {*}
     */
    static clearMessage(sessionId,type){
        return RNNeteaseIm.clearMessage(sessionId,type);
    }
    /** ******************************发送 消息****************************************** **/

    /**
     *1.发送文本消息
     * @param content 文本内容
     */
    static sendTextMessage(content){
        return RNNeteaseIm.sendTextMessage(content);
    }

    /**
     * 发送图片消息
     * @param file 图片文件对象
     * @param type 0:图片 1：视频
     * @param displayName 文件显示名字，如果第三方 APP 不关注，可为空
     * @returns {*}
     */
    static sendImageMessages(file,displayName){
        if(Platform.OS === 'ios'){
            return RNNeteaseIm.sendImageMessages(file,displayName);
        }

        return RNNeteaseIm.sendImageMessage(file.replace("file://",""),displayName);
    }
    /**
     * 发送音频消息
     * @param file 音频文件
     * @param duration 音频持续时间，单位是ms
     * @returns {*}
     */
    //static sendAudioMessage(file,duration){
    //    return RNNeteaseIm.sendAudioMessage(file,duration);
    //}

    /**
     * 发送视频消息
     * @param file 视频文件
     * @param duration 视频持续时间
     * @param width 视频宽度
     * @param height 视频高度
     * @param displayName 视频显示名，可为空
     * @returns {*}
     */
    static sendVideoMessage(file,duration, width, height,displayName){
        return RNNeteaseIm.sendVideoMessage(file,duration, width, height,displayName);
    }
    /**
     * 发送地理位置消息
     * @param latitude 纬度
     * @param longitude 经度
     * @param address 地址信息描述
     * @returns {*}
     */
    static sendLocationMessage(latitude,longitude,address){
        return RNNeteaseIm.sendLocationMessage(latitude,longitude,address);
    }
    /**
     * 发送系统通知
     * @param content
     * @returns {*}
     */
    static sendTipMessage(content){
        return RNNeteaseIm.sendTipMessage(content);
    }

    /**
     * 红包
     * @param type 红包类型
     *          0   一对一
     *          1   一对多(固定金额)
     *          2   一对多(随机金额)
     * @param comments 红包描祝福语 "[恭喜发财，大吉大利]"
     * @param serialNo 流水号
     * @returns {*}
     */
    static sendRedPacketMessage(type, comments, serialNo){
        return RNNeteaseIm.sendRedPacketMessage(type, comments, serialNo);
    }


    /**
     * 拆红包
     * @param sendId 发送红包的sessionId
     * @param hasRedPacket 是否还有红包 '0' '1'是已经拆完
     * @param serialNo 流水号
     * @returns {*}
     */
    static sendRedPacketOpenMessage(sendId,hasRedPacket,serialNo){
        return RNNeteaseIm.sendRedPacketOpenMessage(sendId,hasRedPacket,serialNo);
    }
    /**
     * 转账
     * min 转账最小金额
     * max 转账最大金额
     * @param amount 转账金额
     * @param comments 转账说明
     * @param serialNo 流水号
     * @returns {*}
     */
    static sendBankTransferMessage(amount, comments, serialNo){
        return RNNeteaseIm.sendBankTransferMessage(amount, comments, serialNo);
    }

    /** ******************************blackList 黑名单****************************************** **/

    /**
     * 进入黑名单列表
     * @returns {*} @see observeBlackList
     */
    static startBlackList(){
        return RNNeteaseIm.startBlackList();
    }

    /**
     * 退出黑名单列表
     * @returns {*}
     */
    static stopBlackList(){
        return RNNeteaseIm.stopBlackList();
    }
    /**
     * 获取黑名单列表
     * @returns {*}
     */
    static getBlackList(){
        return RNNeteaseIm.getBlackList();
    }

    /**
     * 加入黑名单
     * @returns {*}
     */
    static addToBlackList(contactId){
        return RNNeteaseIm.addToBlackList(contactId);
    }

    /**
     * 移出黑名单
     * @returns {*}
     */
    static removeFromBlackList(contactId){
        return RNNeteaseIm.removeFromBlackList(contactId);
    }
    /** ******************************team 群组****************************************** **/
    /**
     * 群列表
     * @param keyword
     * @returns {*}
     */
    static getTeamList(keyword){
        return RNNeteaseIm.getTeamList(keyword);
    }

    /**
     * 进入群组列表
     * @returns {*} @see observeTeam
     */
    static startTeamList(){
        return RNNeteaseIm.startTeamList();
    }

    /**
     * 退出群组列表
     * @returns {*}
     */
    static stopTeamList(){
        return RNNeteaseIm.stopTeamList();
    }

    /**
     * 获取本地群资料
     * @param teamId
     * @returns {*}
     */
    static getTeamInfo(teamId){
        return RNNeteaseIm.getTeamInfo(teamId);
    }

    /**
     * 群消息提醒开关
     * @param teamId
     * @param needNotify 开启/关闭消息提醒
     * @returns {*}
     */
    static setTeamNotify(teamId,needNotify){
        return RNNeteaseIm.setTeamNotify(teamId,needNotify);
    }

    /**
     * 好友消息提醒开关
     * @param contactId
     * @param needNotify 开启/关闭消息提醒
     * @returns {*}
     */
    static setMessageNotify(contactId,needNotify){
        return RNNeteaseIm.setMessageNotify(contactId,needNotify);
    }
    /**
     * 群成员禁言
     * @param teamId
     * @param contactId
     * @param mute 开启/关闭禁言
     * @returns {*}
     */
    static setTeamMemberMute(teamId, contactId, mute){
        return RNNeteaseIm.setTeamMemberMute(teamId, contactId, mute);
    }
    /**
     * 获取服务器群资料
     * @param teamId
     * @returns {*}
     */
    static fetchTeamInfo(teamId){
        return RNNeteaseIm.fetchTeamInfo(teamId);
    }

    /**
     * 获取服务器群成员资料
     * @param teamId
     * @returns {*}
     */
    static fetchTeamMemberList(teamId){
        return RNNeteaseIm.fetchTeamMemberList(teamId);
    }

    /**
     * 获取群成员资料及设置
     * @param teamId
     * @param contactId
     * @returns {*}
     */
    static fetchTeamMemberInfo(teamId,contactId){
        return RNNeteaseIm.fetchTeamMemberInfo(teamId,contactId);
    }

    /**
     * 更新群成员名片
     * @param teamId
     * @param contactId
     * @param nick
     * @returns {*}
     */
    static updateMemberNick(teamId,contactId,nick){
        return RNNeteaseIm.updateMemberNick(teamId,contactId,nick);
    }

    /**
     * name 群组名字必填
     * verifyType 验证类型 0 允许任何人加入 1 需要身份验证2 不允许任何人申请加入
     * inviteMode 邀请他人类型 0管理员邀请 1所有人邀请
     * beInviteMode 被邀请人权限 0需要验证 1不需要验证
     * teamUpdateMode 群资料修改权限 0管理员修改 1所有人修改
     * @param fields {name:'群组名字必填'，introduce:'群介绍'，verifyType:'0'，inviteMode:'1'，beInviteMode:'1'，teamUpdateMode:'1'，}
     * @param type '0'讨论组 '1'高级群
     *        当type===0时,fields参数只有name有效;
     *        当type===1时,verifyType:'0'，inviteMode:'1'，beInviteMode:'1'，teamUpdateMode:'1'分别是默认值
     * @param accounts 创建时添加的好友账号ID['abc11','abc12','abc13']
     * @returns {*}
     */
    static createTeam(fields, type, accounts){
        return RNNeteaseIm.createTeam(fields, type, accounts);
    }

    /**
     * 申请加入群组
     * @param teamId
     * @param reason
     * @returns {*}
     */
    static applyJoinTeam(teamId, reason){
        return RNNeteaseIm.applyJoinTeam(teamId, reason);
    }

    /**
     * 解散群组
     * @param teamId
     * @returns {*}
     */
    static dismissTeam(teamId){
        return RNNeteaseIm.dismissTeam(teamId);
    }

    /**
     * 拉人入群
     * @param teamId
     * @param accounts ['abc11','abc12','abc13']
     * @returns {*}
     */
    static addMembers(teamId, accounts){
        return RNNeteaseIm.addMembers(teamId, accounts);
    }

    /**
     * 踢人出群
     * @param teamId
     * @param account['abc12']
     * @returns {*}
     */
    static removeMember(teamId, account){
        return RNNeteaseIm.removeMember(teamId, account);
    }

    /**
     * 主动退群
     * @param teamId
     * @returns {*}
     */
    static quitTeam(teamId){
        return RNNeteaseIm.quitTeam(teamId);
    }

    /**
     * 转让群组
     * @param targetId
     * @param account
     * @param quit
     * @returns {*}
     */
    static transferTeam(teamId, account, quit){
        return RNNeteaseIm.transferTeam(teamId, account, quit);
    }

    /**
     * 修改的群名称
     * @param teamId
     * @param teamName
     * @returns {*}
     */
    static updateTeamName(teamId, teamName){
        return RNNeteaseIm.updateTeamName(teamId, teamName);
    }
    /** ******************************systemMsg 系统通知****************************************** **/

    /**
     * 进入系统通知消息
     * @returns {*} @see observeReceiveSystemMsg
     */
    static startSystemMsg(){
        return RNNeteaseIm.startSystemMsg();
    }

    /**
     * 退出系统通知消息
     * @returns {*}
     */
    static stopSystemMsg(){
        return RNNeteaseIm.stopSystemMsg();
    }

    /**
     * 开始系统通知计数监听
     * @returns {*} @see observeUnreadCountChange
     */
    static startSystemMsgUnreadCount(){
        return RNNeteaseIm.startSystemMsgUnreadCount();
    }

    /**
     * 停止系统通知计数监听
     * @returns {*}
     */
    static stopSystemMsgUnreadCount(){
        return RNNeteaseIm.stopSystemMsgUnreadCount();
    }
    /**
     * 查询系统通知列表
     * @returns {*}
     */
    static querySystemMessagesBlock(offset,limit){
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
    static onSystemNotificationDeal(type,messageId,targetId,fromAccount,pass,timestamp){
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
    static ackAddFriendRequest(messageId,contactId,pass,timestamp){
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
    static passApply(messageId, targetId,fromAccount,pass,timestamp){
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
    static acceptInvite(messageId,targetId,fromAccount,pass,timestamp){
        return RNNeteaseIm.acceptInvite(messageId,targetId,fromAccount,pass,timestamp);
    }
    /**
     * 删除系统通知
     * @param fromAccount
     * @param timestamp
     * @returns {*}
     */
    static deleteSystemMessage(fromAccount,timestamp){
        return RNNeteaseIm.deleteSystemMessage(fromAccount,timestamp);
    }

    /**
     * 删除所有系统通知
     * @returns {*}
     */
    static clearSystemMessages(){
        return RNNeteaseIm.clearSystemMessages();
    }

    /**
     * 将所有系统通知设为已读
     * @returns {*}
     */
    static resetSystemMessageUnreadCount(){
        return RNNeteaseIm.resetSystemMessageUnreadCount();
    }

    /**
     * Android下载文件附件
     * @param messageId
     * @returns {*}
     */
    static downloadAttachment(messageId){
        return RNNeteaseIm.downloadAttachment(messageId,'0');
    }

}
//export default RNNeteaseIm;
