//
//  DWCustomAttachment.h
//  RNNeteaseIm
//
//  Created by Dowin on 2017/6/13.
//  Copyright © 2017年 Dowin. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "ImConfig.h"

@interface DWCustomAttachment : NSObject<NIMCustomAttachment>
@property (assign, nonatomic) NSInteger custType;
@property (strong, nonatomic) NSDictionary *dataDict;
@end
