package com.example.easylink_flutter;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.net.NetworkInterface;
import java.net.SocketException;

import io.fogcloud.sdk.easylink.helper.ComHelper;
import io.fogcloud.sdk.easylink.helper.EasyLinkCallBack;
import io.fogcloud.sdk.easylink.helper.EasyLinkErrCode;
import io.fogcloud.sdk.easylink.helper.EasyLinkParams;
import io.fogcloud.sdk.easylink.jetty.EasyServer;
import io.fogcloud.sdk.easylink.api.EasyLink_plus;

/**
 * And then open mDNS "_easylink_config._tcp.local." Created by wangchao on
 * 4/20/15.
 */
public class EasyLink2 {

    public static final int mPort = 8000;
    private static EasyServer mEasyServer = null;

    private WifiManager mWifiManager;
    private WifiInfo mWifiInfo;
    private EasyLink_plus mEasylinkPlus;
    private boolean eltag = false;
    private Thread workThread = null;

    private ComHelper comfunc = new ComHelper();
    private Context mContext;

    public EasyLink2(Context context) {
        this.mContext = context;
    }

    /**
     * get ssid
     *
     * @return the wifi router which you connected
     */
    public String getSSID() {
        String ssid_mxchip;
        if (mContext != null) {
            mWifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
            mWifiInfo = mWifiManager.getConnectionInfo();
            ssid_mxchip = mWifiInfo.getSSID();
            // 去除第一个和最后一个"
            ssid_mxchip = ssid_mxchip.substring(1, ssid_mxchip.length() - 1);
            return ssid_mxchip;
        } else {
            return null;
        }
    }

    /**
     * Check is network available
     *
     * @return
     */
    public boolean isAvailable() {
        ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (null != cm.getActiveNetworkInfo()) {
            return cm.getActiveNetworkInfo().isAvailable();
        } else {
            return false;
        }
    }

    /**
     * Check is wifi available
     *
     * @return
     */
    public boolean isWifiEnabled() {
        if (mContext != null) {
            WifiManager wifiMan = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
            if (WifiManager.WIFI_STATE_ENABLED == wifiMan.getWifiState()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check is 3rd
     *
     * @return
     */
    public boolean is3rd() {
        ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkINfo = cm.getActiveNetworkInfo();
        if (networkINfo != null && networkINfo.getType() == ConnectivityManager.TYPE_MOBILE) {
            return true;
        }
        return false;
    }

    public void startEasyLink(EasyLinkParams easylinkPara, EasyLinkCallBack easylinkcb) {

        if (ComHelper.checkPara(easylinkPara.ssid)) {
            if (null != mContext)
                startEasyLink(easylinkPara.ssid, easylinkPara.password, easylinkPara.isSendIP, easylinkPara.runSecond,
                        easylinkPara.sleeptime, easylinkPara.extraData, easylinkPara.rc4key, easylinkPara.isSmallMTU,
                        easylinkcb);
            else {
                comfunc.failureCBEasyLink(EasyLinkErrCode.CONTEXT_CODE, EasyLinkErrCode.CONTEXT, easylinkcb);
            }
        } else {
            comfunc.failureCBEasyLink(EasyLinkErrCode.INVALID_CODE, EasyLinkErrCode.INVALID, easylinkcb);
        }
    }

    /**
     * Start easylink
     *
     * @param ssid
     * @param password
     * @param runSecond
     * @param sleeptime
     * @param extraData
     * @param rc4key
     * @param isSmallMTU
     * @param easylinkcb
     */
    private void startEasyLink(String ssid, String password, final boolean isSendIP, final int runSecond,
            final int sleeptime, final String extraData, String rc4key, boolean isSmallMTU,
            final EasyLinkCallBack easylinkcb) {

        if (!eltag) {
            if (null == workThread) {
                workThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(runSecond);
                            if (eltag)
                                stopEasyLink(easylinkcb);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                });
                workThread.start();
            }

            try {
                startEasylink(ssid, password, isSendIP, sleeptime, extraData, rc4key, isSmallMTU, easylinkcb);
                eltag = true;

                if (isSendIP) {
                    try {
                        Log.d("===EasyServer===",String.valueOf(mPort));
                        mEasyServer = new EasyServer(mPort);
                        mEasyServer.start(easylinkcb);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                // comfunc.successCBEasyLink(EasyLinkErrCode.START_CODE,
                // EasyLinkErrCode.SUCCESS, easylinkcb);
            } catch (Exception e) {
                comfunc.failureCBEasyLink(EasyLinkErrCode.EXCEPTION_CODE, e.getMessage(), easylinkcb);
            }
        } else {
            comfunc.failureCBEasyLink(EasyLinkErrCode.BUSY_CODE, EasyLinkErrCode.BUSY, easylinkcb);
        }
    }

    /**
     * Stop EasyLink
     *
     * @param easylinkcb callback
     */
    public void stopEasyLink(final EasyLinkCallBack easylinkcb) {

        new Thread() {
            @Override
            public void run() {
                super.run();
                if (null != mEasylinkPlus && eltag) {
                    if (null != workThread) {
                        workThread = null;
                    }
                    mEasylinkPlus.stopTransmitting();

                    if (null != mEasyServer) {
                        if (mEasyServer.isStarted()) {
                            mEasyServer.stop();
                        }
                    }

                    eltag = false;
                    comfunc.successCBEasyLink(EasyLinkErrCode.STOP_CODE, EasyLinkErrCode.SUCCESS, easylinkcb);
                } else {
                    comfunc.failureCBEasyLink(EasyLinkErrCode.CLOSED_CODE, EasyLinkErrCode.CLOSED, easylinkcb);
                }
            }
        }.start();
    }

    protected void startEasylink(String ssid, String password, boolean isSendIP, int sleeptime, String extraData,
            String rc4key, boolean isSmallMTU, EasyLinkCallBack easylinkcb) {
        // v2 and v3
        int ip = 0;
        // ip and extra or ip without extra
        if (isSendIP) {
            ip = getNormalIP(mContext);
        }
        // only radom
        else if (ComHelper.isInteger(extraData)) {
            ip = Integer.parseInt(extraData);
            extraData = "";
        }
        // only radom
        else {
            ip = 1000;
            // extraData = "";
        }

        Log.d("===ip===", String.valueOf(ip));
        mEasylinkPlus = EasyLink_plus.getInstence(mContext);
        try {
            NetworkInterface intf = NetworkInterface.getByName("wlan0");
            if (intf.getMTU() < 1500 || isSmallMTU) {
                mEasylinkPlus.setSmallMtu(true);
            }
        } catch (SocketException e) {
            e.printStackTrace();
            comfunc.failureCBEasyLink(EasyLinkErrCode.EXCEPTION_CODE, e.getMessage(), easylinkcb);
        }
        try {
            mEasylinkPlus.transmitSettings(ssid, password, ip, sleeptime, extraData, rc4key);
        } catch (Exception e) {
            e.printStackTrace();
            comfunc.failureCBEasyLink(EasyLinkErrCode.EXCEPTION_CODE, e.getMessage(), easylinkcb);
        }
    }

    private int getNormalIP(Context context) {
        mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        mWifiInfo = mWifiManager.getConnectionInfo();
        return mWifiInfo.getIpAddress();
    }
}