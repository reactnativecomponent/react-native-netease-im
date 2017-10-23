//
//  DWCustomAttachment.m
//  RNNeteaseIm
//
//  Created by Dowin on 2017/6/13.
//  Copyright © 2017年 Dowin. All rights reserved.
//

#import "DWCustomAttachment.h"

@implementation DWCustomAttachment

- (NSString *)encodeAttachment{
    NSString *strType = @"";
    switch (self.custType) {
        case CustomMessgeTypeRedpacket:
            strType = @"redpacket";
            break;
        case CustomMessgeTypeBankTransfer:
            strType = @"transfer";
            break;
        case CustomMessgeTypeUrl:
            strType = @"url";
            break;
        case CustomMessgeTypeAccountNotice:
            strType = @"account_notice";
            break;
        case CustomMessgeTypeRedPacketOpenMessage:
            strType = @"redpacketOpen";
            break;
        case CustomMessgeTypeBusinessCard:
            strType = @"card";
            break;
        default:
            break;
    }
    
    NSDictionary *dict = @{@"msgtype" : strType,@"data":self.dataDict};
    NSData *data = [NSJSONSerialization dataWithJSONObject:dict
                                                   options:0
                                                     error:nil];
    NSString *content = nil;
    if (data) {
        content = [[NSString alloc] initWithData:data
                                        encoding:NSUTF8StringEncoding];
    }
    return content;
}

@end
