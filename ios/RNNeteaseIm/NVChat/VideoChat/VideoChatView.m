//
//  VideoChatView.m
//  RNNeteaseIm
//
//  Created by shane on 2018/6/8.
//  Copyright © 2018年 Dowin. All rights reserved.
//

#import "VideoChatView.h"
#import "NTESBundleSetting.h"
#import "NTESGLView.h"
#import "CallingView.h"
#import <AVFoundation/AVFoundation.h>

@interface VideoChatView() <NIMNetCallManagerDelegate, UIGestureRecognizerDelegate>{
    CGFloat centerX_, centerY_;
    CGFloat viewHalfH_, viewhalfW_;
    CGFloat moveX_, moveY_;
}
@property (nonatomic, strong) AVAudioPlayer *player;
@property (nonatomic, strong) UIButton *minimize;
@property (nonatomic, strong) UIButton *accept;
@property (nonatomic, strong) UIButton *reject;

@property (nonatomic, strong) CallingView *callingView;

@property (nonatomic, strong) UIView *bigView;
@property (nonatomic, strong) UIView *smallView;

@property (nonatomic, strong) NTESGLView *remoteGLView;

@property (nonatomic, strong) NSMutableArray *chatRoom;

@property (nonatomic, strong) UITapGestureRecognizer *maximize;
@property (nonatomic, strong) UIPanGestureRecognizer *pan;
@property (nonatomic) CGRect initFrame;
@property (nonatomic) CGRect lastFrame;

@end

@implementation VideoChatView

- (instancetype)init{
    self = [self initWithFrame:[UIScreen mainScreen].bounds];
    return self;
}

- (instancetype)initWithFrame:(CGRect)frame{
    self = [super initWithFrame:frame];
    if (self) {
        id<NIMNetCallManager> manager = [NIMAVChatSDK sharedSDK].netCallManager;
        [manager addDelegate:self];
        [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(hangup) name:VideoChatHangup object:nil];
        _initFrame = frame;
        _lastFrame = CGRectMake(frame.size.width-100, 20, 100, 150);
        [self makeView];
    }
    return self;
}

-(void)makeView{
    //设置视频屏幕常亮
    [[UIApplication sharedApplication] setIdleTimerDisabled:YES];
    _bigView = [[UIView alloc] init];
    _bigView.backgroundColor = [UIColor blackColor];
    _bigView.frame = CGRectMake(0, 0, self.frame.size.width, self.frame.size.height);
    [self addSubview: _bigView];
    
    _smallView = [[UIView alloc] initWithFrame:CGRectMake(self.frame.size.width-100, 20, 100, 150)];
    _smallView.backgroundColor = [UIColor blackColor];
    [self insertSubview:_smallView aboveSubview:_bigView];
    
    _callingView = [[CallingView alloc] initWithFrame:CGRectMake(0, 0, self.frame.size.width, self.frame.size.height)];
    [self insertSubview:_callingView aboveSubview:_smallView];
    
    _accept = [self buttonWithColor:[UIColor colorWithRed:47/255.0f green:216/255.0f blue:173/255.0f alpha:1] title:@"接听"];
    _accept.frame = CGRectMake(self.frame.size.width/2-140, self.frame.size.height-110, 115, 55);
    [_accept addTarget:self action:@selector(buttonEvent:) forControlEvents:UIControlEventTouchUpInside];
    [self insertSubview:_accept aboveSubview:_callingView];
    _reject = [self buttonWithColor:[UIColor colorWithRed:255/255.0f green:98/255.0f blue:98/255.0f alpha:1] title:@"挂断"];
    _reject.frame = CGRectMake(CGRectGetMaxX(_accept.frame)+50, CGRectGetMinY(_accept.frame), 115, 55);
    [_reject addTarget:self action:@selector(buttonEvent:) forControlEvents:UIControlEventTouchUpInside];
    [self insertSubview:_reject aboveSubview:_callingView];
    
    _minimize = [UIButton buttonWithType:UIButtonTypeCustom];
    _minimize.frame = CGRectMake(30, 30, 30, 30);
    [_minimize setBackgroundImage:[UIImage imageNamed:@"shrink"] forState:UIControlStateNormal];
    //[_minimize setTitle:@"最小化" forState:UIControlStateNormal];
    [_minimize addTarget:self action:@selector(minimizeEvent) forControlEvents:UIControlEventTouchUpInside];
    [self insertSubview:_minimize aboveSubview:_bigView];
    _maximize = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(maximizeEvent)];
    _maximize.enabled = NO;
    [self addGestureRecognizer:_maximize];
    _pan = [[UIPanGestureRecognizer alloc] initWithTarget:self action:@selector(panEvent:)];
    _pan.delegate = self;
    _pan.enabled = NO;
    [self addGestureRecognizer:_pan];
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
        [_bigView addSubview:_remoteGLView];
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

