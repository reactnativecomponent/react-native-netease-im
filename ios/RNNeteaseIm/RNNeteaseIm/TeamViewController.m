//
//  TeamViewController.m
//  NIM
//
//  Created by Dowin on 2017/5/4.
//  Copyright © 2017年 Dowin. All rights reserved.
//

#import "TeamViewController.h"

@interface TeamViewController ()<NIMTeamManagerDelegate>
{
NSMutableArray *_myTeams;
}

@end

@implementation TeamViewController

+(instancetype)initWithTeamViewController{
    static TeamViewController *teamVC = nil;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        teamVC = [[TeamViewController alloc]init];
        
    });
    return teamVC;
}
-(void)initWithDelegate{
       [[NIMSDK sharedSDK].teamManager addDelegate:self];
       _myTeams = [self fetchTeams];
    NSMutableArray *teamArr = [NSMutableArray array];
    for (NIMTeam *team in _myTeams) {
        NSMutableDictionary *dic = [NSMutableDictionary dictionary];
        [dic setObject:[NSString stringWithFormat:@"%@",team.teamId] forKey:@"teamId"];
        [dic setObject:[NSString stringWithFormat:@"%@", team.teamName] forKey:@"name"];
        [dic setObject:[NSString stringWithFormat:@"%@", team.avatarUrl ] forKey:@"avatar"];
        [dic setObject:[NSString stringWithFormat:@"%ld", team.type] forKey:@"type"];
        NSArray *keys = [dic allKeys];
        for (NSString *tem  in keys) {
            if ([[dic objectForKey:tem] isEqualToString:@"(null)"]) {
                [dic setObject:@"" forKey:tem];
            }
        }

        [teamArr addObject:dic];
    }
    NIMModel *model = [NIMModel initShareMD];
    model.teamArr = teamArr;
}
-(void)getTeamList:(Success)succ Err:(Errors)err{
    _myTeams = [self fetchTeams];
    NSMutableArray *teamArr = [NSMutableArray array];
    for (NIMTeam *team in _myTeams) {
        NSMutableDictionary *dic = [NSMutableDictionary dictionary];
        [dic setObject:team.teamId forKey:@"teamId"];
        [dic setObject:[NSString stringWithFormat:@"%@", team.teamName] forKey:@"name"];
        [dic setObject:[NSString stringWithFormat:@"%@", team.avatarUrl ] forKey:@"avatar"];
        [dic setObject:[NSString stringWithFormat:@"%ld", team.type] forKey:@"type"];
        NSArray *keys = [dic allKeys];
        for (NSString *tem  in keys) {
            if ([[dic objectForKey:tem] isEqualToString:@"(null)"]) {
                [dic setObject:@"" forKey:tem];
            }
        }
        [teamArr addObject:dic];
    }
    if (teamArr) {
        succ(teamArr);
    }else{
        err(@"网络异常");
    }
}
//创建群组
-(void)createTeam:(NSDictionary *)fields type:(NSString *)type accounts:(NSArray *)accounts Succ:(Success)succ Err:(Errors)err{
    NIMCreateTeamOption *option = [[NIMCreateTeamOption alloc] init];
    option.joinMode   = NIMTeamJoinModeNoAuth;
     option.type       = NIMTeamTypeNormal;
    option.postscript = @"邀请你加入群组";
    option.name  = [fields objectForKey:@"name"]?[fields objectForKey:@"name"]:@"";
    option.intro  = [fields objectForKey:@"introduce"]?[fields objectForKey:@"introduce"]:@"";
    option.joinMode =  [[fields objectForKey:@"verifyType"]?[fields objectForKey:@"verifyType"]:@"0"  integerValue];
    option.inviteMode  = [[fields objectForKey:@"inviteMode"]?[fields objectForKey:@"inviteMode"]:@"1" integerValue];
    option.beInviteMode  = [[fields objectForKey:@"beInviteMode"]?[fields objectForKey:@"beInviteMode"]:@"1" integerValue];
    option.updateInfoMode  = [[fields objectForKey:@"teamUpdateMode"]?[fields objectForKey:@"teamUpdateMode"]:@"1" integerValue];

    if ([type isEqualToString:@"0"]) {
        option.type = NIMTeamTypeNormal;
    }
    if ([type isEqualToString:@"1"]){
        option.type = NIMTeamTypeAdvanced;
    }
    [[NIMSDK sharedSDK].teamManager createTeam:option users:accounts completion:^(NSError *error, NSString *teamId) {
        if (!error) {
            NSDictionary *dic = @{@"teamId":teamId};
            succ(dic);
        }else{
            err(@"创建失败");
        }
    }];
}

