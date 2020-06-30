package com.example.easylink_flutter;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
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
import io.fogcloud.sdk.easylink.api.EasyLink;
import io.fogcloud.sdk.easylink.helper.EasyLinkCallBack;
import io.fogcloud.sdk.easylink.jetty.EasyServer;
import io.fogcloud.sdk.easylink.plus.EasyLink_v2;
import io.fogcloud.sdk.easylink.plus.EasyLink_v3;
import io.fogcloud.sdk.easylink.api.EasyLink_plus;

public class EasylinkMethodChannelHandler implements MethodChannel.MethodCallHandler {
    private static final String TAG = "EasylinkMethodChannelHandler";
    private BinaryMessenger mMessenger;
    private Context mContext;
    private MethodChannel mMethodChannel;
    private EasyLink_plus elp = null;
    private String mSSID;
    private WifiManager mWifiManager;
    private WifiInfo mWifiInfo;
    private Handler handler;

    private NsdClientManager nsdClientManager;

    EasylinkMethodChannelHandler(BinaryMessenger messenger, Context context, MethodChannel methodChannel) {
        assert (messenger != null);
        mMessenger = messenger;
        assert (context != null);
        mContext = context;
        assert (methodChannel != null);
        mMethodChannel = methodChannel;
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
            //Log.d("handleMessage", String.valueOf(msg.what));
            //Log.d("handleMessage", msg.obj.toString());
            }
        };
    }

    @Override
    public void onMethodCall(MethodCall call, MethodChannel.Result result) {
        if (call.method.equals("getwifiinfo")) {
            EasyLink el = new EasyLink(mContext);
            mSSID = el.getSSID();
            Map<String, String> returndic = new HashMap<String, String>();
            returndic.put("SSID", mSSID);
            result.success(returndic);
        } else if (call.method.equals("linkstart")) {
            String ssid = call.argument("ssid");
            String key = call.argument("key");
            String timeout = call.argument("timeout");
            String mode = call.argument("mode");

            nsdClientManager = NsdClientManager.getInstance(mContext, handler);
            nsdClientManager.searchNsdServer("_easylink_config._tcp.");

            if (elp == null) {
                elp = EasyLink_plus.getInstence(mContext);
            }

            result.success("scan start...");
            try {
                NetworkInterface intf = NetworkInterface.getByName("wlan0");
                if (intf.getMTU() < 1500) {
                    elp.setSmallMtu(true);
                    // mFTCListener.isSmallMTU(intf.getMTU());
                }
            } catch (SocketException e) {
                result.success(String.valueOf(e));
                e.printStackTrace();
            }
            try {
                int ip = getNormalIP();
                elp.transmitSettings(ssid, key, ip, 10, "", "");
                // mEasylinkPlus.transmitSettings(ssid.getBytes("UTF-8"),password.getBytes("UTF-8"),
                // userinfo);
            } catch (Exception e) {
                result.success(String.valueOf(e));
                e.printStackTrace();
            }


        } else if (call.method.equals("linkstop")) {
            elp.stopTransmitting();
            mMethodChannel.invokeMethod("onCallback", "Stop");
            result.success("stop");
        } else if (call.method.equals("ls")) {
            nsdClientManager.stop();
            nsdClientManager.searchNsdServer("_easylink_config._tcp.");
        } else {
            result.notImplemented();
        }
    }

    public int getNormalIP() {
        mWifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
        mWifiInfo = mWifiManager.getConnectionInfo();
        return mWifiInfo.getIpAddress();
    }
}
