#import "AppDelegate.h"
#import "GeneratedPluginRegistrant.h"

@implementation AppDelegate


- (BOOL)application:(UIApplication *)application
    didFinishLaunchingWithOptions:(NSDictionary *)launchOptions {
//  [GeneratedPluginRegistrant registerWithRegistry:self];
    
    FlutterViewController* controller = (FlutterViewController*)self.window.rootViewController;
    UINavigationController* rootViewController = [[UINavigationController alloc] initWithRootViewController:controller];
    rootViewController.navigationBar.hidden = YES;
    self.window.rootViewController = rootViewController;
    [GeneratedPluginRegistrant registerWithRegistry:controller];
    
  // Override point for customization after application launch.
  return [super application:application didFinishLaunchingWithOptions:launchOptions];
}

@end
