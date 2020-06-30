<p align="center"><img src="icon/icon.png" width="128"></p>

# EasylinkFlutter

对基于 MXCHIP/MXOS 的 IoT WiFi 硬件模组进行无线网络设置的发送工作的 Flutter 插件。

## 使用方法

1. 引入本插件。
  - 修改 `pubspec.yaml` 文件，在 `dependencies:` 里面加入：
```
  easylink_flutter:
    git:
      url: https://github.com/kagurazakayashi/EasylinkFlutter.git
```
2. 向用户索取「位置服务」权限。
  - 要获得当前 SSID ，「位置服务」权限是必须的。
  - 如果让用户自己输入网络名，可以跳过本步骤和下一步骤。
  - 示例程序中使用的权限获取插件是 `permission_handler` 。
3. 获取 SSID 。
  - 调用 `EasylinkFlutter.getwifiinfo()` ，可以获得一个包含各种信息的字典。
  - 该字典(Map)通常包括三个字符串数据 `[BSSID,SSID,SSIDDATA]` 。
  - 下面是一个示例：
```
  Future<void> getssid() async {
    try {
      Map wifiinfo = await EasylinkFlutter.getwifiinfo();
      print(wifiinfo["SSID"]);
    } on PlatformException {
      print("ERROR");
    }
  }
```
4. 开始配网。
  - 调用 `EasylinkFlutter.linkstart` ，该方法需要以下参数：
    - `ssid`: Wi-Fi 网络名
    - `password`: Wi-Fi 网络密码
    - `mode`: EasyLink 的模式，示例使用的是 `EasyLinkMode.EASYLINK_V2_PLUS`
    - `timeout`: 超时时间
  - 方法会返回一个字符串状态信息。
  - 下面是一个示例：
```
  await EasylinkFlutter.linkstart(
    ssid: "testwifi",
    password: "testpwd",
    mode: EasyLinkMode.EASYLINK_V2_PLUS,
    timeout: 60
  );
```
5. 监听

## 安卓运行时需要注意的事项

- Android 版暂不支持接收设备返回的信息，但可以正常完成配网。
  - Java 代码中已包括支持接收设备信息的方法 `startFTC` 和 `stopFTC`，但目前如果使用会导致闪退。
- Android 版加入插件线前，需要修改 `android\app\src\main\AndroidManifest.xml` 文件：
  - 在 `<manifest>` 节点添加 `xmlns:tools="http://schemas.android.com/tools"`
  - 在 `<application>` 节点添加 `tools:replace="android:label"`

## 支持版本

- `EASYLINK_V2`
- `EASYLINK_V3`
- `EASYLINK_PLUS`

不支持 `EasylinkP2P`