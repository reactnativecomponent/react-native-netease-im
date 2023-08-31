import NimUtils from "./Utils";
import NimFriend from "./im/Friend";
// import NimSession from "./im/Session";
import NimSystemMsg from "./im/SystemMsg";
import NimTeam from "./im/Team";

/**
 *监听列表
 *observeRecentContact 最近会话
 *observeOnlineStatus 在线状态
 *observeFriend 联系人/好友
 *observeTeam 群组
 *observeBlackList 黑名单
 *observeReceiveMessage 接收消息
 *observeReceiveSystemMsg 系统通知
 *observeUnreadCountChange 未读消息数
 *observeMsgStatus 发送消息状态变化
 *observeAudioRecord 录音状态
 *observeDeleteMessage 撤销后删除消息
 *observeAttachmentProgress 未读数变化
 *observeOnKick 被踢出下线
 */
export { NimUtils, NimFriend, NimSession, NimSystemMsg, NimTeam };
