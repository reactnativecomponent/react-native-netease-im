//
//  NTESVideoChatViewController.m
//  NIM
//
//  Created by chris on 15/5/5.
//  Copyright (c) 2015年 Netease. All rights reserved.
//

#import "NTESVideoChatViewController.h"
#import <AVFoundation/AVFoundation.h>
#import "UIAlertView+NTESBlock.h"
#import "GLView.h"
#import "NTESGLView.h"
#import "NTESBundleSetting.h"
#import "NIMAVChat.h"
#import "NIMModel.h"
#import "NSDictionary+NTESJson.h"

#define TAG "RNVideoChatView"

#define NTESUseGLView

#define SCREEN_W [UIScreen mainScreen].bounds.size.width
#define SCREEN_H [UIScreen mainScreen].bounds.size.height

@interface NTESVideoChatViewController ()<NIMNetCallManagerDelegate>

@property (nonatomic,assign) NIMNetCallCamera cameraType;

@property (nonatomic,strong) CALayer *localVideoLayer;

@property (nonatomic,assign) BOOL oppositeCloseVideo;

#if defined (NTESUseGLView)
@property (nonatomic, strong) NTESGLView *remoteGLView;
#endif

@property (nonatomic,strong) NSMutableArray *chatRoom;

@property (nonatomic,weak) UIView   *localView;

@property (nonatomic,weak) UIView   *localPreView;

@property (nonatomic, assign) BOOL calleeBasy;

@end

@implementation NTESVideoChatViewController

- (instancetype)initWithCallee:(NSString *)callee{
    self = [self initWithNibName:nil bundle:nil];
    if (self) {
        self.callInfo.callee = callee;
        self.callInfo.caller = [[NIMSDK sharedSDK].loginManager currentAccount];
    }
    return self;
}


- (instancetype)initWithCaller:(NSString *)caller callId:(uint64_t)callID{
    self = [self initWithNibName:nil bundle:nil];
    if (self) {
        self.callInfo.caller = caller;
        self.callInfo.callee = [[NIMSDK sharedSDK].loginManager currentAccount];
        self.callInfo.callID = callID;
    }
    return self;
}

- (instancetype)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        if (!self.callInfo) {
            _callInfo = [[NetCallChatInfo alloc] init];
        }
        self.callInfo.callType = NIMNetCallTypeVideo;
        _cameraType = [[NTESBundleSetting sharedConfig] startWithBackCamera] ? NIMNetCallCameraBack :NIMNetCallCameraFront;
        
        // ++
        //防止应用在后台状态，此时呼入，会走init但是不会走viewDidLoad,此时呼叫方挂断，导致被叫监听不到，界面无法消去的问题。
        id<NIMNetCallManager> manager = [NIMAVChatSDK sharedSDK].netCallManager;
        [manager addDelegate:self];
    }
    return self;
}

-(void)viewDidDisappear:(BOOL)animated{
    [_remoteGLView removeFromSuperview];
    _remoteGLView = nil;
}

- (void)dealloc{
    NSLog(@"%s dealloc",TAG);
    [[NIMAVChatSDK sharedSDK].netCallManager removeDelegate:self];
}
-(void)viewDidLayoutSubviews{
    _remoteView.frame = CGRectMake(0, 0, self.view.frame.size.width, self.view.frame.size.height);
    [self.view addSubview: _remoteView];
}
- (void)viewDidLoad {
    [super viewDidLoad];
    
    //设置视屏屏幕常亮
    [[UIApplication sharedApplication] setIdleTimerDisabled:YES];
    
    [[NIMAVChatSDK sharedSDK].netCallManager addDelegate:self];
    
    // 初始化remoteView
    _remoteView = [[UIImageView alloc] init];
    _remoteView.frame = CGRectMake(0, 0, self.view.frame.size.width, self.view.frame.size.height);
    [self.view setAutoresizesSubviews:YES];
    [self.view addSubview: _remoteView];
    [self initUI];
    
    _smallView = [[UIView alloc] initWithFrame:CGRectMake(0, 0, 100, 100)];
    //_smallView.backgroundColor = [UIColor redColor];
    //[_remoteView addSubview:_smallView];
    self.localView = _smallView;
    
    NSLog(@"%s viewDidLoad:%@",TAG,self.view);
    __weak typeof(self) wself = self;
    // 检查服务是否可用
    [self checkServiceEnable:^(BOOL result) {
        if (result) {
            if (wself.callInfo.isStart) {
                [self onCalling];
            }
            else if (wself.callInfo.callID) {
                [wself startByCallee];
            }
            else if (wself.callInfo.callee){
                [wself startByCaller];
            }
        }else{
            //用户禁用服务，干掉界面
            if (wself.callInfo.callID) {
                //说明是被叫方
                [[NIMAVChatSDK sharedSDK].netCallManager response:wself.callInfo.callID accept:NO option:nil completion:nil];
            }
            [wself dismiss:nil];
        }
    }];
    
}


