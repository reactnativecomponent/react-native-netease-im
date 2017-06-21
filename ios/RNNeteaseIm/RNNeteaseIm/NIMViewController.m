//
//  NIMViewController.m
//  NIM
//
//  Created by Dowin on 2017/5/8.
//  Copyright © 2017年 Dowin. All rights reserved.
//

#import "NIMViewController.h"

@interface NIMViewController ()<NIMLoginManagerDelegate,NIMConversationManagerDelegate>

@end

@implementation NIMViewController
+(instancetype)initWithController{
    static NIMViewController *nimVC = nil;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        nimVC = [[NIMViewController alloc]init];
    });
    return nimVC;
}
-(instancetype)initWithNIMController{
    self = [super init];
    if (self) {
        
    }
    return self;
}
-(void)addDelegate{
    [[NIMSDK sharedSDK].loginManager addDelegate:self];
    [[NIMSDK sharedSDK].conversationManager addDelegate:self];
    
}
- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
}
//监听网络
#pragma mark - NIMLoginManagerDelegate
- (void)onLogin:(NIMLoginStep)step{
    switch (step) {
        case NIMLoginStepLinkFailed:
            [NIMModel initShareMD].NIMKick = @"未连接";
            break;
        case NIMLoginStepLinking:
            [NIMModel initShareMD].NIMKick = @"连接中";
            break;
        case NIMLoginStepLinkOK:
        case NIMLoginStepSyncOK:
            [self getResouces];
            [NIMModel initShareMD].NIMKick = @"飞马钱包";
            break;
        case NIMLoginStepSyncing:
            [self getResouces];
            [NIMModel initShareMD].NIMKick = @"同步数据";
            break;
        case  NIMLoginStepLoseConnection:
            [NIMModel initShareMD].NIMKick = @"连接断开";
            break;
        case  NIMLoginStepNetChanged:
            [NIMModel initShareMD].NIMKick = @"网络切换";
            break;
        default:
            break;
    }
    
}
//删除一行
-(void)deleteCurrentSession:(NSString *)recentContactId andback:(ERROR)error{
    NSArray *NIMlistArr = [[NIMSDK sharedSDK].conversationManager.allRecentSessions mutableCopy];
    for (NIMRecentSession *recent in NIMlistArr) {
        if ([recent.session.sessionId isEqualToString:recentContactId]) {
            id<NIMConversationManager> manager = [[NIMSDK sharedSDK] conversationManager];
            [manager deleteRecentSession:recent];
            [self getResouces];
        }
    }
}

#pragma NIMLoginManagerDelegate
-(void)onKick:(NIMKickReason)code clientType:(NIMLoginClientType)clientType
{
    NSString *reason = @"你被踢下线";
    
    switch (code) {
        case NIMKickReasonByClient:
        case NIMKickReasonByClientManually:{
            NSString *clientName = [NTESClientUtil clientName:clientType];
            reason = clientName.length ? [NSString stringWithFormat:@"你的帐号被%@端踢出下线，请注意帐号信息安全",clientName] : @"你的帐号被踢出下线，请注意帐号信息安全";
            [NIMModel initShareMD].NIMKick = reason;
            break;
        }
        case NIMKickReasonByServer:
            reason = @"你被服务器踢下线";
            [NIMModel initShareMD].NIMKick = reason;
            break;
        default:
            break;
    }
    [[[NIMSDK sharedSDK] loginManager] logout:^(NSError *error) {
    }];
}
- (void)onAutoLoginFailed:(NSError *)error{
    
    NSLog(@"自动登录失败");
}



#pragma mark - NIMConversationManagerDelegate
- (void)didAddRecentSession:(NIMRecentSession *)recentSession
           totalUnreadCount:(NSInteger)totalUnreadCount{
    
    [self getResouces];
}


