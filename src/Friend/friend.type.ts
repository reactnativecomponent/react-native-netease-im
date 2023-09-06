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
  gender: string;
  email: string;
  birthday: string;
  mobile: string;
}

export enum NIMAddFriendVerifyEnum {
  VERIFY_REQUEST = "0",
  DIRECT_ADD = "1",
}
