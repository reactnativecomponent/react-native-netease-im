//
//  NIMViewController.m
//  NIM
//
//  Created by Dowin on 2017/5/8.
//  Copyright © 2017年 Dowin. All rights reserved.
//

#import "NIMViewController.h"
#import "ContactViewController.h"
#import "ConversationViewController.h"

@interface NIMViewController ()<NIMLoginManagerDelegate,NIMConversationManagerDelegate>{
//    BOOL isLoginFailed;
}

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
    NSString *strStatus = @"0";
    switch (step) {
        case NIMLoginStepLinking://连接服务器
            strStatus = @"3";
            break;
        case NIMLoginStepLinkOK://连接服务器成功
            strStatus = @"5";
//            [self backLogin];
            [self getResouces];
            break;
        case NIMLoginStepLinkFailed://连接服务器失败
            strStatus = @"2";
            break;
        case NIMLoginStepLogining://登录
            strStatus = @"4";
            break;
        case NIMLoginStepLoginOK://登录成功
            strStatus = @"6";
            break;
        case NIMLoginStepLoginFailed://登录失败
            strStatus = @"10";
//            isLoginFailed = YES;
            break;
        case NIMLoginStepSyncing://开始同步
            strStatus = @"13";
            [self getResouces];
            break;
        case NIMLoginStepSyncOK://同步完成
            strStatus = @"14";
            [self getResouces];
            break;
        case NIMLoginStepLoseConnection://连接断开
            strStatus = @"2";
            break;
        case NIMLoginStepNetChanged://网络切换
            strStatus = @"15";
            break;
        default:
            break;
    }
//    NSLog(@"--------------------%@",strStatus);
    [NIMModel initShareMD].NetStatus = strStatus;
}
//删除一行
-(void)deleteCurrentSession:(NSString *)recentContactId andback:(ERROR)error{
    NSArray *NIMlistArr = [[NIMSDK sharedSDK].conversationManager.allRecentSessions mutableCopy];
    for (NIMRecentSession *recent in NIMlistArr) {
        if ([recent.session.sessionId isEqualToString:recentContactId]) {
            id<NIMConversationManager> manager = [[NIMSDK sharedSDK] conversationManager];
            //            [manager deleteRecentSession:recent];
            NIMDeleteMessagesOption *option = [[NIMDeleteMessagesOption alloc]init];
            option.removeSession = YES;
            [manager deleteAllmessagesInSession:recent.session option:option];
            //清除历史记录
            [self getResouces];
        }
    }
}
/*
//登录失败后重新手动登录
- (void)backLogin{
    if (isLoginFailed) {
        isLoginFailed = NO;
        NSLog(@":%@   :%@",_strAccount,_strToken);
        [[NIMSDK sharedSDK].loginManager login:_strAccount token:_strToken completion:^(NSError * _Nullable error) {
            NSLog(@"error:%@",error);
        }];
    }
}*/

