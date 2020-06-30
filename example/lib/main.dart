// 示例程序
import 'dart:ui';

import 'package:flutter/material.dart';
import 'dart:async';

import 'package:connectivity/connectivity.dart';
import 'package:flutter/services.dart';
import 'package:easylink_flutter/easylink_flutter.dart';
import 'package:easylink_flutter/easylink_notification.dart';
import 'package:permission_handler/permission_handler.dart';

void main() => runApp(MyApp());

class PersonData {
  String name = '';
  String password = '';
}

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> with WidgetsBindingObserver {
  String _displayinfo = 'Unknown';
  String _jsoninfo = '';
  final GlobalKey<FormState> _formKey = GlobalKey<FormState>();
  TextEditingController ssidController = TextEditingController();
  TextEditingController pwController = TextEditingController();
  PersonData person = PersonData();
  bool _autovalidate = false;
  bool isstartlink = false;
  String tag = "[EasyLinkFlutter Example APP] ";
  int getrepnum = 0;

  @override
  void initState() {
    getssid();
    getConnectivity();
    ssidController.text = "";
    pwController.text = "";
    WidgetsBinding.instance.addObserver(this);
    super.initState();
  }

  @override
  void didChangeAppLifecycleState(AppLifecycleState state) {
    print("--" + state.toString());
    switch (state) {
      case AppLifecycleState.inactive: // 处于这种状态的应用程序应该假设它们可能在任何时候暂停。
        print("处于这种状态的应用程序应该假设它们可能在任何时候暂停。");
        break;
      case AppLifecycleState.resumed: // 应用程序可见，前台
        print("应用程序可见，前台");
        break;
      case AppLifecycleState.paused: // 应用程序不可见，后台
        print("应用程序不可见，后台");
        if (isstartlink) stopbtn();
        break;
      case AppLifecycleState.detached:
        print("detached");
        break;
    }
  }

  @override
  void dispose() {
    super.dispose();
    ssidController.clear();
    pwController.clear();
    EasylinkFlutter.linkstop();
  }

  getConnectivity() async {
    var connectivityResult = await (Connectivity().checkConnectivity());
    if (connectivityResult != ConnectivityResult.wifi) {
      print("没开WiFi");
    }
  }

//获取权限
  requestPermission(Permission rep) async {
    print("申请权限");
    await rep.request();
    checkPermission(rep);
    getrepnum++;
  }

  checkPermission(Permission rep) async {
    // PermissionStatus permission =
    // await PermissionHandler().checkPermissionStatus(rep);
    // Scaffold.of(context).showSnackBar(SnackBar(
    //   content: Text((await rep.status).toString()),
    // ));
    if (await rep.status.isGranted) {
      print("权限申请通过");
      // 以下两种方式都能获取SSID
      getConnectivity();
      getssid();
    } else {
      print("权限申请被拒绝");
      if (getrepnum < 1) {
        requestPermission(rep);
      }
    }
  }

  Future<void> getssid() async {
    try {
      Map wifiinfo = await EasylinkFlutter.getwifiinfo();
      print(tag + "插件返回信息：");
      print(wifiinfo);
      //wifiinfo: BSSID,SSID,SSIDDATA
      if (wifiinfo.length == 0) {
        checkPermission(Permission.locationWhenInUse);
      } else {
        ssidController.text = wifiinfo["SSID"];
      }
    } on PlatformException {
      //ssidController.text  = '';
    }
  }

  // Platform messages are asynchronous, so we initialize in an async method.
  Future<void> linkstart() async {
    String displayinfo;
    // Platform messages may fail, so we use a try/catch PlatformException.
    setState(() {
      _displayinfo = 'Searching...';
    });

    try {
      displayinfo = await EasylinkFlutter.linkstart(
          ssid: person.name,
          password: person.password,
          mode: EasyLinkMode.EASYLINK_V2_PLUS,
          timeout: 60);
    } on PlatformException {
      displayinfo = 'ERROR ID 1';
    }
    try {
      EasyLinkNotification.instance.addObserver('linkstate', (object) {
        setState(() {
          String cbstr = object;
          if (cbstr != "Stop" && cbstr != "Unknown") {
            EasylinkFlutter.linkstop();
          }
          if (cbstr.substring(0, 1) == "{") {
            _jsoninfo = object;
            _displayinfo = "OK";
          } else {
            _displayinfo = object;
          }
        });
        EasyLinkNotification.instance.removeNotification('linkstate');
      });
    } on PlatformException {
      displayinfo = 'ERROR! ID 2';
    }

    // If the widget was removed from the tree while the asynchronous platform
    // message was in flight, we want to discard the reply rather than calling
    // setState to update our non-existent appearance.
    if (!mounted) return;

    setState(() {
      _displayinfo = displayinfo;
    });
  }

  String _validateName(String value) {
    if (value.isEmpty) return '用户名不能为空.';
    // final RegExp nameExp = RegExp(r'^[0-9a-zA-Z]+$');
    // if (!nameExp.hasMatch(value)) return '只能输入字母和数字.';
    return null;
  }

  String _validatePassWord(String value) {
    if (value.isEmpty) return '密码不能为空.';
    return null;
  }

  void startbtn() {
    // checkPermission();
    final FormState form = _formKey.currentState;
    if (!form.validate()) {
      _autovalidate = true; // 开始验证每个更改.
      print(tag + "表单输入不正确");
    } else {
      form.save();
      if (!isstartlink) {
        isstartlink = true;
        linkstart();
      }
    }
  }

  Future<void> slbtn() async {
    await EasylinkFlutter.ls();
  }

  void stopbtn() {
    isstartlink = false;
    EasylinkFlutter.linkstop();
    _displayinfo = "Stopped.";
  }

  @override
  Widget build(BuildContext context) {
    final mqdwindow = MediaQueryData.fromWindow(window);
    final windowWidth = mqdwindow.size.width;
    return MaterialApp(
      home: WillPopScope(
        onWillPop: () async {
          if (isstartlink) stopbtn();
          return Future.value(true);
        },
        child: Scaffold(
          appBar: AppBar(
            title: Text(_displayinfo),
          ),
          body: Form(
            key: _formKey,
            autovalidate: _autovalidate,
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
                    ),
                    onSaved: (String value) {
                      person.name = value;
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
                    onSaved: (String value) {
                      setState(() {
                        person.password = value;
                      });
                    },
                    controller: pwController,
                  ),
                  Center(
                    child: Column(
                      children: <Widget>[
                        FlatButton(
                          onPressed: startbtn,
                          child: Text('START'),
                        ),
                        // FlatButton(
                        //   onPressed: slbtn,
                        //   child: Text('SL'),
                        // ),
                        FlatButton(
                          onPressed: stopbtn,
                          child: Text('STOP'),
                        ),
                      ],
                    ),
                  ),
                  Container(
                    width: windowWidth,
                    child: Text(_jsoninfo),
                  )
                ],
              ),
            ),
          ),
        ),
      ),
    );
  }
}