//更新群资料
- (void)updateTeam:(NSString *)teamId fieldType:(NSString *)fieldType value:(NSString *)value Succ:(Success)succ Err:(Errors)err{
    if ([fieldType isEqualToString:@"name"]) {//群组名称
        [[NIMSDK sharedSDK].teamManager updateTeamName:value teamId:teamId completion:^(NSError * _Nullable error) {
            if (!error) {
                succ(@"200");
            }else{
                err(error);
            }
        }];
    }else if ([fieldType isEqualToString:@"icon"]) {//头像
        [[NIMSDK sharedSDK].teamManager updateTeamAvatar:value teamId:teamId completion:^(NSError * _Nullable error) {
            if (!error) {
                succ(@"200");
            }else{
                err(error);
            }
        }];
    }else if ([fieldType isEqualToString:@"introduce"]) {//群组介绍
        [[NIMSDK sharedSDK].teamManager updateTeamIntro:value teamId:teamId completion:^(NSError * _Nullable error) {
            if (!error) {
                succ(@"200");
            }else{
                err(error);
            }
        }];
    }else if ([fieldType isEqualToString:@"announcement"]) {//群组公告
        [[NIMSDK sharedSDK].teamManager updateTeamAnnouncement:value teamId:teamId completion:^(NSError * _Nullable error) {
            if (!error) {
                succ(@"200");
            }else{
                err(error);
            }
        }];
    }else if ([fieldType isEqualToString:@"verifyType"]) {//验证类型
        [[NIMSDK sharedSDK].teamManager updateTeamJoinMode:[value integerValue] teamId:teamId completion:^(NSError * _Nullable error) {
            if (!error) {
                succ(@"200");
            }else{
                err(error);
            }
        }];
    }else if ([fieldType isEqualToString:@"inviteMode"]) {//邀请他人类型
        [[NIMSDK sharedSDK].teamManager updateTeamInviteMode:[value integerValue] teamId:teamId completion:^(NSError * _Nullable error) {
            if (!error) {
                succ(@"200");
            }else{
                err(error);
            }
        }];
    }else if ([fieldType isEqualToString:@"beInviteMode"]) {//被邀请人权限
        [[NIMSDK sharedSDK].teamManager updateTeamBeInviteMode:[value integerValue] teamId:teamId completion:^(NSError * _Nullable error) {
            if (!error) {
                succ(@"200");
            }else{
                err(error);
            }
        }];
    }else if ([fieldType isEqualToString:@"teamUpdateMode"]) {//群资料修改权限
        [[NIMSDK sharedSDK].teamManager updateTeamUpdateInfoMode:[value integerValue] teamId:teamId completion:^(NSError * _Nullable error) {
            if (!error) {
                succ(@"200");
            }else{
                err(error);
            }
        }];
    }
}

