import 'package:easylink_flutter/easylink_flutter.dart';
import 'package:easylink_flutter/easylink_flutter_notification.dart';
import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

import 'easylink_flutter_platform_interface.dart';

/// An implementation of [EasyLinkPlatform] that uses method channels.
class MethodChannelTestPlus extends EasyLinkPlatform {
  /// The method channel used to interact with the native platform.
  @visibleForTesting
  final MethodChannel methodChannel = const MethodChannel('easylink_flutter')
    ..setMethodCallHandler((MethodCall methodCall) {
      if ('onCallback' == methodCall.method) {
        EasyLinkNotification.instance
            .postNotification('linkstate', methodCall.arguments);
      }
      // ignore: always_specify_types
      return Future.value(true);
    });

  // 开始配网
  @override
  Future<String?> linkstart(
      {required String ssid,
      required String password,
      EasyLinkMode mode = EasyLinkMode.EASYLINK_V2_PLUS,
      int timeout = 60}) async {
    print(mode);
    // ignore: always_specify_types
    final String? version = await methodChannel.invokeMethod('linkstart', {
      'ssid': ssid,
      'key': password,
      'mode': mode.index.toString(),
      'timeout': timeout.toString()
    });
    print(version);
    return version;
  }

  @override
  Future<void> ls() async {
    await methodChannel.invokeMethod('ls');
  }

  @override
  Future<String?> linkstop() async {
    final String? version = await methodChannel.invokeMethod('linkstop');
    print(version);
    return version;
  }

  @override
  // ignore: always_specify_types
  Future<Map?> getwifiinfo() async {
    // ignore: always_specify_types
    final Map? wifiinfo = await methodChannel.invokeMethod('getwifiinfo');
    //wifiinfo: BSSID,SSID,SSIDDATA
    return wifiinfo;
  }
}
