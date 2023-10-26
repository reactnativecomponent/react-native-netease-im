import { NativeModules, Platform } from "react-native";
import {
  CustomMessageType,
  NIMSessionTypeEnum,
  NIMQueryDirectionEnum,
  NIMSendAttachmentEnum,
  QueryDirectionType,
} from "./session.type";
import { NIMMessage } from "../Message/message.type";
const { RNNeteaseIm } = NativeModules;

class NimSession {
  /**
   * 登陆
   * @param account
   * @param token
   * @returns {*} @see observeRecentContact observeOnlineStatus
   */
  login(contactId: string, token: string) {
    return RNNeteaseIm.login(contactId, token);
  }

  /**
   * 登陆
   * @param account
   * @param token
   * @returns {*} @see observeRecentContact
   */
  autoLogin(contactId: string, token: string) {
    return RNNeteaseIm.autoLogin(contactId, token);
  }
  /**
   * 退出
   * @returns {*}
   */
  logout() {
    return RNNeteaseIm.logout();
  }
  /**
   * 最近会话列表
   * @returns {*}
   */
  getRecentContactList() {
    return RNNeteaseIm.getRecentContactList();
  }
  /**
   * 删除最近会话
   * @param recentContactId
   * @returns {*}
   */
  deleteRecentContact(recentContactId: string) {
    return RNNeteaseIm.deleteRecentContact(recentContactId);
  }
  /**
   * 进入聊天会话
   * @param sessionId
   * @param type
   * @returns {*} @see observeReceiveMessage 接收最近20消息记录
   */
  startSession(sessionId: string, type: NIMSessionTypeEnum) {
    return RNNeteaseIm.startSession(sessionId, type);
  }

  /**
   * 退出聊天会话
   * @returns {*}
   */
  stopSession() {
    return RNNeteaseIm.stopSession();
  }
  /**
   * 获取云端聊天记录
   * @param messageId
   * @param limit 查询结果的条数限制
   * @returns {*}  @see 回调返回最近所有消息记录
   */
  queryMessageListEx(
    messageId: string,
    limit: number,
    direction: QueryDirectionType
  ) {
    return RNNeteaseIm.queryMessageListEx(messageId, limit, direction);
  }

  searchMessages(keyWords: string): Promise<Record<string, NIMMessage>> {
    return RNNeteaseIm.searchMessages(keyWords);
  }

  /**
   * 获取最近聊天内容
   * @param sessionId 聊天会话ID,
   * @param sessionType 聊天类型 0 单聊，1 群聊
   * @param timeLong 消息时间点 0为最新
   * @param direction 查询方向：'new' 新的; 'old' 旧的
   * @param limit 查询结果的条数限制
   * @param asc 查询结果的排序规则，如果为 true，结果按照时间升级排列，如果为 false，按照时间降序排列
   * @returns {*}  @see 回调返回最近所有消息记录
   */
  //TODO: change 'asc' params to boolean in native code
  queryMessageListHistory(
    sessionId: string,
    sessionType: NIMSessionTypeEnum,
    timeLong: string,
    direction: NIMQueryDirectionEnum,
    limit: number,
    asc: boolean
  ) {
    return RNNeteaseIm.queryMessageListHistory(
      sessionId,
      sessionType,
      timeLong,
      direction,
      limit,
      asc
    );
  }
  /**
   *1.发送文本消息
   * @param content 文本内容
   * @param atUserIds @的群成员ID ["abc","abc12"]
   */
  sendTextMessage(content: string, atUserIds?: string[]) {
    return RNNeteaseIm.sendTextMessage(content, atUserIds);
  }

  /**
   * 发送图片消息
   * @param file 图片文件对象
   * @param type 0:图片 1：视频
   * @param displayName 文件显示名字，如果第三方 APP 不关注，可为空
   * @returns {*}
   */
  sendImageMessages(file: string, displayName?: string) {
    if (Platform.OS === "ios") {
      return RNNeteaseIm.sendImageMessages(file, displayName);
    }
    return RNNeteaseIm.sendImageMessage(
      file.replace("file://", ""),
      displayName
    );
  }
  /**
   * 发送音频消息
   * @param file 音频文件
   * @param duration 音频持续时间，单位是ms
   * @returns {*}
   */
  sendAudioMessage(file: string, duration: string) {
    return RNNeteaseIm.sendAudioMessage(file, duration);
  }

  /**
   * 发送视频消息
   * @param file 视频文件
   * @param duration 视频持续时间
   * @param width 视频宽度
   * @param height 视频高度
   * @param displayName 视频显示名，可为空
   * @returns {*}
   */
  sendVideoMessage(
    file: string,
    duration: string,
    width: number,
    height: number,
    displayName?: string
  ) {
    return RNNeteaseIm.sendVideoMessage(
      file,
      duration,
      width,
      height,
      displayName
    );
  }
  /**
   * 发送地理位置消息
   * @param latitude 纬度
   * @param longitude 经度
   * @param address 地址信息描述
   * @returns {*}
   */
  sendLocationMessage(latitude: string, longitude: string, address: string) {
    return RNNeteaseIm.sendLocationMessage(latitude, longitude, address);
  }
  /**
   * 发送系统通知
   * @param content
   * @returns {*}
   */
  sendTipMessage(content: string) {
    return RNNeteaseIm.sendTipMessage(content);
  }

