//
//  BankListViewController.h
//  RNNeteaseIm
//
//  Created by Dowin on 2017/5/12.
//  Copyright © 2017年 Dowin. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "NIMModel.h"
typedef void(^Success)(id param);
typedef void(^Errors)(id erro);
@interface BankListViewController : UIViewController
+(instancetype)initWithBankListViewController;
-(void)getBlackList;
-(void)addToBlackList:(NSString *)contactId success:(Success)suc Err:(Errors)err;
-(void)removeFromBlackList:(NSString *)contactId success:(Success)suc Err:(Errors)err;
@end
