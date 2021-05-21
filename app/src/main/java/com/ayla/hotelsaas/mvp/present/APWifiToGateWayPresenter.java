package com.ayla.hotelsaas.mvp.present;

import android.content.Context;
import android.net.wifi.ScanResult;

import androidx.annotation.NonNull;
import androidx.core.util.Predicate;
import androidx.lifecycle.MutableLiveData;

import com.ayla.hotelsaas.application.Constance;
import com.ayla.hotelsaas.base.BasePresenter;
import com.ayla.hotelsaas.mvp.view.APwifiToGateWayView;
import com.ayla.ng.lib.bootstrap.AylaWiFiSetup;
import com.ayla.ng.lib.bootstrap.common.AylaCallback;
import com.ayla.ng.lib.bootstrap.connectivity.AylaConnectivityManager;
import com.blankj.utilcode.util.NetworkUtils;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;
import kotlin.Result;
import rx.functions.Action1;
import rx.functions.Func1;

public class APWifiToGateWayPresenter extends BasePresenter<APwifiToGateWayView> {

    private AylaWiFiSetup aylaWiFiSetup;

    public void connectToApDevice(Context context, String inputDsn, String homeWiFiSSid, String homeWiFiPwd) {
        Boolean isNeedExit = true;
        MutableLiveData apConfigResult = new MutableLiveData<Result<String>>();
        try {
            aylaWiFiSetup = new AylaWiFiSetup(context, AylaConnectivityManager.from(context, false));
        } catch (Exception e) {
            apConfigResult.setValue(e.toString());
            e.printStackTrace();
        }
        String gatewayIp = NetworkUtils.getGatewayByWifi();
        Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(@io.reactivex.annotations.NonNull ObservableEmitter<Object> emitter) throws Exception {
                aylaWiFiSetup.scanDevices(5, new Predicate<ScanResult>() {
                    @Override
                    public boolean test(ScanResult scanResult) {
                        if (scanResult.SSID.matches(Constance.DEFAULT_SSID_REGEX.toString())) {
                            return true;
                        }
                        return false;
                    }
                }, new AylaCallback<ScanResult[]>() {
                    @Override
                    public void onSuccess(@NonNull ScanResult[] result) {
                        emitter.onNext(result);
                        emitter.onComplete();
                    }

                    @Override
                    public void onFailed(@NonNull Throwable throwable) {
                        emitter.onError(throwable.getCause());

                    }
                });
            }
        }).flatMap(new Func1<ScanResult[], String>() {
            @Override
            public String call(ScanResult[] scanResults) {
                ScanResult scanResult = scanResults[0];
                String bssid = scanResult.BSSID;
                return bssid;
            }

        }).subscribe(new Action1<String>() {
            @Override
            public void call(String s) {
                nameList.add(s);
            }
        });
    }

}
