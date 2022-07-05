package com.nya.easylink_flutter;

import android.content.Context;
import android.net.nsd.NsdServiceInfo;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * author:why
 * created on: 2019/4/28 15:17
 * description:
 */
public class NsdClientManager {

    private static final String TAG = "NsdClientManagerWhy";
    /**
     * Nsd 客户端搜索
     */
    private NsdClient nsdClient;
    private Context context;
    private Handler mHandler;
    private volatile static NsdClientManager mNsdClientManager=null;

    private NsdClientManager(Context context,Handler handler){
        this.context=context;
        this.mHandler=handler;
    }

    /**
     * DCL Single Instance
     * @param context
     * @param handler
     * @return
     */
    public static NsdClientManager getInstance(Context context,Handler handler){
        if(mNsdClientManager==null){
            synchronized (NsdClientManager.class){
                if(mNsdClientManager==null){
                    mNsdClientManager=new NsdClientManager(context,handler);
                }
            }
        }
        return mNsdClientManager;
    }

    /**
     * 通过Nsd 搜索注册过的服务器相关参数进行Socket连接（IP和Port）
     */
    public void searchNsdServer(final String nsdServerName) {
        nsdClient = new NsdClient(context, nsdServerName, new NsdClient.IServerFound() {
            @Override
            public void onServerFound(NsdServiceInfo info, int port) {
                if (info != null) {
                    Log.e(TAG, "onServerFound: "+info.toString() );
                    if (info.getServiceName().equals(nsdServerName)) {
                        //扫描到指定的server停止扫描
                        nsdClient.stopNSDServer();
                        nsdClient = null;
                    }
                }
            }

            @Override
            public void onServerFail() {

            }
        });

        nsdClient.startNSDClient(mHandler);
    }
    public void stop() {
        nsdClient.stopNSDServer();
        nsdClient = null;
    }
}