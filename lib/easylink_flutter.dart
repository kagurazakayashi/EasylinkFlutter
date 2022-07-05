// 插件程序
import 'dart:async';

import 'package:easylink_flutter/easylink_notification.dart';
import 'package:flutter/services.dart';

enum EasyLinkMode {
  EASYLINK_V1,
  EASYLINK_V2,
  EASYLINK_PLUS,
  EASYLINK_V2_PLUS,
  EASYLINK_AWS,
  EASYLINK_SOFT_AP,
  EASYLINK_MODE_MAX,
}

// ignore: avoid_classes_with_only_static_members
class EasylinkFlutter {
  // 建立OC通道
  // static MethodChannel _channel =
  //     const MethodChannel('easylink_flutter').setMethodCallHandler((MethodCall methodCall){

  //     });

  static final MethodChannel _channel = const MethodChannel('easylink_flutter')
    ..setMethodCallHandler((MethodCall methodCall) {
      if ('onCallback' == methodCall.method) {
        EasyLinkNotification.instance.postNotification('linkstate', methodCall.arguments);
      }
      // ignore: always_specify_types
      return Future.value(true);
    });

  // 开始配网
  static Future<String?> linkstart(
      {required String ssid,
      required String password,
      EasyLinkMode mode = EasyLinkMode.EASYLINK_V2_PLUS,
      int timeout = 60}) async {
    print(mode);
    // ignore: always_specify_types
    final String? version = await _channel.invokeMethod('linkstart', {
      'ssid': ssid,
      'key': password,
      'mode': mode.index.toString(),
      'timeout': timeout.toString()
    });
    print(version);
    return version;
  }
  static Future<void> ls() async {
    await _channel.invokeMethod('ls');
  }
  static Future<String?> linkstop() async {
    final String? version = await _channel.invokeMethod('linkstop');
    print(version);
    return version;
  }

  // ignore: always_specify_types
  static Future<Map?> getwifiinfo() async {
    // ignore: always_specify_types
    final Map? wifiinfo = await _channel.invokeMethod('getwifiinfo');
    //wifiinfo: BSSID,SSID,SSIDDATA
    return wifiinfo;
  }
}
