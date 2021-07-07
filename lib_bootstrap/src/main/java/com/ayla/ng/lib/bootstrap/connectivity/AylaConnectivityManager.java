package com.ayla.ng.lib.bootstrap.connectivity;

import android.content.Context;
import android.os.Build;

import androidx.annotation.NonNull;

import com.aylanetworks.aylasdk.util.SystemInfoUtils;

public abstract class AylaConnectivityManager {
    public com.aylanetworks.aylasdk.connectivity.AylaConnectivityManager aylaConnectivityManager;

    protected AylaConnectivityManager(com.aylanetworks.aylasdk.connectivity.AylaConnectivityManager aylaConnectivityManager) {
        this.aylaConnectivityManager = aylaConnectivityManager;
    }

    /**
     * @param context
     * @param interactive 交互式的WiFi切换，当为true时，进入系统设置页面手动选择要连接的WiFi
     * @return
     */
    public static AylaConnectivityManager from(@NonNull Context context, boolean interactive) {
        if (interactive) {
            return new AylaConnectivityManagerInteractiveImp(context);
        } else {
            return (AylaConnectivityManager) (Build.VERSION.SDK_INT >= 29 && SystemInfoUtils.getTargetSdkVersion(context) >= 29 ? new AylaConnectivityManagerAndroid10Imp(context) : new AylaConnectivityManagerPreAndroid10Imp(context));
        }
    }
}
