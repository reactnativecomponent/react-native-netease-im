//
//  TeamViewController.h
//  NIM
//
//  Created by Dowin on 2017/5/4.
//  Copyright © 2017年 Dowin. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "NIMModel.h"
typedef void(^Success)(id param);
typedef void(^Errors)(id erro);
@interface TeamViewController : UIViewController
+(instancetype)initWithTeamViewController;
-(void)initWithDelegate;
//获取群组列表
-(void)getTeamList:(Success)succ Err:(Errors)err;
//创建群组
-(void)createTeam:(NSDictionary *)fields type:(NSString *)type accounts:(NSArray *)accounts Succ:(Success)succ Err:(Errors)err;
//更新群成员名片
- (void)updateMemberNick:(nonnull NSString *)teamId contactId:(nonnull NSString *)contactId nick:(nonnull NSString*)nick Succ:(Success)succ Err:(Errors)err;
//获取群资料
-(void)getTeamInfo:(NSString *)teamId Succ:(Success)succ Err:(Errors)err;
////群成员禁言
-(void)setTeamMemberMute:(NSString *)teamId contactId:(NSString *)contactId mute:(NSString *)mute Succ:(Success)succ Err:(Errors)err;
//获取远程群资料
-(void)fetchTeamInfo:(NSString *)teamId Succ:(Success)succ Err:(Errors)err;
//获取群成员
-(void)getTeamMemberList:(NSString *)teamId Succ:(Success)succ Err:(Errors)err;
//申请加入群组
-(void)applyJoinTeam:(NSString *)teamId message:(NSString *)message Succ:(Success)succ Err:(Errors)err;
//获取群成员资料及设置
- (void)fetchTeamMemberInfo:(NSString *)teamId contactId:(NSString *)contactId Succ:(Success)succ Err:(Errors)err;
//开启/关闭消息提醒
-(void)muteTeam:(NSString *)teamId mute:(NSString *)mute Succ:(Success)succ Err:(Errors)err;
//解散群组
-(void)dismissTeam:(NSString *)teamId Succ:(Success)succ Err:(Errors)err;
//拉人入群
-(void)addMembers:(NSString *)teamId accounts:(NSArray *)count Succ:(Success)succ Err:(Errors)err;
//踢人出群
-(void)removeMember:(NSString *)teamId accounts:(NSArray *)count Succ:(Success)succ Err:(Errors)err;
//主动退群
-(void)quitTeam:(NSString *)teamId Succ:(Success)succ Err:(Errors)err;
//转让群组
-(void)transferManagerWithTeam:(NSString *)teamId
                    newOwnerId:(NSString *)newOwnerId quit:(NSString *)quit Succ:(Success)succ Err:(Errors)err;
//修改自己的群昵称
-(void)updateTeamName:(NSString *)teamId nick:(NSString *)nick Succ:(Success)succ Err:(Errors)err;
-(void)stopTeamList;
//更新群资料
- (void)updateTeam:(NSString *)teamId fieldType:(NSString *)fieldType value:(NSString *)value Succ:(Success)succ Err:(Errors)err;
@end
