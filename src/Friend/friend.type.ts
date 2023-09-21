export type UpdateProfileTypes = {
  NIMUserInfoUpdateTagNick?: string; // name
  NIMUserInfoUpdateTagAvatar?: string; // avatar
  NIMUserInfoUpdateTagSign?: string;
  NIMUserInfoUpdateTagGender?: string;
  NIMUserInfoUpdateTagEmail?: string;
  NIMUserInfoUpdateTagBirth?: string;
  NIMUserInfoUpdateTagMobile?: string;
  NIMUserInfoUpdateTagExt?: string;
};

export enum NIMAddFriendVerifyEnum {
  VERIFY_REQUEST = "0",
  DIRECT_ADD = "1",
}
