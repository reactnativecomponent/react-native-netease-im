//
//  ConversationViewController.m
//  NIM
//
//  Created by Dowin on 2017/5/5.
//  Copyright © 2017年 Dowin. All rights reserved.
//

#import "ConversationViewController.h"
#import <Photos/PhotosTypes.h>
#import "NIMMessageMaker.h"
#import "ContactViewController.h"
#import "NIMKitLocationPoint.h"
#import <AVFoundation/AVFoundation.h>
#define NTESNotifyID        @"id"
#define NTESCustomContent  @"content"

#define NTESCommandTyping  (1)
#define NTESCustom         (2)
#import "NSDictionary+NTESJson.h"
@interface ConversationViewController ()<NIMMediaManagerDelegate,NIMMediaManagerDelegate,NIMSystemNotificationManagerDelegate>{
    NSString *_sessionID;
    NSString *_type;
    NSInteger _index;
    NIMSession *_session;
    NSMutableArray *_sessionArr;
    
}

@end

@implementation ConversationViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
}

+(instancetype)initWithConversationViewController{
    static ConversationViewController *conVC = nil;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        conVC = [[ConversationViewController alloc]init];
        
    });
    return conVC;
}

-(void)startSession:(NSString *)sessionID withType:(NSString *)type{
    _sessionID = sessionID;
    _type = type;
    _session = [NIMSession session:_sessionID type:[_type integerValue]];
    _sessionArr = [NSMutableArray array];
    [self addListener];
}
//本地历史记录
-(void)localSessionList:(NSString *)sessionId sessionType:(NSString *)sessionType timeLong:(NSString *)timeLong direction:(NSString *)direction limit:(NSString *)limit asc:(BOOL)asc success:(Success)succe{
    // NIMMessageSearchOption *option = [[NIMMessageSearchOption alloc]init];
    
    
}

-(void)localSession:(NSInteger)index cerrentmessageId:(NSString *)currentMessageID success:(Success)succe err:(Errors)err{
    _index = index;
    [[NIMSDK sharedSDK].conversationManager markAllMessagesReadInSession:_session];
    if (currentMessageID.length != 0) {
        
        NSArray *currentMessage = [[[NIMSDK sharedSDK] conversationManager] messagesInSession:_session messageIds:@[currentMessageID] ];
        NIMMessage *currentM = currentMessage[0];
        NSArray *messageArr =  [[[NIMSDK sharedSDK] conversationManager]messagesInSession:_session message:currentM limit: index];
        if (messageArr.count != 0) {
            succe([self setTimeArr:messageArr]);
        }else{
           
            err(@"暂无更多");
        }
        
    }
    else{
        NSArray *messageArr =  [[[NIMSDK sharedSDK] conversationManager]messagesInSession:_session message:nil limit: index];
        if ([self setTimeArr:messageArr].count != 0) {
            NSMutableDictionary *dic = [[self setTimeArr:messageArr] objectAtIndex:[self setTimeArr:messageArr].count - 1];
            [[NSUserDefaults standardUserDefaults]setObject:[dic objectForKey:@"time"] forKey:@"timestamp"];
        }
        succe([self setTimeArr:messageArr]);
    }
    
    
}

