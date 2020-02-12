#import "EasylinkFlutterPlugin.h"
#import "MXCHIPAirlink.h"

@implementation EasylinkFlutterPlugin
FlutterMethodChannel* channel;
MXCHIPAirlink *mx;
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
    channel = [FlutterMethodChannel
      methodChannelWithName:@"easylink_flutter"
            binaryMessenger:[registrar messenger]];
    
    EasylinkFlutterPlugin* instance = [[EasylinkFlutterPlugin alloc] init];
    [registrar addMethodCallDelegate:instance channel:channel];
}

- (void)handleMethodCall:(FlutterMethodCall*)call result:(FlutterResult)result {
     if ([@"getwifiinfo" isEqualToString:call.method]) {
        [self ssid:call result:result];
    }else if ([@"linkstart" isEqualToString:call.method]) {
        [self link:call result:result];
    }else if([@"linkstop" isEqualToString:call.method]){
        [self linkstop];
    }else{
        result(FlutterMethodNotImplemented);

    }
}

- (void)ssid:(FlutterMethodCall*)call result:(FlutterResult)result {
//    NSData *ssiddata = [EASYLINK ssidDataForConnectedNetwork];
//    result([[NSString alloc] initWithBytes:ssiddata.bytes length:ssiddata.length encoding:NSUTF8StringEncoding]);
    NSArray *interfaces = (__bridge_transfer NSArray*)CNCopySupportedInterfaces();
    NSDictionary *info = nil;
    for (NSString *ifname in interfaces) {
        info = (__bridge_transfer NSDictionary*)CNCopyCurrentNetworkInfo((__bridge CFStringRef)ifname);
        if (info && [info count]) {
            break;
        }
        info = nil;
    }

//    NSString *ssid = nil;
//    if ( info ){
//        ssid = [info objectForKey:@"SSID"];
//    }
    
    NSDictionary *returninfo = info? info:[NSDictionary dictionary];
    result(returninfo);
}

- (void)linkstart:(FlutterMethodCall*)call result:(FlutterResult)result {
    NSString *ssid = call.arguments[@"ssid"];
    NSString *key = call.arguments[@"key"];
    NSString *timeout = call.arguments[@"timeout"];
    NSString *mode = call.arguments[@"mode"];
    NSLog(@"ssid=%@",ssid);
    NSLog(@"key=%@",key);
    NSLog(@"timeout=%@",timeout);
    NSLog(@"mode=%@",mode);
//    if (!ssid || [ssid length] == 0) result([FlutterError errorWithCode:@"invalid ssid" message:@"ssid cannot be empty" details:@""]);
//    if (!key || [key length] == 0) result([FlutterError errorWithCode:@"invalid password" message:@"password cannot be empty" details:@""]);
//    if (!timeout || [timeout length] == 0) result([FlutterError errorWithCode:@"invalid timeout" message:@"timeout cannot be empty" details:@""]);
    mx = [[MXCHIPAirlink alloc] init];
    EasyLinkMode modeid = [mode intValue];
    NSLog(@"modeid=%d",modeid);
    NSLog(@"modeid=%d",EASYLINK_V2_PLUS);
    
    [mx start:ssid key:key timeout:[timeout intValue] mode:modeid  andCallback:^(MXCHIPAirlinkEvent event) {
        NSLog(@"event=%d",event == MXCHIPAirlinkEventStop);
        if (event == MXCHIPAirlinkEventStop){
            NSLog(@"resultStop");
//            result(@"Stop");
            [channel invokeMethod:@"onCallback" arguments:@"Stop"];
        } else if (event == MXCHIPAirlinkEventFound) {
            NSLog(@"resultFound");
//            result(@"Found");
            [channel invokeMethod:@"onCallback" arguments:@"Found"];
        } else {
            NSLog(@"resultUnknown");
//            result(@"Unknown");
            [channel invokeMethod:@"onCallback" arguments:@"Unknown"];
        }
    }];
    result(@"start...");
}
- (void)linkstop:(FlutterMethodCall*)call result:(FlutterResult)result{
    [mx stop];
    result(@"start...");
}
@end