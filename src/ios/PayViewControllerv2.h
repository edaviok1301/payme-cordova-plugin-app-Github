#import <UIKit/UIKit.h>
#import <Payme/Payme.h>
#import "SdkPaymev2.h"
#import "Firebase.h"

@interface PayViewControllerv2 : UIViewController<PaymeClientDelegate>

+ (instancetype)sharedHelper:(NSDictionary *)inputRequest callback:(NSString *)callbackid;
- (id)initWithArgs:(NSDictionary *)request;
- (void)presentPayMeControllerWithDelegate:(NSDictionary *)request;
- (NSString *)validateEmptyOrNull:(NSString *)value;
- (NSDictionary *) logEvent:(NSString *)eventCategory eventAction:(NSString *)eventAction eventLabel:(NSString *)eventLabel;

@property (strong, nonatomic) NSDictionary* request;
@property (strong, nonatomic) NSString *resultResponse;
@property (strong, nonatomic) NSString *callbackId;
@property NSInteger setEnviroment;

@end
