package com.ayla.hotelsaas.mvp.present;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.wifi.ScanResult;

import androidx.annotation.NonNull;
import androidx.core.util.Predicate;
import androidx.lifecycle.MutableLiveData;
import androidx.media.MediaBrowserServiceCompat;

import com.ayla.hotelsaas.application.Constance;
import com.ayla.hotelsaas.base.BasePresenter;
import com.ayla.hotelsaas.mvp.view.APwifiToGateWayView;
import com.ayla.ng.lib.bootstrap.AylaSetupDevice;
import com.ayla.ng.lib.bootstrap.AylaWiFiSetup;
import com.ayla.ng.lib.bootstrap.common.AylaCallback;
import com.ayla.ng.lib.bootstrap.connectivity.AylaConnectivityManager;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.NetworkUtils;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class APWifiToGateWayPresenter extends BasePresenter<APwifiToGateWayView> {

    private AylaWiFiSetup aylaWiFiSetup;

    @SuppressLint("CheckResult")
    public void connectToApDevice(Context context, String inputDsn, String homeWiFiSSid, String homeWiFiPwd) {
        final Boolean[] isNeedExit = {true};
        MutableLiveData apConfigResult = new MutableLiveData<MediaBrowserServiceCompat.Result<String>>();
        try {
            aylaWiFiSetup = new AylaWiFiSetup(context, AylaConnectivityManager.from(context, false));
        } catch (Exception e) {
            apConfigResult.setValue(e.toString());
            e.printStackTrace();
        }
        String gatewayIp = NetworkUtils.getGatewayByWifi();
        Observable<AylaSetupDevice> objectObservable = Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                aylaWiFiSetup.scanDevices(5, new Predicate<ScanResult>() {
                    @Override
                    public boolean test(ScanResult scanResult) {
                        if (scanResult.SSID.matches(Constance.DEFAULT_SSID_REGEX.toString())) {
                            return true;
                        }
                        return true;
                    }
                }, new AylaCallback<ScanResult[]>() {
                    @Override
                    public void onSuccess(@NonNull ScanResult[] result) {
                        String ssid = result[0].SSID;
                        emitter.onNext(ssid);
                        emitter.onComplete();
                    }

                    @Override
                    public void onFailed(@NonNull Throwable throwable) {
                        emitter.onError(throwable.getCause());

                    }
                });
            }
        }).flatMap(new Function<String, ObservableSource<AylaSetupDevice>>() {

            @Override
            public ObservableSource<AylaSetupDevice> apply(@io.reactivex.annotations.NonNull String apSSid) throws Exception {
                aylaWiFiSetup.connectToNewDevice(apSSid, 20, new AylaCallback<AylaSetupDevice>() {
                    @Override
                    public void onSuccess(@NonNull AylaSetupDevice result) {
                        LogUtils.d("connectToApDevice: 连接到AP设备WiFi热点成功");
                        result.setLanIp(gatewayIp);
                        Observable.just(result);
                    }

                    @Override
                    public void onFailed(@NonNull Throwable throwable) {
                        LogUtils.d("connectToApDevice: 连接到AP设备WiFi热点失败，${throwable.localizedMessage}");
                        Observable.just(throwable);
                    }
                });
                return Observable.error(new Exception("连接到AP设备WiFi热点失败"));
            }
        });
        objectObservable.flatMap(new Function<AylaSetupDevice, ObservableSource<?>>() {
            @Override
            public ObservableSource<?> apply(@io.reactivex.annotations.NonNull AylaSetupDevice aylaSetupDevice) throws Exception {
                if (aylaSetupDevice.getDsn() != inputDsn) {
                    Observable.error(new Exception("网关连接不匹配"));
                } else {
                    Observable.create(new ObservableOnSubscribe<Object>() {
                        @Override
                        public void subscribe(ObservableEmitter<Object> emitter) throws Exception {
                            aylaWiFiSetup.connectDeviceToService(homeWiFiSSid, homeWiFiPwd, Constance.getRandomString(8), 20, new AylaCallback<Object>() {
                                @Override
                                public void onSuccess(@NonNull Object result) {
                                    LogUtils.d("connectToApDevice: AP设备连接到家庭WiFi热点成功${result}");
                                    emitter.onNext(aylaSetupDevice);
                                    emitter.onComplete();
                                }

                                @Override
                                public void onFailed(@NonNull Throwable throwable) {
                                    LogUtils.d("connectToApDevice: AP设备连接到家庭WiFi热点失败，${throwable.localizedMessage}");
                                    emitter.onError(throwable); //此处正常处理
                                }
                            });
                        }
                    });
                    return Observable.error(new Exception("连接到AP设备WiFi热点失败"));
                }
                return Observable.just(aylaSetupDevice);
            }
        }).doAfterTerminate(new Action() {
            @Override
            public void run() throws Exception {
                if (isNeedExit[0]) {
                    LogUtils.d("connectToApDevice: doAfterTerminate 退出AP配网");
                    isNeedExit[0] = false;
                    aylaWiFiSetup.exitSetup();
                }
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        mView.showProgress();
                    }
                })
                .doFinally(new Action() {
                    @Override
                    public void run() throws Exception {
                        mView.hideProgress();
                    }
                }).subscribe(new Consumer<Object>() {


            @Override
            public void accept(Object o) throws Exception {
                mView.onSuccess();
            }


        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                mView.onFailed(throwable);
            }
        });


    }

}
