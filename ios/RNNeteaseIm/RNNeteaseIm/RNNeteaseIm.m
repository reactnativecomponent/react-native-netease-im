//
//  RNNeteaseIm.m
//  RNNeteaseIm
//
//  Created by Dowin on 2017/5/9.
//  Copyright © 2017年 Dowin. All rights reserved.
//

#import "RNNeteaseIm.h"
#import "RCTUtils.h"
#import "RNNotificationCenter.h"
@implementation RNNeteaseIm

@synthesize bridge = _bridge;
- (dispatch_queue_t)methodQueue
{
    return dispatch_get_main_queue();
}

RCT_EXPORT_MODULE()
//手动登录
RCT_EXPORT_METHOD(login:(nonnull NSString *)account token:(nonnull NSString *)token
                  resolve:(RCTPromiseResolveBlock)resolve
                  reject:(RCTPromiseRejectBlock)reject){
    //请将 NIMMyAccount 以及 NIMMyToken 替换成您自己提交到此App下的账号和密码
    [[NIMSDK sharedSDK].loginManager login:account token:token completion:^(NSError *error) {
        if (!error) {
        
            [self setSendState];
            [[NIMViewController initWithController] addDelegate];
            [[RNNotificationCenter sharedCenter] start];
            [[NoticeViewController initWithNoticeViewController]initWithDelegate];
            resolve(account);
        }else{
            reject(@"-1",error, nil);
            NSLog(@"登录失败");
        }
    }];
}

//注销用户
RCT_EXPORT_METHOD(logout){
    [[[NIMSDK sharedSDK] loginManager] logout:^(NSError *error){
    }];
}
//手动删除最近聊天列表
RCT_EXPORT_METHOD(deleteRecentContact:(nonnull NSString * )recentContactId  resolve:(RCTPromiseResolveBlock)resolve
                  reject:(RCTPromiseRejectBlock)reject){
    [[NIMViewController initWithController]deleteCurrentSession:recentContactId andback:^(NSString *error) {
        if (!error) {
            resolve(@"200");
        }else{
            reject(@"-1",error, nil);
        }
    }];
}
//回调最近聊天列表
RCT_EXPORT_METHOD(getRecentContactList:(RCTPromiseResolveBlock)resolve
                  reject:(RCTPromiseRejectBlock)reject){
  [[NIMViewController initWithController]getRecentContactListsuccess:^(id param) {
      resolve(param);
  } andError:^(NSString *error) {
      reject(@"-1",error,nil);
  }];
    
}
////清空聊天记录
//RCT_EXPORT_METHOD(clearMessage:(nonnull  NSString *)sessionId type:(nonnull  NSString *)type){
//    [[ConversationViewController initWithConversationViewController] clearMsg:sessionId type:type];
//    
//}
//获取本地用户资料
RCT_EXPORT_METHOD(getUserInfo:(nonnull NSString * )contactId  resolve:(RCTPromiseResolveBlock)resolve
                  reject:(RCTPromiseRejectBlock)reject){
    [[ContactViewController initWithContactViewController]getUserInFo:contactId Success:^(id param) {
        resolve(param);
    }];
}
//获取服务器用户资料
RCT_EXPORT_METHOD(fetchUserInfo:(nonnull NSString * )contactId   resolve:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject){
    [[ContactViewController initWithContactViewController]fetchUserInfos:contactId Success:^(id param) {
        resolve(param);
    } error:^(NSString *error) {
        reject(@"-1",error, nil);
    }];
}
//保存好友备注
RCT_EXPORT_METHOD(updateUserInfo:(nonnull NSString * )contactId  alias:(nonnull NSString *)alias resolve:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject){
    
    [[ContactViewController initWithContactViewController] upDateUserInfo:contactId alias:alias Success:^(id param) {
        resolve(param);
    } error:^(NSString *error) {
        reject(@"-1",error,nil);
    }];
}
//保存用户信息
RCT_EXPORT_METHOD(updateMyUserInfo:(nonnull  NSDictionary *)userInFo resolve:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject){
    [[ContactViewController initWithContactViewController] updateMyUserInfo:userInFo Success:^(id param) {
        resolve(param);
    } error:^(NSString *error) {
        reject(@"-1",error,nil);
    }];
}
//添加好友
RCT_EXPORT_METHOD(addFriend:(nonnull  NSString * )contactId msg:(nonnull  NSString * )msg resolve:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject){
    [[ContactViewController initWithContactViewController] adduserId:contactId andVerifyType:@"0" andMag:msg Friends:^(NSString *error) {
        reject(@"-1",error, nil);
    } Success:^(NSString *error) {
        resolve(error);
    }];
}
//添加好友，添加验证与否
RCT_EXPORT_METHOD(addFriendWithType:(nonnull  NSString *)contactId verifyType:(nonnull  NSString *)verifyType msg:(nonnull  NSString *)msg resolve:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject){
    [[ContactViewController initWithContactViewController] adduserId:contactId andVerifyType:verifyType andMag:msg Friends:^(NSString *error) {
        reject(@"-1",error, nil);
    } Success:^(NSString *error) {
        resolve(error);
    }];
}
//通过/拒绝对方好友请求
RCT_EXPORT_METHOD(ackAddFriendRequest:(nonnull  NSString *)targetId msg:(nonnull  NSString * )msg timestamp:(nonnull  NSString * )timestamp resolve:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject){
    if ([msg isEqualToString:@"1"]) {
        [[NoticeViewController initWithNoticeViewController]onAccept:targetId timestamp:timestamp sucess:^(id param) {
            resolve(param);
        } error:^(id erro) {
             reject(@"-1",erro, nil);
        }];
    }else{
   [[NoticeViewController initWithNoticeViewController]onRefuse:targetId timestamp:timestamp sucess:^(id param) {
            resolve(param);
        } error:^(id erro) {
             reject(@"-1",erro, nil);
        }];
    }
}
//获取通讯录好友
RCT_EXPORT_METHOD(startFriendList){
    [[ContactViewController initWithContactViewController] getAllContactFriends];
}