-(NSMutableArray *)setTimeArr:(NSArray *)messageArr{
    NSMutableArray *sourcesArr = [NSMutableArray array];
    for (NIMMessage *message in messageArr) {
        NSMutableDictionary *dic = [NSMutableDictionary dictionary];
        NSMutableDictionary *user = [NSMutableDictionary dictionary];
        NIMUser   *messageUser = [[NIMSDK sharedSDK].userManager userInfo:message.from];
        [user setObject:[NSString stringWithFormat:@"%@",messageUser.userInfo.avatarUrl] forKey:@"avatar"];
        [user setObject:[NSString stringWithFormat:@"%@", message.senderName] forKey:@"fromNick"];
        [user setObject:[NSString stringWithFormat:@"%@", message.from] forKey:@"_id"];
        NSArray *key = [user allKeys];
        for (NSString *tem  in key) {
            if ([[user objectForKey:tem] isEqualToString:@"(null)"]) {
                [user setObject:@"" forKey:tem];
            }
        }
        [dic setObject:[NSString stringWithFormat:@"%@", message.text] forKey:@"content"];
        [dic setObject:[NSString stringWithFormat:@"%@", message.session.sessionId] forKey:@"sessionId"];
        [dic setObject:[NSString stringWithFormat:@"%ld", message.session.sessionType] forKey:@"sessionType"];
        [dic setObject:[NSString stringWithFormat:@"%ld", message.messageType] forKey:@"msgType"];
        switch (message.deliveryState) {
            case NIMMessageDeliveryStateFailed:
                [dic setObject:@"2" forKey:@"status"];
                break;
            case NIMMessageDeliveryStateDelivering:
                [dic setObject:@"0" forKey:@"status"];
                break;
            case NIMMessageDeliveryStateDeliveried:
                [dic setObject:@"1" forKey:@"status"];
                break;
            default:
                [dic setObject:@"-1" forKey:@"status"];
                break;
        }
        [dic setObject:[NSString stringWithFormat:@"%d", message.isOutgoingMsg] forKey:@"direct"];
        [dic setObject:[NSString stringWithFormat:@"%f", message.timestamp] forKey:@"createdAt"];
        [dic setObject:[NSString stringWithFormat:@"%@", message.messageId] forKey:@"_id"];
        [dic setObject:[NSString stringWithFormat:@"%d",message.isRemoteRead] forKey:@"isRemoteRead"];
      
        NSArray *keys = [dic allKeys];
        for (NSString *tem  in keys) {
            if ([[dic objectForKey:tem] isEqualToString:@"(null)"]) {
                [dic setObject:@"" forKey:tem];
            }
        }
        if (message.messageType  == 1) {
            NIMImageObject *object = message.messageObject;
            NSMutableDictionary *imageObj = [NSMutableDictionary dictionary];
            [imageObj setObject:[NSString stringWithFormat:@"%@", [object thumbPath] ] forKey:@"thumbPath"];
            [imageObj setObject:[NSString stringWithFormat:@"%@",[object url] ] forKey:@"url"];
            [imageObj setObject:[NSString stringWithFormat:@"%@",[object displayName] ] forKey:@"displayName"];
            [imageObj setObject:[NSString stringWithFormat:@"%f",[object size].height] forKey:@"imageHeight"];
            [imageObj setObject:[NSString stringWithFormat:@"%f",[object size].width] forKey:@"imageWidth"];
            NSArray *keys = [imageObj allKeys];
            for (NSString *tem  in keys) {
                if ([[imageObj objectForKey:tem] isEqualToString:@"(null)"]) {
                    [imageObj setObject:@"" forKey:tem];
                }
            }
            [dic setObject:imageObj forKey:@"imageObj"];
        }
        if(message.messageType == 2){
            NSMutableDictionary *audioObj = [NSMutableDictionary dictionary];
            NIMAudioObject *object = message.messageObject;
            [audioObj setObject:[NSString stringWithFormat:@"%@",object.path] forKey:@"path"];
            [audioObj setObject:[NSString stringWithFormat:@"%@",object.url] forKey:@"url"];
            [audioObj setObject:[NSString stringWithFormat:@"%ld",object.duration] forKey:@"duration"];
            NSArray *keys = [audioObj allKeys];
            for (NSString *tem  in keys) {
                if ([[audioObj objectForKey:tem] isEqualToString:@"(null)"]) {
                    [audioObj setObject:@"" forKey:tem];
                }
            }
            [dic setObject:audioObj forKey:@"audioObj"];
        }
        if(message.messageType == 3 ){
            NSMutableDictionary *VideoObj = [NSMutableDictionary dictionary];
            NIMVideoObject *object = message.messageObject;
            [VideoObj setObject:[NSString stringWithFormat:@"%@",object.url ] forKey:@"url"];
            [VideoObj setObject:[NSString stringWithFormat:@"%@", object.displayName ] forKey:@"displayName"];
            [VideoObj setObject:[NSString stringWithFormat:@"%@", object.coverUrl ] forKey:@"coverUrl"];
            [VideoObj setObject:[NSString stringWithFormat:@"%f",object.coverSize.height ] forKey:@"coverSizeHeight"];
            [VideoObj setObject:[NSString stringWithFormat:@"%f", object.coverSize.width ] forKey:@"coverSizeWidth"];
            [VideoObj setObject:[NSString stringWithFormat:@"%ld",object.duration ] forKey:@"duration"];
            [VideoObj setObject:[NSString stringWithFormat:@"%lld",object.fileLength] forKey:@"fileLength"];
            if([[NSFileManager defaultManager] fileExistsAtPath:object.coverPath]){
                [VideoObj setObject:[NSString stringWithFormat:@"%@",object.coverPath] forKey:@"coverPath"];
            }else{
                //如果封面图下跪了，点进视频的时候再去下一把封面图
                [[NIMSDK sharedSDK].resourceManager download:object.coverUrl filepath:object.coverPath progress:nil completion:^(NSError *error) {
                    if (!error) {
                        [VideoObj setObject:[NSString stringWithFormat:@"%@",object.coverPath] forKey:@"coverPath"];
                    }
                }];
            }
            if ([[NSFileManager defaultManager] fileExistsAtPath:object.path]) {
                [VideoObj setObject:[NSString stringWithFormat:@"%@",object.path] forKey:@"path"];
            }else{
                
                [[NIMObject initNIMObject] downLoadVideo:object Error:^(NSError *error) {
                    if (!error) {
                        [VideoObj setObject:[NSString stringWithFormat:@"%@",object.path] forKey:@"path"];
                    }
                } progress:^(float progress) {
                    NSLog(@"下载进度%.f",progress);
                }];
            }
            NSArray *keys = [VideoObj allKeys];
            for (NSString *tem  in keys) {
                if ([[VideoObj objectForKey:tem] isEqualToString:@"(null)"]) {
                    [VideoObj setObject:@"" forKey:tem];
                }
            }
            [dic setObject:VideoObj forKey:@"videoDic"];
        }
        if(message.messageType == 4){
            NIMLocationObject *object = message.messageObject;
            NSMutableDictionary *locationObj = [NSMutableDictionary dictionary];
            [locationObj setObject:[NSString stringWithFormat:@"%f", object.latitude ] forKey:@"latitude"];
            [locationObj setObject:[NSString stringWithFormat:@"%f", object.longitude ] forKey:@"longitude"];
            [locationObj setObject:[NSString stringWithFormat:@"%@", object.title ] forKey:@"title"];
            NSArray *keys = [locationObj allKeys];
            for (NSString *tem  in keys) {
                if ([[locationObj objectForKey:tem] isEqualToString:@"(null)"]) {
                    [locationObj setObject:@"" forKey:tem];
                }
            }
            [dic setObject:locationObj forKey:@"locationObj"];
            
        }
        if (message.messageType == 5) {
            NSMutableDictionary *notiObj = [NSMutableDictionary dictionary];
            NIMNotificationObject *object = message.messageObject;
            switch (object.notificationType) {
                case NIMNotificationTypeTeam:
                case NIMNotificationTypeChatroom:
                {
                    
                    [notiObj setObject:[NIMKitUtil messageTipContent:message] forKey:@"tipMsg"];
                    break;
                }
                case NIMNotificationTypeNetCall:{
                    [notiObj setObject:[NIMKitUtil messageTipContent:message]forKey:@"tipMsg"];
                    
                    
                    break;
                }
                default:
                    break;
            }
            [dic setObject:notiObj forKey:@"notiObj"];
            
        }
        
        if (message.messageType == 100) {
            NIMCustomObject *customObject = message.messageObject;
            DWCustomAttachment *obj = customObject.attachment;
            if (obj) {
                switch (obj.custType) {
                    case CustomMessgeTypeRedpacket: //红包
                    {
                        [dic setObject:obj.dataDict forKey:@"redPacketObj"];
                        [dic setObject:@"redpacket" forKey:@"custType"];
                    }
                        break;
                    case CustomMessgeTypeBankTransfer: //转账
                    {
                        [dic setObject:obj.dataDict  forKey:@"bankTransferObj"];
                        [dic setObject:@"transfer" forKey:@"custType"];
                    }
                        break;
                    case CustomMessgeTypeUrl: //链接
                    {
                        [dic setObject:obj.dataDict  forKey:@"urlObj"];
                        [dic setObject:@"url" forKey:@"custType"];
                    }
                        break;
                    case CustomMessgeTypeAccountNotice: //账户通知，与账户金额相关变动
                    {
                        [dic setObject:obj.dataDict  forKey:@"accountNoticeObj"];
                        [dic setObject:@"account_notice" forKey:@"custType"];
                    }
                        break;
                    case CustomMessgeTypeRedPacketOpenMessage: //拆红包消息
                    {
                        NSDictionary *dataDict = [self dealWithData:obj.dataDict];
                        if (dataDict) {
                            [dic setObject:dataDict  forKey:@"redpacketOpenObj"];
                            [dic setObject:@"redpacketOpen" forKey:@"custType"];
                        }else{

                            continue;//终止本次循环
                        }
                    }
                        break;
                    default:
                        break;
                        
                }
            }
        }
        [dic setObject:user forKey:@"user"];
        [sourcesArr addObject:dic];
    }
    
    return sourcesArr;
    
}
//取消录音
- (void)onCancelRecording
{
    [[NIMSDK sharedSDK].mediaManager cancelRecord];
}
//结束录音
- (void)onStopRecording
{
    
    [[NIMSDK sharedSDK].mediaManager stopRecord];
    
}
//开始录音
- (void)onStartRecording
{
    NIMAudioType type = NIMAudioTypeAAC;
    NSTimeInterval duration = 60.0;
    
    [[NIMSDK sharedSDK].mediaManager addDelegate:self];
    
    [[NIMSDK sharedSDK].mediaManager record:type
                                   duration:duration];
}
//开始播放录音
- (void)play:(NSString *)filepath{
    [[NIMSDK sharedSDK].mediaManager addDelegate:self];
    if (filepath) {
        [[NIMSDK sharedSDK].mediaManager play:filepath];
    }
}
//停止播放
- (void)stopPlay{
    [[NIMSDK sharedSDK].mediaManager stopPlay];
}
//发送录音
-(void)sendAudioMessage:(  NSString *)file duration:(  NSString *)duration{
    if (file) {
        [[[NIMSDK sharedSDK] chatManager] sendMessage:[NIMMessageMaker msgWithAudio:file] toSession:_session error:nil];
    }
}
//发送文字消息
-(void)sendMessage:(NSString *)mess{
    NIMMessage *message = [[NIMMessage alloc] init];
    message.text    = mess;
    message.apnsContent = mess;
    //发送消息
    [[NIMSDK sharedSDK].chatManager sendMessage:message toSession:_session error:nil];
}
//发送图片
-(void)sendImageMessages:(  NSString *)path  displayName:(  NSString *)displayName{
    
    NIMMessage *message = [NIMMessageMaker msgWithImagePath:path];
    [[NIMSDK sharedSDK].chatManager sendMessage:message toSession:_session error:nil];
}

