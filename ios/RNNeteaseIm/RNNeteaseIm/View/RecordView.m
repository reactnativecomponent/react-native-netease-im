//
//  RecordView.m
//  RNNeteaseIm
//
//  Created by Dowin on 2017/6/27.
//  Copyright © 2017年 Dowin. All rights reserved.
//

#import "RecordView.h"
#import "DWRecordButton.h"

@interface RecordView()<DWRecordDelegate>

@end

@implementation RecordView

- (dispatch_queue_t)methodQueue
{
    return dispatch_get_main_queue();
}

RCT_EXPORT_MODULE()

RCT_EXPORT_VIEW_PROPERTY(onChange, RCTBubblingEventBlock)
RCT_EXPORT_VIEW_PROPERTY(textArr, NSArray);


- (UIView *)view{
    //实际组件的具体大小由js控制
    DWRecordButton *recordBtn = [[DWRecordButton alloc]init];
    recordBtn.delegate = self;
    return recordBtn;
}

#pragma mark -- DWRecordButtonDelegate

- (void)recordTouchDownAction:(DWRecordButton *)btn{
    NSLog(@"开始录音");
    if (btn.highlighted) {
        btn.highlighted = YES;
        [btn setButtonStateWithRecording];
    }
    if (!btn.onChange) {
        return;
    }
    btn.onChange(@{@"status":@"Start"});
}
- (void)recordTouchUpOutsideAction:(DWRecordButton *)btn{
    NSLog(@"取消录音");
    [btn setButtonStateWithNormal];
    if (!btn.onChange) {
        return;
    }
    btn.onChange(@{@"status":@"Canceled"});
}
- (void)recordTouchUpInsideAction:(DWRecordButton *)btn{
    NSLog(@"完成录音");
    [btn setButtonStateWithNormal];
    if (!btn.onChange) {
        return;
    }
    btn.onChange(@{@"status":@"Complete"});
}
- (void)recordTouchDragInsideAction:(DWRecordButton *)btn{
    //持续调用
}
- (void)recordTouchDragOutsideAction:(DWRecordButton *)btn{
    //持续调用
}
//中间状态  从 TouchDragOutside ---> TouchDragInside
- (void)recordTouchDragEnterAction:(DWRecordButton *)btn{
    NSLog(@"继续录音");
    if (!btn.onChange) {
        return;
    }
    btn.onChange(@{@"status":@"Continue"});
}
//中间状态  从 TouchDragInside ---> TouchDragOutside
- (void)recordTouchDragExitAction:(DWRecordButton *)btn{
    NSLog(@"将要取消录音");
    if (!btn.onChange) {
        return;
    }
    btn.onChange(@{@"status":@"Move"});
}


@end
