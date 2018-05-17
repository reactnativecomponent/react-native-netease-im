//
//  RNVideoChatView.m
//  RNNimAvchat
//
//  Created by zpd106.
//  Copyright © 2018. All rights reserved.
//

#import "RNVideoChatView.h"
#import "NTESVideoChatViewController.h"
#import "NIMModel.h"

#define SCREEN_W [UIScreen mainScreen].bounds.size.width
#define SCREEN_H [UIScreen mainScreen].bounds.size.height

#define TAG "RNVideoChatView"

@interface RNVideoChatView ()

@property (nonatomic, strong) NTESVideoChatViewController * vc;
@property (nonatomic, strong) UIView * uv;

@end

@implementation RNVideoChatView

- (instancetype) initWithFrame:(CGRect)frame{
    self = [super initWithFrame:frame];
    if (self) {
        __weak typeof(self) wself = self;
        NSLog(@"%s initWithFrame:wself:%@", TAG, wself);
        dispatch_async(dispatch_get_main_queue(), ^{
            wself.vc = [[NTESVideoChatViewController alloc] init];
            
//            wself.vc.view.frame = CGRectMake(0, 0, 300, 400);
//            wself.vc.view.transform = CGAffineTransformMakeScale(0.5, 0.5);

            wself.uv = [[UIView alloc] init];
            wself.uv.frame = frame;
            wself.vc.view.frame = wself.uv.frame;
//            wself.vc.view.transform = CGAffineTransformMakeScale(1, 1);
            [wself.vc.view setAutoresizesSubviews:YES];
            [wself.uv setAutoresizesSubviews:YES];
            [wself.uv addSubview: wself.vc.view];

            NSLog(@"%s initWithFrame:vc:%@", TAG, wself.vc.view);
            NSLog(@"%s initWithFrame:uv:%@", TAG, wself.uv);
            [wself addSubview:wself.uv];
        });
    }
    return self;
}

- (instancetype)init {
    if (self = [super init]) {
        
    }
    return self;
}


// 拨打
-(void)call:(NSString *)callee {
    NSLog(@"%s call:%@", TAG, callee);
    dispatch_async(dispatch_get_main_queue(), ^{
        if(_vc != nil){
            [_vc startCall:callee];
        }
    });
}

// 接听/拒绝
-(void)accept:(BOOL )type callid:(NSString *)callID from:(NSString *)caller {
    NSLog(@"%s accept:%d, %@, %@", TAG, type, callID, caller);
    dispatch_async(dispatch_get_main_queue(), ^{
        if(callID != nil && caller != nil){
            [_vc initWithCaller:caller callId:[callID longLongValue]];
            [_vc response:type];
        }
    });
}

// 挂断
-(void)hangup {
    NSLog(@"%s hangup", TAG);
    dispatch_async(dispatch_get_main_queue(), ^{
        if(_vc != nil){
            [_vc hangup];
        }
    });
}

-(void)setWidth:(NSInteger)width
{
    self.uv.frame = CGRectMake(0, 0, width, self.uv.frame.size.height);;
    [self layoutSubviews];
}

-(void)setHeight:(NSInteger)height
{
    self.uv.frame = CGRectMake(0, 0, self.uv.frame.size.width, height);;
    [self layoutSubviews];
}

@end
