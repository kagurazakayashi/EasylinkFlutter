// 示例程序
// ignore: directives_ordering
import 'dart:async';
import 'dart:io';
import 'dart:ui';

import 'package:connectivity_plus/connectivity_plus.dart';
import 'package:easylink_flutter/easylink_flutter.dart';
import 'package:easylink_flutter/easylink_flutter_notification.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:permission_handler/permission_handler.dart';

void main() => runApp(const MyApp());

class PersonData {
  String name = '';
  String password = '';
}

class MyApp extends StatefulWidget {
  const MyApp({Key? key}) : super(key: key);

  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> with WidgetsBindingObserver {
  String _displayinfo = 'Unknown';
  String _jsoninfo = '';
  String _btntext = '▶️ START';
  final GlobalKey<FormState> _formKey = GlobalKey<FormState>();
  TextEditingController ssidController = TextEditingController();
  TextEditingController pwController = TextEditingController();
  PersonData person = PersonData();
  bool isstartlink = false;
  String tag = '[EasyLinkFlutter Example APP] ';
  int getrepnum = 0;
  bool isOpenWiFi = false;
  final EasyLink _easyLink = EasyLink();
  int ssidNullCount = 0;

  @override
  void initState() {
    getConnectivity();
    ssidController.text = '';
    pwController.text = '';
    WidgetsBinding.instance.addObserver(this);
    super.initState();
  }

  @override
  void didChangeAppLifecycleState(AppLifecycleState state) {
    switch (state) {
      case AppLifecycleState.inactive: // 处于这种状态的应用程序应该假设它们可能在任何时候暂停。
        // print('处于这种状态的应用程序应该假设它们可能在任何时候暂停。');
        break;
      case AppLifecycleState.resumed: // 应用程序可见，前台
        // print('应用程序可见，前台');
        break;
      case AppLifecycleState.paused: // 应用程序不可见，后台
        // print('应用程序不可见，后台');
        // ignore: always_put_control_body_on_new_line
        if (isstartlink) stopbtn();
        break;
      case AppLifecycleState.detached:
        // print('detached');
        break;
    }
  }

  @override
  void dispose() {
    super.dispose();
    ssidController.clear();
    pwController.clear();
    _easyLink.linkstop();
  }

  // ignore: always_declare_return_types
  getConnectivity() async {
    // ignore: unnecessary_parenthesis
    final ConnectivityResult connectivityResult =
        await (Connectivity().checkConnectivity());
    if (connectivityResult != ConnectivityResult.wifi) {
      print('没开WiFi');
      setState(() {
        _displayinfo = "未连接Wi-Fi";
      });
      isOpenWiFi = false;
    } else {
      setState(() {
        _displayinfo = "Unknown";
      });
      isOpenWiFi = true;
    }
  }

//获取权限
  // ignore: always_declare_return_types
  requestPermission(BuildContext context, Permission rep) async {
    print('申请权限');
    await rep.request();
    checkPermission(context, rep);
    getrepnum++;
  }

  // ignore: always_declare_return_types
  checkPermission(BuildContext context, Permission rep) async {
    // PermissionStatus permission =
    // await PermissionHandler().checkPermissionStatus(rep);
    // Scaffold.of(context).showSnackBar(SnackBar(
    //   content: Text((await rep.status).toString()),
    // ));
    if (await rep.status.isGranted) {
      print('权限申请通过');
      // 以下两种方式都能获取SSID
      getConnectivity();
      if (isOpenWiFi) {
        getssid(context);
      }
    } else {
      print('权限申请被拒绝');
      if (getrepnum < 1) {
        requestPermission(context, rep);
      }
    }
  }

  Future<void> getssid(BuildContext context) async {
    try {
      // ignore: always_specify_types
      final Map? wifiinfo = await _easyLink.getwifiinfo();
      print('$tag插件返回信息：');
      print(wifiinfo);
      //wifiinfo: BSSID,SSID,SSIDDATA
      if (wifiinfo != null) {
        if (wifiinfo.isEmpty) {
          if (ssidNullCount < 5) {
            checkPermission(context, Permission.locationWhenInUse);
            // } else if (ssidNullCount == 5) {
            //   showOASDialog(context);
          }
          ssidNullCount++;
        } else {
          ssidController.text = wifiinfo['SSID'] as String;
        }
      }
    } on PlatformException {
      //ssidController.text  = '';
    }
  }

  void showOASDialog(BuildContext context) async {
    var alert = AlertDialog(
      title: const Text("未获取到位置权限"),
      content: const Text('请点击确认跳转到设置页面并开启位置权限的\'仅在使用中允许\''),
      actions: [
        TextButton(
          onPressed: () {
            openAppSettings();
          },
          child: const Text('确认'),
        ),
        TextButton(
          onPressed: () {
            Navigator.pop(context);
          },
          child: const Text('取消'),
        ),
      ],
    );
    showDialog<void>(
      context: context,
      builder: (BuildContext context) {
        return alert;
      },
    );
  }

  // Platform messages are asynchronous, so we initialize in an async method.
  Future<void> linkstart() async {
    String? displayinfo;
    // Platform messages may fail, so we use a try/catch PlatformException.
    setState(() {
      _displayinfo = 'Searching...';
    });

    try {
      displayinfo = await _easyLink.linkstart(
          ssid: person.name,
          password: person.password,
          mode: EasyLinkMode.EASYLINK_V2_PLUS,
          timeout: 60);
    } on PlatformException {
      displayinfo = 'ERROR ID 1';
    }
    try {
      // ignore: always_specify_types
      EasyLinkNotification.instance.addObserver('linkstate', (object) {
        setState(() {
          final String cbstr = object as String;
          if (cbstr != 'Stop' && cbstr != 'Unknown') {
            _easyLink.linkstop();
          }
          if (cbstr.substring(0, 1) == '{') {
            _jsoninfo = object;
            _displayinfo = 'OK';
          } else {
            _displayinfo = object;
          }
          isstartlink = false;
          _btntext = '▶️ START';
        });
        EasyLinkNotification.instance.removeNotification('linkstate');
      });
    } on PlatformException {
      displayinfo = 'ERROR! ID 2';
    }

    // If the widget was removed from the tree while the asynchronous platform
    // message was in flight, we want to discard the reply rather than calling
    // setState to update our non-existent appearance.
    // ignore: always_put_control_body_on_new_line
    if (!mounted) return;

    setState(() {
      _displayinfo = displayinfo!;
    });
  }

  String? _validateName(String? value) {
    if (value != null && value.isEmpty) return '用户名不能为空.';
    // final RegExp nameExp = RegExp(r'^[0-9a-zA-Z]+$');
    // if (!nameExp.hasMatch(value)) return '只能输入字母和数字.';
    return null;
  }

  String? _validatePassWord(String? value) {
    // ignore: always_put_control_body_on_new_line
    if (value != null && value.isEmpty) return '密码不能为空.';
    return null;
  }

  void startbtn() {
    // checkPermission();
    final FormState? form = _formKey.currentState;
    if (form == null) {
      return;
    }
    if (!form.validate()) {
      print('$tag表单输入不正确');
    } else {
      form.save();
      if (!isstartlink) {
        isstartlink = true;
        _btntext = '⏹ STOP';
        _jsoninfo = '';
        linkstart();
      }
    }
  }

  void startorstopbtn() {
    if (isstartlink) {
      stopbtn();
    } else {
      startbtn();
    }
  }

  Future<void> slbtn() async {
    await _easyLink.ls();
  }

  void stopbtn() {
    isstartlink = false;
    _btntext = '▶️ START';
    _easyLink.linkstop();
    _displayinfo = 'Stopped.';
  }

  @override
  Widget build(BuildContext context) {
    // getssid(context);
    final MediaQueryData mqdwindow = MediaQueryData.fromWindow(window);
    final double windowWidth = mqdwindow.size.width;
    return MaterialApp(
      home: Builder(builder: (context) {
        getssid(context);
        return WillPopScope(
          onWillPop: () async {
            // ignore: always_put_control_body_on_new_line
            if (isstartlink) stopbtn();
            // ignore: always_specify_types
            return Future.value(true);
          },
          child: Scaffold(
            appBar: AppBar(
              leading: Image.asset('images/icon.png'),
              title: Text(_displayinfo),
              actions: [
                IconButton(
                  onPressed: () {
                    ssidNullCount = 0;
                    getssid(context);
                  },
                  icon: const Icon(Icons.repeat),
                ),
              ],
            ),
            body: Form(
              key: _formKey,
              child: SingleChildScrollView(
                child: Column(
                  children: <Widget>[
                    TextFormField(
                      // initialValue: person.name,
                      textCapitalization: TextCapitalization.words,
                      // style: TextStyle(color: Colors.white),
                      decoration: const InputDecoration(
                        border: UnderlineInputBorder(),
                        filled: true,
                        icon: Icon(
                          Icons.wifi,
                          // color: Colors.white,
                        ),
                        hintText: 'WiFi SSID',
                        // hintStyle: TextStyle(color: Colors.white54),
                        labelText: 'WiFi SSID',
                        // labelStyle: TextStyle(color: Colors.white54),
                        // fillColor: Colors.white,
                        // suffixIcon: IconButton(
                        //   onPressed: () {
                        //     showOASDialog(context);
                        //   },
                        //   icon: const Icon(Icons.repeat),
                        // ),
                      ),
                      onSaved: (String? value) {
                        person.name = value!;
                      },
                      validator: _validateName,
                      controller: ssidController,
                    ),
                    const SizedBox(height: 24.0),
                    TextFormField(
                      decoration: const InputDecoration(
                        border: UnderlineInputBorder(),
                        filled: true,
                        icon: Icon(
                          Icons.vpn_key,
                          // color: Colors.white,
                        ),
                        hintText: 'WiFi Password',
                        // hintStyle: TextStyle(color: Colors.white54),
                        labelText: 'WiFi Password',
                        // labelStyle: TextStyle(color: Colors.white54),
                        // fillColor: Colors.white,
                      ),
                      validator: _validatePassWord,
                      onSaved: (String? value) {
                        setState(() {
                          person.password = value!;
                        });
                      },
                      controller: pwController,
                    ),
                    Center(
                      child: Column(
                        children: <Widget>[
                          TextButton(
                            onPressed: startorstopbtn,
                            child: Text(_btntext),
                          ),
                          TextButton(
                            onPressed: () {
                              stopbtn();
                              exit(0);
                            },
                            child: const Text('EXIT'),
                          ),
                        ],
                      ),
                    ),
                    SizedBox(
                      width: windowWidth,
                      child: Text(_jsoninfo),
                    )
                  ],
                ),
              ),
            ),
          ),
        );
      }),
    );
  }
}
