package com.example.easylink_flutter;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import io.flutter.plugin.common.BinaryMessenger;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

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
    }

    @Override
    public void onMethodCall(MethodCall call, final MethodChannel.Result result) {
        if (call.method.equals("getwifiinfo")) {
            EasyLink el = new EasyLink(mContext);
            mSSID = el.getSSID();
            Map<String, String> returndic = new HashMap<String, String>();
            if (mSSID != "<unknown ssid>") {
                returndic.put("SSID", mSSID);
            }
            result.success(returndic);
        } else if (call.method.equals("linkstart")) {
            String ssid = call.argument("ssid");
            String key = call.argument("key");
            String timeout = call.argument("timeout");
            String mode = call.argument("mode");

            Handler myhandler = new Handler() {
                public void handleMessage(android.os.Message msg) {
                    elp.stopTransmitting();
                    nsdClientManager.stop();
                    mMethodChannel.invokeMethod("onCallback", "Stop");
                }
            };
            myhandler.sendMessageDelayed(Message.obtain(myhandler, 1), Integer.parseInt(timeout)*1000);

            nsdClientManager = NsdClientManager.getInstance(mContext, new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    String result = (String) msg.obj;
                    Map<String, String> rdata = new HashMap<String, String>();
                    String [] resultArr = result.split(", ");
                    for(String item:resultArr) {
                        String [] itemArr = item.split(": ");
                        if (itemArr.length >= 2) {
                            String itemval = itemArr[1];
                            if (itemArr.length > 2) {
                                char[] txtchar = itemval.toCharArray();
                                for (int i = 0; i < txtchar.length; i++) {
                                    int charascii = (int)txtchar[i];
                                    if (charascii < 32 || charascii > 125) {
                                        txtchar[i] = '`';
                                    }
                                }
                                itemval = String.valueOf(txtchar);
                                String [] txtarr = itemval.split("`");
                                for (String txtitem:txtarr) {
                                    if(txtitem.indexOf("1RF") != -1) {
                                        String [] srf = txtitem.split("1RF");
                                        srf[1] = "RF"+srf[1];
                                        for (String srfitem:srf) {
                                            String [] srfkv = srfitem.split("=");
                                            rdata.put(srfkv[0], srfkv[1]);
                                        }
                                    } else {
                                        String [] txtkv = txtitem.split("=");
                                        if (txtkv.length >= 2) rdata.put(txtkv[0], txtkv[1]);
                                    }
                                }
                            } else {
                                rdata.put(itemArr[0], itemval);
                            }
                        }
                    }
                    JSONObject node = new JSONObject();
                    for (String key : rdata.keySet()) {
                        try {
                            node.put(key, rdata.get(key));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    mMethodChannel.invokeMethod("onCallback", node.toString());
                }
            });
            nsdClientManager.searchNsdServer("_easylink_config._tcp.");

            if (elp == null) {
                elp = EasyLink_plus.getInstence(mContext);
            }

            result.success("start");
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
            linkstop(result);
        } else {
            result.notImplemented();
        }
    }

    private void linkstop(MethodChannel.Result result) {
        elp.stopTransmitting();
        nsdClientManager.stop();
        mMethodChannel.invokeMethod("onCallback", "Stop");
        result.success("stop");
    }

    public int getNormalIP() {
        mWifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
        mWifiInfo = mWifiManager.getConnectionInfo();
        return mWifiInfo.getIpAddress();
    }
}
