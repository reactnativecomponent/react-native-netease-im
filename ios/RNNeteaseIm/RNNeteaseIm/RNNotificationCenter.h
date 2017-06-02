//
//  RNNotificationCenter.h
//  RNNeteaseIm
//
//  Created by Dowin on 2017/5/24.
//  Copyright © 2017年 Dowin. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface RNNotificationCenter : NSObject
+ (instancetype)sharedCenter;
- (void)start;
@end
