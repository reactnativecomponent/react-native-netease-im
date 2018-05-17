//
//  NetCallChatInfo.h
//  NIMVideoDEMO
//
//  Created by 张茜倩 on 16/5/4.
//  Copyright © 2016年 Xiqian Zhang. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface NetCallChatInfo : NSObject

@property(nonatomic,strong) NSString *caller;

@property(nonatomic,strong) NSString *callee;

@property(nonatomic,assign) UInt64 callID;

@property(nonatomic,assign) NIMNetCallType callType;

@property(nonatomic,assign) NSTimeInterval startTime;

@property(nonatomic,assign) BOOL isStart;

@property(nonatomic,assign) BOOL isMute;

@property(nonatomic,assign) BOOL useSpeaker;

@property(nonatomic,assign) BOOL disableCammera;

@property(nonatomic,assign) BOOL localRecording;


@end
