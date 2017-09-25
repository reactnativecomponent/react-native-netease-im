//
//  NIMMessageMaker.h
//  NIMKit
//
//  Created by chris.
//  Copyright (c) 2015年 NetEase. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <NIMSDK/NIMSDK.h>

@class NIMKitLocationPoint;

@interface NIMMessageMaker : NSObject

+ (NIMMessage*)msgWithText:(NSString*)text andApnsMembers:(NSArray *)members andeSession:(NIMSession *)session;

+ (NIMMessage *)msgWithAudio:(NSString *)filePath andeSession:(NIMSession *)session;

+ (NIMMessage *)msgWithImage:(UIImage *)image andeSession:(NIMSession *)session;

+ (NIMMessage *)msgWithImagePath:(NSString *)path andeSession:(NIMSession *)session;

+ (NIMMessage *)msgWithVideo:(NSString *)filePath andeSession:(NIMSession *)session;

+ (NIMMessage *)msgWithLocation:(NIMKitLocationPoint*)locationPoint andeSession:(NIMSession *)session;

+ (NIMMessage*)msgWithCustom:(NIMObject *)attachment andeSession:(NIMSession *)session;

+ (NIMMessage*)msgWithCustomAttachment:(DWCustomAttachment *)attachment andeSession:(NIMSession *)session;

@end
