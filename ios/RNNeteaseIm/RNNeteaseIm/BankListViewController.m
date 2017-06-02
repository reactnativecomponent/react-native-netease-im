//
//  BankListViewController.m
//  RNNeteaseIm
//
//  Created by Dowin on 2017/5/12.
//  Copyright © 2017年 Dowin. All rights reserved.
//

#import "BankListViewController.h"

@interface BankListViewController ()

@end

@implementation BankListViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
}

+(instancetype)initWithBankListViewController{
    static BankListViewController *BackVC = nil;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        BackVC = [[BankListViewController alloc]init];
        
    });
    return BackVC;
}
-(void)initWithDelegate{
}
-(void)getBlackList{
    NSMutableArray *list = [[NSMutableArray alloc] init];
    for (NIMUser *user in [NIMSDK sharedSDK].userManager.myBlackList) {
        NIMKitInfo *info = [[NIMKit sharedKit] infoByUser:user.userId option:nil];
        NSMutableDictionary *dic = [NSMutableDictionary dictionary];
        [dic setObject:[NSString stringWithFormat:@"%@", info.showName] forKey:@"name"];
        [dic setObject:[NSString stringWithFormat:@"%@", info.infoId] forKey:@"contactId"];
        [dic setObject:[NSString stringWithFormat:@"%@", info.avatarUrlString ] forKey:@"avatar"];
        NSArray *keys = [dic allKeys];
        for (NSString *tem  in keys) {
            if ([[dic objectForKey:tem] isEqualToString:@"(null)"]) {
                [dic setObject:@"" forKey:tem];
            }
        }
        [list addObject:dic];
    }
    NIMModel *model = [NIMModel initShareMD];
    model.bankList = list;
}
-(void)addToBlackList:(NSString *)contactId success:(Success)suc Err:(Errors)err{
    [[NIMSDK sharedSDK].userManager addToBlackList:contactId completion:^(NSError * _Nullable error) {
        if (!error) {
            [self getBlackList];
            suc(@"拉黑成功!");
        }else{
            err(@"拉黑失败!");
        }
    }];
}
-(void)removeFromBlackList:(NSString *)contactId success:(Success)suc Err:(Errors)err{
    [[NIMSDK sharedSDK].userManager removeFromBlackBlackList:contactId completion:^(NSError * _Nullable error) {
        if (!error) {
            [self getBlackList];
            suc(@"移除成功!");
        }else{
            err(@"移除失败!");
        }
    }];
}


@end