- (void)minimizeEvent{
    [self sendNotification:Resize];
    [UIView animateWithDuration:0.2 animations:^{
        self.frame = _lastFrame;
        _bigView.frame = self.bounds;
        _remoteGLView.frame = _bigView.bounds;
    }];
    _smallView.hidden = YES;
    _reject.hidden = YES;
    _minimize.hidden = YES;
    _maximize.enabled = YES;
    //_pan.enabled = YES;
}

- (void)maximizeEvent{
    [UIView animateWithDuration:0.2 animations:^{
        self.frame = _initFrame;
    } completion:^(BOOL finished) {
        _bigView.frame = self.bounds;
        _remoteGLView.frame = _bigView.bounds;
        _smallView.hidden = NO;
        _reject.hidden = NO;
        _minimize.hidden = NO;
        _maximize.enabled = NO;
        //_pan.enabled = NO;
    }];
}

- (void)panEvent:(UIPanGestureRecognizer*)pan{
    centerX_ = pan.view.center.x;
    centerY_ = pan.view.center.y;
    viewHalfH_ = _lastFrame.size.height/2;
    viewhalfW_ = _lastFrame.size.width/2;
    CGPoint point=[pan translationInView:self];
    if (viewhalfW_<=centerX_+point.x&&centerX_+point.x<=screenW-viewhalfW_) {
        _lastFrame.origin.x += point.x;
    }
    if (viewHalfH_<=centerY_+point.y&&centerY_+point.y<=screenH-viewHalfH_) {
        _lastFrame.origin.y += point.y;
    }
    self.frame = _lastFrame;
    [pan setTranslation:CGPointMake(0, 0) inView:self];
}

- (void)buttonEvent:(UIButton*)btn{
    if (btn==_accept) {
        [self.player stop];
        [self response:YES];
        [self sendNotification:Start];
        _callingView.hidden = YES;
        _accept.hidden = YES;
        _reject.frame = CGRectMake(screenW/2-62, screenH-110, 115, 55);
    }
    else if (btn==_reject){
        if (self.callInfo.isStart) {
            [self hangup];
        }
        else{
            [self response:NO];
        }
    }
    else
        return;
}

- (void)onCalling{
    
}

- (void)onChatting{
    
}

- (void)response:(BOOL)accept{
    __weak typeof(self) wself = self;
    NIMNetCallOption *option = [[NIMNetCallOption alloc] init];
    [self fillUserSetting:option];
    [[NIMAVChatSDK sharedSDK].netCallManager response:self.callInfo.callID accept:accept option:option completion:^(NSError *error, UInt64 callID) {
        if (!error) {
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
//对方挂断
- (void)onHangup:(UInt64)callID by:(NSString *)user{
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
    [self removeFromSuperview];
    [self sendNotification:End];
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

- (void)playSenderRing{
    [self.player stop];
    NSURL *url = [[NSBundle mainBundle] URLForResource:@"video_chat_tip_sender" withExtension:@"aac"];
    self.player = [[AVAudioPlayer alloc] initWithContentsOfURL:url error:nil];
    self.player.numberOfLoops = 20;
    [self.player play];
}

- (void)sendNotification:(NSString*)type{
    [[NSNotificationCenter defaultCenter] postNotificationName:VideoChatViewNotification object:@{type:type}];
}

- (void)dealloc{
    [[NSNotificationCenter defaultCenter] removeObserver:self];
    [[UIApplication sharedApplication] setIdleTimerDisabled:NO];
    [[NIMAVChatSDK sharedSDK].netCallManager removeDelegate:self];
}

@end
