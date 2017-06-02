//
//  NIMKit.m
//  NIMKit
//
//  Created by amao on 8/14/15.
//  Copyright (c) 2015 NetEase. All rights reserved.
//

#import "NIMKit.h"
#import "NIMKitInfoFetchOption.h"
#import "NIMKitDataProviderImpl.h"
#import "NIMKitNotificationFirer.h"
extern NSString *const NIMKitUserInfoHasUpdatedNotification;
extern NSString *const NIMKitTeamInfoHasUpdatedNotification;


@interface NIMKit()
@property (nonatomic,strong)    NIMKitNotificationFirer *firer;
@end


@implementation NIMKit
- (instancetype)init
{
    if (self = [super init]) {
         _provider = [[NIMKitDataProviderImpl alloc] init];
    }
    return self;
}
- (void)notifyTeamInfoChanged:(NSArray *)teamIds{
    if (teamIds.count) {
        for (NSString *teamId in teamIds) {
            [self notifyTeam:teamId];
        }
    }else{
        [self notifyTeam:nil];
    }
}
- (void)notifyTeam:(NSString *)teamId
{
    NIMKitFirerInfo *info = [[NIMKitFirerInfo alloc] init];
    if (teamId.length) {
        NIMSession *session = [NIMSession session:teamId type:NIMSessionTypeTeam];
        info.session = session;
    }
    info.notificationName = NIMKitTeamInfoHasUpdatedNotification;
    [self.firer addFireInfo:info];
}
- (void)notifyTeamMemebersChanged:(NSArray *)teamIds
{
    if (teamIds.count) {
        for (NSString *teamId in teamIds) {
            [self notifyTeamMemebers:teamId];
        }
    }else{
        [self notifyTeamMemebers:nil];
    }
}

- (void)notifyTeamMemebers:(NSString *)teamId
{
    NIMKitFirerInfo *info = [[NIMKitFirerInfo alloc] init];
    if (teamId.length) {
        NIMSession *session = [NIMSession session:teamId type:NIMSessionTypeTeam];
        info.session = session;
    }
    extern NSString *NIMKitTeamMembersHasUpdatedNotification;
    info.notificationName = NIMKitTeamMembersHasUpdatedNotification;
    [self.firer addFireInfo:info];
}

+ (instancetype)sharedKit
{
    static NIMKit *instance = nil;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        instance = [[NIMKit alloc] init];
    });
    return instance;
}
- (void)notfiyUserInfoChanged:(NSArray *)userIds{
    if (!userIds.count) {
        return;
    }
    for (NSString *userId in userIds) {
        NIMSession *session = [NIMSession session:userId type:NIMSessionTypeP2P];
        NIMKitFirerInfo *info = [[NIMKitFirerInfo alloc] init];
        info.session = session;
        info.notificationName = NIMKitUserInfoHasUpdatedNotification;
        [self.firer addFireInfo:info];
    }
}

- (NIMKitInfo *)infoByUser:(NSString *)userId option:(NIMKitInfoFetchOption *)option
{
    NIMKitInfo *info = nil;
    if (self.provider && [self.provider respondsToSelector:@selector(infoByUser:option:)]) {
        info = [self.provider infoByUser:userId option:option];
    }
    return info;
}

- (NIMKitInfo *)infoByTeam:(NSString *)teamId option:(NIMKitInfoFetchOption *)option
{
    NIMKitInfo *info = nil;
    if (self.provider && [self.provider respondsToSelector:@selector(infoByTeam:option:)]) {
        info = [self.provider infoByTeam:teamId option:option];
    }
    return info;

}

@end



