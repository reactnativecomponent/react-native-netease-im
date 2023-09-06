export enum NIMMessageTypeEnum {
  TEXT = "text",
  VOICE = "voice",
  IMAGE = "image",
  VIDEO = "video",
  FILE = "file",
  ROBOT = "robot",
  BANK_TRANSFER = "transfer",
  ACCOUNT_NOTICE = "account_notice",
  EVENT = "event",
  LOCATION = "location",
  NOTIFICATION = "notification",
  TIP = "tip",
  RED_PACKET = "redpacket",
  RED_PACKET_OPEN = "redpacketOpen",
  LINK = "url",
  CARD = "card",
  CUSTOM = "custom",
}

export enum NIMMessageStatusEnum {
  SEND_DRAFT = "send_draft",
  SEND_FAILED = "send_failed",
  SEND_SENDING = "send_going",
  SEND_SUCCESS = "send_succed",
  RECEIVE_READ = "receive_read",
  RECEIVE_UNREAD = "receive_unread",
}
