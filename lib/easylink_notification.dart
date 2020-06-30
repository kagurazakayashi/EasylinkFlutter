typedef GetObject = Function(dynamic object);

class EasyLinkNotification {
  // 工厂模式
  factory EasyLinkNotification() => _getInstance();

  static EasyLinkNotification get instance => _getInstance();
  static EasyLinkNotification _instance;

  static EasyLinkNotification _getInstance() {
    _instance ??= EasyLinkNotification._internal();
    return _instance;
  }

  // ignore: sort_constructors_first
  EasyLinkNotification._internal() {
    _instance = EasyLinkNotification._internal();
  }

  //创建Map来记录名称
  Map<String, dynamic> postNameMap = <String, dynamic>{};

  GetObject getObject;

  //添加监听者方法
  // ignore: always_declare_return_types
  addObserver(String postName, object(dynamic object)) {

    postNameMap[postName] = null;
    getObject = object;
  }

  //发送通知传值
  // ignore: always_declare_return_types
  postNotification(String postName, dynamic object) {
    //检索Map是否含有postName
    if (postNameMap.containsKey(postName)) {

      postNameMap[postName] = object;
      getObject(postNameMap[postName]);
    }

  }
  //移除通知
  // ignore: always_declare_return_types
  removeNotification(String postName) {
      
     if (postNameMap.containsKey(postName)) {

        postNameMap.remove(postName);
     }
  }
}