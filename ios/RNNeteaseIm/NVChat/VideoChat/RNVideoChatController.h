//
//  RNVideoChatController.h
//  RNNeteaseIm
//
//  Created by shane on 2018/5/21.
//  Copyright © 2018年 Dowin. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "NetCallChatInfo.h"
#import <RCTViewManager.h>

@interface RNVideoChatController : UIViewController

@property (nonatomic,strong) NetCallChatInfo *callInfo;

+(instancetype)initWithVideoChatViewController;

@end
