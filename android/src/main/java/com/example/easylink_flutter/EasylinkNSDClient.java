package com.example.easylink_flutter;

import android.content.Context;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.util.Log;

public class EasylinkNSDClient {
    private static final String TAG = "EasylinkNSDClient";
    //NSD管理器
    private NsdManager mNsdManager;
    private NsdServiceInfo mServiceInfo;


    public EasylinkNSDClient(Context context) {
        Log.d(TAG, "EasylinkNSDClient context");
        mNsdManager = (NsdManager) context.getSystemService(Context.NSD_SERVICE);
        initNSDDiscoveryListener();
        initNSDResolveListener();
    }

    private NsdManager.DiscoveryListener mNSDDiscoveryListener = null; //搜寻监听器
    private NsdManager.ResolveListener mNSDResolveListener = null;//    解析监听器

    //注册NSD服务网络的监听，发现NSD网络后会在对应的方法回调
    private void initNSDDiscoveryListener() {
        mNSDDiscoveryListener = new NsdManager.DiscoveryListener() {
            @Override
            public void onStartDiscoveryFailed(String serviceType, int errorCode) {
                Log.i(TAG, "onStartDiscoveryFailed--> " + serviceType + ":" + errorCode);
            }

            @Override
            public void onStopDiscoveryFailed(String serviceType, int errorCode) {
                Log.i(TAG, "onStopDiscoveryFailed--> " + serviceType + ":" + errorCode);
            }

            @Override
            public void onDiscoveryStarted(String serviceType) {
                Log.i(TAG, "onDiscoveryStarted--> " + serviceType );
            }

            @Override
            public void onDiscoveryStopped(String serviceType) {
                Log.i(TAG, "onDiscoveryStopped--> " + serviceType );
            }

            @Override
            public void onServiceFound(NsdServiceInfo serviceInfo) {//关键的回调方法
                //这里的 serviceInfo里面只有NSD服务器的主机名，要解析后才能得到该主机名的其他数据信息
                Log.i(TAG, "onServiceFound Info--> " + serviceInfo);
                mServiceInfo = serviceInfo;
                //开始解析数据
                mNsdManager.resolveService(mServiceInfo, mNSDResolveListener);

            }

            @Override
            public void onServiceLost(NsdServiceInfo serviceInfo) {
                Log.i(TAG, "onServiceLost--> " + serviceInfo);
            }
        };
    }

    //注册解析NSD网络的监听 ,解析NSD数据后回调
    private void initNSDResolveListener() {
        mNSDResolveListener = new NsdManager.ResolveListener() {
            @Override
            public void onResolveFailed(NsdServiceInfo serviceInfo, int errorCode) {

            }

            @Override
            public void onServiceResolved(NsdServiceInfo serviceInfo) {//到这里就是我们要的最终数据信息
                Log.i(TAG, "resolution : " + serviceInfo.getServiceName() + " \n host_from_server: " + serviceInfo.getHost() +
                        "\n port from server: " + serviceInfo.getPort());
                String hostAddress = serviceInfo.getHost().getHostAddress();


                Log.i(TAG, "hostAddress ip--> " + hostAddress );


            }
        };

    }

    private final String mServerType = "_http._tcp.";

    //发现周边的NSD相关网络
    public void discoveryNSDServer() {
        //三个参数
        //第一个参数要和NSD服务器端定的ServerType一样，
        //第二个参数是固定的
        //第三个参数是扫描监听器
        mNsdManager.discoverServices(mServerType, NsdManager.PROTOCOL_DNS_SD, mNSDDiscoveryListener);
    }

    //对得到的NDSServiceInfo进行解析
    public void resoleServer() {
        //第一个参数是扫描得到的对象，第二个参数是解析监听对象
        mNsdManager.resolveService(mServiceInfo, mNSDResolveListener);
    }
}
