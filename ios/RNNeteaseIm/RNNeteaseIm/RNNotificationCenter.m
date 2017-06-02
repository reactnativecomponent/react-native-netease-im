//
//  RNNotificationCenter.m
//  RNNeteaseIm
//
//  Created by Dowin on 2017/5/24.
//  Copyright © 2017年 Dowin. All rights reserved.
//

#import "RNNotificationCenter.h"
#import <AVFoundation/AVFoundation.h>
#import "NSDictionary+NTESJson.h"
@interface RNNotificationCenter () <NIMSystemNotificationManagerDelegate,NIMChatManagerDelegate>
@property (nonatomic,strong) AVAudioPlayer *player; //播放提示音
@end

@implementation RNNotificationCenter

+ (instancetype)sharedCenter
{
    static RNNotificationCenter *instance = nil;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        instance = [[RNNotificationCenter alloc] init];
    });
    return instance;
}
- (void)start
{
    DDLogInfo(@"Notification Center Setup");
}
- (instancetype)init {
    self = [super init];
    if(self) {
      
        NSURL *url = [[NSBundle mainBundle] URLForResource:@"message" withExtension:@"wav"];
        _player = [[AVAudioPlayer alloc] initWithContentsOfURL:url error:nil];
        
        [[NIMSDK sharedSDK].systemNotificationManager addDelegate:self];
      
        [[NIMSDK sharedSDK].chatManager addDelegate:self];
    }
    return self;
}
- (void)dealloc{
    [[NIMSDK sharedSDK].systemNotificationManager removeDelegate:self];
    [[NIMSDK sharedSDK].chatManager removeDelegate:self];
}
#pragma mark - NIMChatManagerDelegate
- (void)onRecvMessages:(NSArray *)messages
{
    static BOOL isPlaying = NO;
    if (isPlaying) {
        return;
    }
    isPlaying = YES;
    [self playMessageAudioTip];
    dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(0.3 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
        isPlaying = NO;
    });
    [self checkMessageAt:messages];
}

- (void)playMessageAudioTip
{
//    UINavigationController *nav = [NTESMainTabController instance].selectedViewController;
//    BOOL needPlay = YES;
//    for (UIViewController *vc in nav.viewControllers) {
//        if ([vc isKindOfClass:[NIMSessionViewController class]] ||  [vc isKindOfClass:[NTESLiveViewController class]] || [vc isKindOfClass:[NTESNetChatViewController class]])
//        {
//            needPlay = NO;
//            break;
//        }
//    }
//    if (needPlay) {
//        [self.player stop];
//        [[AVAudioSession sharedInstance] setCategory: AVAudioSessionCategoryAmbient error:nil];
//        [self.player play];
//    }
}

- (void)checkMessageAt:(NSArray *)messages
{
   }

#pragma mark - NIMSystemNotificationManagerDelegate
- (void)onReceiveCustomSystemNotification:(NIMCustomSystemNotification *)notification{
    
    NSString *content = notification.content;

    NSData *data = [content dataUsingEncoding:NSUTF8StringEncoding];
    if (data)
    {
        NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:data
                                                             options:0
                                                               error:nil];
         NSLog(@"收到------消息%@",[dict jsonString:NTESCustomContent]);
        if ([dict isKindOfClass:[NSDictionary class]])
        {
            if ([dict jsonInteger:NTESNotifyID] == NTESCustom)
            {
                NSLog(@"收到消息%@",notification);
//                //SDK并不会存储自定义的系统通知，需要上层结合业务逻辑考虑是否做存储。这里给出一个存储的例子。
//                NTESCustomNotificationObject *object = [[NTESCustomNotificationObject alloc] initWithNotification:notification];
//                //这里只负责存储可离线的自定义通知，推荐上层应用也这么处理，需要持久化的通知都走可离线通知
//                if (!notification.sendToOnlineUsersOnly) {
//                    [[NTESCustomNotificationDB sharedInstance] saveNotification:object];
//                }
//                if (notification.setting.shouldBeCounted) {
//                    [[NSNotificationCenter defaultCenter] postNotificationName:NTESCustomNotificationCountChanged object:nil];
//                }
                NSString *content  = [dict jsonString:NTESCustomContent];
                 NSLog(@"收到消息%@",content);
//                [[NTESMainTabController instance].selectedViewController.view makeToast:content duration:2.0 position:CSToastPositionCenter];
            }
        }
    }
}




@end
