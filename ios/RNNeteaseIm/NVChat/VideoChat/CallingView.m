//
//  CallingView.m
//  RNNeteaseIm
//
//  Created by shane on 2018/5/21.
//  Copyright © 2018年 Dowin. All rights reserved.
//

#import "CallingView.h"

@implementation CallingView

- (instancetype)initWithFrame:(CGRect)frame{
    self = [super initWithFrame:frame];
    if (self) {
        self.userInteractionEnabled = YES;
        self.backgroundColor = [UIColor colorWithRed:248/255.0f green:255/255.0f blue:255/255.0f alpha:1];
        [self initView];
    }
    return self;
}

-(void)initView{
    UILabel *label1 = [[UILabel alloc] initWithFrame:CGRectMake(0, 75, screenW, 30)];
    label1.text = @"专家团队";
    label1.textAlignment = NSTextAlignmentCenter;
    label1.font = [UIFont systemFontOfSize:24];
    label1.textColor = [UIColor colorWithRed:51/255.0f green:51/255.0f blue:51/255.0f alpha:1];
    [self addSubview:label1];
    
    UILabel *label2 = [[UILabel alloc] initWithFrame:CGRectMake(0, 110, screenW, 20)];
    label2.text = @"正在邀请您视频通话…";
    label2.textAlignment = NSTextAlignmentCenter;
    label2.font = [UIFont systemFontOfSize:12];\
    label2.textColor = [UIColor colorWithRed:47/255.0f green:216/255.0f blue:173/255.0f alpha:1];
    [self addSubview:label2];
    
    UIView *round1 = [self createRound:280];
    round1.alpha = 0.1;
    [self addSubview:round1];
    UIView *round2 = [self createRound:260];
    round2.alpha = 0.3;
    [self addSubview:round2];
    UIView *round3 = [self createRound:240];
    round3.alpha = 0.7;
    [self addSubview:round3];
    
    UIImageView *imv = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"avatar_nurse"]];
    imv.frame = CGRectMake(0, 0, 220, 220);
    imv.center = CGPointMake(screenW/2, screenH/2);
    imv.layer.cornerRadius = 110;
    imv.layer.masksToBounds = YES;
    [self addSubview:imv];
}

- (UIView*)createRound:(CGFloat)radius{
    UIView *round = [[UIView alloc] initWithFrame:CGRectMake(0, 0, radius, radius)];
    round.center = CGPointMake(screenW/2, screenH/2);
    round.layer.borderColor = [UIColor lightGrayColor].CGColor;
    round.layer.borderWidth = 1;
    round.layer.cornerRadius = radius/2;
    return round;
}

@end