#pragma mark - UI
- (void)initUI
{
    self.remoteView.userInteractionEnabled = YES;
    
    self.localRecordingView.layer.cornerRadius = 10.0;
    self.localRecordingRedPoint.layer.cornerRadius = 4.0;
    self.lowMemoryView.layer.cornerRadius = 10.0;
    self.lowMemoryRedPoint.layer.cornerRadius = 4.0;
    self.refuseBtn.exclusiveTouch = YES;
    self.acceptBtn.exclusiveTouch = YES;
    if ([[UIApplication sharedApplication] applicationState] == UIApplicationStateActive) {
        [self initRemoteGLView];
    }
}

#pragma mark - Interface
//正在接听中界面
- (void)startInterface{
    self.acceptBtn.hidden = YES;
    self.refuseBtn.hidden   = YES;
//    self.hungUpBtn.hidden   = NO;
    self.connectingLabel.hidden = NO;
    self.connectingLabel.text = @"正在呼叫，请稍候...";
    self.switchModelBtn.hidden = YES;
    self.switchCameraBtn.hidden = YES;
    self.muteBtn.hidden = YES;
    self.disableCameraBtn.hidden = YES;
    self.localRecordBtn.hidden = YES;
    self.localRecordingView.hidden = YES;
    self.lowMemoryView.hidden = YES;
    [self.hungUpBtn removeTarget:self action:NULL forControlEvents:UIControlEventTouchUpInside];
    [self.hungUpBtn addTarget:self action:@selector(hangup) forControlEvents:UIControlEventTouchUpInside];
    self.localView = self.remoteView;
    
}

//选择是否接听界面
- (void)waitToCallInterface{
    self.acceptBtn.hidden = NO;
    self.refuseBtn.hidden   = NO;
    self.hungUpBtn.hidden   = YES;
//    NSString *nick = [NTESSessionUtil showNick:self.callInfo.caller inSession:nil];
//    self.connectingLabel.text = [nick stringByAppendingString:@"的来电"];
    self.muteBtn.hidden = YES;
    self.switchCameraBtn.hidden = YES;
    self.disableCameraBtn.hidden = YES;
    self.localRecordBtn.hidden = YES;
    self.localRecordingView.hidden = YES;
    self.lowMemoryView.hidden = YES;
    self.switchModelBtn.hidden = YES;
}

//连接对方界面
- (void)connectingInterface{
    self.acceptBtn.hidden = YES;
    self.refuseBtn.hidden   = YES;
//    self.hungUpBtn.hidden   = NO;
    self.connectingLabel.hidden = NO;
    self.connectingLabel.text = @"正在连接对方...请稍后...";
    self.switchModelBtn.hidden = YES;
    self.switchCameraBtn.hidden = YES;
    self.muteBtn.hidden = YES;
    self.disableCameraBtn.hidden = YES;
    self.localRecordBtn.hidden = YES;
    self.localRecordingView.hidden = YES;
    self.lowMemoryView.hidden = YES;
    [self.hungUpBtn removeTarget:self action:NULL forControlEvents:UIControlEventTouchUpInside];
    [self.hungUpBtn addTarget:self action:@selector(hangup) forControlEvents:UIControlEventTouchUpInside];
}