//发送视频
-(void)sendTextMessage:(  NSString *)path duration:(  NSString *)duration width:(  NSString *)width height:(  NSString *)height displayName:(  NSString *)displayName{
    NIMMessage *message;
    //    if (image) {
    //        message = [NIMMessageMaker msgWithImage:image];
    //    }else{
    message = [NIMMessageMaker msgWithVideo:path];
    //    }
    [[NIMSDK sharedSDK].chatManager sendMessage:message toSession:_session error:nil];
    
}

//发送自定义消息
-(void)sendCustomMessage:(  NSString *)attachment config:(  NSString *)config{
    NIMMessage *message;
    NIMObject *obj = [NIMObject initNIMObject];
    obj.attachment =attachment;
    message = [NIMMessageMaker msgWithCustom:obj];
    [[NIMSDK sharedSDK].chatManager sendMessage:message toSession:_session error:nil];
}

//发送地理位置消息
-(void)sendLocationMessage:(  NSString *)latitude longitude:(  NSString *)longitude address:(  NSString *)address{
    NIMLocationObject *locaObj = [[NIMLocationObject alloc]initWithLatitude:[latitude doubleValue] longitude:[longitude doubleValue] title:address];
    NIMKitLocationPoint *locationPoint = [[NIMKitLocationPoint alloc]initWithLocationObject:locaObj];
    NIMMessage *message = [NIMMessageMaker msgWithLocation:locationPoint];
    [[NIMSDK sharedSDK].chatManager sendMessage:message toSession:_session error:nil];
}
//发送自定义消息2
-(void)sendCustomMessage:(NSInteger )custType data:(NSDictionary *)dataDict{
    NIMMessage *message;
    DWCustomAttachment *obj = [[DWCustomAttachment alloc]init];
    obj.custType = custType;
    obj.dataDict = dataDict;
    message = [NIMMessageMaker msgWithCustomAttachment:obj];
    [[NIMSDK sharedSDK].chatManager sendMessage:message toSession:_session error:nil];
}

