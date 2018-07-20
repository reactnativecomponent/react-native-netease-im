//
//  RNVideoChatView.h
//  RNNimAvchat
//
//  Created by zpd106.
//  Copyright © 2018. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface RNVideoChatView : UIView

-(void)call:(NSString *)callee;

-(void)accept:(BOOL )type callid:(NSString *)callID from:(NSString *)caller;

-(void)hangup;

@end
