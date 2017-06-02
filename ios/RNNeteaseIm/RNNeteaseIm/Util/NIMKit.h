//
//  NIMKit.h
//  NIMKit
//
//  Created by amao on 8/14/15.
//  Copyright (c) 2015 NetEase. All rights reserved.
//

#import <Foundation/Foundation.h>


//! Project version number for NIMKit.
FOUNDATION_EXPORT double NIMKitVersionNumber;

//! Project version string for NIMKit.
FOUNDATION_EXPORT const unsigned char NIMKitVersionString[];

// In this header, you should import all the public headers of your framework using statements like #import <NIMKit/PublicHeader.h>

#import <NIMSDK/NIMSDK.h>

/**
 *  基础Model
 */
#import "NIMKitInfo.h"
#import "NIMKitDataProvider.h"










@interface NIMKit : NSObject

+ (instancetype)sharedKit;


/**
 *  内容提供者，由上层开发者注入。如果没有则使用默认 provider
 */
@property (nonatomic,strong)    id<NIMKitDataProvider> provider;

/**
 *  NIMKit图片资源所在的 bundle 名称。
 */
@property (nonatomic,copy)      NSString *resourceBundleName;

/**
 *  NIMKit表情资源所在的 bundle 名称。
 */
@property (nonatomic,copy)      NSString *emoticonBundleName;

/**
 *  NIMKit设置资源所在的 bundle 名称。
 */
@property (nonatomic,copy)      NSString *settingBundleName;


/**
 *  用户信息变更通知接口
 *
 *  @param userId 用户id
 */
- (void)notfiyUserInfoChanged:(NSArray *)userIds;

/**
 *  群信息变更通知接口
 *
 *  @param teamId 群id
 */
- (void)notifyTeamInfoChanged:(NSArray *)teamIds;


/**
 *  群成员变更通知接口
 *
 *  @param teamId 群id
 */
- (void)notifyTeamMemebersChanged:(NSArray *)teamIds;

/**
 *  返回用户信息
 */
- (NIMKitInfo *)infoByUser:(NSString *)userId
                    option:(NIMKitInfoFetchOption *)option;

/**
 *  返回群信息
 */
- (NIMKitInfo *)infoByTeam:(NSString *)teamId
                    option:(NIMKitInfoFetchOption *)option;

@end