//发送提醒消息
-(void)sendTipMessage:( NSString *)content{
    
}
//- (NIMKitMediaFetcher *)mediaFetcher
//{
//    if (!_mediaFetcher) {
//        _mediaFetcher = [[NIMKitMediaFetcher alloc] init];
//    }
//    return _mediaFetcher;
//}

//发送红包消息
- (void)sendRedPacketMessage:(NSString *)type comments:(NSString *)comments serialNo:(NSString *)serialNo{
    NSDictionary *dict = @{@"type":type,@"comments":comments,@"serialNo":serialNo};
    [self sendCustomMessage:CustomMessgeTypeRedpacket data:dict];
}
//发送转账消息
- (void)sendBankTransferMessage:(NSString *)amount comments:(NSString *)comments serialNo:(NSString *)serialNo{
    NSDictionary *dict = @{@"amount":amount,@"comments":comments,@"serialNo":serialNo};
    [self sendCustomMessage:CustomMessgeTypeBankTransfer data:dict];
}

//发送拆红包消息
-(void)sendRedPacketOpenMessage:(NSString *)sendId hasRedPacket:(NSString *)hasRedPacket serialNo:(NSString *)serialNo{
    NSString *strMyId = [NIMSDK sharedSDK].loginManager.currentAccount;
    NSDictionary *dict = @{@"sendId":sendId,@"openId":strMyId,@"hasRedPacket":hasRedPacket,@"serialNo":serialNo};
    [self sendCustomMessage:CustomMessgeTypeRedPacketOpenMessage data:dict];

}



//设置好友消息提醒
-(void)muteMessage:(NSString *)contactId mute:(NSString *)mute Succ:(Success)succ Err:(Errors)err{
    BOOL on;
    if ([mute isEqualToString:@"1"]) {
        on = true;
    }else{
        on = false;
    }
    [[NIMSDK sharedSDK].userManager updateNotifyState:on forUser:contactId completion:^(NSError *error) {
        if (!error) {
            succ(@"200");
        }else{
            err(@"操作失败");
        }
    }];
}

#pragma mark - NIMChatManagerDelegate

- (void)willSendMessage:(NIMMessage *)message
{
    [self refrashMessage:message From:@"send"];
    NIMModel *model = [NIMModel initShareMD];
    model.startSend = @{@"start":@"true"};
}
//发送结果
- (void)sendMessage:(NIMMessage *)message didCompleteWithError:(NSError *)error
{
    if (!error) {
        [self refrashMessage:message From:@"send"];
        [[NSUserDefaults standardUserDefaults]setObject: [NSString stringWithFormat:@"%f", message.timestamp] forKey:@"timestamp"];
    }
    NIMModel *model = [NIMModel initShareMD];
    if ([[NSString stringWithFormat:@"%@", error] isEqualToString:@"(null)"]) {
        model.endSend = @{@"end":@"true",@"error":@""};
    }else{
        model.endSend = @{@"end":@"true",@"error":[NSString stringWithFormat:@"%@", error]};
    }
}

//发送进度
-(void)sendMessage:(NIMMessage *)message progress:(float)progress
{
    [self refrashMessage:message From:@"send"];
    NIMModel *model = [NIMModel initShareMD];
    model.endSend = @{@"progress":[NSString stringWithFormat:@"%f",progress]};
}


//接收消息
- (void)onRecvMessages:(NSArray *)messages
{
    NIMMessage *message = messages.firstObject;
    if ([message.session.sessionId isEqualToString:_sessionID]) {
        [self refrashMessage:message From:@"receive"];
        NIMMessageReceipt *receipt = [[NIMMessageReceipt alloc] initWithMessage:message];
        
        [[[NIMSDK sharedSDK] chatManager] sendMessageReceipt:receipt
                                                  completion:nil];
        //标记已读消息
        [[NIMSDK sharedSDK].conversationManager markAllMessagesReadInSession:_session];
    }
    
}


- (void)fetchMessageAttachment:(NIMMessage *)message progress:(float)progress
{
    NSLog(@"55555555555");
    //    if ([message.session isEqual:_session]) {
    //        [self.interactor updateMessage:message];
    //    }
}

- (void)fetchMessageAttachment:(NIMMessage *)message didCompleteWithError:(NSError *)error
{
    NSLog(@"66666666666");
    //    if ([message.session isEqual:_session]) {
    //        NIMMessageModel *model = [self.interactor findMessageModel:message];
    //        //下完缩略图之后，因为比例有变化，重新刷下宽高。
    //        [model calculateContent:self.tableView.frame.size.width force:YES];
    //        [self.interactor updateMessage:message];
    //    }
}

- (void)onRecvMessageReceipt:(NIMMessageReceipt *)receipt
{
    
    NIMModel *mode = [NIMModel initShareMD];
    mode.receipt = @"1";
}


