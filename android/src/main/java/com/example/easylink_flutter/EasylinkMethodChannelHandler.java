package com.example.easylink_flutter;

import android.util.Log;
import io.flutter.plugin.common.BinaryMessenger;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;

import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.fogcloud.sdk.easylink.helper.EasyLinkCallBack;
import io.fogcloud.sdk.easylink.jetty.EasyServer;
import io.fogcloud.sdk.easylink.plus.EasyLink_v2;
import io.fogcloud.sdk.easylink.plus.EasyLink_v3;
import io.fogcloud.sdk.easylink.api.EasyLink_plus;

public class EasylinkMethodChannelHandler implements MethodChannel.MethodCallHandler {
    private BinaryMessenger mMessenger;
    private Context mContext;
    private MethodChannel mMethodChannel;
    private EasyLink_plus ea = null;
    private String mSSID;
    private EasyServer mEasyServer;
    private WifiManager mWifiManager;
    private WifiInfo mWifiInfo;

    EasylinkMethodChannelHandler(BinaryMessenger messenger, Context context, MethodChannel methodChannel) {
        assert (messenger != null);
        mMessenger = messenger;
        assert (context != null);
        mContext = context;
        assert (methodChannel != null);
        mMethodChannel = methodChannel;
    }

    @Override
    public void onMethodCall(MethodCall call, MethodChannel.Result result) {
        if (call.method.equals("getwifiinfo")) {
            mSSID = "";//ea.getSSID();
            Map<String, String> returndic = new HashMap<String, String>();
            returndic.put("SSID",mSSID);
            result.success(returndic);
        } else if (call.method.equals("linkstart")) {
            String ssid = call.argument("ssid");
            String key = call.argument("key");
            String timeout = call.argument("timeout");
            String mode = call.argument("mode");
//            ea.startEasylink(ssid,key);
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

//            ea.startFTC(ssid, key, new FTCListener() {
//                @Override
//                public void onFTCfinished(String ip, String jsonString) {
//                    Log.d("onFTCfinished!", ip);
//                    Log.d("onFTCfinished!", jsonString);
//                }
//
//                @Override
//                public void isSmallMTU(int MTU) {
//
//                }
//            });


            Log.d("===EASYLINK===","mEasyServer EasyServer");
            mEasyServer = new EasyServer(8000);
            Log.d("===EASYLINK===","mEasyServer EasyLinkCallBack");
            mEasyServer.start(new EasyLinkCallBack() {
                @Override
                public void onSuccess(int code, String message) {
                    Log.d("===EASYLINK===","mEasyServer onSuccess "+message);
                }

                @Override
                public void onFailure(int code, String message) {
                    Log.d("===EASYLINK===","mEasyServer onFailure "+message);
                }
            });


            if (ea == null) {
                Log.d("===EASYLINK===","init EasyLink_plus");
                ea = EasyLink_plus.getInstence(mContext);
            }

            try {
                NetworkInterface intf = NetworkInterface.getByName("wlan0");
                if (intf.getMTU() < 1500) {
                    ea.setSmallMtu(true);
//                mFTCListener.isSmallMTU(intf.getMTU());
                }
            } catch (SocketException e) {
                e.printStackTrace();
            }
            try {
                Log.d("===EASYLINK===","EasyLink_plus start");
                int ip = getNormalIP();
                Log.d("===EASYLINK IP===",Integer.toString(ip));
                ea.transmitSettings(ssid,key, ip, 10, "", "");
//            mEasylinkPlus.transmitSettings(ssid.getBytes("UTF-8"),password.getBytes("UTF-8"), userinfo);
            } catch (Exception e) {
                e.printStackTrace();
            }



            result.success("start");
        } else if (call.method.equals("linkstop")) {
            ea.stopTransmitting();
            mEasyServer.stop();
            mMethodChannel.invokeMethod("onCallback","Stop");
            result.success("stop");
        }else if (call.method.equals("ls")) {
            mEasyServer = new EasyServer(8000);
            mEasyServer.start(new EasyLinkCallBack() {
                @Override
                public void onSuccess(int code, String message) {
                    Log.d("Easylink", message);
                }

                @Override
                public void onFailure(int code, String message) {
                    Log.d("Easylink", message);
                }
            });
            // ea.startFTC(new EasyLinkCallBack() {
            //     @Override
            //     public void onSuccess(int code, String message) {
            //         Log.d("Easylink", message);
            //     }

            //     @Override
            //     public void onFailure(int code, String message) {
            //         Log.d("Easylink", message);
            //     }
            // });
        } else {
            result.notImplemented();
        }
    }

    public int getNormalIP() {
        mWifiManager = (WifiManager) mContext
                .getSystemService(Context.WIFI_SERVICE);
        mWifiInfo = mWifiManager.getConnectionInfo();
        return mWifiInfo.getIpAddress();
    }
}
