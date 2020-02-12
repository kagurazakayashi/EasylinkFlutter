// 示例程序
import 'package:flutter/material.dart';
import 'dart:async';

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

class _MyAppState extends State<MyApp> {
  String _displayinfo = 'Unknown';
  final GlobalKey<FormState> _formKey = GlobalKey<FormState>();
  TextEditingController ssidController = TextEditingController();
  TextEditingController pwController = TextEditingController();
  PersonData person = PersonData();
  bool _autovalidate = false;

  @override
  void initState() {
    getpermission();
    super.initState();
  }

  @override
  void dispose() {
    super.dispose();
    ssidController.clear();
    pwController.clear();
  }

  getpermission() {
    requestPermission(PermissionGroup.locationAlways);
  }

//获取权限
  requestPermission(PermissionGroup rep) async {
    await PermissionHandler().requestPermissions([rep]);
    checkPermission(rep);
  }

  checkPermission(PermissionGroup rep) async {
    PermissionStatus permission =
        await PermissionHandler().checkPermissionStatus(rep);
    if (permission == PermissionStatus.granted) {
      print("权限申请通过");
      getssid();
    } else {
      print("权限申请被拒绝");
    }
  }

  Future<void> getssid() async {
    try {
      Map wifiinfo = await EasylinkFlutter.getwifiinfo();
      //wifiinfo: BSSID,SSID,SSIDDATA
      ssidController.text = wifiinfo["SSID"];
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
      EasyLinkNotification.instance.addObserver('linkstate', (object) {
        setState(() {
          _displayinfo = object;
        });
        EasyLinkNotification.instance.removeNotification('linkstate');
      });
    } on PlatformException {
      displayinfo = 'ERROR';
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

  void _handleSubmitted() {
    print("*******************");
    // checkPermission();
    final FormState form = _formKey.currentState;
    if (!form.validate()) {
      _autovalidate = true; // 开始验证每个更改.
      // print("------------------");
      print("error");
      // print("------------------");
      // showInSnackBar('提交前请改正用红色标记错误.');
    } else {
      form.save();
      print("++++++++++++++++++");
      print(person.name);
      print(person.password);
      print("++++++++++++++++++");
      linkstart();
    }
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
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
                      Icons.person,
                      // color: Colors.white,
                    ),
                    hintText: '您的用户名',
                    // hintStyle: TextStyle(color: Colors.white54),
                    labelText: '用户名 *',
                    // labelStyle: TextStyle(color: Colors.white54),
                    // fillColor: Colors.white,
                  ),
                  onSaved: (String value) {
                    print(value);
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
                      Icons.person,
                      // color: Colors.white,
                    ),
                    hintText: '64位一下密码',
                    // hintStyle: TextStyle(color: Colors.white54),
                    labelText: '密码 *',
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
                  child: Text('$_displayinfo'),
                ),
                Center(
                  child: FlatButton(
                    onPressed: _handleSubmitted,
                    child: Text('START'),
                  ),
                ),
              ],
            ),
          ),
        ),
      ),
    );
  }
}