//
//  RNVideoChatController.m
//  RNNeteaseIm
//
//  Created by shane on 2018/5/21.
//  Copyright © 2018年 Dowin. All rights reserved.
//

#import "RNVideoChatController.h"
#import <AVFoundation/AVFoundation.h>
#import "UIAlertView+NTESBlock.h"
#import "GLView.h"
#import "NTESGLView.h"
#import "NTESBundleSetting.h"
#import "NIMAVChat.h"
#import "NIMModel.h"
#import "NSDictionary+NTESJson.h"
#import "CallingView.h"

@interface RNVideoChatController()<NIMNetCallManagerDelegate>

@property (nonatomic, strong) UIButton *accept;
@property (nonatomic, strong) UIButton *reject;

@property (nonatomic, strong) CallingView *callingView;

@property (nonatomic,strong) UIView *bigView;
@property (nonatomic,strong) UIView *smallView;

@property (nonatomic, strong) NTESGLView *remoteGLView;

@property (nonatomic,strong) NSMutableArray *chatRoom;

@end

@implementation RNVideoChatController

+(instancetype)initWithVideoChatViewController{
    static RNVideoChatController *conVC = nil;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        conVC = [[RNVideoChatController alloc]init];
    });
    return conVC;
}

-(instancetype)init{
    self = [super init];
    if (self) {
        id<NIMNetCallManager> manager = [NIMAVChatSDK sharedSDK].netCallManager;
        [manager addDelegate:self];
        self.view.frame = [UIScreen mainScreen].bounds;
    }
    return self;
}

- (void)viewDidLoad {
    [super viewDidLoad];
    //设置视频屏幕常亮
    [[UIApplication sharedApplication] setIdleTimerDisabled:YES];
    self.view.backgroundColor = [UIColor whiteColor];
    _bigView = [[UIView alloc] init];
    _bigView.backgroundColor = [UIColor blackColor];
    _bigView.frame = CGRectMake(0, 0, self.view.frame.size.width, self.view.frame.size.height);
    [self.view addSubview: _bigView];
    UIButton *btn = [UIButton buttonWithType:UIButtonTypeCustom];
    btn.frame = CGRectMake(100, 100, 50, 50);
    btn.backgroundColor = [UIColor redColor];
    [btn addTarget:self action:@selector(buttonEvent:) forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:btn];
    _smallView = [[UIView alloc] initWithFrame:CGRectMake(self.view.frame.size.width-150, 0, 150, 200)];
    _smallView.backgroundColor = [UIColor blackColor];
    //[self.view insertSubview:_smallView aboveSubview:_bigView];
    
    _callingView = [[CallingView alloc] initWithFrame:CGRectMake(0, 0, screenW, screenH)];
    //[self.view insertSubview:_callingView aboveSubview:_smallView];
    
    _accept = [self buttonWithColor:[UIColor colorWithRed:47/255.0f green:216/255.0f blue:173/255.0f alpha:1] title:@"接听"];
    _accept.frame = CGRectMake(screenW/2-80, screenH-110, 115, 55);
    [_accept addTarget:self action:@selector(buttonEvent:) forControlEvents:UIControlEventTouchUpInside];
    [self.view insertSubview:_accept aboveSubview:_callingView];
    _reject = [self buttonWithColor:[UIColor colorWithRed:255/255.0f green:98/255.0f blue:98/255.0f alpha:1] title:@"挂断"];
    _reject.frame = CGRectMake(CGRectGetMaxX(_accept.frame)+25, CGRectGetMinY(_accept.frame), 115, 55);
    [_reject addTarget:self action:@selector(buttonEvent:) forControlEvents:UIControlEventTouchUpInside];
    [self.view insertSubview:_reject aboveSubview:_callingView];
    
    UITapGestureRecognizer *tap = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(tap)];
    [self.view addGestureRecognizer:tap];
}

-(void)tap{
    NSLog(@"tap");
}