- (void)onRecvRevokeMessageNotification:(NIMRevokeMessageNotification *)notification
{
    NSString * tip = [self tipOnMessageRevoked:notification];
    NIMMessage *tipMessage = [self msgWithTip:tip];
    NIMMessageSetting *setting = [[NIMMessageSetting alloc] init];
    setting.shouldBeCounted = NO;
    tipMessage.setting = setting;
    tipMessage.timestamp = notification.timestamp;
    
    // saveMessage 方法执行成功后会触发 onRecvMessages: 回调，但是这个回调上来的 NIMMessage 时间为服务器时间，和界面上的时间有一定出入，所以要提前先在界面上插入一个和被删消息的界面时间相符的 Tip, 当触发 onRecvMessages: 回调时，组件判断这条消息已经被插入过了，就会忽略掉。
    [[NIMSDK sharedSDK].conversationManager saveMessage:tipMessage
                                             forSession:notification.session
                                             completion:nil];
}

#pragma mark - NIMMediaManagerDelegate
- (void)recordAudio:(NSString *)filePath didBeganWithError:(NSError *)error {
    if (!filePath || error) {
        [self onRecordFailed:error];
    }
}

- (void)recordAudio:(NSString *)filePath didCompletedWithError:(NSError *)error {
    if(!error) {
        if ([self recordFileCanBeSend:filePath]) {
            [[[NIMSDK sharedSDK] chatManager] sendMessage:[NIMMessageMaker msgWithAudio:filePath] toSession:_session error:nil];
        }else{
            [self showRecordFileNotSendReason];
        }
    } else {
        NSLog(@"^^^^%@",error);
    }
}

- (void)recordAudioDidCancelled {
    
}
//监听录音状态
- (void)recordAudioProgress:(NSTimeInterval)currentTime{
    NIMModel *model = [NIMModel initShareMD];
    NSDictionary *Audic = @{@"currentTime":[NSString stringWithFormat:@"%f",currentTime],@"recordPower":[NSString stringWithFormat:@"%f",[[NIMSDK sharedSDK].mediaManager recordPeakPower]]};
    NSLog(@"---------%@",Audic);
    model.audioDic = Audic;
}

//播发结束回调
- (void)playAudio:(NSString *)filePath didCompletedWithError:(nullable NSError *)error{
    if(!error) {
        NIMModel *model = [NIMModel initShareMD];
        NSDictionary *Audic = @{@"playEnd":@"true"};
        model.audioDic = Audic;
    } else {
        NSLog(@"%@",error);
    }
}

- (void)recordAudioInterruptionBegin {
    [[NIMSDK sharedSDK].mediaManager cancelRecord];
}
#pragma mark - 录音相关接口
- (void)onRecordFailed:(NSError *)error{}

- (BOOL)recordFileCanBeSend:(NSString *)filepath
{
    return YES;
}

- (void)showRecordFileNotSendReason{}


#pragma mark - NIMConversationManagerDelegate
- (void)messagesDeletedInSession:(NIMSession *)session{
    //    [self.interactor resetMessages];
    //    [self.tableView reloadData];
}

- (void)didAddRecentSession:(NIMRecentSession *)recentSession
           totalUnreadCount:(NSInteger)totalUnreadCount{
    [self changeUnreadCount:recentSession totalUnreadCount:totalUnreadCount];
}

- (void)didUpdateRecentSession:(NIMRecentSession *)recentSession
              totalUnreadCount:(NSInteger)totalUnreadCount{
    [self changeUnreadCount:recentSession totalUnreadCount:totalUnreadCount];
}

- (void)didRemoveRecentSession:(NIMRecentSession *)recentSession
              totalUnreadCount:(NSInteger)totalUnreadCount{
    [self changeUnreadCount:recentSession totalUnreadCount:totalUnreadCount];
}


- (void)changeUnreadCount:(NIMRecentSession *)recentSession
         totalUnreadCount:(NSInteger)totalUnreadCount{
    
    //    if ([recentSession.session isEqual:self.session]) {
    //        return;
    //    }
    //    [self changeLeftBarBadge:totalUnreadCount];
}

- (void)addListener
{
    [[NIMSDK sharedSDK].chatManager addDelegate:self];
    [[NIMSDK sharedSDK].conversationManager addDelegate:self];
    [[NIMSDK sharedSDK].systemNotificationManager addDelegate:self];
}

#pragma mark - NIMSystemNotificationManagerDelegate
- (void)onReceiveCustomSystemNotification:(NIMCustomSystemNotification *)notification
{
    if (!notification.sendToOnlineUsersOnly) {
        return;
    }
    NSData *data = [[notification content] dataUsingEncoding:NSUTF8StringEncoding];
    if (data) {
        NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:data
                                                             options:0
                                                               error:nil];
        if ([dict jsonInteger:NTESNotifyID] == NTESCommandTyping &&_session.sessionType == NIMSessionTypeP2P && [notification.sender isEqualToString:_session.sessionId])
        {
            NSLog(@"正在输入...");
        }
    }
}


