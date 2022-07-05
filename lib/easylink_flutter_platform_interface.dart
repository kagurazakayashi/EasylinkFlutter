import 'package:easylink_flutter/easylink_flutter.dart';
import 'package:plugin_platform_interface/plugin_platform_interface.dart';

import 'easylink_flutter_method_channel.dart';

abstract class EasyLinkPlatform extends PlatformInterface {
  /// Constructs a EasyLinkPlatform.
  EasyLinkPlatform() : super(token: _token);

  static final Object _token = Object();

  static EasyLinkPlatform _instance = MethodChannelTestPlus();

  /// The default instance of [EasyLinkPlatform] to use.
  ///
  /// Defaults to [MethodChannelTestPlus].
  static EasyLinkPlatform get instance => _instance;

  /// Platform-specific implementations should set this with their own
  /// platform-specific class that extends [EasyLinkPlatform] when
  /// they register themselves.
  static set instance(EasyLinkPlatform instance) {
    PlatformInterface.verifyToken(instance, _token);
    _instance = instance;
  }

  Future<String?> linkstart(
      {required String ssid,
      required String password,
      EasyLinkMode mode = EasyLinkMode.EASYLINK_V2_PLUS,
      int timeout = 60}) {
    throw UnimplementedError('linkstart() has not been implemented.');
  }

  Future<void> ls() {
    throw UnimplementedError('ls() has not been implemented.');
  }

  Future<String?> linkstop() {
    throw UnimplementedError('linkstop() has not been implemented.');
  }

  // ignore: always_specify_types
  Future<Map?> getwifiinfo() {
    throw UnimplementedError('getwifiinfo() has not been implemented.');
  }
}
