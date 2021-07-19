package com.ayla.ng.lib.bootstrap;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Predicate;

import com.android.volley.Response;
import com.ayla.ng.lib.bootstrap.common.AylaCallback;
import com.ayla.ng.lib.bootstrap.common.AylaDisposable;
import com.ayla.ng.lib.bootstrap.connectivity.AylaConnectivityManager;
import com.aylanetworks.aylasdk.AylaAPIRequest;
import com.aylanetworks.aylasdk.AylaLog;
import com.aylanetworks.aylasdk.AylaNetworks;
import com.aylanetworks.aylasdk.AylaSessionManager;
import com.aylanetworks.aylasdk.AylaSystemSettings;
import com.aylanetworks.aylasdk.error.AylaError;
import com.aylanetworks.aylasdk.error.ErrorListener;
import com.aylanetworks.aylasdk.error.NetworkError;
import com.aylanetworks.aylasdk.error.TimeoutError;
import com.aylanetworks.aylasdk.setup.AylaSetupDevice;
import com.aylanetworks.aylasdk.setup.AylaWifiStatus;

import java.lang.reflect.Constructor;

/**
 * AP配网入口
 * 功能实现步骤：
 * 1. {@link AylaWiFiSetup#scanDevices(int, Predicate, AylaCallback)}
 * 2. {@link AylaWiFiSetup#connectToNewDevice(String, int, AylaCallback)}
 * 3. {@link AylaWiFiSetup#connectDeviceToService(String, String, String, int, AylaCallback)}
 * 4. {@link AylaWiFiSetup#reconnectToOriginalNetwork(int, AylaCallback)}
 */
public class AylaWiFiSetup {
    private static final String TAG = "AylaWiFiSetup";
    private com.aylanetworks.aylasdk.setup.next.wifi.AylaWiFiSetup aylaWiFiSetup;

    public AylaWiFiSetup(@NonNull Context context, @NonNull AylaConnectivityManager connectivityManager) throws Exception {
        // TODO: 4/15/21 因为 com.aylanetworks.aylasdk.setup.next.wifi.AylaWiFiSetup 还没有放开构造方法，暂时用反射的方式实例化
        Log.d(TAG, "consumer been called ,remember to exitSetup when finish");
        AylaSystemSettings systemSettings = new AylaSystemSettings();
        systemSettings.context = context;
        AylaNetworks.initialize(systemSettings);

        Constructor<com.aylanetworks.aylasdk.setup.next.wifi.AylaWiFiSetup> constructor = com.aylanetworks.aylasdk.setup.next.wifi.AylaWiFiSetup.class.getDeclaredConstructor(Context.class, AylaSessionManager.class, com.aylanetworks.aylasdk.connectivity.AylaConnectivityManager.class);
        constructor.setAccessible(true);
        aylaWiFiSetup = constructor.newInstance(context, null, connectivityManager.aylaConnectivityManager);
        AylaLog.setConsoleLogLevel(AylaLog.LogLevel.Verbose);
    }

    /**
     * 扫描手机附近的设备热点
     *
     * @param timeoutInSeconds 超时时间，单位 秒
     * @param filter           扫描过滤
     * @param aylaCallback
     * @return
     */
    public AylaDisposable scanDevices(
            int timeoutInSeconds,
            @NonNull Predicate<ScanResult> filter, @NonNull AylaCallback<ScanResult[]> aylaCallback) {
        AylaAPIRequest aylaAPIRequest = aylaWiFiSetup.scanForAccessPoints(timeoutInSeconds, new com.aylanetworks.aylasdk.util.AylaPredicate<ScanResult>() {
            @Override
            public boolean test(ScanResult scanResult) {
                return filter.test(scanResult);
            }
        }, new Response.Listener<ScanResult[]>() {
            @Override
            public void onResponse(ScanResult[] response) {
                aylaCallback.onSuccess(response);
            }
        }, new ErrorListener() {
            @Override
            public void onErrorResponse(AylaError aylaError) {
                aylaCallback.onFailed(aylaError);
            }
        });
        return new AylaDisposable() {
            @Override
            public void dispose() {
                aylaAPIRequest.cancel();
            }

            @Override
            public boolean isDisposed() {
                return aylaAPIRequest.isCanceled();
            }
        };
    }

