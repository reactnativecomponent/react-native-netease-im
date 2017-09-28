//
//  NIMViewController.h
//  NIM
//
//  Created by Dowin on 2017/5/8.
//  Copyright © 2017年 Dowin. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "NIMModel.h"
#import "NTESClientUtil.h"
typedef void(^SUCCESS) (id param);
typedef void(^ERROR)(NSString *error);
@interface NIMViewController : UIViewController

@property (copy, nonatomic) NSString *strAccount;
@property (copy, nonatomic) NSString *strToken;

+(instancetype)initWithController;
-(instancetype)initWithNIMController;
-(void)deleteCurrentSession:(NSString *)recentContactId andback:(ERROR)error;
//获取最近聊天列表回调
-(void)getRecentContactListsuccess:(SUCCESS)suc andError:(ERROR)err;
-(void)addDelegate;
- (void)getResouces;
@end