-(UIButton*)buttonWithColor:(UIColor*)backgroundColor title:(NSString*)title{
    UIButton *btn = [UIButton buttonWithType:UIButtonTypeCustom];
    [btn setBackgroundColor: backgroundColor];
    [btn setTitle:title forState:UIControlStateNormal];
    [btn setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
    btn.layer.cornerRadius = 25;
    return btn;
}

- (NTESGLView *)remoteGLView{
    if (!_remoteGLView) {
        _remoteGLView = [[NTESGLView alloc] initWithFrame:_bigView.bounds];
        // [_remoteGLView setContentMode:[[NTESBundleSetting sharedConfig] videochatRemoteVideoContentMode]];
        [_remoteGLView setContentMode:UIViewContentModeScaleAspectFill];
        [_remoteGLView setBackgroundColor:[UIColor clearColor]];
        _remoteGLView.autoresizingMask = UIViewAutoresizingFlexibleLeftMargin | UIViewAutoresizingFlexibleRightMargin | UIViewAutoresizingFlexibleTopMargin | UIViewAutoresizingFlexibleBottomMargin | UIViewAutoresizingFlexibleWidth | UIViewAutoresizingFlexibleHeight;
    }
    return _remoteGLView;
}

-(NetCallChatInfo *)callInfo{
    if (!_callInfo) {
        _callInfo = [[NetCallChatInfo alloc] init];
        _callInfo.callType = NIMNetCallTypeVideo;
    }
    return _callInfo;
}

- (void)buttonEvent:(UIButton*)btn{
    if (btn==_accept) {
        [self response:YES];
        _callingView.hidden = YES;
        _accept.hidden = YES;
        _reject.frame = CGRectMake(screenW/2-80, screenH-110, 115, 55);
    }
    else if (btn==_reject){
        if (self.callInfo.isStart) {
            //挂断
            [self hangup];
        }
        else{
            [self response:NO];
        }
    }
    else
        return;
}

- (void)response:(BOOL)accept{
    __weak typeof(self) wself = self;
    NIMNetCallOption *option = [[NIMNetCallOption alloc] init];
    [self fillUserSetting:option];
    [[NIMAVChatSDK sharedSDK].netCallManager response:self.callInfo.callID accept:accept option:option completion:^(NSError *error, UInt64 callID) {
        if (!error) {
            //[wself onCalling];
            wself.callInfo.isStart = YES;
            [wself.chatRoom addObject:wself.callInfo.callee];
            NSTimeInterval delay = 10.f; //10秒后判断下聊天室
            dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(delay * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
                if (wself.chatRoom.count == 1) {
                    // 通话失败
                    [wself hangup];
                }
            });
        }else{
            wself.chatRoom = nil;
            [wself dismiss];
        }
    }];
    //dismiss需要放在self后面，否在ios7下会有野指针
    if (accept) {
        //[self waitForConnectiong];
    }else{
        [self dismiss];
    }
}

- (void)hangup{
    [[NIMAVChatSDK sharedSDK].netCallManager hangup:self.callInfo.callID];
    self.chatRoom = nil;
    [self dismiss];
}

#pragma mark - NIMNetCallManagerDelegate

- (void)onHangup:(UInt64)callID by:(NSString *)user{    //对方挂断
    if (self.callInfo.callID == callID) {
        if (self.callInfo.localRecording) {
            __weak typeof(self) wself = self;
            dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(2 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
                [wself dismiss];
            });
        }
        else {
            [self dismiss];
        }
    }else{
        [self dismiss];
    }
}

-(void)onCallEstablished:(UInt64)callID     //建立
{
    if (self.callInfo.callID == callID) {
        self.callInfo.startTime = [NSDate date].timeIntervalSince1970;
    }
}

- (void)onCallDisconnected:(UInt64)callID withError:(NSError *)error    //异常断开
{
    if (self.callInfo.callID == callID) {
        [self dismiss];
        self.chatRoom = nil;
    }
}

