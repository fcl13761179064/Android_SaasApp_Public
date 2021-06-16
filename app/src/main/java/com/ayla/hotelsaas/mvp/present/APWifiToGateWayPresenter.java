package com.ayla.hotelsaas.mvp.present;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.wifi.ScanResult;
import android.os.Handler;

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

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import retrofit2.adapter.rxjava2.Result;

public class APWifiToGateWayPresenter extends BasePresenter<APwifiToGateWayView> {

    private AylaWiFiSetup aylaWiFiSetup;
    private Observable<AylaSetupDevice> mConnextNewDeviceobservable;
    private Observable<AylaSetupDevice> connectDeviceToServiceObservalble;
    Boolean isNeedExit = true;
    private String randomString;

    public void connectToApDevice(Context context, String inputDsn, String homeWiFiSSid, String homeWiFiPwd) {

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
                        return false;
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
                        emitter.onError(new Exception("连接到AP设备WiFi热点失败"));

                    }
                });
            }
        }).flatMap(new Function<String, ObservableSource<AylaSetupDevice>>() {

            @Override
            public ObservableSource<AylaSetupDevice> apply(String apSSid) throws Exception {
                mConnextNewDeviceobservable = Observable.create(new ObservableOnSubscribe<AylaSetupDevice>() {
                    @Override
                    public void subscribe(ObservableEmitter<AylaSetupDevice> emitter) throws Exception {
                        aylaWiFiSetup.connectToNewDevice(apSSid, 20, new AylaCallback<AylaSetupDevice>() {
                            @Override
                            public void onSuccess(@NonNull AylaSetupDevice result) {
                                LogUtils.d("connectToApDevice: 连接到AP设备WiFi热点成功");
                                result.setLanIp(gatewayIp);
                                emitter.onNext(result);
                                emitter.onComplete();
                            }

                            @Override
                            public void onFailed(@NonNull Throwable throwable) {
                                LogUtils.d("connectToApDevice: 连接到AP设备WiFi热点失败，" + throwable.getMessage());
                                emitter.onError(throwable);
                            }
                        });
                    }
                }).retryWhen(new Function<Observable<Throwable>, ObservableSource<?>>() {
                    @Override
                    public ObservableSource<?> apply(Observable<Throwable> throwableObservable) throws Exception {
                        long startTime = System.currentTimeMillis();
                        return throwableObservable.flatMap(new Function<Throwable, ObservableSource<?>>() {
                            @Override
                            public ObservableSource<?> apply(Throwable throwable) throws Exception {
                                long currentTime = System.currentTimeMillis();
                                long s = currentTime - startTime;
                                if (s > 60_000) {
                                    return Observable.error(throwable);
                                } else {
                                    return Observable.timer(3, TimeUnit.SECONDS);
                                }
                            }
                        });
                    }
                });
                return mConnextNewDeviceobservable;
            }
        });
        objectObservable.flatMap(new Function<AylaSetupDevice, ObservableSource<AylaSetupDevice>>() {
            @Override
            public ObservableSource<AylaSetupDevice> apply(AylaSetupDevice aylaSetupDevice) throws Exception {
                if (!inputDsn.equals(aylaSetupDevice.getDsn())) {
                    Observable.error(new Exception("网关连接不匹配"));
                } else {
                    connectDeviceToServiceObservalble = Observable.create(new ObservableOnSubscribe<AylaSetupDevice>() {
                        @Override
                        public void subscribe(ObservableEmitter<AylaSetupDevice> emitter) throws Exception {
                            randomString = Constance.getRandomString(8);
                            aylaWiFiSetup.connectDeviceToService(homeWiFiSSid, homeWiFiPwd, randomString, 20, new AylaCallback<Object>() {
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
                    }).retryWhen(new Function<Observable<Throwable>, ObservableSource<?>>() {
                        @Override
                        public ObservableSource<?> apply(Observable<Throwable> throwableObservable) throws Exception {
                            long startTime = System.currentTimeMillis();
                            return throwableObservable.flatMap(new Function<Throwable, ObservableSource<?>>() {
                                @Override
                                public ObservableSource<?> apply(Throwable throwable) throws Exception {
                                    long currentTime = System.currentTimeMillis();
                                    long s = currentTime - startTime;
                                    if (s > 60_000) {
                                        return Observable.error(throwable);
                                    } else {
                                        return Observable.timer(3, TimeUnit.SECONDS);
                                    }
                                }
                            });
                        }
                    });
                }
                return connectDeviceToServiceObservalble;
            }
        }).doAfterTerminate(new Action() {
            @Override
            public void run() throws Exception {
                if (isNeedExit) {
                    LogUtils.d("connectToApDevice: doAfterTerminate 退出AP配网");
                    isNeedExit = false;
                    aylaWiFiSetup.exitSetup();
                }
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        mView.showProgress("连接中...");
                    }
                })
                .doFinally(new Action() {
                    @Override
                    public void run() throws Exception {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mView.hideProgress();
                            }
                        }, 2000);

                    }
                }).subscribe(new Consumer<AylaSetupDevice>() {
            @Override
            public void accept(AylaSetupDevice aylaSetupDevice) throws Exception {
                mView.onSuccess(aylaSetupDevice, randomString);

            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                mView.onFailed(throwable);
            }
        });

    }

}
