import { NIMUserGenderEnum } from "../User/user.type";
import { NIMCommonBooleanType } from "../utils/common.type";

export enum NIMTeamMessageNotifyEnum {
  All = "0",
  Manager = "1",
  Mute = "2",
}

export enum NIMCreateTeamVerifyEnum {
  Free = "0",
  //You need to apply first, and the administrator can join
  Apply = "1",
  //Private group, does not accept applications, can only join the group by invitation
  Private = "2",
}

export enum NIMTeamOperationType {
  NIMTeamOperationTypeInvite = 0,
  NIMTeamOperationTypeKick = 1,
  NIMTeamOperationTypeLeave = 2,
  NIMTeamOperationTypeUpdate = 3,
  NIMTeamOperationTypeDismiss = 4,
  NIMTeamOperationTypeApplyPass = 5,
  NIMTeamOperationTypeTransferOwner = 6,
  NIMTeamOperationTypeAddManager = 7,
  NIMTeamOperationTypeRemoveManager = 8,
  NIMTeamOperationTypeAcceptInvitation = 9,
  NIMTeamOperationTypeMute = 10,
}

export enum NIMTeamOperationTypeUpdateDetail {
  NIMTeamUpdateTagName = "NIMTeamUpdateTagName",
  NIMTeamUpdateTagIntro = "NIMTeamUpdateTagIntro",
  NIMTeamUpdateTagAnouncement = "NIMTeamUpdateTagAnouncement",
  NIMTeamUpdateTagJoinMode = "NIMTeamUpdateTagJoinMode",
  NIMTeamUpdateTagAvatar = "NIMTeamUpdateTagAvatar",
  NIMTeamUpdateTagInviteMode = "NIMTeamUpdateTagInviteMode",
  NIMTeamUpdateTagBeInviteMode = "NIMTeamUpdateTagBeInviteMode",
  NIMTeamUpdateTagUpdateInfoMode = "NIMTeamUpdateTagUpdateInfoMode",
  NIMTeamUpdateTagMuteMode = "NIMTeamUpdateTagMuteMode",
}

export enum NIMCreateTeamInviteModeEnum {
  Manager = "0",
  All = "1",
}

export enum NIMTeamUpdateModeEnum {
  Manager = "0",
  All = "1",
}

export enum NIMTeamBeInviteModeEnum {
  NeedAuth = "0",
  NoAuth = "1",
}

export enum NIMCreateTeamTypeEnum {
  Normal = "0",
  Advanced = "1",
}

export interface NIMCreateTeamOptionsType {
  name: string;
  introduce: string;
  verifyType: NIMCreateTeamVerifyEnum;
  inviteMode: NIMCreateTeamInviteModeEnum;
  beInviteMode: NIMTeamBeInviteModeEnum;
  teamUpdateMode: NIMTeamUpdateModeEnum;
}

export enum NIMUpdateTeamFieldEnum {
  name = "name",
  icon = "icon",
  introduce = "introduce",
  announcement = "announcement",
  verifyType = "verifyType",
  inviteMode = "inviteMode",
  beInviteMode = "beInviteMode",
  teamUpdateMode = "teamUpdateMode",
}

export interface NIMCreateTeamResultType {
  teamId: string;
}

export interface NIMTeamItemType {
  teamId: string;
  name: string;
  type: NIMCreateTeamTypeEnum;
  avatar: string;
}

export interface NIMTeamDetailType {
  announcement: string;
  avatar: string;
  createTime: string;
  creator: string;
  introduce: string;
  memberCount: string;
  memberLimit: string;
  mute: NIMCommonBooleanType;
  name: string;
  teamBeInviteMode: NIMTeamBeInviteModeEnum;
  teamId: string;
  type: NIMCreateTeamTypeEnum;
  verifyType: NIMCreateTeamVerifyEnum;
}

export enum NIMTeamMemberEnum {
  Normal = "0",
  Owner = "1",
  Manager = "2",
  Apply = "3",
}

export interface NIMTeamMemberType {
  alias: string;
  avatar: string;
  birthday: string;
  contactId: string;
  createTime: string;
  customInfo: string;
  email: string;
  extension: string;
  extensionMap: string;
  gender: NIMUserGenderEnum;
  isInBlackList: NIMCommonBooleanType;
  isMe: NIMCommonBooleanType;
  isMute: NIMCommonBooleanType;
  isMyFriend: NIMCommonBooleanType;
  mobile: string;
  mute: NIMCommonBooleanType;
  name: string;
  nickname: string;
  signature: string;
  teamId: string;
  type: NIMTeamMemberEnum;
  userId: string;
}