//申请加入群组
-(void)applyJoinTeam:(NSString *)teamId message:(NSString *)message Succ:(Success)succ Err:(Errors)err{
    [[NIMSDK sharedSDK].teamManager applyToTeam:teamId message:message completion:^(NSError * _Nullable error, NIMTeamApplyStatus applyStatus) {
        if (!error) {
            switch (applyStatus) {
                case NIMTeamApplyStatusAlreadyInTeam:
                    err(@"您已经在群里");
                    break;
                case NIMTeamApplyStatusWaitForPass:
                    succ(@"申请成功，等待验证");
                default:
                    break;
            }
        }
        else{
            DDLogDebug(@"Jion team failed: %@", error.localizedDescription);
            switch (error.code) {
                case NIMRemoteErrorCodeTeamAlreadyIn:
                    err(@"已经在群里");
                    break;
                default:
                    err(@"群申请失败");
                    break;
            }
        }
        DDLogDebug(@"Jion team status: %zd", applyStatus);
    
    }];
}
//获取本地群资料
-(void)getTeamInfo:(NSString *)teamId Succ:(Success)succ Err:(Errors)err{
  NIMTeam *team =   [[NIMSDK sharedSDK].teamManager teamById:teamId];
    if (team) {
        NSMutableDictionary *teamDic = [NSMutableDictionary dictionary];
                [teamDic setObject:[NSString stringWithFormat:@"%@",team.teamId] forKey:@"teamId"];
                [teamDic setObject:[NSString stringWithFormat:@"%@",team.teamName] forKey:@"name"];
                [teamDic setObject:[NSString stringWithFormat:@"%ld", team.type] forKey:@"type"];
                [teamDic setObject:[NSString stringWithFormat:@"%@", team.avatarUrl] forKey:@"avatar"];
                [teamDic setObject:[NSString stringWithFormat:@"%@",team.intro] forKey:@"introduce"];
                [teamDic setObject:[NSString stringWithFormat:@"%@",team.announcement]forKey:@"announcement"];
                [teamDic setObject:[NSString stringWithFormat:@"%@",team.owner] forKey:@"creator"];
                [teamDic setObject:[NSString stringWithFormat:@"%ld", team.memberNumber ] forKey:@"memberCount"];
                [teamDic setObject:[NSString stringWithFormat:@"%ld",team.level] forKey:@"memberLimit"];
                [teamDic setObject:[NSString stringWithFormat:@"%f", team.createTime ] forKey:@"createTime"];
                [teamDic setObject:[NSString stringWithFormat:@"%d", team.notifyForNewMsg ] forKey:@"mute"];
                [teamDic setObject:[NSString stringWithFormat:@"%ld",team.joinMode] forKey:@"verifyType"];
                [teamDic setObject:[NSString stringWithFormat:@"%ld",team.beInviteMode] forKey:@"teamBeInviteMode"];
                NSArray *keys = [teamDic allKeys];
                for (NSString *tem  in keys) {
                    if ([[teamDic objectForKey:tem] isEqualToString:@"(null)"]) {
                        [teamDic setObject:@"" forKey:tem];
                    }
                }
                succ(teamDic);
            }

    else{
        err(@"获取群资料失败，请重新获取");
    }
}
//群成员禁言
-(void)setTeamMemberMute:(NSString *)teamId contactId:(NSString *)contactId mute:(NSString *)mute Succ:(Success)succ Err:(Errors)err{
    BOOL isMute = YES;
    if ([mute isEqualToString:@"1"]) {//禁言
        isMute = YES;
    }else{
        isMute = NO;
    }
    [[NIMSDK sharedSDK].teamManager updateMuteState:isMute userId:contactId inTeam:teamId completion:^(NSError * _Nullable error) {
        if (!error) {
            succ(@"200");
        }else{
            err(error);
        }
    }];
}
//更新群成员名片
- (void)updateMemberNick:(nonnull NSString *)teamId contactId:(nonnull NSString *)contactId nick:(nonnull NSString*)nick Succ:(Success)succ Err:(Errors)err{
    [[NIMSDK sharedSDK].teamManager updateUserNick:contactId newNick:nick inTeam:teamId completion:^(NSError * _Nullable error) {
        if (!error) {
            succ(@"200");
        }else{
            err(error);
        }
    }];
}

