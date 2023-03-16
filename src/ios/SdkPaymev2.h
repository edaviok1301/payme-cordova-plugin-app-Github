#import <UIKit/UIKit.h>
#import <Cordova/CDVPlugin.h>
#import <Payme/Payme.h>
#import "PayViewControllerv2.h"
@interface SdkPaymev2 : CDVPlugin<PaymeClientDelegate>

+ (SdkPaymev2 *)sdkPayme;

- (void)initPayme:(CDVInvokedUrlCommand*)command;
- (void)sendResponsePay:(NSString *)responseText callbackId:(NSString *)callbackId;

@property (copy, nonatomic) NSString *responsePayCallbackId;

@end