-(void)refrashMessage:(NIMMessage *)message From:(NSString *)from{
    NSMutableArray *messageArr = [NSMutableArray array];
    NSMutableDictionary *dic2 = [NSMutableDictionary dictionary];
    NIMUser   *user = [[NIMSDK sharedSDK].userManager userInfo:message.from];
    NSMutableDictionary *dources = [NSMutableDictionary dictionary];
    [dources setObject:[NSString stringWithFormat:@"%@",user.userInfo.avatarUrl] forKey:@"avatar"];
    [dources setObject:[NSString stringWithFormat:@"%@", message.senderName] forKey:@"fromNick"];
    [dources setObject:[NSString stringWithFormat:@"%@", message.from] forKey:@"_id"];
    NSArray *key = [dources allKeys];
    for (NSString *tem  in key) {
        if ([[dources objectForKey:tem] isEqualToString:@"(null)"]) {
            [dources setObject:@"" forKey:tem];
        }
    }
    switch (message.deliveryState) {
        case NIMMessageDeliveryStateFailed:
            [dic2 setObject:@"2" forKey:@"status"];
            break;
        case NIMMessageDeliveryStateDelivering:
            [dic2 setObject:@"0" forKey:@"status"];
            break;
        case NIMMessageDeliveryStateDeliveried:
            [dic2 setObject:@"1" forKey:@"status"];
            break;
        default:
            [dic2 setObject:@"-1" forKey:@"status"];
            break;
    }
    [dic2 setObject:[NSString stringWithFormat:@"%@", message.text] forKey:@"content"];
    [dic2 setObject:[NSString stringWithFormat:@"%@", message.session.sessionId] forKey:@"sessionId"];
    [dic2 setObject:[NSString stringWithFormat:@"%ld", message.session.sessionType] forKey:@"sessionType"];
    [dic2 setObject:[NSString stringWithFormat:@"%ld", message.messageType] forKey:@"msgType"];
    [dic2 setObject:[NSString stringWithFormat:@"%d", message.isOutgoingMsg] forKey:@"direct"];
    [dic2 setObject:[NSString stringWithFormat:@"%f", message.timestamp] forKey:@"createdAt"];
    [dic2 setObject:[NSString stringWithFormat:@"%@", message.messageId] forKey:@"_id"];
    [dic2 setObject:[NSString stringWithFormat:@"%d",message.isRemoteRead] forKey:@"isRemoteRead"];
    NSArray *keys = [dic2 allKeys];
    for (NSString *tem  in keys) {
        if ([[dic2 objectForKey:tem] isEqualToString:@"(null)"]) {
            [dic2 setObject:@"" forKey:tem];
        }
    }
    [dic2 setObject:dources forKey:@"user"];
    if (message.messageType  == 1) {
        NIMImageObject *object = message.messageObject;
        NSMutableDictionary *imageObj = [NSMutableDictionary dictionary];
        [imageObj setObject:[NSString stringWithFormat:@"%@", [object thumbPath] ] forKey:@"thumbPath"];
        [imageObj setObject:[NSString stringWithFormat:@"%@",[object url] ] forKey:@"url"];
        [imageObj setObject:[NSString stringWithFormat:@"%@",[object displayName] ] forKey:@"displayName"];
        [imageObj setObject:[NSString stringWithFormat:@"%f",[object size].height] forKey:@"imageHeight"];
        [imageObj setObject:[NSString stringWithFormat:@"%f",[object size].width] forKey:@"imageWidth"];
        [dic2 setObject:imageObj forKey:@"imageObj"];
    }
    if(message.messageType == 2){
        NSMutableDictionary *audioObj = [NSMutableDictionary dictionary];
        NIMAudioObject *object = message.messageObject;
        [audioObj setObject:[NSString stringWithFormat:@"%@",object.path] forKey:@"path"];
        [audioObj setObject:[NSString stringWithFormat:@"%@",object.url] forKey:@"url"];
        [audioObj setObject:[NSString stringWithFormat:@"%ld",object.duration] forKey:@"duration"];
        NSArray *keys = [audioObj allKeys];
        for (NSString *tem  in keys) {
            if ([[audioObj objectForKey:tem] isEqualToString:@"(null)"]) {
                [audioObj setObject:@"" forKey:tem];
            }
        }
        [dic2 setObject:audioObj forKey:@"audioObj"];
    }
    if(message.messageType == 3 ){
        NSMutableDictionary *VideoObj = [NSMutableDictionary dictionary];
        NIMVideoObject *object = message.messageObject;
        [VideoObj setObject:[NSString stringWithFormat:@"%@",object.url ] forKey:@"url"];
        [VideoObj setObject:[NSString stringWithFormat:@"%@", object.displayName ] forKey:@"displayName"];
        [VideoObj setObject:[NSString stringWithFormat:@"%@", object.coverUrl ] forKey:@"coverUrl"];
        [VideoObj setObject:[NSString stringWithFormat:@"%f",object.coverSize.height ] forKey:@"coverSizeHeight"];
        [VideoObj setObject:[NSString stringWithFormat:@"%f", object.coverSize.width ] forKey:@"coverSizeWidth"];
        [VideoObj setObject:[NSString stringWithFormat:@"%ld",object.duration ] forKey:@"duration"];
        [VideoObj setObject:[NSString stringWithFormat:@"%lld",object.fileLength] forKey:@"fileLength"];
        if([[NSFileManager defaultManager] fileExistsAtPath:object.coverPath]){
            [VideoObj setObject:[NSString stringWithFormat:@"%@",object.coverPath] forKey:@"coverPath"];
        }else{
            //如果封面图下跪了，点进视频的时候再去下一把封面图
            [[NIMSDK sharedSDK].resourceManager download:object.coverUrl filepath:object.coverPath progress:nil completion:^(NSError *error) {
                if (!error) {
                    [VideoObj setObject:[NSString stringWithFormat:@"%@",object.coverPath] forKey:@"coverPath"];
                }
            }];
        }
        if ([[NSFileManager defaultManager] fileExistsAtPath:object.path]) {
            [VideoObj setObject:[NSString stringWithFormat:@"%@",object.path] forKey:@"path"];
        }else{
            
            [[NIMObject initNIMObject] downLoadVideo:object Error:^(NSError *error) {
                if (!error) {
                    [VideoObj setObject:[NSString stringWithFormat:@"%@",object.path] forKey:@"path"];
                }
            } progress:^(float progress) {
                NSLog(@"下载进度%.f",progress);
            }];
        }
        NSArray *keys = [VideoObj allKeys];
        for (NSString *tem  in keys) {
            if ([[VideoObj objectForKey:tem] isEqualToString:@"(null)"]) {
                [VideoObj setObject:@"" forKey:tem];
            }
        }
        [dic2 setObject:VideoObj forKey:@"videoDic"];
    }
    if(message.messageType == 4){
        NIMLocationObject *object = message.messageObject;
        NSMutableDictionary *locationObj = [NSMutableDictionary dictionary];
        [locationObj setObject:[NSString stringWithFormat:@"%f", object.latitude ] forKey:@"latitude"];
        [locationObj setObject:[NSString stringWithFormat:@"%f", object.longitude ] forKey:@"longitude"];
        [locationObj setObject:[NSString stringWithFormat:@"%@", object.title ] forKey:@"title"];
        NSArray *keys = [locationObj allKeys];
        for (NSString *tem  in keys) {
            if ([[locationObj objectForKey:tem] isEqualToString:@"(null)"]) {
                [locationObj setObject:@"" forKey:tem];
            }
        }
        [dic2 setObject:locationObj forKey:@"locationObj"];
    }
    if (message.messageType == 5) {
        NSMutableDictionary *notiObj = [NSMutableDictionary dictionary];
        NIMNotificationObject *object = message.messageObject;
        switch (object.notificationType) {
            case NIMNotificationTypeTeam:
            case NIMNotificationTypeChatroom:
            {
                
                [notiObj setObject:[NIMKitUtil messageTipContent:message] forKey:@"tipMsg"];
                break;
            }
            case NIMNotificationTypeNetCall:{
                [notiObj setObject:[NIMKitUtil messageTipContent:message]forKey:@"tipMsg"];
                
                
                break;
            }
            default:
                break;
        }
        [dic2 setObject:notiObj forKey:@"notiObj"];
    }
    if (message.messageType == 100) {
        NIMCustomObject *customObject = message.messageObject;
        DWCustomAttachment *obj = customObject.attachment;
        if (obj) {
            switch (obj.custType) {
                case CustomMessgeTypeRedpacket: //红包
                {
                    [dic2 setObject:obj.dataDict forKey:@"redPacketObj"];
                    [dic2 setObject:@"redpacket" forKey:@"custType"];
                }
                    break;
                case CustomMessgeTypeBankTransfer: //转账
                {
                    [dic2 setObject:obj.dataDict  forKey:@"bankTransferObj"];
                    [dic2 setObject:@"transfer" forKey:@"custType"];
                }
                    break;
                case CustomMessgeTypeUrl: //链接
                {
                    [dic2 setObject:obj.dataDict  forKey:@"urlObj"];
                    [dic2 setObject:@"url" forKey:@"custType"];
                }
                    break;
                case CustomMessgeTypeAccountNotice: //账户通知，与账户金额相关变动
                {
                    [dic2 setObject:obj.dataDict  forKey:@"accountNoticeObj"];
                    [dic2 setObject:@"account_notice" forKey:@"custType"];
                }
                    break;
                case CustomMessgeTypeRedPacketOpenMessage: //拆红包消息
                {
                    NSDictionary *dataDict = [self dealWithData:obj.dataDict];
                    if (dataDict) {
                        [dic2 setObject:dataDict  forKey:@"redpacketOpenObj"];
                        [dic2 setObject:@"redpacketOpen" forKey:@"custType"];
                    }else{
                        return;
                    }
                }
                    break;
                default:
                    break;
            }
        }
    }
    [messageArr addObject:dic2];
    //接收消息
    NIMModel *model = [NIMModel initShareMD];
    if ([from isEqualToString:@"receive"]) {
        model.ResorcesArr = messageArr;
    }else if ([from isEqualToString:@"send"]){
        //发送消息
        model.sendState = messageArr;
    }
}
//处理拆红包消息
- (NSDictionary *)dealWithData:(NSDictionary *)dict{
    NSString *strOpenId = [self stringFromKey:@"openId" andDict:dict];
    NSString *strSendId = [self stringFromKey:@"sendId" andDict:dict];
    NSString *strNo = [self stringFromKey:@"serialNo" andDict:dict];
    NSString *strMyId = [NIMSDK sharedSDK].loginManager.currentAccount;
    NSString *strContent;
    NSString *lastString = @"";
    NSInteger hasRedPacket = [[dict objectForKey:@"hasRedPacket"] integerValue];
    if (hasRedPacket == 1) {//红包已领完
        lastString = @"，你的红包已被领完";
    }
    if ([strOpenId isEqualToString:strMyId]&&[strSendId isEqualToString:strMyId]) {
        strContent = [NSString stringWithFormat:@"你领取了自己发的红包%@",lastString ];
    }else if ([strOpenId isEqualToString:strMyId]){
        NSString *strSendName = [self getUserName:strSendId];
        strContent = [NSString stringWithFormat:@"你领取了%@的红包",strSendName];
    }else if([strSendId isEqualToString:strMyId]){
        NSString *strOpenName = [self getUserName:strOpenId];
        strContent = [NSString stringWithFormat:@"%@领取了你的红包%@",strOpenName,lastString];
    }else{//别人发的别人领的
        NSString *strSenderName = [self getUserName:strSendId];
        NSString *strOpenName = [self getUserName:strOpenId];
        strContent = [NSString stringWithFormat:@"%@领取了%@的红包",strOpenName,strSenderName];
    }
    NSDictionary *dataDict = @{@"tipMsg":strContent,@"serialNo":strNo};
    return dataDict;
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

//转发消息
-(void)forwardMessage:(NSString *)messageId sessionId:(NSString *)sessionId sessionType:(NSString *)sessionType content:(NSString *)content success:(Success)succe{
    NIMSession *session = [NIMSession session:sessionId type:[sessionType integerValue]];
    NSArray *currentMessage = [[[NIMSDK sharedSDK] conversationManager] messagesInSession:_session messageIds:@[messageId]];
    NIMMessage *message = currentMessage[0];
    [[NIMSDK sharedSDK].chatManager forwardMessage:message toSession:session error:nil];
    //发送消息
    NIMMessage *messages = [[NIMMessage alloc] init];
    messages.text    = content;
    [[NIMSDK sharedSDK].chatManager sendMessage:messages toSession:session error:nil];
    succe(@"已发送");
}
//撤回消息
-(void)revokeMessage:(NSString *)messageId success:(Success)succe{
    NSArray *currentMessage = [[[NIMSDK sharedSDK] conversationManager] messagesInSession:_session messageIds:@[messageId]];
    NIMMessage *currentmessage = currentMessage[0];
    __weak typeof(self) weakSelf = self;
    [[NIMSDK sharedSDK].chatManager revokeMessage:currentmessage completion:^(NSError * _Nullable error) {
        if (error) {
            if (error.code == NIMRemoteErrorCodeDomainExpireOld) {
                UIAlertView *alert = [[UIAlertView alloc] initWithTitle:nil message:@"发送时间超过2分钟的消息，不能被撤回" delegate:nil cancelButtonTitle:@"确定" otherButtonTitles:nil, nil];
                [alert show];
            }else{
                DDLogError(@"revoke message eror code %zd",error.code);
                NSLog(@"消息撤回失败，请重试");
            }
        }
        else
        {
            NSString * tip = [self tipOnMessageRevoked:currentmessage];
            NIMMessage *tipMessage = [self msgWithTip:tip];
            tipMessage.timestamp = currentmessage.timestamp;
            
            // saveMessage 方法执行成功后会触发 onRecvMessages: 回调，但是这个回调上来的 NIMMessage 时间为服务器时间，和界面上的时间有一定出入，所以要提前先在界面上插入一个和被删消息的界面时间相符的 Tip, 当触发 onRecvMessages: 回调时，组件判断这条消息已经被插入过了，就会忽略掉。
            [[NIMSDK sharedSDK].conversationManager saveMessage:tipMessage forSession:_session completion:nil];
        }
    }];
    
}
//删除一条信息
-(void)deleteMsg:(NSString *)messageId{
    NSArray *currentMessage = [[[NIMSDK sharedSDK] conversationManager] messagesInSession:_session messageIds:@[messageId]];
    NIMMessage *message = currentMessage[0];
    [[NIMSDK sharedSDK].conversationManager deleteMessage:message];
}
//清空聊天记录
-(void)clearMsg:(NSString *)contactId type:(NSString *)type{
    NIMSession  *session = [NIMSession session:contactId type:[type integerValue]];
    [[NIMSDK sharedSDK].conversationManager deleteAllmessagesInSession:session removeRecentSession:NO];
}
- (NIMMessage *)msgWithTip:(NSString *)tip
{
    NIMMessage *message        = [[NIMMessage alloc] init];
    NIMTipObject *tipObject    = [[NIMTipObject alloc] init];
    message.messageObject      = tipObject;
    message.text               = tip;
    NIMMessageSetting *setting = [[NIMMessageSetting alloc] init];
    setting.apnsEnabled        = NO;
    setting.shouldBeCounted    = NO;
    message.setting            = setting;
    return message;
}

- (NSString *)tipOnMessageRevoked:(id)message
{
    NSString *fromUid = nil;
    NIMSession *session = nil;
    
    if ([message isKindOfClass:[NIMMessage class]])
    {
        fromUid = [(NIMMessage *)message from];
        session = [(NIMMessage *)message session];
    }
    else if([message isKindOfClass:[NIMRevokeMessageNotification class]])
    {
        fromUid = [(NIMRevokeMessageNotification *)message fromUserId];
        session = [(NIMRevokeMessageNotification *)message session];
    }
    else
    {
        assert(0);
    }
    
    BOOL isFromMe = [fromUid isEqualToString:[[NIMSDK sharedSDK].loginManager currentAccount]];
    NSString *tip = @"你";
    if (!isFromMe) {
        switch (session.sessionType) {
            case NIMSessionTypeP2P:
                tip = @"对方";
                break;
            case NIMSessionTypeTeam:{
                NIMKitInfoFetchOption *option = [[NIMKitInfoFetchOption alloc] init];
                option.session = session;
                NIMKitInfo *info = [[NIMKit sharedKit] infoByUser:fromUid option:option];
                tip = info.showName;
            }
                break;
            default:
                break;
        }
    }
    return [NSString stringWithFormat:@"%@撤回了一条消息",tip];
}
//麦克风权限
- (void)onTouchVoiceSucc:(Success)succ Err:(Errors)err{
    if ([[AVAudioSession sharedInstance] respondsToSelector:@selector(requestRecordPermission:)]) {
        [[AVAudioSession sharedInstance] performSelector:@selector(requestRecordPermission:) withObject:^(BOOL granted) {
            if (granted) {
                dispatch_async(dispatch_get_main_queue(), ^{
                    succ(@"200");
                });
            }
            else {
                dispatch_async(dispatch_get_main_queue(), ^{
                    err(@"没有麦克风权限");
                });
            }
        }];
    }
    
}


-(void)stopSession;
{
    [[NIMSDK sharedSDK].chatManager removeDelegate:self];
    [[NIMSDK sharedSDK].conversationManager removeDelegate:self];
    [[NIMSDK sharedSDK].systemNotificationManager removeDelegate:self];
}
@end
