import { NativeModules } from "react-native";
import { NIMAddFriendVerifyEnum, NIMUserInfo } from "./friend.type";
const { RNNeteaseIm } = NativeModules;

/**
 * 进入好友
 * @returns {*} @see observeFriend
 */
function startFriendList() {
  return RNNeteaseIm.startFriendList();
}
/**
 * 退出好友
 * @returns {*}
 */
function stopFriendList() {
  return RNNeteaseIm.stopFriendList();
}
/**
 * 获取本地用户资料
 * @param contactId
 * @returns {*}
 */
function getUserInfo(contactId: string) {
  return RNNeteaseIm.getUserInfo(contactId);
}
/**
 * 获取服务器用户资料
 * @param contactId
 * @returns {*}
 */
function fetchUserInfo(contactId: string) {
  return RNNeteaseIm.fetchUserInfo(contactId);
}
/**
 * 保存用户资料
 * @param contactId {'NIMUserInfoUpdateTagNick':'昵称'}
 * @returns {*}
 */
function updateMyUserInfo(userInFo: NIMUserInfo) {
  return RNNeteaseIm.updateMyUserInfo(userInFo);
}
/**
 * 保存好友备注
 * @param contactId
 * @returns {*}
 */
function updateUserInfo(contactId: string, alias: string) {
  return RNNeteaseIm.updateUserInfo(contactId, alias);
}
/**
 * 好友列表
 * @param keyword
 * @returns {*}
 */
function getFriendList(keyword: string) {
  return RNNeteaseIm.getFriendList(keyword);
}
/**
 * 添加好友
 * @param contactId
 * @param msg remark
 * @returns {*}
 */
function addFriend(contactId: string, msg: string) {
  return RNNeteaseIm.addFriend(contactId, msg);
}

/**
 * 添加好友
 * @param contactId
 * @param verifyType "1" 直接添加 其他 验证添加
 * @param msg 备注
 * @returns {*}
 */
function addFriendWithType(
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
function deleteFriend(contactId: string) {
  return RNNeteaseIm.deleteFriend(contactId);
}
/**
 * 进入黑名单列表
 * @returns {*} @see observeBlackList
 */
function startBlackList() {
  return RNNeteaseIm.startBlackList();
}
/**
 * 退出黑名单列表
 * @returns {*}
 */
function stopBlackList() {
  return RNNeteaseIm.stopBlackList();
}
/**
 * 获取黑名单列表
 * @returns {*}
 */
function getBlackList() {
  return RNNeteaseIm.getBlackList();
}
/**
 * 加入黑名单
 * @returns {*}
 */
function addToBlackList(contactId: string) {
  return RNNeteaseIm.addToBlackList(contactId);
}
/**
 * 移出黑名单
 * @returns {*}
 */
function removeFromBlackList(contactId: string) {
  return RNNeteaseIm.removeFromBlackList(contactId);
}

export const NimFriend = {
  startFriendList,
  stopFriendList,
  getUserInfo,
  fetchUserInfo,
  updateMyUserInfo,
  updateUserInfo,
  getFriendList,
  addFriend,
  addFriendWithType,
  deleteFriend,
  startBlackList,
  getBlackList,
  addToBlackList,
  removeFromBlackList,
  stopBlackList,
};
