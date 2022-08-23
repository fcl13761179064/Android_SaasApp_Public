/*
 * create by cairurui on 1/21/19 4:24 PM.
 * Copyright (c) 2019 SunseaIoT. All rights reserved.
 */

package com.ayla.hotelsaas.utils;

import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import com.blankj.utilcode.util.Utils;

import java.lang.reflect.Field;

import static android.content.Context.WIFI_SERVICE;

/**
 * Created by charry on 2018/7/24.
 */

public class WifiUtil {
    // 获取当前手机的wifi信息
    public static String getConnectWifiSsid() {
        String ssid = "";
        WifiManager wifiManager = (WifiManager) Utils.getApp().getApplicationContext().getSystemService(WIFI_SERVICE);
        if (null != wifiManager) {
            WifiInfo info = wifiManager.getConnectionInfo();
            int networkId = info.getNetworkId();
            if (networkId != -1) {
                ssid = info.getSSID();
                try {
                    Class<?> wifiSsidClass = Class.forName("android.net.wifi.WifiSsid");
                    Field noneField = wifiSsidClass.getDeclaredField("NONE");
                    noneField.setAccessible(true);
                    String NONE = (String) noneField.get(null);
                    if (ssid.equals(NONE)) {
                        ssid = "";
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        if (ssid.startsWith("\"") && ssid.endsWith("\"")) {
            ssid = ssid.substring(1, ssid.length() - 1);
        }
        return ssid;
    }
}