//接听中界面(视频)
- (void)videoCallingInterface{
//    NIMNetCallNetStatus status = [NIMAVChatSDK sharedSDK].netCallManager.netStatus;
//    [self.netStatusView refreshWithNetState:status];
    self.acceptBtn.hidden = YES;
    self.refuseBtn.hidden   = YES;
    self.hungUpBtn.hidden   = NO;
    self.connectingLabel.hidden = YES;
    self.muteBtn.hidden = NO;
    self.switchCameraBtn.hidden = NO;
    self.disableCameraBtn.hidden = NO;
    self.localRecordBtn.hidden = NO;
//    self.switchModelBtn.hidden = NO;
    self.muteBtn.selected = self.callInfo.isMute;
    self.disableCameraBtn.selected = self.callInfo.disableCammera;
    self.localRecordBtn.selected = self.callInfo.localRecording;
    self.localRecordingView.hidden = !self.callInfo.localRecording;
    self.lowMemoryView.hidden = YES;
//    [self.switchModelBtn setTitle:@"语音模式" forState:UIControlStateNormal];
    [self.hungUpBtn removeTarget:self action:NULL forControlEvents:UIControlEventTouchUpInside];
    [self.hungUpBtn addTarget:self action:@selector(hangup) forControlEvents:UIControlEventTouchUpInside];
    self.localVideoLayer.hidden = NO;
    self.localPreView.hidden = NO;
}


- (IBAction)acceptToCall:(id)sender{
    BOOL accept = (sender == self.acceptBtn);
    //防止用户在点了接收后又点拒绝的情况
    [self response:accept];
}

- (IBAction)mute:(BOOL)sender{
    self.callInfo.isMute = !self.callInfo.isMute;
    [[NIMAVChatSDK sharedSDK].netCallManager setMute:self.callInfo.isMute];
    self.muteBtn.selected = self.callInfo.isMute;
}

- (IBAction)switchCamera:(id)sender{
//    if (self.cameraType == NIMNetCallCameraFront) {
//        self.cameraType = NIMNetCallCameraBack;
//    }else{
//        self.cameraType = NIMNetCallCameraFront;
//    }
//    [[NIMAVChatSDK sharedSDK].netCallManager switchCamera:self.cameraType];
//    self.switchCameraBtn.selected = (self.cameraType == NIMNetCallCameraBack);
}

- (IBAction)disableCammera:(id)sender{
    self.callInfo.disableCammera = !self.callInfo.disableCammera;
    [[NIMAVChatSDK sharedSDK].netCallManager setCameraDisable:self.callInfo.disableCammera];
    self.disableCameraBtn.selected = self.callInfo.disableCammera;
    if (self.callInfo.disableCammera) {
        [self.localVideoLayer removeFromSuperlayer];
        [[NIMAVChatSDK sharedSDK].netCallManager control:self.callInfo.callID type:NIMNetCallControlTypeCloseVideo];
    }else{
        [self.localView.layer addSublayer:self.localVideoLayer];
        [[NIMAVChatSDK sharedSDK].netCallManager control:self.callInfo.callID type:NIMNetCallControlTypeOpenVideo];
    }
}


// 拨打电话
- (void)startCall:(NSString *)callee{
    // 被叫人
    if(callee){
        self.callInfo.callee = callee;
    }
    [self startByCaller];
}

#pragma mark - Call Life

- (void)startByCallee{
    NSLog(@"%s startByCallee",TAG);
    
    //告诉对方可以播放铃声了
    self.callInfo.isStart = YES;
    NSMutableArray *room = [[NSMutableArray alloc] init];
    [room addObject:self.callInfo.caller];
    self.chatRoom = room;
    [[NIMAVChatSDK sharedSDK].netCallManager control:self.callInfo.callID type:NIMNetCallControlTypeFeedabck];
    [self waitToCallInterface];
}

