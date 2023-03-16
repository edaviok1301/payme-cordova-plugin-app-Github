#import <UIKit/UIKit.h>
#import <Payme/Payme.h>
#import "SdkPayme.h"
#import "RSA.h"
#import "Firebase.h"

@interface PayViewController : UIViewController<PaymeClientDelegate>

+ (instancetype)sharedHelper:(NSDictionary *)inputRequest callback:(NSString *)callbackid;
- (id)initWithArgs:(NSDictionary *)request;
- (void)presentPayMeControllerWithDelegate:(NSDictionary *)request;
- (NSString *)validateEmptyOrNull:(NSString *)value;
- (NSString *)getPrivateKey;
- (NSString* )decryptString:(NSString *)string;
- (NSDictionary *) logEvent:(NSString *)eventCategory eventAction:(NSString *)eventAction eventLabel:(NSString *)eventLabel;

@property (strong, nonatomic) NSDictionary* request;
@property (strong, nonatomic) NSString *resultResponse;
@property (strong, nonatomic) NSString *callbackId;
@property NSInteger setEnviroment;

@end
