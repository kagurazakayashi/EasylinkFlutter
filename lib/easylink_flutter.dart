// You have generated a new plugin project without specifying the `--platforms`
// flag. A plugin project with no platform support was generated. To add a
// platform, run `flutter create -t plugin --platforms <platforms> .` under the
// same directory. You can also find a detailed instruction on how to add
// platforms in the `pubspec.yaml` at
// https://flutter.dev/docs/development/packages-and-plugins/developing-packages#plugin-platforms.

import 'easylink_flutter_platform_interface.dart';

enum EasyLinkMode {
  EASYLINK_V1,
  EASYLINK_V2,
  EASYLINK_PLUS,
  EASYLINK_V2_PLUS,
  EASYLINK_AWS,
  EASYLINK_SOFT_AP,
  EASYLINK_MODE_MAX,
}

class EasyLink {
  Future<String?> linkstart(
      {required String ssid,
      required String password,
      EasyLinkMode mode = EasyLinkMode.EASYLINK_V2_PLUS,
      int timeout = 60}) {
    return EasyLinkPlatform.instance.linkstart(
        ssid: ssid, password: password, mode: mode, timeout: timeout);
  }

  Future<void> ls() {
    return EasyLinkPlatform.instance.ls();
  }

  Future<String?> linkstop() {
    return EasyLinkPlatform.instance.linkstop();
  }

  // ignore: always_specify_types
  Future<Map?> getwifiinfo() {
    return EasyLinkPlatform.instance.getwifiinfo();
  }
}