- (void)startByCaller{
    self.callInfo.isStart = YES;
    NSArray *callees = [NSArray arrayWithObjects:self.callInfo.callee, nil];
    
    NIMNetCallOption *option = [[NIMNetCallOption alloc] init];
    option.extendMessage = @"音视频请求扩展信息";
    option.apnsContent = [NSString stringWithFormat:@"%@请求", self.callInfo.callType == NIMNetCallTypeAudio ? @"网络通话" : @"视频聊天"];
    option.apnsSound = @"video_chat_tip_receiver.aac";
    [self fillUserSetting:option];
    
    option.videoCaptureParam.startWithCameraOn = (self.callInfo.callType == NIMNetCallTypeVideo);
    
    __weak typeof(self) wself = self;
    
    [[NIMAVChatSDK sharedSDK].netCallManager start:callees type:NIMNetCallMediaTypeVideo option:option completion:^(NSError *error, UInt64 callID) {
        if (!error && wself) {
            wself.callInfo.callID = callID;
            wself.chatRoom = [[NSMutableArray alloc]init];
            //十秒之后如果还是没有收到对方响应的control字段，则自己发起一个假的control，用来激活铃声并自己先进入房间
            NSTimeInterval delayTime = 10;
            dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(delayTime * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
                [wself onControl:callID from:wself.callInfo.callee type:NIMNetCallControlTypeFeedabck];
            });
        }else{
            if (error) {
               
            }else{
                //说明在start的过程中把页面关了。。
                [[NIMAVChatSDK sharedSDK].netCallManager hangup:callID];
            }
            [wself dismiss:nil];
        }
    }];
}

- (void)onCalling{
    NSLog(@"%s onCalling",TAG);
    [self videoCallingInterface];
}

- (void)waitForConnectiong{
    NSLog(@"%s waitForConnectiong",TAG);
    [self connectingInterface];
}

- (void)onCalleeBusy{
    NSLog(@"%s onCalleeBusy",TAG);
    // 被叫繁忙
}

//#pragma mark -NIMNetCallManagerDelegate
//- (void)onReceive:(UInt64)callID from:(NSString *)caller type:(NIMNetCallType)type message:(nullable NSString *)extendMessage{
//    NSLog(@"%s onReceive:%@",TAG, caller);
//    if ([NIMAVChatSDK sharedSDK].netCallManager.currentCallID > 0)
//    {
//        [[NIMAVChatSDK sharedSDK].netCallManager control:callID type:NIMNetCallControlTypeBusyLine];
//        return;
//    };
//
//    // 通知给js
//    NIMModel *model = [NIMModel initShareMD];
//    NSDictionary *dd = @{@"status": @YES, @"callid": [NSString stringWithFormat:@"%llu",callID], @"from": caller};
//    model.videoReceive = dd;
//
//}

- (void)afterCheckService{
    if (self.callInfo.isStart)
    {
        [self onCalling];
    }
    else if (self.callInfo.callID)
    {
        [self startByCallee];
    }
}

- (void)onResponse:(UInt64)callID from:(NSString *)callee accepted:(BOOL)accepted{
    NSLog(@"%s onResponse:%llu,%@,%d",TAG, callID, callee, accepted);
    if (self.callInfo.callID == callID) {
        if (!accepted) {
            // 对方拒绝
            self.chatRoom = nil;
            __weak typeof(self) wself = self;
            dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(3.5 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
                [wself dismiss:nil];
            });
            
            // 通知给js
            NIMModel *model = [NIMModel initShareMD];
            NSDictionary *dd = @{@"status": @NO, @"message": @"对方拒绝接听"};
            model.videoAccept = dd;
        }else{
            [self onCalling];
            [self.chatRoom addObject:callee];
            
            // 通知给js
            NIMModel *model = [NIMModel initShareMD];
            NSDictionary *dd = @{@"status": @YES, @"callid": [NSString stringWithFormat:@"%llu",callID], @"callee": callee};
            model.videoAccept = dd;
        }
        

    }
}

-(void)onCallEstablished:(UInt64)callID
{
    if (self.callInfo.callID == callID) {
        self.callInfo.startTime = [NSDate date].timeIntervalSince1970;
    }
}

- (void)onCallDisconnected:(UInt64)callID withError:(NSError *)error
{
    if (self.callInfo.callID == callID) {
        [self dismiss:nil];
        self.chatRoom = nil;
    }
}

