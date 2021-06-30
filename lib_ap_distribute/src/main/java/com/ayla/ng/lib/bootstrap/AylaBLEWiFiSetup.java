package com.ayla.ng.lib.bootstrap;


import android.Manifest;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresPermission;
import androidx.core.util.Predicate;

import com.android.volley.Response;
import com.ayla.ng.lib.bootstrap.common.AylaCallback;
import com.ayla.ng.lib.bootstrap.common.AylaDisposable;
import com.aylanetworks.aylasdk.AylaAPIRequest;
import com.aylanetworks.aylasdk.AylaLog;
import com.aylanetworks.aylasdk.AylaNetworks;
import com.aylanetworks.aylasdk.AylaSystemSettings;
import com.aylanetworks.aylasdk.error.AylaError;
import com.aylanetworks.aylasdk.error.ErrorListener;
import com.aylanetworks.aylasdk.localdevice.ble.AylaBLEDeviceManager;
import com.aylanetworks.aylasdk.localdevice.ble.ScanRecordHelper;
import com.aylanetworks.aylasdk.setup.AylaBLEWiFiSetupDevice;
import com.aylanetworks.aylasdk.setup.AylaWifiScanResults;
import com.aylanetworks.aylasdk.setup.AylaWifiStatus;
import com.aylanetworks.aylasdk.setup.ble.AylaConnectStatusCharacteristic;
import com.aylanetworks.aylasdk.setup.ble.AylaSetupTokenGattCharacteristic;
import com.aylanetworks.aylasdk.setup.ble.listeners.OnConnectStatusChangedListener;

/**
 * 蓝牙配网入口
 */
public class AylaBLEWiFiSetup {
    private static final String TAG = "AylaBLEWiFiSetup";
    private com.aylanetworks.aylasdk.setup.AylaBLEWiFiSetup aylaBLEWiFiSetup;