- (void)didUpdateRecentSession:(NIMRecentSession *)recentSession
              totalUnreadCount:(NSInteger)totalUnreadCount{
    [self getResouces];
}
-(void)getRecentContactListsuccess:(SUCCESS)suc andError:(ERROR)err{
    NSArray *NIMlistArr = [[NIMSDK sharedSDK].conversationManager.allRecentSessions mutableCopy];
    NSMutableArray *sessionList = [NSMutableArray array];
    for (NIMRecentSession *recent in NIMlistArr) {
        NSMutableDictionary *dic = [NSMutableDictionary dictionary];
        [dic setObject:recent.session.sessionId forKey:@"contactId"];
        [dic setObject:[NSString stringWithFormat:@"%ld", recent.session.sessionType] forKey:@"sessionType"];
        //未读
        [dic setObject:[NSString stringWithFormat:@"%ld", recent.unreadCount] forKey:@"unreadCount"];
        //群组名称或者聊天对象名称
        [dic setObject:[NSString stringWithFormat:@"%@", [self nameForRecentSession:recent] ] forKey:@"name"];
        //账号
        [dic setObject:[NSString stringWithFormat:@"%@", recent.lastMessage.from] forKey:@"account"];
        //消息类型
        [dic setObject:[NSString stringWithFormat:@"%ld", recent.lastMessage.messageType] forKey:@"msgType"];
        //消息状态
        [dic setObject:[NSString stringWithFormat:@"%ld", recent.lastMessage.deliveryState] forKey:@"msgStatus"];
        //消息ID
        [dic setObject:[NSString stringWithFormat:@"%@", recent.lastMessage.messageId] forKey:@"messageId"];
        //消息内容
        [dic setObject:[NSString stringWithFormat:@"%@", [self contentForRecentSession:recent] ] forKey:@"content"];
        //发送时间
        [dic setObject:[NSString stringWithFormat:@"%@", [self timestampDescriptionForRecentSession:recent] ] forKey:@"time"];
        
        [dic setObject:[NSString stringWithFormat:@"%@", [self imageUrlForRecentSession:recent] ?  [self imageUrlForRecentSession:recent] : @""] forKey:@"imagePath"];
        if (recent.session.sessionType == 1) {
            NIMTeam *team = [[[NIMSDK sharedSDK] teamManager]teamById:recent.lastMessage.session.sessionId];
            [dic setObject:[NSString stringWithFormat:@"%ld",team.memberNumber] forKey:@"memberCount"];
        }
       
        [sessionList addObject:dic];
    }
    if (sessionList) {
        suc(sessionList);
    }else{
        err(@"网络异常");
    }
    
}
-(void)getResouces{
    
    NSString *currentAccout = [[NIMSDK sharedSDK].loginManager currentAccount];
    
    NSArray *NIMlistArr = [[NIMSDK sharedSDK].conversationManager.allRecentSessions mutableCopy];
    NSMutableArray *sessionList = [NSMutableArray array];
    for (NIMRecentSession *recent in NIMlistArr) {
        
        
        if (recent.session.sessionType == NIMSessionTypeP2P) {
            NSMutableDictionary *dic = [NSMutableDictionary dictionary];
            [dic setObject:recent.session.sessionId forKey:@"contactId"];
            [dic setObject:[NSString stringWithFormat:@"%ld", recent.session.sessionType] forKey:@"sessionType"];
            //未读
            [dic setObject:[NSString stringWithFormat:@"%ld", recent.unreadCount] forKey:@"unreadCount"];
            //群组名称或者聊天对象名称
            [dic setObject:[NSString stringWithFormat:@"%@", [self nameForRecentSession:recent] ] forKey:@"name"];
            //账号
            [dic setObject:[NSString stringWithFormat:@"%@",recent.lastMessage.from] forKey:@"account"];
            //消息类型
            [dic setObject:[NSString stringWithFormat:@"%ld", recent.lastMessage.messageType] forKey:@"msgType"];
            //消息状态
            [dic setObject:[NSString stringWithFormat:@"%ld", recent.lastMessage.deliveryState] forKey:@"msgStatus"];
            //消息ID
            [dic setObject:[NSString stringWithFormat:@"%@", recent.lastMessage.messageId] forKey:@"messageId"];
            //消息内容
            [dic setObject:[NSString stringWithFormat:@"%@", [self contentForRecentSession:recent] ] forKey:@"content"];
            //发送时间
            [dic setObject:[NSString stringWithFormat:@"%@", [self timestampDescriptionForRecentSession:recent] ] forKey:@"time"];
            
            [dic setObject:[NSString stringWithFormat:@"%@", [self imageUrlForRecentSession:recent] ?  [self imageUrlForRecentSession:recent] : @""] forKey:@"imagePath"];
            [sessionList addObject:dic];
            
        }
        else{
            if ( [[NIMSDK sharedSDK].teamManager isMyTeam:recent.lastMessage.session.sessionId]) {
                NSMutableDictionary *dic = [NSMutableDictionary dictionary];
                [dic setObject:recent.session.sessionId forKey:@"contactId"];
                [dic setObject:[NSString stringWithFormat:@"%ld", recent.session.sessionType] forKey:@"sessionType"];
                //未读
                [dic setObject:[NSString stringWithFormat:@"%ld", recent.unreadCount] forKey:@"unreadCount"];
                //群组名称或者聊天对象名称
                [dic setObject:[NSString stringWithFormat:@"%@", [self nameForRecentSession:recent] ] forKey:@"name"];
                //账号
                [dic setObject:recent.lastMessage.from forKey:@"account"];
                //消息类型
                [dic setObject:[NSString stringWithFormat:@"%ld", recent.lastMessage.messageType] forKey:@"msgType"];
                //消息状态
                [dic setObject:[NSString stringWithFormat:@"%ld", recent.lastMessage.deliveryState] forKey:@"msgStatus"];
                //消息ID
                [dic setObject:[NSString stringWithFormat:@"%@", recent.lastMessage.messageId] forKey:@"messageId"];
                //消息内容
                [dic setObject:[NSString stringWithFormat:@"%@", [self contentForRecentSession:recent] ] forKey:@"content"];
                //发送时间
                [dic setObject:[NSString stringWithFormat:@"%@", [self timestampDescriptionForRecentSession:recent] ] forKey:@"time"];
                
                [dic setObject:[NSString stringWithFormat:@"%@", [self imageUrlForRecentSession:recent] ?  [self imageUrlForRecentSession:recent] : @""] forKey:@"imagePath"];
                NIMTeam *team = [[[NIMSDK sharedSDK] teamManager]teamById:recent.lastMessage.session.sessionId];
                [dic setObject:[NSString stringWithFormat:@"%ld",team.memberNumber] forKey:@"memberCount"];
                [sessionList addObject:dic];
                
            }
        }
    }
    
    [NIMModel initShareMD].recentListArr = sessionList;
}
//会话标题
- (NSString *)nameForRecentSession:(NIMRecentSession *)recent{
    if (recent.session.sessionType == NIMSessionTypeP2P) {
        return [NIMKitUtil showNick:recent.session.sessionId inSession:recent.session];
    }else{
        NIMTeam *team = [[NIMSDK sharedSDK].teamManager teamById:recent.session.sessionId];
        return team.teamName;
    }
}
//会话头像
-(NSString *)imageUrlForRecentSession:(NIMRecentSession *)recent{
    NIMKitInfo *info = nil;
    if (recent.session.sessionType == NIMSessionTypeTeam)
    {
        info = [[NIMKit sharedKit] infoByTeam:recent.session.sessionId option:nil];
    }
    else
    {
        NIMKitInfoFetchOption *option = [[NIMKitInfoFetchOption alloc] init];
        option.session = recent.session;
        info = [[NIMKit sharedKit] infoByUser:recent.session.sessionId option:option];
    }
    NSURL *url = info.avatarUrlString ? [NSURL URLWithString:info.avatarUrlString] : nil;
    return url;
}
//会话内容
- (NSString *)contentForRecentSession:(NIMRecentSession *)recent{
    NSString *content = [self messageContent:recent.lastMessage];
    return content;
}
//会话时间
- (NSString *)timestampDescriptionForRecentSession:(NIMRecentSession *)recent{
    return [NIMKitUtil showTime:recent.lastMessage.timestamp showDetail:NO];
}

