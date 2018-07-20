//
//  UIAlertView+NTESBlock.h
//  eim_iphone
//
//  Created by amao on 12-11-7.
//  Copyright (c) 2012年 Netease. All rights reserved.
//

#import <UIKit/UIKit.h>
typedef void (^AlertBlock)(NSInteger);
NS_ASSUME_NONNULL_BEGIN
@interface UIAlertView (NTESBlock)
- (void)showAlertWithCompletionHandler: (__nullable AlertBlock)block;
- (void)clearActionBlock;
@end



@interface UIAlertController (NTESBlock)
- (UIAlertController *)addAction:(NSString *)title
                           style:(UIAlertActionStyle)style
                         handler:(void (^ __nullable)(UIAlertAction *action))handler;

- (void)show;
@end
NS_ASSUME_NONNULL_END