    /**
     * 使用完毕后，必须及时调用{@link AylaBLEWiFiSetup##exitSetup()}进行销毁处理
     *
     * @param context
     * @throws Exception
     */
    @RequiresPermission(allOf = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION})
    public AylaBLEWiFiSetup(@NonNull Context context) throws Exception {
        Log.d(TAG, "consumer been called ,remember to exitSetup when finish");
        AylaSystemSettings systemSettings = new AylaSystemSettings();
        systemSettings.context = context;
        AylaNetworks.initialize(systemSettings);
        this.aylaBLEWiFiSetup = new com.aylanetworks.aylasdk.setup.AylaBLEWiFiSetup(context, null);
        this.aylaBLEWiFiSetup.setOnConnectStatusChangedListener(new OnConnectStatusChangedListener() {
            @Override
            public void onConnected(String s) {
                Log.d(TAG, "onConnected: ");
            }

            @Override
            public void onConnectionStateChanged(AylaConnectStatusCharacteristic.State state) {
                Log.d(TAG, "onConnectionStateChanged: "+state);
            }

            @Override
            public void onConnectionError(String s, AylaWifiStatus.HistoryItem.Error error) {
                Log.e(TAG, "onConnectionError: "+s+" "+error);
            }
        });
        AylaLog.setConsoleLogLevel(AylaLog.LogLevel.Verbose);
    }

    /**
     * 扫描BLE设备
     *
     * @param timeoutInSeconds
     * @param filter
     * @param aylaCallback
     * @return
     */
    public AylaDisposable scanDevices(int timeoutInSeconds, @NonNull Predicate<BLEScanResult> filter, @NonNull AylaCallback<com.ayla.ng.lib.bootstrap.AylaBLEWiFiSetupDevice[]> aylaCallback) {
        Log.d(TAG, "scanDevices: ");
        AylaAPIRequest aylaAPIRequest = aylaBLEWiFiSetup.scanDevices(timeoutInSeconds * 1000, new AylaBLEDeviceManager.ScanFilter() {
            @Override
            public boolean filter(ScanRecordHelper scanRecordHelper) {
                return filter.test(new BLEScanResult(scanRecordHelper.getLocalName(), scanRecordHelper.getServiceUuids()));
            }
        }, new Response.Listener<AylaBLEWiFiSetupDevice[]>() {
            @Override
            public void onResponse(AylaBLEWiFiSetupDevice[] response) {
                Log.d(TAG, "scanDevices: onResponse: ");
                com.ayla.ng.lib.bootstrap.AylaBLEWiFiSetupDevice[] result = new com.ayla.ng.lib.bootstrap.AylaBLEWiFiSetupDevice[response.length];
                for (int i = 0; i < response.length; i++) {
                    result[i] = new com.ayla.ng.lib.bootstrap.AylaBLEWiFiSetupDevice(response[i]);
                }
                aylaCallback.onSuccess(result);
            }
        }, new ErrorListener() {
            @Override
            public void onErrorResponse(AylaError aylaError) {
                Log.e(TAG, "scanDevices: onErrorResponse: ", aylaError);
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
     * 连接到一个BLE设备
     *
     * @param aylaBLEWiFiSetupDevice
     * @param aylaCallback
     * @return
     */
    public AylaDisposable connectToBLEDevice(com.ayla.ng.lib.bootstrap.AylaBLEWiFiSetupDevice aylaBLEWiFiSetupDevice, @NonNull AylaCallback<Object> aylaCallback) {
        Log.d(TAG, "connectToBLEDevice: ");
        AylaAPIRequest aylaAPIRequest = aylaBLEWiFiSetup.connectToBLEDevice(aylaBLEWiFiSetupDevice.aylaBLEWiFiSetupDevice, new Response.Listener<AylaAPIRequest.EmptyResponse>() {
            @Override
            public void onResponse(AylaAPIRequest.EmptyResponse response) {
                Log.d(TAG, "connectToBLEDevice: onResponse: ");
                aylaCallback.onSuccess(1);
            }
        }, new ErrorListener() {
            @Override
            public void onErrorResponse(AylaError aylaError) {
                Log.e(TAG, "connectToBLEDevice: onErrorResponse: ", aylaError);
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
     * 通知BLE设备扫描周围的WiFi热点
     *
     * @param timeoutInSeconds
     * @param aylaCallback
     * @return
     */
    public AylaDisposable scanForAccessPoints(int timeoutInSeconds, @NonNull AylaCallback<AylaWifiScanResult[]> aylaCallback) {
        Log.d(TAG, "scanForAccessPoints: ");
        AylaAPIRequest aylaAPIRequest = aylaBLEWiFiSetup.scanForAccessPoints(timeoutInSeconds, new Response.Listener<AylaWifiScanResults>() {
            @Override
            public void onResponse(AylaWifiScanResults response) {
                Log.d(TAG, "scanForAccessPoints: onResponse: " + response);
                AylaWifiScanResult[] results = new AylaWifiScanResult[response.results.length];
                for (int i = 0; i < response.results.length; i++) {
                    results[i] = new AylaWifiScanResult(response.results[i]);
                }
                aylaCallback.onSuccess(results);
            }
        }, new ErrorListener() {
            @Override
            public void onErrorResponse(AylaError aylaError) {
                Log.e(TAG, "scanForAccessPoints: onErrorResponse: ", aylaError);
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
     * 发送setupToken给BLE设备
     *
     * @param setupToken
     * @return
     */
    public AylaDisposable sendSetupToken(String setupToken, @NonNull AylaCallback<Object> aylaCallback) {
        Log.d(TAG, "sendSetupToken: ");
        AylaAPIRequest aylaAPIRequest = aylaBLEWiFiSetup.sendSetupToken(setupToken, new Response.Listener<AylaSetupTokenGattCharacteristic>() {
            @Override
            public void onResponse(AylaSetupTokenGattCharacteristic response) {
                Log.d(TAG, "sendSetupToken: onResponse: ");
                aylaCallback.onSuccess(1);
            }
        }, new ErrorListener() {
            @Override
            public void onErrorResponse(AylaError aylaError) {
                Log.e(TAG, "sendSetupToken: onErrorResponse: ", aylaError);
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
     * 通知BLE设备连接家庭WiFi
     *
     * @param ssid
     * @param password
     * @param wifiSecurityType
     * @param timeoutInSeconds
     * @return
     */
    public AylaDisposable connectDeviceToAP(@NonNull String ssid,
                                            @Nullable String password,
                                            WifiSecurityType wifiSecurityType,
                                            int timeoutInSeconds, @NonNull AylaCallback<String> aylaCallback) {
        Log.d(TAG, "connectDeviceToAP: ");
        AylaAPIRequest aylaAPIRequest = aylaBLEWiFiSetup.connectDeviceToAP(timeoutInSeconds, ssid, password, wifiSecurityType.wifiSecurityType, new Response.Listener<AylaConnectStatusCharacteristic>() {
            @Override
            public void onResponse(AylaConnectStatusCharacteristic response) {
                Log.d(TAG, "connectDeviceToAP: onResponse: ");
                if (response.getState() == AylaConnectStatusCharacteristic.State.CONNECTED) {
                    aylaCallback.onSuccess(aylaBLEWiFiSetup.getSetupDevice().getDsn());
                }
            }
        }, new ErrorListener() {
            @Override
            public void onErrorResponse(AylaError aylaError) {
                Log.e(TAG, "connectDeviceToAP: onErrorResponse: ", aylaError);
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
        aylaBLEWiFiSetup.exitSetup(new Response.Listener<AylaAPIRequest.EmptyResponse>() {
            @Override
            public void onResponse(AylaAPIRequest.EmptyResponse response) {
                Log.d(TAG, "exitSetup: onResponse: ");
                AylaNetworks.shutDown();
            }
        }, new ErrorListener() {
            @Override
            public void onErrorResponse(AylaError aylaError) {
                Log.e(TAG, "exitSetup: onErrorResponse: ",aylaError);
                AylaNetworks.shutDown();
            }
        });
    }
}