#pragma NIMLoginManagerDelegate
-(void)onKick:(NIMKickReason)code clientType:(NIMLoginClientType)clientType
{

        switch (code) {
            case NIMKickReasonByClient:{//被另外一个客户端踢下线 (互斥客户端一端登录挤掉上一个登录中的客户端)
                [NIMModel initShareMD].NIMKick = @"1";
            }
                break;
            case NIMKickReasonByClientManually:{//被另外一个客户端手动选择踢下线
                [NIMModel initShareMD].NIMKick = @"3";
            }
                break;
            case NIMKickReasonByServer:{//你被服务器踢下线
                [NIMModel initShareMD].NIMKick = @"2";
            }
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
//删除所有会话回调
- (void)allMessagesDeleted{
    [self getResouces];
}

- (NSString *)getMessageType:(NIMMessageType)messageType{
    NSString *result = @"";
    switch (messageType) {
        case NIMMessageTypeText:
            result = @"text";
            break;
        case NIMMessageTypeImage:
            result = @"image";
            break;
        case NIMMessageTypeAudio:
            result = @"voice";
            break;
        case NIMMessageTypeVideo:
            result = @"video";
            break;
        case NIMMessageTypeLocation:
            result = @"location";
            break;
        case NIMMessageTypeNotification:
            result = @"notification";
            break;
        case NIMMessageTypeTip:
            result = @"tip";
            break;
        case NIMMessageTypeRobot:
            result = @"robot";
            break;
        case NIMMessageTypeRtcCallRecord:
            result = @"callRecord";
            break;
        case NIMMessageTypeCustom:
            result = @"custom";
            break;
            
        default:
            break;
    }
    
    return result;
};



-(void)getRecentContactListsuccess:(SUCCESS)suc andError:(ERROR)err{
    NSArray *NIMlistArr = [[NIMSDK sharedSDK].conversationManager.allRecentSessions mutableCopy];
    NSMutableArray *sessionList = [NSMutableArray array];
    for (NIMRecentSession *recent in NIMlistArr) {
        NSMutableDictionary *dic = [NSMutableDictionary dictionary];
        [dic setObject:[NSString stringWithFormat:@"%@",recent.session.sessionId] forKey:@"contactId"];
        [dic setObject:[NSString stringWithFormat:@"%zd", recent.session.sessionType] forKey:@"sessionType"];
        //未读
        [dic setObject:[NSString stringWithFormat:@"%zd", recent.unreadCount] forKey:@"unreadCount"];
        //群组名称或者聊天对象名称
        [dic setObject:[NSString stringWithFormat:@"%@", [self nameForRecentSession:recent] ] forKey:@"name"];
        //账号
        [dic setObject:[NSString stringWithFormat:@"%@", recent.lastMessage.from] forKey:@"account"];
        //消息类型
        [dic setObject:[NSString stringWithFormat:@"%@", [self getMessageType: recent.lastMessage.messageType]] forKey:@"msgType"];
        //消息状态
        [dic setObject:[NSString stringWithFormat:@"%zd", recent.lastMessage.deliveryState] forKey:@"msgStatus"];
        //消息ID
        [dic setObject:[NSString stringWithFormat:@"%@", recent.lastMessage.messageId] forKey:@"messageId"];
        //消息内容
        [dic setObject:[NSString stringWithFormat:@"%@", [self contentForRecentSession:recent] ] forKey:@"content"];

        if (recent.lastMessage.messageType == NIMMessageTypeNotification) {           
        // if message type is NIMNotificationTypeTeam/NIMNotificationTypeChatroom/NIMNotificationTypeNetCall
        [dic setObject:[[ConversationViewController initWithConversationViewController]setNotiTeamObj:recent.lastMessage] forKey:@"extend"];        
        }

        //发送时间
        [dic setObject:[NSString stringWithFormat:@"%f", recent.lastMessage.timestamp * 1000] forKey:@"time"];
        
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
    
//    NSString *currentAccout = [[NIMSDK sharedSDK].loginManager currentAccount];
    NSInteger allUnreadNum = 0;
    NSArray *NIMlistArr = [[NIMSDK sharedSDK].conversationManager.allRecentSessions mutableCopy];
    NSMutableArray *sessionList = [NSMutableArray array];
    for (NIMRecentSession *recent in NIMlistArr) {
        
        
        if (recent.session.sessionType == NIMSessionTypeP2P) {
            NSMutableDictionary *dic = [NSMutableDictionary dictionary];
            [dic setObject:[NSString stringWithFormat:@"%@",recent.session.sessionId] forKey:@"contactId"];
            [dic setObject:[NSString stringWithFormat:@"%zd", recent.session.sessionType] forKey:@"sessionType"];
            //未读
            NSString *strUnreadCount = [NSString stringWithFormat:@"%ld", recent.unreadCount];
            allUnreadNum = allUnreadNum + [strUnreadCount integerValue];
            [dic setObject:strUnreadCount forKey:@"unreadCount"];
            //群组名称或者聊天对象名称
            [dic setObject:[NSString stringWithFormat:@"%@", [self nameForRecentSession:recent] ] forKey:@"name"];
            //账号
            [dic setObject:[NSString stringWithFormat:@"%@",recent.lastMessage.session.sessionId] forKey:@"account"];
            //消息类型
            [dic setObject:[NSString stringWithFormat:@"%@", [self getMessageType: recent.lastMessage.messageType]] forKey:@"msgType"];
            //消息状态
            [dic setObject:[NSString stringWithFormat:@"%zd", recent.lastMessage.deliveryState] forKey:@"msgStatus"];
            //消息ID
            [dic setObject:[NSString stringWithFormat:@"%@", recent.lastMessage.messageId] forKey:@"messageId"];
            //消息内容
            [dic setObject:[NSString stringWithFormat:@"%@", [self contentForRecentSession:recent] ] forKey:@"content"];
            //发送时间
            [dic setObject:[NSString stringWithFormat:@"%f", recent.lastMessage.timestamp * 1000] forKey:@"time"];

            [dic setObject:[NSString stringWithFormat:@"%@", [self imageUrlForRecentSession:recent] ?  [self imageUrlForRecentSession:recent] : @""] forKey:@"imagePath"];
            NIMUser *user = [[NIMSDK sharedSDK].userManager userInfo:recent.lastMessage.session.sessionId];
            NSString *strMute = user.notifyForNewMsg?@"1":@"0";
            [dic setObject:strMute forKey:@"mute"];
            [sessionList addObject:dic];
            
        }
        else{
            if ( [[NIMSDK sharedSDK].teamManager isMyTeam:recent.lastMessage.session.sessionId]) {
                NSMutableDictionary *dic = [NSMutableDictionary dictionary];
                [dic setObject:[NSString stringWithFormat:@"%@",recent.session.sessionId] forKey:@"contactId"];
                [dic setObject:[NSString stringWithFormat:@"%zd", recent.session.sessionType] forKey:@"sessionType"];
                //未读
                NSString *strUnreadCount = [NSString stringWithFormat:@"%zd", recent.unreadCount];
                allUnreadNum = allUnreadNum + [strUnreadCount integerValue];
                [dic setObject:strUnreadCount forKey:@"unreadCount"];
                //群组名称或者聊天对象名称
                [dic setObject:[NSString stringWithFormat:@"%@", [self nameForRecentSession:recent] ] forKey:@"name"];
                //账号
                [dic setObject:[NSString stringWithFormat:@"%@", recent.lastMessage.from] forKey:@"account"];
                //消息类型
                [dic setObject:[NSString stringWithFormat:@"%@", [self getMessageType: recent.lastMessage.messageType]] forKey:@"msgType"];
                //消息状态
                [dic setObject:[NSString stringWithFormat:@"%zd", recent.lastMessage.deliveryState] forKey:@"msgStatus"];
                //消息ID
                [dic setObject:[NSString stringWithFormat:@"%@", recent.lastMessage.messageId] forKey:@"messageId"];
                //消息内容
                [dic setObject:[NSString stringWithFormat:@"%@", [self contentForRecentSession:recent] ] forKey:@"content"];

                if (recent.lastMessage.messageType == NIMMessageTypeNotification) {           
                // if message type is NIMNotificationTypeTeam/NIMNotificationTypeChatroom/NIMNotificationTypeNetCall
                    [dic setObject:[[ConversationViewController initWithConversationViewController]setNotiTeamObj:recent.lastMessage] forKey:@"extend"];        
                }
                //发送时间
                [dic setObject:[NSString stringWithFormat:@"%f", recent.lastMessage.timestamp * 1000 ] forKey:@"time"];

                [dic setObject:[NSString stringWithFormat:@"%@", [self imageUrlForRecentSession:recent] ?  [self imageUrlForRecentSession:recent] : @""] forKey:@"imagePath"];
                NIMTeam *team = [[[NIMSDK sharedSDK] teamManager]teamById:recent.lastMessage.session.sessionId];
                [dic setObject:[NSString stringWithFormat:@"%zd",team.memberNumber] forKey:@"memberCount"];
                NSString *strMute = team.notifyStateForNewMsg == NIMTeamNotifyStateAll ? @"1" : @"0";
                [dic setObject:strMute forKey:@"mute"];
                [sessionList addObject:dic];
                
            }
        }
    }
    
    NSDictionary *recentDict = @{@"recents":sessionList,@"unreadCount":[NSString stringWithFormat:@"%zd",allUnreadNum]};
    [NIMModel initShareMD].recentDict = recentDict;
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
    if ((lastMessage.session.sessionType == NIMSessionTypeP2P) || (lastMessage.messageType == NIMMessageTypeTip)||([lastMessage.from isEqualToString:[NIMSDK sharedSDK].loginManager.currentAccount]) ) {
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
            case CustomMessgeTypeBusinessCard: //名片
            {
                if([message.from isEqualToString:[NIMSDK sharedSDK].loginManager.currentAccount]){//如果是自己
                    text = [NSString stringWithFormat:@"你推荐了%@", [obj.dataDict objectForKey:@"name"]];
                }else{
                    text = [NSString stringWithFormat:@"向你推荐了%@", [obj.dataDict objectForKey:@"name"]];
                }
            }
                break;
            case CustomMessgeTypeCustom: //自定义
            {
                text = [self dealWithCustomData:obj.dataDict];
            }
                break;
            default:
                text = @"[未知消息]";
                break;
        }
    }
    return text;
}

//处理自定义消息
- (NSString *)dealWithCustomData:(NSDictionary *)dict{
    NSString *recentContent = [self stringFromKey:@"recentContent" andDict:dict];
    return recentContent;
}

//处理拆红包消息
- (NSString *)dealWithData:(NSDictionary *)dict{
    NSString *strOpenId = [self stringFromKey:@"openId" andDict:dict];
    NSString *strSendId = [self stringFromKey:@"sendId" andDict:dict];
    NSString *strMyId = [NIMSDK sharedSDK].loginManager.currentAccount;
    NSString *strContent = @"";

    if ([strOpenId isEqualToString:strMyId]&&[strSendId isEqualToString:strMyId]) {
        strContent = [NSString stringWithFormat:@"你领取了自己发的红包" ];
    }else if ([strOpenId isEqualToString:strMyId]){
        NSString *strSendName = [self getUserName:strSendId];
        strContent = [NSString stringWithFormat:@"你领取了%@的红包",strSendName];
    }else if([strSendId isEqualToString:strMyId]){
        NSString *strOpenName = [self getUserName:strOpenId];
        strContent = [NSString stringWithFormat:@"%@领取了你的红包",strOpenName];
    }
//    else{
//        NSString *strSenderName = [self getUserName:strSendId];
//        NSString *strOpenName = [self getUserName:strOpenId];
//        strContent = [NSString stringWithFormat:@"%@领取了%@的红包",strOpenName,strSenderName];
//    }
    return strContent;
}

- (NSString *)getUserName:(NSString *)userID{
    NSString *strTmpName = @"";
    NIMUser *user = [[NIMSDK sharedSDK].userManager userInfo:userID];
    strTmpName = user.alias;
    if (![strTmpName length]) {
        strTmpName = user.userInfo.nickName;
    }
    if (![strTmpName length]) {//从服务器获取
        [[ContactViewController initWithContactViewController]fetchUserInfos:userID Success:^(id param) {

        } error:^(NSString *error) {
            
        }];
        strTmpName = userID;
    }
    return strTmpName;
}

- (NSString *)stringFromKey:(NSString *)strKey andDict:(NSDictionary *)dict{
    NSString *text = [dict objectForKey:strKey];
    return text?text:@" ";
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
        NSString *strContent = [NIMKitUtil teamNotificationFormatedMessage:lastMessage];
        return strContent;
    }
    return @"[未知消息]";
}

- (void)dealloc{
    [[NIMSDK sharedSDK].loginManager removeDelegate:self];
}

@end
