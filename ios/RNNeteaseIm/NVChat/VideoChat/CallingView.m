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
    label1.font = [UIFont systemFontOfSize:25];
    label1.textColor = [UIColor colorWithRed:51/255.0f green:51/255.0f blue:51/255.0f alpha:1];
    [self addSubview:label1];
    
    UILabel *label2 = [[UILabel alloc] initWithFrame:CGRectMake(0, 110, screenW, 20)];
    label2.text = @"正在邀请您视频通话…";
    label2.textAlignment = NSTextAlignmentCenter;
    label2.font = [UIFont systemFontOfSize:12];
    label2.textColor = [UIColor colorWithRed:41/255.0f green:217/255.0f blue:173/255.0f alpha:1];
    [self addSubview:label2];
    
    UIImage *image = [UIImage imageNamed:@"avatar_nurse"];
    UIImageView *imv = [[UIImageView alloc] initWithImage:image];
    imv.frame = CGRectMake(0, 0, image.size.width, image.size.height);
    imv.center = CGPointMake(screenW/2, screenH/2);
    [self addSubview:imv];
}

@end
