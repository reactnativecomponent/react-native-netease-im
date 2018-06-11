//
//  VideoChatView.h
//  RNNeteaseIm
//
//  Created by shane on 2018/6/8.
//  Copyright © 2018年 Dowin. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "NetCallChatInfo.h"

#define VideoChatViewNotification @"VideoChatViewNotification"
#define Resize  @"Resize"
#define Start   @"Start"
#define End     @"End"
#define VideoChatHangup @"VideoChatHangup"

@interface VideoChatView : UIView
@property (nonatomic,strong) NetCallChatInfo *callInfo;
- (void)playSenderRing;
@end
