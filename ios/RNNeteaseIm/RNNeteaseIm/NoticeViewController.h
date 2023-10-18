//
//  NoticeViewController.h
//  NIM
//
//  Created by Dowin on 2017/5/4.
//  Copyright © 2017年 Dowin. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "NIMModel.h"
typedef void(^Success)(id param);
typedef void(^Errors)(id erro);
typedef NS_ENUM(NSInteger, NotificationHandleType) {
    NotificationHandleTypePending = 0,
    NotificationHandleTypeOk,
    NotificationHandleTypeNo,
    NotificationHandleTypeOutOfDate
};

static const NSInteger MaxNotificationCount = 20;
@interface NoticeViewController : UIViewController
+(instancetype)initWithNoticeViewController;
-(void)initWithDelegate;
- (void)stopSystemMsg;
-(void)deleteNotice:(NSString *)targetID timestamp:(NSString *)timestamp;
-(void)deleAllNotic;
-(void)setAllread;
-(void)onAccept:(NSString *)targetID timestamp:(NSString *)timestamp sucess:(Success)success error:(Errors)err;

-(void)ackAddFriendRequest:(NSString *)targetID isAccept:(nonnull NSString*)isAccept timestamp:(NSString *)timestamp sucess:(Success)success error:(Errors)err;

-(void)onRefuse:(NSString *)targetID timestamp:(NSString *)timestamp sucess:(Success)success error:(Errors)err;
@end
