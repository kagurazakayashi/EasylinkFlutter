package com.example.easylink_flutter;

import android.util.Log;
import io.flutter.plugin.common.BinaryMessenger;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import java.util.HashMap;
import java.util.Map;

import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.fogcloud.sdk.easylink.helper.EasyLinkCallBack;

public class EasylinkMethodChannelHandler implements MethodChannel.MethodCallHandler {
    private BinaryMessenger mMessenger;
    private Context mContext;
    private MethodChannel mMethodChannel;
    private EasyLinkAPI ea;
    private String mSSID;

    EasylinkMethodChannelHandler(BinaryMessenger messenger, Context context, MethodChannel methodChannel) {
        assert (messenger != null);
        mMessenger = messenger;
        assert (context != null);
        mContext = context;
        assert (methodChannel != null);
        mMethodChannel = methodChannel;
        ea = new EasyLinkAPI(mContext);
    }

    @Override
    public void onMethodCall(MethodCall call, MethodChannel.Result result) {
        if (call.method.equals("getwifiinfo")) {
            mSSID = ea.getSSID();
            Map<String, String> returndic = new HashMap<String, String>();
            returndic.put("SSID",mSSID);
            result.success(returndic);
        } else if (call.method.equals("linkstart")) {
            String ssid = call.argument("ssid");
            String key = call.argument("key");
            String timeout = call.argument("timeout");
            String mode = call.argument("mode");
            ea.startEasylink(ssid,key);
//            ea.startFTC(ssid,key,new EasyLinkCallBack() {
//                @Override
//                public void onSuccess(int code, String message) {
//                    Log.d("Easylink", message);
//                }
//
//                @Override
//                public void onFailure(int code, String message) {
//                    Log.d("Easylink", message);
//                }
//            });
            result.success("start");
        } else if (call.method.equals("linkstop")) {
            ea.stopEasyLink();
            mMethodChannel.invokeMethod("onCallback","Stop");
            result.success("stop");
        } else {
            result.notImplemented();
        }
    }
}