- (NSString *)messageContent:(NIMMessage*)lastMessage{
    NSString *text = @"";
    switch (lastMessage.messageType) {
        case NIMMessageTypeText:
            text = lastMessage.text;
            break;
        case NIMMessageTypeAudio:
            text = @"[语音]";
            break;
        case NIMMessageTypeImage:
            text = @"[图片]";
            break;
        case NIMMessageTypeVideo:
            text = @"[视频]";
            break;
        case NIMMessageTypeLocation:
            text = @"[位置]";
            break;
        case NIMMessageTypeNotification:{
            return [self notificationMessageContent:lastMessage];
        }
        case NIMMessageTypeFile:
            text = @"[文件]";
            break;
        case NIMMessageTypeTip:
            text = lastMessage.text;
            break;
        case NIMMessageTypeCustom:{
            text = [self getCustomType:lastMessage];
        }
            break;
        default:
            text = @"[未知消息]";
    }
    if (lastMessage.session.sessionType == NIMSessionTypeP2P || lastMessage.messageType == NIMMessageTypeTip) {
        return text;
    }else{
        NSString *nickName = [NIMKitUtil showNick:lastMessage.from inSession:lastMessage.session];
        return nickName.length ? [nickName stringByAppendingFormat:@" : %@",text] : @"";
    }
}
//获得数据类型
- (NSString *)getCustomType:(NIMMessage *)message{
    NIMCustomObject *customObject = message.messageObject;
    DWCustomAttachment *obj = customObject.attachment;
    NSString *text = @"[未知消息]";
    if (obj) {
        switch (obj.custType) {
            case CustomMessgeTypeRedpacket: //红包
            {
                text = [NSString stringWithFormat:@"[红包]%@", [obj.dataDict objectForKey:@"comments"]];
            }
                break;
            case CustomMessgeTypeBankTransfer: //转账
            {
                text = [NSString stringWithFormat:@"[转账]%@", [obj.dataDict objectForKey:@"comments"]];
            }
                break;
            case CustomMessgeTypeUrl: //链接
            {
               text = [obj.dataDict objectForKey:@"title"];
            }
                break;
            case CustomMessgeTypeAccountNotice: //账户通知
            {
                text = [obj.dataDict objectForKey:@"title"];
            }
                break;
            case CustomMessgeTypeRedPacketOpenMessage: //拆红包
            {
                text = [self dealWithData:obj.dataDict];
            }
                break;
            default:
                text = @"[未知消息]";
                break;
        }
    }
    return text;
}

