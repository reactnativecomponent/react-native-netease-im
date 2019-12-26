//
//  ImConfig.h
//  Test
//
//  Created by Kinooo on 2019/12/26.
//  Copyright © 2019年 Kinooo. All rights reserved.
//

#ifndef ImConfig_h
#define ImConfig_h

#import <NIMSDK/NIMSDK.h>
#import "NTESGlobalMacro.h"
#import "NIMKitInfo.h"
#import "NIMKitUtil.h"
#import "NIMKitInfoFetchOption.h"
//#import <CocoaLumberjack/CocoaLumberjack.h>
#import <NIMAVChat/NIMAVChat.h>

#import "NIMKit.h"
#import "NIMObject.h"
#import "DWCustomAttachment.h"
#define NTESNotifyID        @"id"
#define NTESCustomContent  @"content"

#define NTESCommandTyping  (1)
#define NTESCustom         (2)

//#ifdef DEBUG
//static DDLogLevel ddLogLevel = DDLogLevelVerbose;
//#else
//static DDLogLevel ddLogLevel = DDLogLevelInfo;
//#endif

#define NTES_USE_CLEAR_BAR - (BOOL)useClearBar{return YES;}

#define NTES_FORBID_INTERACTIVE_POP - (BOOL)forbidInteractivePop{return YES;}
// Include any system framework and library headers here that should be included in all compilation units.
// You will also need to set the Prefix Header build setting of one or more of your targets to reference this file.
#define CustomMessgeTypeUnknown  101  //未知消息类型
#define CustomMessgeTypeRedpacket  5  //红包类型 redpacket
#define CustomMessgeTypeBankTransfer  6   //转账类型 transfer
#define CustomMessgeTypeUrl  7   // 连接类型 url
#define CustomMessgeTypeAccountNotice  8   //账户通知，与账户金额相关变动 account_notice
#define CustomMessgeTypeRedPacketOpenMessage  9     //发送拆红包
#define CustomMessgeTypeBusinessCard  10     //名片
#define CustomMessgeTypeCustom  102     //名片


#endif /* ImConfig_h */