    /**
     * 手机连接到指定的设备热点上
     *
     * @param ssid             目标设备的热点名称
     * @param timeoutInSeconds 超时时间，单位 秒
     * @param aylaCallback
     * @return
     */
    public AylaDisposable connectToNewDevice(
            @NonNull String ssid,
            int timeoutInSeconds,
            AylaCallback<com.ayla.ng.lib.bootstrap.AylaSetupDevice> aylaCallback) {
        Log.d(TAG, "connectToNewDevice: ");
        AylaAPIRequest aylaAPIRequest = aylaWiFiSetup.connectToNewDevice(ssid, timeoutInSeconds, new Response.Listener<AylaSetupDevice>() {
            @Override
            public void onResponse(AylaSetupDevice response) {
                Log.d(TAG, "connectToNewDevice onResponse: ");
                aylaCallback.onSuccess(new com.ayla.ng.lib.bootstrap.AylaSetupDevice(response));
            }
        }, new ErrorListener() {
            @Override
            public void onErrorResponse(AylaError aylaError) {
                Log.e(TAG, "connectToNewDevice onErrorResponse: ", aylaError);
                aylaCallback.onFailed(aylaError);
            }
        });
        return new AylaDisposable() {
            @Override
            public void dispose() {
                aylaAPIRequest.cancel();
            }

            @Override
            public boolean isDisposed() {
                return aylaAPIRequest.isCanceled();
            }
        };
    }

    /**
     * 通知设备连接家庭路由器
     *
     * @param ssid             家庭路由器的WiFi名称
     * @param password         家庭路由器的WiFi密码
     * @param setupToken       ayla设备上云携带的随机数，8位的数字字母组合
     * @param timeoutInSeconds 超时时间，单位 秒
     * @param aylaCallback
     * @return
     */
    public AylaDisposable connectDeviceToService(
            @NonNull String ssid,
            @Nullable String password,
            @Nullable String setupToken,
            int timeoutInSeconds,
            AylaCallback<Object> aylaCallback) {
        Log.d(TAG, "connectDeviceToService: ");
        AylaAPIRequest aylaAPIRequest = aylaWiFiSetup.connectDeviceToService(ssid, password, setupToken, null, null, timeoutInSeconds, new Response.Listener<AylaWifiStatus>() {
            @Override
            public void onResponse(AylaWifiStatus response) {
                Log.d(TAG, "connectDeviceToService onResponse: ");
                aylaCallback.onSuccess(1);
            }
        }, new ErrorListener() {
            @Override
            public void onErrorResponse(AylaError aylaError) {
                Log.e(TAG, "connectDeviceToService onErrorResponse: ", aylaError);
                if(aylaError instanceof NetworkError || aylaError instanceof TimeoutError){
                    aylaCallback.onSuccess(1);
                }else{
                    aylaCallback.onFailed(aylaError);

                }
            }
        });
        return new AylaDisposable() {
            @Override
            public void dispose() {
                aylaAPIRequest.cancel();
            }

            @Override
            public boolean isDisposed() {
                return aylaAPIRequest.isCanceled();
            }
        };
    }

    /**
     * 手机恢复连接到原家庭路由器
     *
     * @param timeoutInSeconds 超时时间，单位 秒
     * @param aylaCallback
     * @return
     */
    public AylaDisposable reconnectToOriginalNetwork(
            int timeoutInSeconds, AylaCallback<Object> aylaCallback) {
        Log.d(TAG, "reconnectToOriginalNetwork: ");
        AylaAPIRequest aylaAPIRequest = aylaWiFiSetup.reconnectToOriginalNetwork(timeoutInSeconds, new Response.Listener<AylaAPIRequest.EmptyResponse>() {
            @Override
            public void onResponse(AylaAPIRequest.EmptyResponse response) {
                Log.d(TAG, "reconnectToOriginalNetwork onResponse: ");
                aylaCallback.onSuccess(1);
            }
        }, new ErrorListener() {
            @Override
            public void onErrorResponse(AylaError aylaError) {
                Log.e(TAG, "reconnectToOriginalNetwork onErrorResponse: ", aylaError);
                aylaCallback.onFailed(aylaError);
            }
        });
        return new AylaDisposable() {
            @Override
            public void dispose() {
                aylaAPIRequest.cancel();
            }

            @Override
            public boolean isDisposed() {
                return aylaAPIRequest.isCanceled();
            }
        };
    }

    /**
     * 退出配网流程
     */
    public void exitSetup() {
        Log.d(TAG, "exitSetup: ");
        aylaWiFiSetup.exitSetup(new Response.Listener<AylaAPIRequest.EmptyResponse>() {
            @Override
            public void onResponse(AylaAPIRequest.EmptyResponse response) {
                Log.d(TAG, "exitSetup: onResponse: ");
                AylaNetworks.shutDown();
            }
        }, new ErrorListener() {
            @Override
            public void onErrorResponse(AylaError aylaError) {
                Log.e(TAG, "exitSetup: onErrorResponse: ", aylaError);
                AylaNetworks.shutDown();
            }
        });
    }

}