- (void)dismiss:(void (^)(void))completion{
    NSLog(@"%s dismiss",TAG);
    //只要页面消失，就挂断
    if (self.callInfo.callID != 0) {
        [[NIMAVChatSDK sharedSDK].netCallManager hangup:self.callInfo.callID];
        self.chatRoom = nil;
    }
    [self dismissViewControllerAnimated:YES completion:nil];
    
    // 通知给js
    NIMModel *model = [NIMModel initShareMD];
    NSDictionary *dd = @{@"status":@YES};
    model.videoHangup = dd;
}

- (void)hangup{
    NSLog(@"%s hangup",TAG);
    [[NIMAVChatSDK sharedSDK].netCallManager hangup:self.callInfo.callID];
    self.chatRoom = nil;
    [self dismiss:nil];
}

- (void)onHangup:(UInt64)callID by:(NSString *)user{
    if (self.callInfo.callID == callID) {
        if (self.callInfo.localRecording) {
            __weak typeof(self) wself = self;
            dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(2 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
                [wself dismiss:nil];
            });
        }
        else {
            [self dismiss:nil];
        }
    }else{
        [self dismiss:nil];
    }
}

- (void)response:(BOOL)accept{
    __weak typeof(self) wself = self;
    NSLog(@"%s response:%d",TAG,accept);
//    NIMNetCallOption *option = [[NIMNetCallOption alloc] init];
//    option.webrtcCompatible = YES;
//    option.serverRecordAudio = YES;
//    option.serverRecordVideo = YES;
//    //指定 option 中的 videoCaptureParam 参数
//    NIMNetCallVideoCaptureParam *param = [[NIMNetCallVideoCaptureParam alloc] init];
//    //清晰度480P
//    param.preferredVideoQuality = NIMNetCallVideoQuality480pLevel;
//    //裁剪类型 NO
//    param.videoCrop  = NIMNetCallVideoCropNoCrop;
//    //打开初始为前置摄像头
//    param.startWithBackCamera = NO;
//    option.videoCaptureParam = param;
    NIMNetCallOption *option = [[NIMNetCallOption alloc] init];
    [self fillUserSetting:option];
    [[NIMAVChatSDK sharedSDK].netCallManager response:self.callInfo.callID accept:accept option:option completion:^(NSError *error, UInt64 callID) {
        if (!error) {
            [wself onCalling];
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
            [wself dismiss:nil];
        }
    }];
    //dismiss需要放在self后面，否在ios7下会有野指针
    if (accept) {
        [self waitForConnectiong];
    }else{
        [self dismiss:nil];
    }
}


- (void)setLocalVideoLayer:(CALayer *)localVideoLayer{
    if (_localVideoLayer != localVideoLayer) {
        _localVideoLayer = localVideoLayer;
    }
}

- (void)onLocalPreviewReady:(CALayer *)layer{
    if (self.localVideoLayer) {
        [self.localVideoLayer removeFromSuperlayer];
    }
    self.localVideoLayer = layer;
    layer.frame = self.localView.bounds;
    [self.localView.layer addSublayer:layer];
}

#pragma mark - NIMNetCallManagerDelegate
- (void)onLocalDisplayviewReady:(UIView *)displayView
{
    if (_calleeBasy) {
        return;
    }
    
    if (self.localPreView) {
        [self.localPreView removeFromSuperview];
    }
    
    self.localPreView = displayView;
    displayView.frame = self.localView.bounds;
    
    [self.localView addSubview:displayView];
}


#if defined(NTESUseGLView)
- (void)onRemoteYUVReady:(NSData *)yuvData
                   width:(NSUInteger)width
                  height:(NSUInteger)height
                    from:(NSString *)user{
//    NSLog(@"%s onRemoteYUVReady:%@",TAG,user);
    if (([[UIApplication sharedApplication] applicationState] == UIApplicationStateActive) && !self.oppositeCloseVideo) {
        
        if (!_remoteGLView) {
            [self initRemoteGLView];
        }
        [_remoteGLView render:yuvData width:width height:height];
        
        //把本地view设置在对方的view之上
//        [self.remoteGLView addSubview:self.localView];
//                [self.remoteGLView addSubview:dismissBtn];
    }

}
#else
- (void)onRemoteImageReady:(CGImageRef)image{
    NSLog(@"%s onRemoteImageReady:%@",TAG,image);
    if (self.oppositeCloseVideo) {
        return;
    }
    self.remoteView.contentMode = UIViewContentModeScaleAspectFill;
    self.remoteView.image = [UIImage imageWithCGImage:image];
}
#endif

