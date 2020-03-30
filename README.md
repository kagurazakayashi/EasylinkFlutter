# EasylinkFlutter

对基于 MXCHIP/MXOS 的 IoT WiFi 硬件模组进行无线网络设置的发送工作的 Flutter 插件。

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