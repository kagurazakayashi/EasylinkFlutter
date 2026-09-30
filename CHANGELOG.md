## 0.10.0 2026.9.30

- 升级到 Flutter 3.47 / Dart 3.13，pubspec 约束改为 `sdk: ^3.13.4`
- Android 构建链升级为 Gradle 9.3.1、AGP 9.1.0、Kotlin 2.4.0、Java 17，构建脚本改为 Kotlin DSL
- Android 库模块与示例工程改用 `namespace`，移除 v1 embedding 的 `registerWith`
- `easylinkv3` 依赖改为从阿里云 jcenter 镜像获取，并排除与 AndroidX 冲突的 support 库
- 示例工程依赖升级：`connectivity_plus` 7、`permission_handler` 13、`flutter_lints` 6
- 示例代码适配新 API：`PopScope`、`MediaQuery.sizeOf`、`ConnectivityResult` 列表返回值
- 清理分析配置中已被 Dart 3 移除或弃用的 lint 规则

## 0.9.1 2020.7.1

- 停止前自动判断运行状态

## 0.9.0 2020.7.1

- Android处理超时和后台结束事件

## 0.8.2 2020.6.30

- 更新图标、协议

## 0.8.1 2020.6.30

- 获取ssid逻辑修改

## 0.8.0 2020.6.30

- iOS和Android返回统一的信息和json格式

## 0.7.0 2020.6.29

- 修复判断权限是否获取成功时的问题

## 0.6.0 2020.4.2

- 修复 EasyServer 导致崩溃的问题

## 0.5.0 2020.4.1

- 修复Android版启动画面

## 0.4.1 2020.5.30

- Android手动停止时触发invokeMethod

## 0.4.0 2020.5.30

- 删除日志输出,修改权限要求

## 0.3.2 2020.3.22

- 增加停止按钮

## 0.3.1 2020.3.21

- Android-Java端代码

## 0.3.0 2020.3.20

- Android端支持准备工作

## 0.2.1 2020.2.13

- 配网成功时可以返回详细信息（原始数据）

## 0.2.0 2020.2.12

- Flutter插件代码和简单示例

## 0.1.0 2020.2.11

- iOS-OC端代码

## 0.0.1 2020.2.10

- Initial commit
