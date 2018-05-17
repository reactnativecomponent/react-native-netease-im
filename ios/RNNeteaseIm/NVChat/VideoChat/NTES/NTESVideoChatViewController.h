//
//  NTESVideoChatViewController.h
//  NIM
//
//  Created by chris on 15/5/5.
//  Copyright (c) 2015年 Netease. All rights reserved.
//

#import "NTESVideoChatViewController.h"
#import "NetCallChatInfo.h"
#import <RCTViewManager.h>

@class NTESVideoChatNetStatusView;

@interface NTESVideoChatViewController : UIViewController

@property (nonatomic,strong) IBOutlet UIImageView *remoteView;

@property (nonatomic,strong) IBOutlet UIView   *smallView;

@property (nonatomic,strong) IBOutlet UIButton *hungUpBtn;   //挂断按钮

@property (nonatomic,strong) IBOutlet UIButton *acceptBtn; //接通按钮

@property (nonatomic,strong) IBOutlet UIButton *refuseBtn;   //拒接按钮

@property (nonatomic,strong) IBOutlet UILabel  *durationLabel;//通话时长

@property (nonatomic,strong) IBOutlet UIButton *muteBtn;     //静音按钮

@property (nonatomic,strong) IBOutlet UIButton *switchModelBtn; //模式转换按钮

@property (nonatomic,strong) IBOutlet UIButton *switchCameraBtn; //切换前后摄像头

@property (nonatomic,strong) IBOutlet UIButton *disableCameraBtn; //禁用摄像头按钮

@property (weak, nonatomic) IBOutlet UIButton *localRecordBtn; //录制

@property (nonatomic,strong) IBOutlet UILabel  *connectingLabel;  //等待对方接听

@property (nonatomic,strong) IBOutlet NTESVideoChatNetStatusView *netStatusView;//网络状况

@property (weak, nonatomic) IBOutlet UIView *localRecordingView;

@property (weak, nonatomic) IBOutlet UIView *localRecordingRedPoint;

@property (weak, nonatomic) IBOutlet UIView *lowMemoryView;

@property (weak, nonatomic) IBOutlet UIView *lowMemoryRedPoint;


@property (nonatomic,strong) NetCallChatInfo *callInfo;

//@property (nonatomic,strong) AVAudioPlayer *player; //播放提示音

@property (nonatomic, strong) NSString *peerUid;

//主叫方是自己，发起通话，初始化方法
- (instancetype)initWithCallee:(NSString *)callee;
//被叫方是自己，接听界面，初始化方法
- (instancetype)initWithCaller:(NSString *)caller
                        callId:(uint64_t)callID;


//开始呼叫
- (void)startCall:(NSString *)callee;
//主叫方开始界面回调
- (void)startByCaller;
//被叫方开始界面回调
- (void)startByCallee;
//同意后正在进入聊天界面
- (void)waitForConnectiong;
//双方开始通话
- (void)onCalling;
//挂断
- (void)hangup;
//接受/拒接通话
- (void)response:(BOOL)accept;
//退出界面
- (void)dismiss:(void (^)(void))completion;
// 被叫忙
- (void)onCalleeBusy;

//#pragma mark - Ring
////铃声 - 正在呼叫请稍后
//- (void)playConnnetRing;
////铃声 - 对方暂时无法接听
//- (void)playHangUpRing;
////铃声 - 对方正在通话中
//- (void)playOnCallRing;
////铃声 - 对方无人接听
//- (void)playTimeoutRing;
////铃声 - 接收方铃声
//- (void)playReceiverRing;
////铃声 - 拨打方铃声
//- (void)playSenderRing;

@end
