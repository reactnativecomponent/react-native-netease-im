//
//  ContactViewController.h
//  NIM
//
//  Created by Dowin on 2017/5/2.
//  Copyright © 2017年 Dowin. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "NIMModel.h"

typedef void(^Error)(NSString *error);
typedef void(^Success)(id param);
@interface ContactViewController : UIViewController
+(instancetype)initWithContactViewController;
-(void)initWithDelegate;
-(void)getAllContactFriends;
-(void)adduserId:(NSString *)userId andVerifyType:(NSString *)strType andMag:(NSString *)msg Friends:(Error)err  Success:(Error )success;
-(void)getUserInFo:(NSString *)userId Success:(Success )success;
-(void)fetchUserInfos:(NSString *)userId Success:(Success )success error:(Error )error;
-(void)deleteFriends:(NSString *)userId Success:(Success )success error:(Error )err;
//获取好友列表，回调
-(void)getFriendList:(Success )success error:(Error )error;
- (void)disealloc;
-(void)upDateUserInfo:(NSString *)contactId alias:(NSString *)alias Success:(Success )success error:(Error )err;
-(void)updateMyUserInfo:(NSDictionary *)userInFo Success:(Success )success error:(Error )err;
@end
