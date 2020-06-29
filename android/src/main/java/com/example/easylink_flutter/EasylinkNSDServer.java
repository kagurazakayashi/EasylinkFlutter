package com.example.easylink_flutter;

import android.content.Context;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.util.Log;


public class EasylinkNSDServer {
    private static final String TAG = "EasylinkNSDServer";
    private NsdManager.RegistrationListener mRegistrationListener;
    private NsdManager mNsdManager;

    public EasylinkNSDServer(Context context)
    {
        Log.d(TAG, "EasylinkNSDServer context");
        mNsdManager = (NsdManager) context.getSystemService(Context.NSD_SERVICE);
    }

    public void init() {
        mRegistrationListener = new NsdManager.RegistrationListener() {
            @Override
            public void onRegistrationFailed(NsdServiceInfo serviceInfo, int errorCode) {
                Log.e(TAG, "NsdServiceInfo onRegistrationFailed");
            }

            @Override
            public void onUnregistrationFailed(NsdServiceInfo serviceInfo, int errorCode) {
                Log.i(TAG, "onUnregistrationFailed serviceInfo: " + serviceInfo + " ,errorCode:" + errorCode);
            }

            @Override
            public void onServiceRegistered(NsdServiceInfo serviceInfo) {
                Log.i(TAG, "onServiceRegistered: " + serviceInfo);
            }

            @Override
            public void onServiceUnregistered(NsdServiceInfo serviceInfo) {
                Log.i(TAG, "onServiceUnregistered serviceInfo: " + serviceInfo);
            }
        };
    }

    public void registerService(String serviceName, int port) {
        NsdServiceInfo serviceInfo = new NsdServiceInfo();
        serviceInfo.setServiceName(serviceName);
        serviceInfo.setPort(port);
        serviceInfo.setServiceType("_easylink_config._tcp.local");//客户端发现服务器是需要对应的这个Type字符串
        mNsdManager.registerService(serviceInfo, NsdManager.PROTOCOL_DNS_SD, mRegistrationListener);
    }

    public void stopNSDServer() {
        mNsdManager.unregisterService(mRegistrationListener);
    }



}
