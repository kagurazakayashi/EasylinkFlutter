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
        [self linkstart:call result:result];
    }else if([@"linkstop" isEqualToString:call.method]){
        [self linkstop:call result:result];
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
//    if (!ssid || [ssid length] == 0) result([FlutterError errorWithCode:@"invalid ssid" message:@"ssid cannot be empty" details:@""]);
//    if (!key || [key length] == 0) result([FlutterError errorWithCode:@"invalid password" message:@"password cannot be empty" details:@""]);
//    if (!timeout || [timeout length] == 0) result([FlutterError errorWithCode:@"invalid timeout" message:@"timeout cannot be empty" details:@""]);
    mx = [[MXCHIPAirlink alloc] init];
    EasyLinkMode modeid = [mode intValue];
    
    [mx start:ssid key:key timeout:[timeout intValue] mode:modeid  andCallback:^(MXCHIPAirlinkEvent event) {
        if (event == MXCHIPAirlinkEventStop){
//            result(@"Stop");
            [channel invokeMethod:@"onCallback" arguments:@"Stop"];
        } else if (event == MXCHIPAirlinkEventFound) {
//            result(@"Found");
            NSMutableDictionary *datadic = nil;
            if (mx.mataDataDict) {
                datadic = [NSMutableDictionary dictionary];
                for (NSString *key in mx.mataDataDict) {
                    NSData *val = [mx.mataDataDict objectForKey:key];
                    NSString *strv = [[NSString alloc] initWithData:val encoding:NSUTF8StringEncoding];
//                    NSString *strv = [val base64EncodedStringWithOptions:NSDataBase64EncodingEndLineWithLineFeed];
                    [datadic setValue:strv forKey:key];
                }
            } else {
                datadic = [NSMutableDictionary dictionary];
            }
            if (mx.name && [mx.name length] > 0) {
                [datadic setValue:mx.name forKey:@"name"];
            }
            NSData *jsonData = [NSJSONSerialization dataWithJSONObject:datadic options:NSJSONWritingPrettyPrinted error:nil];
            NSString *jsonString = [[NSString alloc] initWithData:jsonData encoding:NSUTF8StringEncoding];
            [channel invokeMethod:@"onCallback" arguments:jsonString];
        } else {
//            result(@"Unknown");
            [channel invokeMethod:@"onCallback" arguments:@"Unknown"];
        }
        mx = nil;
    }];
    result(@"start");
}
- (void)linkstop:(FlutterMethodCall*)call result:(FlutterResult)result{
    [mx stop];
    result(@"stop");
}
@end
