/**
 * Created by dowin on 2017/8/2.
 */
'use strict'
import { NativeModules,Platform } from 'react-native'
const { RNNeteaseIm } = NativeModules
class Friend {
    /**
     * 进入好友
     * @returns {*} @see observeFriend
     */
    startFriendList(){
        return RNNeteaseIm.startFriendList()
    }
    /**
     * 退出好友
     * @returns {*}
     */
    stopFriendList(){
        return RNNeteaseIm.stopFriendList()
    }
    /**
     * 获取本地用户资料
     * @param contactId
     * @returns {*}
     */
    getUserInfo(contactId){
        return RNNeteaseIm.getUserInfo(contactId)
    }
    /**
     * 获取服务器用户资料
     * @param contactId
     * @returns {*}
     */
    fetchUserInfo(contactId){
        return RNNeteaseIm.fetchUserInfo(contactId)
    }
    /**
     * 保存用户资料
     * @param contactId {'NIMUserInfoUpdateTagNick':'昵称'}
     * @returns {*}
     */
    updateMyUserInfo(userInFo){
        return RNNeteaseIm.updateMyUserInfo(userInFo)
    }
    /**
     * 保存好友备注
     * @param contactId
     * @returns {*}
     */
    updateUserInfo(contactId, alias){
        return RNNeteaseIm.updateUserInfo(contactId, alias)
    }
    /**
     * 好友列表
     * @param keyword
     * @returns {*}
     */
    getFriendList(keyword){
        return RNNeteaseIm.getFriendList(keyword)
    }
    /**
     * 添加好友
     * @param contactId
     * @param msg 备注
     * @returns {*}
     */
    addFriend(contactId, msg){
        return RNNeteaseIm.addFriend(contactId, msg)
    }

    /**
     * 添加好友
     * @param contactId
     * @param verifyType "1" 直接添加 其他 验证添加
     * @param msg 备注
     * @returns {*}
     */
    addFriendWithType(contactId, verifyType, msg){
        return RNNeteaseIm.addFriendWithType(contactId, verifyType, msg);
    }
    /**
     * 删除好友
     * @param contactId
     * @returns {*}
     */
    deleteFriend(contactId){
        return RNNeteaseIm.deleteFriend(contactId)
    }
    /**
     * 进入黑名单列表
     * @returns {*} @see observeBlackList
     */
    startBlackList(){
        return RNNeteaseIm.startBlackList()
    }
    /**
     * 退出黑名单列表
     * @returns {*}
     */
    stopBlackList(){
        return RNNeteaseIm.stopBlackList()
    }
    /**
     * 获取黑名单列表
     * @returns {*}
     */
    getBlackList(){
        return RNNeteaseIm.getBlackList()
    }
    /**
     * 加入黑名单
     * @returns {*}
     */
    addToBlackList(contactId){
        return RNNeteaseIm.addToBlackList(contactId)
    }
    /**
     * 移出黑名单
     * @returns {*}
     */
    removeFromBlackList(contactId){
        return RNNeteaseIm.removeFromBlackList(contactId)
    }
}
export default new Friend()
