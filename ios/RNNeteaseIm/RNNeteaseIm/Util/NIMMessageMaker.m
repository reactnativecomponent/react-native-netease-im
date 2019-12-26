//
//  NIMMessageMaker.m
//  NIMKit
//
//  Created by chris.
//  Copyright (c) 2015年 NetEase. All rights reserved.
//

#import "NIMMessageMaker.h"
#import "NSString+NIMKit.h"
#import "NIMKitLocationPoint.h"


@implementation NIMMessageMaker

+ (NIMMessage*)msgWithText:(NSString*)text andApnsMembers:(NSArray *)members andeSession:(NIMSession *)session
{
    
    NIMMessage *message = [[NIMMessage alloc] init];
    message.text    = text;
    message.apnsContent = text;
    if (members.count) {
        NIMMessageApnsMemberOption *apnsMemberOption = [[NIMMessageApnsMemberOption alloc]init];
        apnsMemberOption.userIds = members;
        apnsMemberOption.forcePush = YES;
        apnsMemberOption.apnsContent = @"有人@了你";
        message.apnsMemberOption = apnsMemberOption;
    }
    message.apnsContent = text;
    [NIMMessageMaker setupMessagePushBody:message andSession:session];
    return message;
}

+ (NIMMessage*)msgWithAudio:(NSString*)filePath andeSession:(NIMSession *)session
{
    NIMAudioObject *audioObject = [[NIMAudioObject alloc] initWithSourcePath:filePath];
    NIMMessage *message = [[NIMMessage alloc] init];
    message.messageObject = audioObject;
    message.text = @"发来了一段语音";
    [NIMMessageMaker setupMessagePushBody:message andSession:session];
    return message;
}
+ (NIMMessage*)msgWithCustom:(NIMObject *)attachment andeSession:(NIMSession *)session
{
    
    NIMMessage *message               = [[NIMMessage alloc] init];
    NIMCustomObject *customObject     = [[NIMCustomObject alloc] init];
    customObject.attachment           = attachment;
    message.messageObject             = customObject;
    message.apnsContent = @"发来了一条未知消息";
    [NIMMessageMaker setupMessagePushBody:message andSession:session];
    return message;
}
+ (NIMMessage*)msgWithCustomAttachment:(DWCustomAttachment *)attachment andeSession:(NIMSession *)session
{
    
    NIMMessage *message               = [[NIMMessage alloc] init];
    NIMCustomObject *customObject     = [[NIMCustomObject alloc] init];
    customObject.attachment           = attachment;
    message.messageObject             = customObject;
    NSString *text = @"";
    switch (attachment.custType) {
        case CustomMessgeTypeRedpacket:
            text = [NSString stringWithFormat:@"[红包]%@", [attachment.dataDict objectForKey:@"comments"]];
            break;
        case CustomMessgeTypeBankTransfer:
            text = [NSString stringWithFormat:@"[转账]%@", [attachment.dataDict objectForKey:@"comments"]];
            break;
        case CustomMessgeTypeUrl:
            text = [attachment.dataDict objectForKey:@"title"];
            break;
        case CustomMessgeTypeAccountNotice:
            text = [attachment.dataDict objectForKey:@"title"];
            break;
        case CustomMessgeTypeRedPacketOpenMessage:{
            text = @"";
            NIMMessageSetting *seting = [[NIMMessageSetting alloc]init];
            seting.apnsEnabled = NO;
            seting.shouldBeCounted = NO;
            message.setting = seting;
        }
            break;
        case CustomMessgeTypeBusinessCard: //名片
        {
            text = [NSString stringWithFormat:@"[名片]%@", [attachment.dataDict objectForKey:@"name"]];
        }
            break;
        case CustomMessgeTypeCustom: //自定义
        {
            text = [NSString stringWithFormat:@"%@", [attachment.dataDict objectForKey:@"pushContent"]];
        }
            break;
        default:
            text = @"发来了一条未知消息";
            break;
    }
    message.apnsContent = text;
    [NIMMessageMaker setupMessagePushBody:message andSession:session];
    return message;
}

