#import <UIKit/UIKit.h>
#import <Payme/Payme.h>
#import "SdkPayme.h"
#import "RSA.h"

@interface PayViewController : UIViewController<PaymeClientDelegate>

+ (instancetype)sharedHelper:(NSDictionary *)inputRequest callback:(NSString *)callbackid;
- (id)initWithArgs:(NSDictionary *)request;
- (void)presentPayMeControllerWithDelegate:(NSDictionary *)request;
- (NSString *)validateEmptyOrNull:(NSString *)value;
- (NSString *)getPrivateKey;
- (NSString* )decryptString:(NSString *)string;

@property (strong, nonatomic) NSDictionary* request;
@property (strong, nonatomic) NSString *resultResponse;
@property (strong, nonatomic) NSString *callbackId;
@property NSInteger setEnviroment;

@end
