//
//  NIMModel.m
//  NIM
//
//  Created by Dowin on 2017/4/28.
//  Copyright © 2017年 Dowin. All rights reserved.
//

#import "NIMModel.h"

@implementation NIMModel
+(instancetype)initShareMD{
    static NIMModel *nimModel =nil;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        nimModel = [[NIMModel alloc]init];
    });
    return nimModel;
}

//-(void)setRecentListArr:(NSMutableArray *)recentListArr{
//    if (_recentListArr != recentListArr) {
//        _recentListArr = recentListArr;
//        self.myBlock(1, recentListArr);
//    }
//}

- (void)setRecentDict:(NSDictionary *)recentDict{
    if ((_recentDict != recentDict)&&(recentDict.count)) {
        _recentDict = recentDict;
        if (self.myBlock) {
            self.myBlock(1, recentDict);
        }
    }
}

-(void)setNetStatus:(NSString *)NetStatus{
    if ((_NetStatus != NetStatus)&&(NetStatus.length)) {
        _NetStatus = NetStatus;
        if (self.myBlock) {
            self.myBlock(0, NetStatus);
        }
    }
}
-(void)setNIMKick:(NSString *)NIMKick{
    if ((_NIMKick != NIMKick)&&(NIMKick.length)) {
        _NIMKick = NIMKick;
        if (self.myBlock) {
            self.myBlock(2, NIMKick);
        }
    }
}
//通信录列表
-(void)setContactList:(NSMutableDictionary *)contactList{
    if (_contactList != contactList) {
        _contactList = contactList;
        if (self.myBlock) {
            self.myBlock(3, contactList);
        }
    }
}
-(void)setNotiArr:(NSMutableArray *)notiArr{
    _notiArr = notiArr;
    if (self.myBlock) {
        self.myBlock(5, notiArr);
    }
}
-(void)setTeamArr:(NSMutableArray *)teamArr{
    if (self.myBlock) {
        self.myBlock(4, teamArr);
    }
}
//未读条数
-(void)setUnreadCount:(NSInteger)unreadCount{
    if (self.myBlock) {
        self.myBlock(6, [NSString stringWithFormat:@"%ld",unreadCount]);
    }
    
}
//
-(void)setResorcesArr:(NSMutableArray *)ResorcesArr{
    if (self.myBlock) {
        self.myBlock(7, ResorcesArr);
    }
}
//开始发送
-(void)setStartSend:(NSDictionary *)startSend{
    if (startSend.count) {
        if (self.myBlock) {
             self.myBlock(8, startSend);
        }
    }
}
//结束发送
-(void)setEndSend:(NSDictionary *)endSend{
    if (endSend.count) {
        if (self.myBlock) {
            self.myBlock(9, endSend);
        }
    }
}
//发送进度（图片等附件）
-(void)setProcessSend:(NSDictionary *)processSend{
    if (processSend.count) {
        if (self.myBlock) {
            self.myBlock(10, processSend);
        }
    }
}
//已读通知
-(void)setReceipt:(NSString *)receipt{
    if (receipt.length) {
        if (self.myBlock) {
          self.myBlock(11, receipt);
        }
    }
}
//发送消息
-(void)setSendState:(NSMutableArray *)sendState{
    if (_sendState != sendState) {
        if (self.myBlock) {
        self.myBlock(12, sendState);
        }

    }
}
//黑名单列表
-(void)setBankList:(NSMutableArray *)bankList{
        if (self.myBlock) {
        self.myBlock(13, bankList);
    }
}

//录音进度
-(void)setAudioDic:(NSDictionary *)audioDic{
    if (audioDic.count) {
        if (self.myBlock) {
            self.myBlock(14, audioDic);
        }
    }
}

- (void)setDeleteMessDict:(NSDictionary *)deleteMessDict{
    if (deleteMessDict.count) {
        if (self.myBlock) {
            self.myBlock(15, deleteMessDict);
        }
    }
}

@end