//获取通讯录好友回调
RCT_EXPORT_METHOD(getFriendList:(nonnull NSString *)keyword resolve:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject){
    [[ContactViewController initWithContactViewController] getFriendList:^(id param) {
        resolve(param);
    } error:^(NSString *error) {
        reject(@"-1",error,nil);
    }];
}

//通讯录好友
RCT_EXPORT_METHOD(stopFriendList){
    
    [[ContactViewController initWithContactViewController] disealloc];
    
}
//删除好友
RCT_EXPORT_METHOD(deleteFriend:(nonnull  NSString *)contactId resolve:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject){
    [[ContactViewController initWithContactViewController]deleteFriends:contactId Success:^(id param) {
        resolve(param);
    } error:^(NSString *error) {
        reject(@"-1",error,nil);
    }];
}

//开始获取群组
RCT_EXPORT_METHOD(startTeamList){
    
    [[TeamViewController initWithTeamViewController]initWithDelegate];
}
//创建群组
RCT_EXPORT_METHOD(createTeam:(nonnull NSDictionary *)filelds type:(nonnull NSString *)type accounts:(nonnull NSArray *)accounts resolve:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject) {
    [[TeamViewController initWithTeamViewController]createTeam:filelds type:type accounts:accounts Succ:^(id param) {
        resolve(param);
    } Err:^(id erro) {
        reject(@"-1",erro,nil);
    }];
}
//获取群回调列表
RCT_EXPORT_METHOD(getTeamList:(nonnull NSString *)keyWord resolve:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject){
    [[TeamViewController initWithTeamViewController]getTeamList:^(id param) {
        resolve(param);
    } Err:^(id erro) {
        reject(@"-1",erro,nil);
    }];
}
//申请加入群组
RCT_EXPORT_METHOD(applyJoinTeam:(nonnull NSString *)teamId reason:(nonnull NSString *)reason  resolve:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject){
    
    [[TeamViewController initWithTeamViewController]applyJoinTeam:teamId message:reason Succ:^(id param) {
        resolve(param);
    } Err:^(id erro) {
        reject(@"-1",erro,nil);
    }];
}
//获取本地群资料
RCT_EXPORT_METHOD(getTeamInfo:(nonnull NSString *)teamId resolve:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject){
    [[TeamViewController initWithTeamViewController]getTeamInfo:teamId Succ:^(id param) {
        resolve(param);
    } Err:^(id erro) {
        reject(@"-1",erro, nil);
    }];
}
//获取远程群资料
RCT_EXPORT_METHOD(fetchTeamInfo:(nonnull NSString *)teamId resolve:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject){
   [[TeamViewController initWithTeamViewController]fetchTeamInfo:teamId Succ:^(id param) {
       resolve(param);
   } Err:^(id erro) {
       reject(@"-1",erro,nil);
   }];
}
//获取群成员
RCT_EXPORT_METHOD(fetchTeamMemberList:(nonnull NSString *)teamId resolve:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject){
   [[TeamViewController initWithTeamViewController] getTeamMemberList:teamId Succ:^(id param) {
    resolve(param);
   } Err:^(id erro) {
    reject(@"-1",erro,nil);
   }];
}
//开启/关闭群组消息提醒
RCT_EXPORT_METHOD(setTeamNotify:(nonnull NSString *)teamId needNotify:(nonnull NSString *)needNotify resolve:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject){
   [[TeamViewController initWithTeamViewController]muteTeam:teamId mute:needNotify Succ:^(id param) {
       resolve(param);
   } Err:^(id erro) {
       reject(@"-1",erro,nil);
   }];
}
//好友消息提醒开关
RCT_EXPORT_METHOD(setMessageNotify:(nonnull NSString *)contactId needNotify:(nonnull NSString *)needNotify resolve:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject){
    [[ConversationViewController initWithConversationViewController]muteMessage:contactId mute:needNotify Succ:^(id param) {
        resolve(param);
    } Err:^(id erro) {
        reject(@"-1",erro,nil);
    }];
}
//解散群
RCT_EXPORT_METHOD(dismissTeam:(nonnull NSString *)teamId resolve:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject){
    [[TeamViewController initWithTeamViewController] dismissTeam:teamId Succ:^(id param) {
        resolve(param);
    } Err:^(id erro) {
        reject(@"-1",erro,nil);
    }];
}
//拉人入群
RCT_EXPORT_METHOD(addMembers:(nonnull NSString *)teamId accounts:(nonnull NSArray *)accounts resolve:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject){
   [[TeamViewController initWithTeamViewController] addMembers:teamId accounts:accounts Succ:^(id param) {
     resolve(param);
   } Err:^(id erro) {
     reject(@"-1",erro,nil);
   }];
}
//踢人出群
RCT_EXPORT_METHOD(removeMember:(nonnull NSString *)teamId accounts:(nonnull NSArray *)count resolve:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject){
    [[TeamViewController initWithTeamViewController] removeMember:teamId accounts:count Succ:^(id param) {
        resolve(param);
    } Err:^(id erro) {
        reject(@"-1",erro,nil);
    }];
}
//主动退群
RCT_EXPORT_METHOD(quitTeam:(nonnull NSString *)teamId  resolve:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject){
    [[TeamViewController initWithTeamViewController]quitTeam:teamId Succ:^(id param) {
        resolve(param);
    } Err:^(id erro) {
        reject(@"-1",erro,nil);
    }];
}
//转让群组
RCT_EXPORT_METHOD(transferTeam:(nonnull NSString *)teamId account:(nonnull NSString *)account quit:(nonnull NSString *)quit resolve:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject){
    [[TeamViewController initWithTeamViewController] transferManagerWithTeam:teamId newOwnerId:account quit:quit Succ:^(id param) {
        resolve(param);
    } Err:^(id erro) {
        reject(@"-1",erro,nil);
    }];
}
//修改群昵称
RCT_EXPORT_METHOD(updateTeamName:(nonnull NSString *)teamId nick:(nonnull NSString *)nick  resolve:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject){
    [[TeamViewController initWithTeamViewController] updateTeamName:teamId nick:nick Succ:^(id param) {
        resolve(param);
    } Err:^(id erro) {
        reject(@"-1",erro,nil);
    }];
}
//退出讨论组列表
RCT_EXPORT_METHOD(stopTeamList){
    [[TeamViewController initWithTeamViewController]stopTeamList];
}
//
//获取系统消息
RCT_EXPORT_METHOD(startSystemMsg){
    [[NoticeViewController initWithNoticeViewController] initWithDelegate];
}
//获取系统消息
RCT_EXPORT_METHOD(stopSystemMsg){
    [[NoticeViewController initWithNoticeViewController] stopSystemMsg];
}
//删除系统消息一列
RCT_EXPORT_METHOD(deleteSystemMessage:(nonnull  NSString *)targetId timestamp:(nonnull NSString *)timestamp){
    [[NoticeViewController initWithNoticeViewController] deleteNotice:targetId timestamp:timestamp];
}
//将系统消息标记为已读
RCT_EXPORT_METHOD(setAllread){
    [[NoticeViewController initWithNoticeViewController] setAllread];
}
//清空系统信息
RCT_EXPORT_METHOD(clearSystemMessages){
    [[NoticeViewController initWithNoticeViewController] deleAllNotic];
}
//会话开始
RCT_EXPORT_METHOD(startSession:(nonnull  NSString *)sessionId type:(nonnull  NSString *)type){
    [[ConversationViewController initWithConversationViewController]startSession:sessionId withType:type];
}
//会话通知返回按钮
RCT_EXPORT_METHOD(stopSession){
    [[ConversationViewController initWithConversationViewController]stopSession];
}
//聊天界面历史记录
RCT_EXPORT_METHOD(queryMessageListEx:(nonnull  NSString *)messageId limit:(int)limit resolve:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject){
    [[ConversationViewController initWithConversationViewController]localSession:limit cerrentmessageId:messageId success:^(id param) {
        resolve(param);
    } err:^(id erro) {
        reject(@"-1",erro, nil);
    }];
   
}
//本地历史记录
RCT_EXPORT_METHOD(queryMessageEx:(nonnull  NSString *)sessionId sessionType:(nonnull  NSString *)sessionType timeLong:(nonnull  NSString *)timeLong direction:(nonnull  NSString *)direction limit:(nonnull  NSString *)limit   asc:(BOOL)asc resolve:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject){
    [[ConversationViewController initWithConversationViewController]localSessionList:sessionId sessionType:sessionType timeLong:timeLong direction:direction limit:limit asc:asc success:^(id param) {
        resolve(param);
    }];
}
//转发消息
RCT_EXPORT_METHOD(sendForwardMessage:(nonnull NSString *)messageId sessionId:(nonnull NSString *)sessionId sessionType:(nonnull NSString *)sessionType content:(nonnull NSString *)content resolve:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject){
        [[ConversationViewController initWithConversationViewController]forwardMessage:messageId sessionId:sessionId sessionType:sessionType content:content success:^(id param) {
            resolve(param);
        }];
}
//撤回消息
RCT_EXPORT_METHOD(revokeMessage:(nonnull NSString *)messageId  resolve:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject){
   [[ConversationViewController initWithConversationViewController]revokeMessage:messageId success:^(id param) {
       resolve(param);
   }];
}
//删除会话内容
RCT_EXPORT_METHOD(deleteMessage:(nonnull NSString *)messageId){
    [[ConversationViewController initWithConversationViewController]delete:messageId];
}
//清空聊天记录
RCT_EXPORT_METHOD(clearMessage:(nonnull  NSString *)sessionId sessionId:(nonnull  NSString *)type){
    [[ConversationViewController initWithConversationViewController] clearMsg:sessionId type:type];
}
//发送文字消息,atUserIds为@用户名单，@功能仅适用于群组
RCT_EXPORT_METHOD(sendTextMessage:(nonnull  NSString *)content atUserIds:(NSArray *)atUserIds){
    [[ConversationViewController initWithConversationViewController]sendMessage:content andApnsMembers:atUserIds];
}
//发送图片消息
RCT_EXPORT_METHOD(sendImageMessages:(nonnull  NSString *)file  displayName:(nonnull  NSString *)displayName){
    [[ConversationViewController initWithConversationViewController]sendImageMessages:file  displayName:displayName];
}
//发送音频消息
RCT_EXPORT_METHOD(sendAudioMessage:(nonnull  NSString *)file duration:(nonnull  NSString *)duration){
    [[ConversationViewController initWithConversationViewController]sendAudioMessage:file duration:duration];
}
//发送自定义消息
RCT_EXPORT_METHOD(sendCustomMessage:(nonnull  NSString *)attachment config:(nonnull  NSString *)config){
    [[ConversationViewController initWithConversationViewController]sendCustomMessage:attachment config:config];
}
//发送视频消息
RCT_EXPORT_METHOD(sendVideoMessage:(nonnull  NSString *)file duration:(nonnull  NSString *)duration width:(nonnull  NSString *)width height:(nonnull  NSString *)height displayName:(nonnull  NSString *)displayName){
    [[ConversationViewController initWithConversationViewController]sendTextMessage:file duration:duration width:width height:height displayName:displayName];
}
//发送地理位置消息
RCT_EXPORT_METHOD(sendLocationMessage:(nonnull  NSString *)latitude longitude:(nonnull  NSString *)longitude address:(nonnull  NSString *)address){
    [[ConversationViewController initWithConversationViewController]sendLocationMessage:latitude longitude:longitude address:address];
}
//开启录音权限
RCT_EXPORT_METHOD(onTouchVoice:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject){
    [[ConversationViewController initWithConversationViewController]onTouchVoiceSucc:^(id param) {
        resolve(param);
    } Err:^(id erro) {
        reject(@"-1",erro,nil);
    }];
}
//  开始录音
RCT_EXPORT_METHOD(startAudioRecord){
    [[ConversationViewController initWithConversationViewController]onStartRecording];
}
//  结束录音
RCT_EXPORT_METHOD(endAudioRecord){
    [[ConversationViewController initWithConversationViewController]onStopRecording];
}
//  取消录音
RCT_EXPORT_METHOD(cancelAudioRecord){
    [[ConversationViewController initWithConversationViewController]onCancelRecording];
}
//开始播放录音
RCT_EXPORT_METHOD(play:(nonnull NSString *)filepath){
    [[ConversationViewController initWithConversationViewController]play:filepath];
}
//停止播放
RCT_EXPORT_METHOD(stopPlay){
    [[ConversationViewController initWithConversationViewController]stopPlay];
}