- (void)onLocalDisplayviewReady:(UIView *)displayView   //本地预览就绪
{
    displayView.frame = _smallView.bounds;
    [_smallView addSubview:displayView];
}

- (void)onRemoteYUVReady:(NSData *)yuvData width:(NSUInteger)width height:(NSUInteger)height from:(NSString *)user{
    if (([[UIApplication sharedApplication] applicationState] == UIApplicationStateActive)) {
        [self.remoteGLView render:yuvData width:width height:height];
    }
}

- (void)onControl:(UInt64)callID from:(NSString *)user type:(NIMNetCallControlType)control{
    switch (control) {
        case NIMNetCallControlTypeFeedabck:{
            NSMutableArray *room = self.chatRoom;
            if (room && !room.count) {
                
                if (!self.callInfo.caller) {
                    return;
                }
                [room addObject:self.callInfo.caller];
                
                //40秒之后查看一下聊天室状态，如果聊天室还在一个人的话，就播放铃声超时
                __weak typeof(self) wself = self;
                uint64_t callId = self.callInfo.callID;
                NSTimeInterval delayTime = 30;//超时时间
                dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(delayTime * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
                    NSMutableArray *room = wself.chatRoom;
                    if (wself && room && room.count == 1)
                    {
                        //如果超时后，也没有响应，房间存在，就挂断本次通话callID
                        [[NIMAVChatSDK sharedSDK].netCallManager hangup:callId];
                        wself.chatRoom = nil;
                        [self dismiss];
                    }
                });
            }
            break;
        }
            
        case NIMNetCallControlTypeBusyLine:
            
            break;
        default:
            break;
    }
}

- (void)dismiss{
    //只要页面消失，就挂断
    if (self.callInfo.callID != 0) {
        [[NIMAVChatSDK sharedSDK].netCallManager hangup:self.callInfo.callID];
        self.chatRoom = nil;
    }
    [self.view removeFromSuperview];
    //[self dismissViewControllerAnimated:YES completion:nil];
}

- (void)fillUserSetting:(NIMNetCallOption *)option
{
    option.autoRotateRemoteVideo = [[NTESBundleSetting sharedConfig] videochatAutoRotateRemoteVideo];
    option.webrtcCompatible = YES;
    option.serverRecordAudio     = YES;
    option.serverRecordVideo     = YES;
    option.preferredVideoEncoder = [[NTESBundleSetting sharedConfig] perferredVideoEncoder];
    option.preferredVideoDecoder = [[NTESBundleSetting sharedConfig] perferredVideoDecoder];
    option.videoMaxEncodeBitrate = [[NTESBundleSetting sharedConfig] videoMaxEncodeKbps] * 1000;
    option.autoDeactivateAudioSession = [[NTESBundleSetting sharedConfig] autoDeactivateAudioSession];
    option.audioDenoise = [[NTESBundleSetting sharedConfig] audioDenoise];
    option.voiceDetect = [[NTESBundleSetting sharedConfig] voiceDetect];
    option.audioHowlingSuppress = [[NTESBundleSetting sharedConfig] audioHowlingSuppress];
    option.preferHDAudio =  [[NTESBundleSetting sharedConfig] preferHDAudio];
    option.scene = [[NTESBundleSetting sharedConfig] scene];
    
    NIMNetCallVideoCaptureParam *param = [[NIMNetCallVideoCaptureParam alloc] init];
    [self fillVideoCaptureSetting:param];
    option.videoCaptureParam = param;
    
}

- (void)fillVideoCaptureSetting:(NIMNetCallVideoCaptureParam *)param
{
    param.preferredVideoQuality = [[NTESBundleSetting sharedConfig] preferredVideoQuality];
    param.videoCrop  = [[NTESBundleSetting sharedConfig] videochatVideoCrop];
    param.startWithBackCamera   = [[NTESBundleSetting sharedConfig] startWithBackCamera];
    
}

- (void)dealloc{
    [[NIMAVChatSDK sharedSDK].netCallManager removeDelegate:self];
}

@end
