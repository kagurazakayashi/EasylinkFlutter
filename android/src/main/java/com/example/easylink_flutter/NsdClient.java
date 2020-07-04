package com.example.easylink_flutter;

import android.content.Context;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import java.util.ArrayList;

/**
 * @author why
 * @date 2019.04.28
 */
public class NsdClient {

    public static final String TAG = "NsdClientWhy";

    private final String NSD_SERVER_TYPE = "_easylink_config._tcp.";
    private NsdManager.DiscoveryListener mDiscoveryListener;
    private NsdManager.ResolveListener mResolverListener;
    public NsdManager mNsdManager;
    private Context mContext;
    private String mServiceName;
    private Handler mHandler;
    private IServerFound mIServerFound;
    private ArrayList<String> discoveryList = new ArrayList<>();
    private ArrayList<String> resolveList = new ArrayList<>();
    private Thread mNSDClientThread;

    /**
     * @param context:上下文对象
     * @param serviceName   客户端扫描 指定的地址
     * @param iServerFound  回调
     */
    public NsdClient(Context context, String serviceName, IServerFound iServerFound) {
        mContext = context;
        mServiceName = serviceName;
        mIServerFound = iServerFound;
    }

    public void startNSDClient(final Handler handler) {
        mNSDClientThread = new Thread() {
            @Override
            public void run() {
                mHandler = handler;
                mNsdManager = (NsdManager) mContext.getSystemService(Context.NSD_SERVICE);
                initializeDiscoveryListener();// 初始化监听器
                initializeResolveListener();// 初始化解析器
                mNsdManager.discoverServices(NSD_SERVER_TYPE, NsdManager.PROTOCOL_DNS_SD, mDiscoveryListener);// 开启扫描
            }
        };
        mNSDClientThread.start();
    }

    /**
     * 扫描解析前的 NsdServiceInfo
     */
    private void initializeDiscoveryListener() {
        mDiscoveryListener = new NsdManager.DiscoveryListener() {
            @Override
            public void onStartDiscoveryFailed(String serviceType, int errorCode) {
                mNsdManager.stopServiceDiscovery(this);
//                Log.e(TAG, "onStartDiscoveryFailed():");
            }

            @Override
            public void onStopDiscoveryFailed(String serviceType, int errorCode) {
                mNsdManager.stopServiceDiscovery(this);
//                Log.e(TAG, "onStopDiscoveryFailed():");
            }

            @Override
            public void onDiscoveryStarted(String serviceType) {
//                Log.e(TAG, "onDiscoveryStarted():");
//                Log.d("===onDiscoveryStarted===", serviceType);
            }

            @Override
            public void onDiscoveryStopped(String serviceType) {
//                Log.e(TAG, "onDiscoveryStopped():");
            }

            /**
             *
             * @param serviceInfo
             */
            @Override
            public void onServiceFound(NsdServiceInfo serviceInfo) {
//                Log.e(TAG, "onServiceFound: " + serviceInfo);
//                Log.d("Name: ", String.valueOf(serviceInfo.getServiceType().equals(mServiceName)));
                discoveryList.add(serviceInfo.toString());
                // 根据咱服务器的定义名称，指定解析该 NsdServiceInfo
                if (serviceInfo.getServiceType().equals(mServiceName)) {
                    String serviceName = serviceInfo.getServiceName();
                    String serviceType = serviceInfo.getServiceType();
                    Log.e(TAG, "onServiceResolved 已收到:" + serviceName);
                    mNsdManager.resolveService(serviceInfo, mResolverListener);
                }
            }

            @Override
            public void onServiceLost(NsdServiceInfo serviceInfo) {
//                Log.e(TAG, "onServiceLost(): serviceInfo=" + serviceInfo);
                discoveryList.remove(serviceInfo.toString());
            }
        };
    }

    /**
     * 解析发现的NsdServiceInfo
     */
    private void initializeResolveListener() {
        mResolverListener = new NsdManager.ResolveListener() {
            @Override
            public void onResolveFailed(NsdServiceInfo serviceInfo, int errorCode) {
//                Log.e(TAG, "onResolveFailed:" + serviceInfo + " ErrorCode: " + errorCode);
            }

            @Override
            public void onServiceResolved(NsdServiceInfo serviceInfo) {
//                Log.e(TAG, "onServiceResolved:" + serviceInfo);
                int port = serviceInfo.getPort();
                String serviceName = serviceInfo.getServiceName();
                String hostAddress = serviceInfo.getHost().getHostAddress();

                Message message = Message.obtain();
                message.what = 1;
                message.obj = serviceInfo.toString();
                mHandler.sendMessage(message);
//                Log.e(TAG, "onServiceResolved 已解析:" + " host:" + hostAddress + ":" + port + " ----- serviceName: "+ serviceName);
            }
        };
    }

    public void stopNSDServer() {
        mNsdManager.stopServiceDiscovery(mDiscoveryListener);
        if (mNSDClientThread.getState() != Thread.State.TERMINATED) {
            mNSDClientThread.interrupt();
        }
        mNSDClientThread = null;
        mNsdManager = null;
        mDiscoveryListener = null;
        mResolverListener = null;
    }


    public interface IServerFound {

        /**
         * 回調 指定解析的结果
         */
        void onServerFound(NsdServiceInfo serviceInfo, int port);

        /**
         * 無合適 回調失敗
         */
        void onServerFail();
    }
}