+ (NIMMessage*)msgWithVideo:(NSString*)filePath andeSession:(NIMSession *)session
{
    NSDateFormatter *dateFormatter = [[NSDateFormatter alloc] init];
    [dateFormatter setDateFormat:@"yyyy-MM-dd HH:mm"];
    NSString *dateString = [dateFormatter stringFromDate:[NSDate date]];
    NIMVideoObject *videoObject = [[NIMVideoObject alloc] initWithSourcePath:filePath];
    videoObject.displayName = [NSString stringWithFormat:@"视频发送于%@",dateString];
    NIMMessage *message = [[NIMMessage alloc] init];
    message.messageObject = videoObject;
    message.apnsContent = @"发来了一段视频";
    [NIMMessageMaker setupMessagePushBody:message andSession:session];
    return message;
}
+ (NIMMessage*)msgWithImage:(UIImage*)image andeSession:(NIMSession *)session
{
    NIMImageObject *imageObject = [[NIMImageObject alloc] initWithImage:image];
    NIMImageOption *option  = [[NIMImageOption alloc] init];
    option.compressQuality  = 0.7;
    imageObject.option      = option;
    return [NIMMessageMaker generateImageMessage:imageObject andeSession:session];
}

+ (NIMMessage *)msgWithImagePath:(NSString*)path andeSession:(NIMSession *)session
{
    NSDateFormatter *dateFormatter = [[NSDateFormatter alloc] init];
    [dateFormatter setDateFormat:@"yyyy-MM-dd HH:mm"];
    NSString *dateString = [dateFormatter stringFromDate:[NSDate date]];
    NIMImageObject * imageObject = [[NIMImageObject alloc] initWithFilepath:path];
    imageObject.displayName = [NSString stringWithFormat:@"图片发送于%@",dateString];
    NIMMessage *message     = [[NIMMessage alloc] init];
    message.messageObject   = imageObject;
    message.apnsContent = @"发来了一张图片";
    return [NIMMessageMaker generateImageMessage:imageObject  andeSession:session];
}

+ (NIMMessage *)generateImageMessage:(NIMImageObject *)imageObject andeSession:(NIMSession *)session
{
    NSDateFormatter *dateFormatter = [[NSDateFormatter alloc] init];
    [dateFormatter setDateFormat:@"yyyy-MM-dd HH:mm"];
    NSString *dateString = [dateFormatter stringFromDate:[NSDate date]];
    imageObject.displayName = [NSString stringWithFormat:@"图片发送于%@",dateString];
    NIMMessage *message     = [[NIMMessage alloc] init];
    message.messageObject   = imageObject;
    message.apnsContent = @"发来了一张图片";
    [NIMMessageMaker setupMessagePushBody:message andSession:session];
    return message;
}


+ (NIMMessage*)msgWithLocation:(NIMKitLocationPoint *)locationPoint andeSession:(NIMSession *)session{
    NIMLocationObject *locationObject = [[NIMLocationObject alloc] initWithLatitude:locationPoint.coordinate.latitude
                                                                          longitude:locationPoint.coordinate.longitude
                                                                              title:locationPoint.title];
    NIMMessage *message               = [[NIMMessage alloc] init];
    message.messageObject             = locationObject;
    message.apnsContent = @"发来了一条位置信息";
    [NIMMessageMaker setupMessagePushBody:message andSession:session];
    return message;
}

+ (void)setupMessagePushBody:(NIMMessage *)message andSession:(NIMSession *)session{
    NSMutableDictionary *payload = [NSMutableDictionary dictionary];
    NSString *strSessionID = @"";
    if (session.sessionType == NIMSessionTypeP2P) {//点对点
        strSessionID = [NIMSDK sharedSDK].loginManager.currentAccount;
    }else{
        strSessionID = [NSString stringWithFormat:@"%@",session.sessionId];
    }
    NSString *strSessionType = [NSString stringWithFormat:@"%zd",session.sessionType];
    [payload setObject:@{@"sessionId":strSessionID,@"sessionType":strSessionType} forKey:@"sessionBody"];
    message.apnsPayload = payload;
}

@end
