# EasylinkFlutter

对基于 MXCHIP/MXOS 的 IoT WiFi 硬件模组进行无线网络设置的发送工作的 Flutter 插件。

## 已知问题

- 安卓版暂不支持接收设备返回的信息，但可以正常完成配网。
  - Java 代码中已包括支持接收设备信息的方法 `startFTC` 和 `stopFTC`，但目前如果使用会导致闪退。

## 支持版本

- `EASYLINK_V2`
- `EASYLINK_V3`
- `EASYLINK_PLUS`

不支持 `EasylinkP2P`