//发送红包消息
RCT_EXPORT_METHOD(sendRedPacketMessage:(NSString *)type comments:(NSString *)comments serialNo:(NSString *)serialNo){
    [[ConversationViewController initWithConversationViewController] sendRedPacketMessage:type comments:comments serialNo:serialNo];
}

//发送拆红包消息
RCT_EXPORT_METHOD(sendRedPacketOpenMessage:(NSString *)sendId hasRedPacket:(NSString *)hasRedPacket serialNo:(NSString *)serialNo){
    [[ConversationViewController initWithConversationViewController] sendRedPacketOpenMessage:sendId hasRedPacket:hasRedPacket serialNo:serialNo];
}

//发送转账消息
RCT_EXPORT_METHOD(sendBankTransferMessage:(NSString *)amount comments:(NSString *)comments serialNo:(NSString *)serialNo){
    [[ConversationViewController initWithConversationViewController] sendBankTransferMessage:amount comments:comments serialNo:serialNo];
}


//发送提醒消息
RCT_EXPORT_METHOD(sendTipMessage:(nonnull  NSString *)content){
//    [[ConversationViewController initWithConversationViewController]sendMessage:content];
}
//获取黑名单列表
RCT_EXPORT_METHOD(startBlackList){
    [[BankListViewController initWithBankListViewController]getBlackList];
}
RCT_EXPORT_METHOD(stopBlackList){
}
//添加黑名单
RCT_EXPORT_METHOD(addToBlackList:(nonnull NSString *)contactId  resolve:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject){
     [[BankListViewController initWithBankListViewController]addToBlackList:contactId success:^(id param) {
         resolve(param);
     } Err:^(id erro) {
       reject(@"-1",erro, nil);
     }];
}
//移出黑名单
RCT_EXPORT_METHOD(removeFromBlackList:(nonnull NSString *)contactId  resolve:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject){
    [[BankListViewController initWithBankListViewController]removeFromBlackList:contactId success:^(id param) {
        resolve(param);
    } Err:^(id erro) {
        reject(@"-1",erro, nil);
    }];
}

