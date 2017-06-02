//
//  NIMModel.h
//  NIM
//
//  Created by Dowin on 2017/4/28.
//  Copyright © 2017年 Dowin. All rights reserved.
//

#import <Foundation/Foundation.h>

typedef void(^onSuccess)(NSInteger index,id param);
@interface NIMModel : NSObject
+(instancetype)initShareMD;
@property(nonatomic,strong)onSuccess myBlock;
@property(nonatomic,strong)NSMutableArray *recentListArr;
@property(nonatomic,strong)NSString *NetStatus;
@property(nonatomic,strong)NSString *NIMKick;
@property(nonatomic,strong)NSMutableDictionary *contactList;
@property(nonatomic,strong)NSMutableArray *notiArr;
@property(nonatomic,assign)NSInteger unreadCount;
@property(nonatomic,strong)NSMutableArray *teamArr;
@property(nonatomic,assign)NSMutableArray *ResorcesArr;
@property(nonatomic,strong)NSMutableArray *sendState;
@property(nonatomic,strong)NSDictionary *startSend;
@property(nonatomic,strong)NSDictionary *endSend;
@property(nonatomic,strong)NSDictionary *processSend;
@property(nonatomic,strong)NSString *receipt;
@property(nonatomic,strong)NSMutableArray *bankList;
@property(nonatomic,strong)NSDictionary *audioDic;
- (void)insertMessages:(NSArray *)messages;
@end
