package com.ayla.hotelsaas.widget;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class WifiUtils {
    private WifiManager mWifiManager;
    private static WifiUtils mInstance;

    private WifiUtils(Context context) {
        mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
    }

    public static WifiUtils getInstance(Context context) {
        if (mInstance == null) {
            synchronized (WifiUtils.class) {
                if (mInstance == null) {
                    mInstance = new WifiUtils(context.getApplicationContext());
                }
            }
        }
        return mInstance;
    }

    /***
     * 判断wifi是否打开
     */
    public Boolean mIsopenWifi() {
        return mWifiManager.isWifiEnabled();
    }

    /***
     * 打开wifi
     */
    public void openWifi() {
        if (!mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(true);
        }
    }

    /**
     * 关闭wifi
     */
    public void closeWifi() {
        if (mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(false);
        }
    }

    /**
     * 获取wifi扫描结果
     */
    public List<ScanResult> getWifiScanResult() {
        mWifiManager.startScan();
        List<String> newList = new ArrayList();
        List<ScanResult> mScanResultList = new ArrayList<>();
        List<ScanResult> scanResultList = mWifiManager.getScanResults();
        Iterator it = scanResultList.iterator();
        while (it.hasNext()) {
            ScanResult obj = (ScanResult) it.next();
            if (!TextUtils.isEmpty(obj.SSID) && !newList.contains(obj.SSID)) {
                newList.add(obj.SSID);
                mScanResultList.add(obj);
            }
        }
        return mScanResultList;
    }

    /**
     * 获取当前wifi信息
     */
    public   int getCurrentWifiInfoLevel() {
        try {
            WifiInfo connectionInfo = mWifiManager.getConnectionInfo();
            int rssi = connectionInfo.getRssi();
            return rssi;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    /**
     * 获取wifi等级，总共分为四级
     *
     * @param rssi
     * @return
     */
    public int getWifiSignal(int rssi) {
        if (rssi == Integer.MAX_VALUE) {
            return -1;
        }
        return mWifiManager.calculateSignalLevel(rssi, 4);
    }

    /**
     * 判断是否2.4Gwifi
     *
     * @param frequency
     * @return
     */
    public boolean is24GHzWifi(int frequency) {
        return frequency > 2400 && frequency < 2500;
    }

    /**
     * 判断是否5Gwifi
     *
     * @param frequency
     * @return
     */
    public boolean is5GHzWifi(int frequency) {
        return frequency > 4900 && frequency < 5900;
    }
}
