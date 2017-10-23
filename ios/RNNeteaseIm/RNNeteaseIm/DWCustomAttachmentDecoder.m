//
//  DWCustomAttachmentDecoder.m
//  RNNeteaseIm
//
//  Created by Dowin on 2017/6/13.
//  Copyright © 2017年 Dowin. All rights reserved.
//

#import "DWCustomAttachmentDecoder.h"

@implementation DWCustomAttachmentDecoder

- (id<NIMCustomAttachment>)decodeAttachment:(NSString *)content
{
    id<NIMCustomAttachment> attachment = nil;
    
    NSData *data = [content dataUsingEncoding:NSUTF8StringEncoding];
    if (data) {
        NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:data
                                                             options:0
                                                               error:nil];
        if ([dict isKindOfClass:[NSDictionary class]])
        {
            NSString *strType     = [self jsonString:@"msgtype" andDict:dict];
            NSInteger custType = 0;
            if ([strType isEqualToString:@"redpacket"]) {//红包
                custType = CustomMessgeTypeRedpacket;
            }else if([strType isEqualToString:@"transfer"]){//转账
                custType = CustomMessgeTypeBankTransfer;
            }else if([strType isEqualToString:@"url"]){//链接
                custType = CustomMessgeTypeUrl;
            }else if([strType isEqualToString:@"account_notice"]){//账户资金变动
                custType = CustomMessgeTypeAccountNotice;
            }else if ([strType isEqualToString:@"redpacketOpen"]){//拆红包消息
                custType = CustomMessgeTypeRedPacketOpenMessage;
            }else if([strType isEqualToString:@"card"]){//名片
                custType = CustomMessgeTypeBusinessCard;
            }
            NSDictionary *dataDict = [self jsonDict:@"data" andDict:dict];
            DWCustomAttachment *obj = [[DWCustomAttachment alloc]init];
            obj.custType = custType;
            obj.dataDict = dataDict;
            attachment = obj;
        }
    }
    return attachment;
}


- (NSString *)jsonString: (NSString *)key andDict:(NSDictionary *)dict
{
    id object = [dict objectForKey:key];
    if ([object isKindOfClass:[NSString class]])
    {
        return object;
    }
    else if([object isKindOfClass:[NSNumber class]])
    {
        return [object stringValue];
    }
    return nil;
}

- (NSDictionary *)jsonDict: (NSString *)key andDict:(NSDictionary *)dict
{
    id object = [dict objectForKey:key];
    return [object isKindOfClass:[NSDictionary class]] ? object : nil;
}

@end