-(void)setSendState{
    NIMModel *mod = [NIMModel initShareMD];
    mod.myBlock = ^(NSInteger index, id param) {
        switch (index) {
            case 0:
                //网络状态
                [_bridge.eventDispatcher sendDeviceEventWithName:@"observeOnlineStatus" body:@{@"status":param}];
                break;
            case 1:
                //最近会话列表
//                [_bridge.eventDispatcher sendDeviceEventWithName:@"observeRecentContact" body:@{@"sessionList":param}];
                [_bridge.eventDispatcher sendDeviceEventWithName:@"observeRecentContact" body:(NSDictionary *)param];
                break;
            case 2:
                //被踢出 status：1是被挤下线，2是被服务器踢下线，3是另一个客户端手动踢下线（这是在支持多客户端登录的情况下）
                [_bridge.eventDispatcher sendDeviceEventWithName:@"observeOnKick" body:@{@"status":param}];
                break;
            case 3:
                //通讯录
                [_bridge.eventDispatcher sendDeviceEventWithName:@"observeFriend" body:param];
                break;
            case 4:
                //群
                [_bridge.eventDispatcher sendDeviceEventWithName:@"observeTeam" body:param];
                break;
            case 5:
                //系统通知
                [_bridge.eventDispatcher sendDeviceEventWithName:@"observeReceiveSystemMsg" body:param];
                break;
            case 6:
                //系统通知未读条数
                [_bridge.eventDispatcher sendDeviceEventWithName:@"observeUnreadCount" body:param];
                break;
            case 7:
                //聊天记录
                [_bridge.eventDispatcher sendDeviceEventWithName:@"observeReceiveMessage" body:param];
                break;
            case 8:
                //开始发送
                [_bridge.eventDispatcher sendDeviceEventWithName:@"observeStartSend" body:param];
                break;
            case 9:
                //发送结束
                [_bridge.eventDispatcher sendDeviceEventWithName:@"observeEndSend" body:param];
                break;
            case 10:
                //发送进度（'附件图片等'）
                [_bridge.eventDispatcher sendDeviceEventWithName:@"observeProgressSend" body:param];
                break;
            case 11:
                //已读回执
                [_bridge.eventDispatcher sendDeviceEventWithName:@"observeReceipt" body:param];
                break;
            case 12:
                //发送消息
                [_bridge.eventDispatcher sendDeviceEventWithName:@"observeMsgStatus" body:param];
                break;
            case 13:
                //黑名单列表
                [_bridge.eventDispatcher sendDeviceEventWithName:@"observeBlackList" body:param];
                break;
            case 14:
                //录音进度 分贝
                [_bridge.eventDispatcher sendDeviceEventWithName:@"observeAudioRecord" body:param];
                break;
            case 15:
                //删除撤销消息通知
                [_bridge.eventDispatcher sendDeviceEventWithName:@"observeDeleteMessage" body:param];
                break;
            default:
                break;
        }
        
    };
}


@end
