export enum NIMUserGenderEnum {
  UNKNOWN = "0",
  MALE = "1",
  FEMALE = "2",
}

export interface NIMUserInfo {
  isMyFriend: string;
  isMe: string;
  isInBlackList: string;
  mute: string;
  contactId: string;
  name: string;
  alias: string;
  avatar: string;
  avatarLocal: string;
  signature: string;
  gender: NIMUserGenderEnum;
  email: string;
  birthday: string;
  mobile: string;
}