  /**
   * 红包
   * @param type 红包类型
   *          0   一对一
   *          1   一对多(固定金额)
   *          2   一对多(随机金额)
   * @param comments 红包描祝福语 "[恭喜发财，大吉大利]"
   * @param serialNo 流水号
   * @returns {*}
   */
  sendRedPacketMessage(
    type: NIMSendAttachmentEnum,
    comments: string,
    serialNo: string
  ) {
    return RNNeteaseIm.sendRedPacketMessage(type, comments, serialNo);
  }

  /**
   * 名片
   * @param type 个人名片 群名片 公众号名片
   * @param name
   * @param imgPath
   * @param sessionId
   * @returns {*}
   */
  sendCardMessage(
    toSessionType: string,
    toSessionId: string,
    name: string,
    imgPath: string,
    cardSessionId: string,
    cardSessionType: string
  ) {
    return RNNeteaseIm.sendCardMessage(
      toSessionType,
      toSessionId,
      name,
      imgPath,
      cardSessionId,
      cardSessionType
    );
  }
  /**
   * 拆红包
   * @param sendId 发送红包的sessionId
   * @param hasRedPacket '0': mantled, '1': dismantled
   * @param serialNo 流水号
   * @returns {*}
   */
  sendRedPacketOpenMessage(
    sendId: string,
    hasRedPacket: string,
    serialNo: string
  ) {
    return RNNeteaseIm.sendRedPacketOpenMessage(sendId, hasRedPacket, serialNo);
  }
  /**
   * 转账
   * min 转账最小金额
   * max 转账最大金额
   * @param amount 转账金额
   * @param comments 转账说明
   * @param serialNo 流水号
   * @returns {*}
   */
  sendBankTransferMessage(amount: string, comments: string, serialNo: string) {
    return RNNeteaseIm.sendBankTransferMessage(amount, comments, serialNo);
  }
  /**
   * 发送自定义消息
   * @param attachment
   * @returns {*}
   */
  sendCustomMessage(custType: number, attachment: any) {
    return RNNeteaseIm.sendCustomMessage(custType, attachment);
  }

  /**
   * 
   * @param dataDict 
   * @param sessionId 
   * @param sessionType 
   * @param content 
   * @returns 
   *  NimSession.forwardMultipleTextMessage(
      { messages: NIMMessage[] },
      '14198181486',
      NIMSessionTypeEnum.P2P,
      'test 12345',
    );
   */
  forwardMultipleTextMessage(
    dataDict: {
      messages: NIMMessage[];
    },
    sessionId: string,
    sessionType: NIMSessionTypeEnum,
    content: string | null
  ) {
    return RNNeteaseIm.forwardMultipleTextMessage(
      dataDict,
      sessionId,
      sessionType,
      content
    );
  }
  /**
   * 开启录音权限
   * @returns {*}
   */
  onTouchVoice() {
    return RNNeteaseIm.onTouchVoice();
  }
  /**
   * 开始录音
   * @returns {*}
   */
  startAudioRecord() {
    return RNNeteaseIm.startAudioRecord();
  }

  /**
   * 结束录音,自动发送
   * @returns {*} @see observeAudioRecord
   */
  endAudioRecord() {
    return RNNeteaseIm.endAudioRecord();
  }

  /**
   * 取消播放录音
   * @returns {*}
   */
  cancelAudioRecord() {
    return RNNeteaseIm.cancelAudioRecord();
  }
  /**
   * 转发消息操作
   * @param messageId
   * @param sessionId
   * @param sessionType
   * @param content
   * @returns {*}
   */
  sendForwardMessage(
    messageIds: string[],
    sessionId: string,
    sessionType: NIMSessionTypeEnum,
    content: string
  ) {
    return RNNeteaseIm.sendForwardMessage(
      messageIds,
      sessionId,
      sessionType,
      content
    );
  }
  /**
   * 消息撤回
   * @param messageId
   * @returns {*}
   */
  revokeMessage(messageId: string) {
    return RNNeteaseIm.revokeMessage(messageId);
  }

  /**
   * 重发消息到服务器
   * @param messageId
   * @returns {*}
   */
  resendMessage(messageId: string) {
    return RNNeteaseIm.resendMessage(messageId);
  }
  /**
   * 消息删除
   * @param messageId
   * @returns {*}
   */
  deleteMessage(messageId: string) {
    return RNNeteaseIm.deleteMessage(messageId);
  }
  /**
   * 清空聊天记录
   * @param messageId
   * @returns {*}
   */
  clearMessage(sessionId: string, type: NIMSessionTypeEnum) {
    return RNNeteaseIm.clearMessage(sessionId, type);
  }

  /**
   * Android下载文件附件
   * @param messageId
   * @returns {*}
   */
  downloadAttachment(messageId: string) {
    return RNNeteaseIm.downloadAttachment(messageId, "0");
  }

  /**
   * 更新录音消息是否播放过的状态
   * @param messageId
   * @returns {*}
   */
  updateAudioMessagePlayStatus(messageId: string) {
    return RNNeteaseIm.updateAudioMessagePlayStatus(messageId);
  }

  getLaunch() {
    if (Platform.OS === "android") {
      return RNNeteaseIm.getLaunch();
    }
  }
}

export default new NimSession();