//处理拆红包消息
- (NSString *)dealWithData:(NSDictionary *)dict{
    NSString *strOpenId = [dict objectForKey:@"openId"];
    NSString *strSendId = [dict objectForKey:@"sendId"];
    NSString *strMyId = [NIMSDK sharedSDK].loginManager.currentAccount;
    NSString *strContent = @"";
    NSString *lastString = @"";
    NSInteger hasRedPacket = [[dict objectForKey:@"hasRedPacket"] integerValue];
    if (hasRedPacket == 1) {//红包已领完
        lastString = @"，你的红包已被领完";
    }
    if ([strOpenId isEqualToString:strMyId]&&[strSendId isEqualToString:strMyId]) {
        strContent = [NSString stringWithFormat:@"你领取了自己发的红包%@",lastString ];
    }else if ([strOpenId isEqualToString:strMyId]){
        NSString *strSendName = [dict objectForKey:@"sendName"];
        strContent = [NSString stringWithFormat:@"你领取了%@的红包",strSendName];
    }else if([strSendId isEqualToString:strMyId]){
        NSString *strOpenName = [dict objectForKey:@"openName"];
        strContent = [NSString stringWithFormat:@"%@领取了你的红包%@",strOpenName,lastString];
    }
    return strContent;
}

- (NSString *)notificationMessageContent:(NIMMessage *)lastMessage{
    NIMNotificationObject *object = lastMessage.messageObject;
    if (object.notificationType == NIMNotificationTypeNetCall) {
        NIMNetCallNotificationContent *content = (NIMNetCallNotificationContent *)object.content;
        if (content.callType == NIMNetCallTypeAudio) {
            return @"[网络通话]";
        }
        return @"[视频聊天]";
    }
    if (object.notificationType == NIMNotificationTypeTeam) {
        NIMTeam *team = [[NIMSDK sharedSDK].teamManager teamById:lastMessage.session.sessionId];
        if (team.type == NIMTeamTypeNormal) {
            return @"[讨论组信息更新]";
        }else{
            return @"[群信息更新]";
        }
    }
    return @"[未知消息]";
}

- (void)dealloc{
    [[NIMSDK sharedSDK].loginManager removeDelegate:self];
}

@end