- (void)initRemoteGLView {
#if defined(NTESUseGLView)
    NSLog(@"%s initRemoteGLView",TAG);
    _remoteGLView = [[NTESGLView alloc] initWithFrame:_remoteView.bounds];
    // [_remoteGLView setContentMode:[[NTESBundleSetting sharedConfig] videochatRemoteVideoContentMode]];
    [_remoteGLView setContentMode:UIViewContentModeCenter];
    [_remoteGLView setBackgroundColor:[UIColor clearColor]];
    _remoteGLView.autoresizingMask = UIViewAutoresizingFlexibleLeftMargin | UIViewAutoresizingFlexibleRightMargin | UIViewAutoresizingFlexibleTopMargin | UIViewAutoresizingFlexibleBottomMargin | UIViewAutoresizingFlexibleWidth | UIViewAutoresizingFlexibleHeight;
    NSLog(@"%s initRemoteGLView: _remoteGLView %@",TAG, _remoteGLView);
    [_remoteView addSubview:_remoteGLView];
#endif
}


- (void)resetRemoteImage{
    NSLog(@"%s resetRemoteImage",TAG);
#if defined (NTESUseGLView)
    [self.remoteGLView render:nil width:0 height:0];
#endif
    
    self.remoteView.image = [UIImage imageNamed:@"netcall_bkg.jpg"];
}




- (void)onControl:(UInt64)callID
             from:(NSString *)user
             type:(NIMNetCallControlType)control{
    NSLog(@"%s onControl:%llu,%@",TAG, callID, user);
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
                        [self dismiss:nil];
                    }
                });
            }
            break;
        }
            
        case NIMNetCallControlTypeBusyLine:
            NSLog(@"%s onControl:占线",TAG);
            
            break;
        default:
            break;
    }
}


#pragma mark - Misc
//检查设备可用性
- (void)checkServiceEnable:(void(^)(BOOL))result{
    NSLog(@"%s checkServiceEnable",TAG);
    if ([[AVAudioSession sharedInstance] respondsToSelector:@selector(requestRecordPermission:)]) {
        [[AVAudioSession sharedInstance] performSelector:@selector(requestRecordPermission:) withObject:^(BOOL granted) {
            dispatch_async_main_safe(^{
                if (granted) {
                    NSString *mediaType = AVMediaTypeVideo;
                    AVAuthorizationStatus authStatus = [AVCaptureDevice authorizationStatusForMediaType:mediaType];
                    if(authStatus == AVAuthorizationStatusRestricted || authStatus == AVAuthorizationStatusDenied){
                        UIAlertView *alert = [[UIAlertView alloc] initWithTitle:nil
                                                                        message:@"相机权限受限,无法视频聊天"
                                                                       delegate:nil
                                                              cancelButtonTitle:@"确定"
                                                              otherButtonTitles:nil];
                        [alert show];
                        
                    }else{
                        //成功，相机麦克风都可用
                        if (result) {
                            result(YES);
                        }
                    }
                }
                else {
                    UIAlertView *alert = [[UIAlertView alloc] initWithTitle:nil
                                                                    message:@"麦克风权限受限,无法聊天"
                                                                   delegate:nil
                                                          cancelButtonTitle:@"确定"
                                                          otherButtonTitles:nil];
                    [alert show];
                }
                
            });
        }];
    }
}

// dict字典转json字符串
- (NSString *)jsonStringWithDictionary:(NSDictionary *)dict
{
    if (dict && 0 != dict.count)
    {
        NSError *error = nil;
        // NSJSONWritingOptions 是"NSJSONWritingPrettyPrinted"的话有换位符\n；是"0"的话没有换位符\n。
        NSData *jsonData = [NSJSONSerialization dataWithJSONObject:dict options:0 error:&error];
        NSString *jsonString = [[NSString alloc] initWithData:jsonData encoding:NSUTF8StringEncoding];
        return jsonString;
    }
    
    return nil;
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

@end