//获取远程资料
-(void)fetchTeamInfo:(NSString *)teamId Succ:(Success)succ Err:(Errors)err{
    [[NIMSDK sharedSDK].teamManager fetchTeamInfo:teamId completion:^(NSError * _Nullable error, NIMTeam * _Nullable team) {
        if (!error) {
            NSMutableDictionary *teamDic = [NSMutableDictionary dictionary];
            [teamDic setObject:[NSString stringWithFormat:@"%@",team.teamId] forKey:@"teamId"];
            [teamDic setObject:[NSString stringWithFormat:@"%@",team.teamName] forKey:@"name"];
            [teamDic setObject:[NSString stringWithFormat:@"%ld", team.type] forKey:@"type"];
            [teamDic setObject:[NSString stringWithFormat:@"%@", team.avatarUrl] forKey:@"avatar"];
            [teamDic setObject:[NSString stringWithFormat:@"%@",team.intro] forKey:@"introduce"];
            [teamDic setObject:[NSString stringWithFormat:@"%@",team.announcement]forKey:@"announcement"];
            [teamDic setObject:[NSString stringWithFormat:@"%@",team.owner] forKey:@"creator"];
            [teamDic setObject:[NSString stringWithFormat:@"%ld", team.memberNumber ] forKey:@"memberCount"];
            [teamDic setObject:[NSString stringWithFormat:@"%ld",team.level] forKey:@"memberLimit"];
            [teamDic setObject:[NSString stringWithFormat:@"%f", team.createTime ] forKey:@"createTime"];
            [teamDic setObject:[NSString stringWithFormat:@"%d", team.notifyForNewMsg ] forKey:@"mute"];
            [teamDic setObject:[NSString stringWithFormat:@"%ld",team.joinMode] forKey:@"verifyType"];
            [teamDic setObject:[NSString stringWithFormat:@"%ld",team.beInviteMode] forKey:@"teamBeInviteMode"];
            [teamDic setObject:[NSString stringWithFormat:@"%ld",team.inviteMode] forKey:@"teamInviteMode"];
            [teamDic setObject:[NSString stringWithFormat:@"%ld",team.updateInfoMode] forKey:@"teamUpdateMode"];
            NSArray *keys = [teamDic allKeys];
            for (NSString *tem  in keys) {
                if ([[teamDic objectForKey:tem] isEqualToString:@"(null)"]) {
                    [teamDic setObject:@"" forKey:tem];
                }
            }
            succ(teamDic);
        }else{
            err(error);
        }
    }];
}
//获取群成员
-(void)getTeamMemberList:(NSString *)teamId Succ:(Success)succ Err:(Errors)err{

    [[NIMSDK sharedSDK].teamManager fetchTeamMembers:teamId completion:^(NSError * _Nullable error, NSArray<NIMTeamMember *> * _Nullable members) {
        if (!error) {
            NSMutableArray *arr = [NSMutableArray array];
            for (NIMTeamMember *member in members) {
                NSMutableDictionary *memb = [NSMutableDictionary dictionary];
                [memb setObject:[NSString stringWithFormat:@"%@", member.teamId] forKey:@"teamId"];
                [memb setObject:[NSString stringWithFormat:@"%@", member.userId] forKey:@"userId"];
                [memb setObject:[NSString stringWithFormat:@"%ld", member.type ] forKey:@"type"];
                [memb setObject:[NSString stringWithFormat:@"%@", member.nickname]  forKey:@"nickname"];
                [memb setObject:[NSString stringWithFormat:@"%d", member.isMuted]  forKey:@"isMuted"];
                [memb setObject:[NSString stringWithFormat:@"%f", member.createTime]  forKey:@"createTime"];
                [memb setObject:[NSString stringWithFormat:@"%@", member.customInfo]  forKey:@"customInfo"];
                NIMUser   *user = [[NIMSDK sharedSDK].userManager userInfo:member.userId];
                BOOL isMe          = [member.userId isEqualToString:[NIMSDK sharedSDK].loginManager.currentAccount];
                BOOL isMyFriend    = [[NIMSDK sharedSDK].userManager isMyFriend:member.userId];
                BOOL isInBlackList = [[NIMSDK sharedSDK].userManager isUserInBlackList:member.userId];
                BOOL needNotify    = [[NIMSDK sharedSDK].userManager notifyForNewMsg:member.userId];
                [memb setObject:[NSString stringWithFormat:@"%@", user.userId] forKey:@"contactId"];
                [memb setObject:[NSString stringWithFormat:@"%@", user.alias] forKey:@"alias"];
                [memb setObject:[NSString stringWithFormat:@"%@",user.userInfo.nickName] forKey:@"name"];
                [memb setObject:[NSString stringWithFormat:@"%@",user.userInfo.avatarUrl] forKey:@"avatar"];
                [memb setObject:[NSString stringWithFormat:@"%@",user.userInfo.sign] forKey:@"signature"];
                [memb setObject:[NSString stringWithFormat:@"%ld", user.userInfo.gender ] forKey:@"gender"];
                [memb setObject:[NSString stringWithFormat:@"%@",user.userInfo.email] forKey:@"email"];
                [memb setObject:[NSString stringWithFormat:@"%@",user.userInfo.birth] forKey:@"birthday"];
                [memb setObject:[NSString stringWithFormat:@"%@",user.userInfo.mobile] forKey:@"mobile"];
                [memb setObject:[NSString stringWithFormat:@"%@",user.userInfo.ext] forKey:@"extension"];
                [memb setObject:[NSString stringWithFormat:@"%d",isMe] forKey:@"isMe"];
                [memb setObject:[NSString stringWithFormat:@"%d",isMyFriend] forKey:@"isMyFriend"];
                [memb setObject:[NSString stringWithFormat:@"%d",isInBlackList] forKey:@"isInBlackList"];
                [memb setObject:[NSString stringWithFormat:@"%d",needNotify] forKey:@"needNotify"];
                [memb setObject:@"" forKey:@"extensionMap"];
                NSArray *keys = [memb allKeys];
                for (NSString *tem  in keys) {
                    if ([[memb objectForKey:tem] isEqualToString:@"(null)"]) {
                        [memb setObject:@"" forKey:tem];
                    }
                }
                [arr addObject:memb];
            }
        succ(arr);
        }else{
            err(error);
        }
    }];
}
//获取群成员资料及设置
- (void)fetchTeamMemberInfo:(NSString *)teamId contactId:(NSString *)contactId Succ:(Success)succ Err:(Errors)err{
    NIMTeamMember *member = [[NIMSDK sharedSDK].teamManager teamMember:contactId inTeam:teamId];
    NSMutableDictionary *memb = [NSMutableDictionary dictionary];
    [memb setObject:[NSString stringWithFormat:@"%@", member.teamId] forKey:@"teamId"];
    [memb setObject:[NSString stringWithFormat:@"%@", member.userId] forKey:@"userId"];
    [memb setObject:[NSString stringWithFormat:@"%ld", member.type ] forKey:@"type"];
    [memb setObject:[NSString stringWithFormat:@"%@", member.nickname]  forKey:@"nickname"];
    [memb setObject:[NSString stringWithFormat:@"%d", member.isMuted]  forKey:@"isMuted"];
    [memb setObject:[NSString stringWithFormat:@"%f", member.createTime]  forKey:@"createTime"];
    [memb setObject:[NSString stringWithFormat:@"%@", member.customInfo]  forKey:@"customInfo"];
    NIMUser   *user = [[NIMSDK sharedSDK].userManager userInfo:member.userId];
    BOOL isMe          = [member.userId isEqualToString:[NIMSDK sharedSDK].loginManager.currentAccount];
    BOOL isMyFriend    = [[NIMSDK sharedSDK].userManager isMyFriend:member.userId];
    BOOL isInBlackList = [[NIMSDK sharedSDK].userManager isUserInBlackList:member.userId];
    BOOL needNotify    = [[NIMSDK sharedSDK].userManager notifyForNewMsg:member.userId];
    [memb setObject:[NSString stringWithFormat:@"%@", user.userId] forKey:@"contactId"];
    [memb setObject:[NSString stringWithFormat:@"%@", user.alias] forKey:@"alias"];
    [memb setObject:[NSString stringWithFormat:@"%@",user.userInfo.nickName] forKey:@"name"];
    [memb setObject:[NSString stringWithFormat:@"%@",user.userInfo.avatarUrl] forKey:@"avatar"];
    [memb setObject:[NSString stringWithFormat:@"%@",user.userInfo.sign] forKey:@"signature"];
    [memb setObject:[NSString stringWithFormat:@"%ld", user.userInfo.gender ] forKey:@"gender"];
    [memb setObject:[NSString stringWithFormat:@"%@",user.userInfo.email] forKey:@"email"];
    [memb setObject:[NSString stringWithFormat:@"%@",user.userInfo.birth] forKey:@"birthday"];
    [memb setObject:[NSString stringWithFormat:@"%@",user.userInfo.mobile] forKey:@"mobile"];
    [memb setObject:[NSString stringWithFormat:@"%@",user.userInfo.ext] forKey:@"extension"];
    [memb setObject:[NSString stringWithFormat:@"%d",isMe] forKey:@"isMe"];
    [memb setObject:[NSString stringWithFormat:@"%d",isMyFriend] forKey:@"isMyFriend"];
    [memb setObject:[NSString stringWithFormat:@"%d",isInBlackList] forKey:@"isInBlackList"];
    [memb setObject:[NSString stringWithFormat:@"%d",needNotify] forKey:@"needNotify"];
    [memb setObject:@"" forKey:@"extensionMap"];
    NSArray *keys = [memb allKeys];
    for (NSString *tem  in keys) {
        if ([[memb objectForKey:tem] isEqualToString:@"(null)"]) {
            [memb setObject:@"" forKey:tem];
        }
    }
    succ(memb);
}


