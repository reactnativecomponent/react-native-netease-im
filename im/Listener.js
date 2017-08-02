/**
 * Created by dowin on 2017/8/2.
 */
class Listener {
    static addListener =
        [{'observeRecentContact': '最近会话'}, {'observeOnlineStatus': '在线状态'}, {'observeFriend': '联系人'},
            {'observeTeam': '群组'}, {'observeBlackList': '黑名单'}, {'observeReceiveMessage': '接收消息'},
            {'observeReceiveSystemMsg': '系统通知'}, {'observeUnreadCountChange': '未读数变化'}, {'observeMsgStatus': '发送消息状态变化'},
            {'observeAudioRecord': '录音状态'}, {'observeDeleteMessage': '撤销后删除消息'}, {'observeAttachmentProgress': '未读数变化'},
            {'observeOnKick': '被踢出下线'},
        ];
}
module.exports = new Listener();