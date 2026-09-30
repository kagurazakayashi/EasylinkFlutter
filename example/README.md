# easylink_flutter_example

Demonstrates how to use the `easylink_flutter` plugin.

The app requests the location permission, reads the current Wi-Fi SSID through
the plugin, and starts an EasyLink delivery with the SSID and password typed in
the form.

## Run on a device

```
flutter run -d <device-id>
```

Or install the debug build directly:

```
flutter build apk --debug
adb install -r build/app/outputs/flutter-apk/app-debug.apk
```

`permission_handler` is used only by this example to request the location
permission that Android requires for reading the Wi-Fi SSID.
