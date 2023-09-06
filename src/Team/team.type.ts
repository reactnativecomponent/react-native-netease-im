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
