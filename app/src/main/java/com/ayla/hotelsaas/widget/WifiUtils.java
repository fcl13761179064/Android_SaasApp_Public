package com.ayla.hotelsaas.widget;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.text.TextUtils;

import java.util.ArrayList;
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
     * 打开wifi
     */
    public void openWifi(){
        if(!mWifiManager.isWifiEnabled()){
            mWifiManager.setWifiEnabled(true);
        }
    }

    /**
     * 关闭wifi
     */
    public void closeWifi(){
        if(mWifiManager.isWifiEnabled()){
            mWifiManager.setWifiEnabled(false);
        }
    }

    /**
     * 获取wifi扫描结果
     */
    public List<ScanResult> getWifiScanResult(){
        List<ScanResult> mScanResultList = new ArrayList<>();
        List<ScanResult> scanResultList =  mWifiManager.getScanResults();
        if(scanResultList != null && scanResultList.size() > 0){
            for (int i = 0; i < scanResultList.size(); i++) {
                ScanResult scanResult = scanResultList.get(i);
                if(scanResult != null && !TextUtils.isEmpty(scanResult.SSID)){
                    mScanResultList.add(scanResult);
                }else {
                    continue;
                }
            }
        }
        return mScanResultList;
    }
    /**
     * 获取wifi等级，总共分为四级
     * @param rssi
     * @return
     */
    public int getWifiSignal(int rssi){
        if(rssi == Integer.MAX_VALUE){
            return -1;
        }
        return mWifiManager.calculateSignalLevel(rssi,4);
    }
    /**
     * 判断是否2.4Gwifi
     * @param frequency
     * @return
     */
    public  boolean is24GHzWifi(int frequency){
        return frequency > 2400 && frequency < 2500;
    }
    /**
     * 判断是否5Gwifi
     * @param frequency
     * @return
     */
    public boolean is5GHzWifi(int frequency) {
        return frequency > 4900 && frequency < 5900;
    }
}
