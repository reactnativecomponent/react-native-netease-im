import { NativeModules } from "react-native";
import { NIMAddFriendVerifyEnum, NIMUserInfo } from "./friend.type";
const { RNNeteaseIm } = NativeModules;

class NimFriend {
  /**
   * 进入好友
   * @returns {*} @see observeFriend
   */
  startFriendList() {
    return RNNeteaseIm.startFriendList();
  }
  /**
   * 退出好友
   * @returns {*}
   */
  stopFriendList() {
    return RNNeteaseIm.stopFriendList();
  }
  /**
   * 获取本地用户资料
   * @param contactId
   * @returns {*}
   */
  getUserInfo(contactId: string) {
    return RNNeteaseIm.getUserInfo(contactId);
  }
  /**
   * 获取服务器用户资料
   * @param contactId
   * @returns {*}
   */
  fetchUserInfo(contactId: string) {
    return RNNeteaseIm.fetchUserInfo(contactId);
  }
  /**
   * 保存用户资料
   * @param contactId {'NIMUserInfoUpdateTagNick':'昵称'}
   * @returns {*}
   */
  updateMyUserInfo(userInFo: NIMUserInfo) {
    return RNNeteaseIm.updateMyUserInfo(userInFo);
  }
  /**
   * 保存好友备注
   * @param contactId
   * @returns {*}
   */
  updateUserInfo(contactId: string, alias: string) {
    return RNNeteaseIm.updateUserInfo(contactId, alias);
  }
  /**
   * 好友列表
   * @param keyword
   * @returns {*}
   */
  getFriendList(keyword: string) {
    return RNNeteaseIm.getFriendList(keyword);
  }
  /**
   * 添加好友
   * @param contactId
   * @param msg remark
   * @returns {*}
   */
  addFriend(contactId: string, msg: string) {
    return RNNeteaseIm.addFriend(contactId, msg);
  }

  /**
   * 添加好友
   * @param contactId
   * @param verifyType "1" 直接添加 其他 验证添加
   * @param msg 备注
   * @returns {*}
   */
  addFriendWithType(
    contactId: string,
    verifyType: NIMAddFriendVerifyEnum,
    msg
  ) {
    return RNNeteaseIm.addFriendWithType(contactId, verifyType, msg);
  }
  /**
   * 删除好友
   * @param contactId
   * @returns {*}
   */
  deleteFriend(contactId: string) {
    return RNNeteaseIm.deleteFriend(contactId);
  }
  /**
   * 进入黑名单列表
   * @returns {*} @see observeBlackList
   */
  startBlackList() {
    return RNNeteaseIm.startBlackList();
  }
  /**
   * 退出黑名单列表
   * @returns {*}
   */
  stopBlackList() {
    return RNNeteaseIm.stopBlackList();
  }
  /**
   * 获取黑名单列表
   * @returns {*}
   */
  getBlackList() {
    return RNNeteaseIm.getBlackList();
  }
  /**
   * 加入黑名单
   * @returns {*}
   */
  addToBlackList(contactId: string) {
    return RNNeteaseIm.addToBlackList(contactId);
  }
  /**
   * 移出黑名单
   * @returns {*}
   */
  removeFromBlackList(contactId: string) {
    return RNNeteaseIm.removeFromBlackList(contactId);
  }
}

export default new NimFriend();
