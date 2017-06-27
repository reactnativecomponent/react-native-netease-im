//
//  DWRecordButton.h
//  RNNeteaseIm
//
//  Created by Dowin on 2017/6/27.
//  Copyright © 2017年 Dowin. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "UIView+React.h"
@class DWRecordButton;

@protocol DWRecordDelegate <NSObject>

@optional
- (void)recordTouchDownAction:(DWRecordButton *)btn;
- (void)recordTouchUpOutsideAction:(DWRecordButton *)btn;
- (void)recordTouchUpInsideAction:(DWRecordButton *)btn;
- (void)recordTouchDragEnterAction:(DWRecordButton *)btn;
- (void)recordTouchDragInsideAction:(DWRecordButton *)btn;
- (void)recordTouchDragOutsideAction:(DWRecordButton *)btn;
- (void)recordTouchDragExitAction:(DWRecordButton *)btn;

@end


@interface DWRecordButton : UIButton

@property (assign, nonatomic) id<DWRecordDelegate> delegate;
@property (copy, nonatomic) RCTBubblingEventBlock onChange;
@property (copy, nonatomic) NSArray *textArr;
- (void)setButtonStateWithRecording;
- (void)setButtonStateWithNormal;
- (void)setButtonStateWithCancel;

@end