//开启/关闭消息提醒
-(void)muteTeam:(NSString *)teamId mute:(NSString *)mute Succ:(Success)succ Err:(Errors)err{
    BOOL on;
    if ([mute isEqualToString:@"1"]) {
        on = true;
    }else{
        on = false;
    }
    [[[NIMSDK sharedSDK] teamManager] updateNotifyState:on
                                                 inTeam:teamId
                                             completion:^(NSError *error) {
                                                 if (!error) {
                                                     succ(@"200");
                                                 }else{
                                                     err(error);
                                                 }
                                             }];

}
//解散群组
-(void)dismissTeam:(NSString *)teamId Succ:(Success)succ Err:(Errors)err{
    [[NIMSDK sharedSDK].teamManager dismissTeam:teamId completion:^(NSError *error) {
        if (!error) {
            succ(@"200");
        }else{
            err([NSString stringWithFormat:@"解散失败 code:%zd",error.code]);
        }
    }];

}
//拉人入群
-(void)addMembers:(NSString *)teamId accounts:(NSArray *)count Succ:(Success)succ Err:(Errors)err{
    NSString *postscript = @"邀请你加入群组";
    [[NIMSDK sharedSDK].teamManager addUsers:count toTeam:teamId postscript:postscript completion:^(NSError *error, NSArray *members) {
        if (!error) {
            succ(@"200");
        }else{
            err([NSString stringWithFormat:@"邀请失败 code:%zd",error.code]);
        }
    }];

}
//踢人出群
-(void)removeMember:(NSString *)teamId accounts:(NSArray *)count Succ:(Success)succ Err:(Errors)err{
        [[NIMSDK sharedSDK].teamManager kickUsers:count fromTeam:teamId completion:^(NSError * _Nullable error) {
            if (!error) {
                succ(@"200");
            }else{
                err(@"移除失败");
            }
        }];
}
//主动退群
-(void)quitTeam:(NSString *)teamId Succ:(Success)succ Err:(Errors)err{
    [[NIMSDK sharedSDK].teamManager quitTeam:teamId completion:^(NSError * _Nullable error) {
        if (!error) {
            succ(@"200");
        }else{
            err(error);
        }
    }];
}
//转让群组
-(void)transferManagerWithTeam:(NSString *)teamId
                    newOwnerId:(NSString *)newOwnerId quit:(NSString *)quit Succ:(Success)succ Err:(Errors)err{
    BOOL isLeave;
    if ([quit isEqualToString:@"1"]) {
        isLeave = true;
    }else{
        isLeave = false;
    }
    [[NIMSDK sharedSDK].teamManager transferManagerWithTeam:teamId newOwnerId:newOwnerId isLeave:isLeave completion:^(NSError * _Nullable error) {
        if (!error) {
            succ(@"200");
        }else{
            err(err);
        }
    }];
}
//修改群昵称
-(void)updateTeamName:(NSString *)teamId nick:(NSString *)nick Succ:(Success)succ Err:(Errors)err{
  [[NIMSDK sharedSDK].teamManager updateTeamName:nick teamId:teamId completion:^(NSError * _Nullable error) {
      if (!error) {
          succ(@"200");
      }else{
      err(err);
      }
  }];
}
-(void)stopTeamList{
     [[NIMSDK sharedSDK].teamManager removeDelegate:self];
}
- (NSMutableArray *)fetchTeams{
    NSMutableArray *myTeams = [[NSMutableArray alloc]init];
    for (NIMTeam *team in [NIMSDK sharedSDK].teamManager.allMyTeams) {
//        if (team.type == NIMTeamTypeNormal) {
            [myTeams addObject:team];
//        }
    }
    return myTeams;
}

@end
