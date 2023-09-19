import { NativeModules } from "react-native";
import {
  NIMCreateTeamOptionsType,
  NIMCreateTeamTypeEnum,
  NIMTeamMessageNotifyEnum,
  NIMUpdateTeamFieldEnum,
} from "./team.type";
const { RNNeteaseIm } = NativeModules;

class NimTeam {
  /**
   * 群列表
   * @param keyword
   * @returns {*}
   */
  getTeamList(keyword: string) {
    return RNNeteaseIm.getTeamList(keyword);
  }

  /**
   * 进入群组列表
   * @returns {*} @see observeTeam
   */
  startTeamList() {
    return RNNeteaseIm.startTeamList();
  }

  /**
   * 退出群组列表
   * @returns {*}
   */
  stopTeamList() {
    return RNNeteaseIm.stopTeamList();
  }

  /**
   * 获取本地群资料
   * @param teamId
   * @returns {*}
   */
  getTeamInfo(teamId) {
    return RNNeteaseIm.getTeamInfo(teamId);
  }

  /**
   * 群消息提醒开关
   * @param teamId
   * @param needNotify 开启/关闭消息提醒
   * @returns {*}
   */
  setTeamNotify(teamId: string, needNotify: boolean) {
    return RNNeteaseIm.setTeamNotify(teamId, needNotify);
  }

  /**
   * 好友消息提醒开关
   * @param contactId
   * @param needNotify 开启/关闭消息提醒
   * @returns {*}
   */
  setMessageNotify(contactId: string, needNotify: boolean) {
    return RNNeteaseIm.setMessageNotify(contactId, needNotify);
  }
  /**
   * 群成员禁言
   * @param teamId
   * @param contactId
   * @param mute 开启/关闭禁言
   * @returns {*}
   */
  setTeamMemberMute(
    teamId: string,
    contactId: string,
    mute: NIMTeamMessageNotifyEnum
  ) {
    return RNNeteaseIm.setTeamMemberMute(teamId, contactId, mute);
  }
  /**
   * 获取服务器群资料
   * @param teamId
   * @returns {*}
   */
  fetchTeamInfo(teamId: string) {
    return RNNeteaseIm.fetchTeamInfo(teamId);
  }

  /**
   * 获取服务器群成员资料
   * @param teamId
   * @returns {*}
   */
  fetchTeamMemberList(teamId: string) {
    return RNNeteaseIm.fetchTeamMemberList(teamId);
  }

  /**
   * 获取群成员资料及设置
   * @param teamId
   * @param contactId
   * @returns {*}
   */
  fetchTeamMemberInfo(teamId: string, contactId: string) {
    return RNNeteaseIm.fetchTeamMemberInfo(teamId, contactId);
  }

  /**
   * 更新群成员名片
   * @param teamId
   * @param contactId
   * @param nick
   * @returns {*}
   */
  updateMemberNick(teamId: string, contactId: string, nick: string) {
    return RNNeteaseIm.updateMemberNick(teamId, contactId, nick);
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
  createTeam(
    fields: NIMCreateTeamOptionsType,
    type: NIMCreateTeamTypeEnum,
    accounts: string[]
  ) {
    return RNNeteaseIm.createTeam(fields, type, accounts);
  }
  /**
   * 更新群资料
   * verifyType 验证类型 0 允许任何人加入 1 需要身份验证2 不允许任何人申请加入
   * inviteMode 邀请他人类型 0管理员邀请 1所有人邀请
   * beInviteMode 被邀请人权限 0需要验证 1不需要验证
   * teamUpdateMode 群资料修改权限 0管理员修改 1所有人修改
   *
   * @param teamId
   * @param fieldType:name(群组名称) icon(头像) introduce(群组介绍) announcement(群组公告)
   *                             verifyType(验证类型) inviteMode(邀请他人类型) beInviteMode(被邀请人权限) teamUpdateMode(群资料修改权限)
   * @param value
   * @param promise
   */
  updateTeam(
    teamId: string,
    fieldType: NIMUpdateTeamFieldEnum,
    value: string
  ): Promise<string> {
    return RNNeteaseIm.updateTeam(teamId, fieldType, value);
  }

  /**
   * 申请加入群组
   * @param teamId
   * @param reason
   * @returns {*}
   */
  applyJoinTeam(teamId: string, reason: string) {
    return RNNeteaseIm.applyJoinTeam(teamId, reason);
  }

  /**
   * disband team
   * @param teamId
   * @returns {*}
   */
  dismissTeam(teamId: string): Promise<string> {
    return RNNeteaseIm.dismissTeam(teamId);
  }

  /**
   * 拉人入群
   * @param teamId
   * @param accounts ['abc11','abc12','abc13']
   * @returns {*}
   */
  addMembers(teamId: string, accounts: string[]): Promise<string> {
    return RNNeteaseIm.addMembers(teamId, accounts);
  }

  /**
   * remove a member
   * @param teamId
   * @param account['abc12']
   * @returns {*}
   */
  removeMember(teamId: string, account: string[]): Promise<string> {
    return RNNeteaseIm.removeMember(teamId, account);
  }

  /**
   * 主动退群
   * @param teamId
   * @returns {*}
   */
  quitTeam(teamId: string) {
    return RNNeteaseIm.quitTeam(teamId);
  }

  /**
   * 转让群组
   * @param targetId
   * @param account
   * @param quit
   * @returns {*}
   */
  transferTeam(teamId: string, account: string, quit: "true" | "false") {
    return RNNeteaseIm.transferTeam(teamId, account, quit);
  }

  /**
   * 修改的群名称
   * @param teamId
   * @param teamName
   * @returns {*}
   */
  updateTeamName(teamId: string, teamName: string): Promise<string> {
    return RNNeteaseIm.updateTeamName(teamId, teamName);
  }

  /**
   * update my custom info
   * @param newInfo: JSON string
   * @param teamId: string
   * @returns {*}
   */
  updateMyCustomInfo(newInfo: string, teamId: string): Promise<string> {
    return RNNeteaseIm.updateMyCustomInfo(newInfo, teamId);
  }

  addManagersToTeam(teamId: string, userIds: Array<string>): Promise<string> {
    return RNNeteaseIm.addManagersToTeam(teamId, userIds);
  }

  removeManagersFromTeam(
    teamId: string,
    userIds: Array<string>
  ): Promise<string> {
    return RNNeteaseIm.removeManagersFromTeam(teamId, userIds);
  }
}

export default new NimTeam();
