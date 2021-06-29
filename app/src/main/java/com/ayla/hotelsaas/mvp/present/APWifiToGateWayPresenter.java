package com.ayla.hotelsaas.mvp.present;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.os.Handler;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.util.Predicate;
import androidx.lifecycle.MutableLiveData;
import androidx.media.MediaBrowserServiceCompat;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.application.Constance;
import com.ayla.hotelsaas.base.BasePresenter;
import com.ayla.hotelsaas.mvp.view.APwifiToGateWayView;
import com.ayla.hotelsaas.ui.ApDistributeGuideActivity;
import com.ayla.hotelsaas.ui.CustomToast;
import com.ayla.hotelsaas.widget.CustomAlarmDialog;
import com.ayla.ng.lib.bootstrap.AylaSetupDevice;
import com.ayla.ng.lib.bootstrap.AylaWiFiSetup;
import com.ayla.ng.lib.bootstrap.common.AylaCallback;
import com.ayla.ng.lib.bootstrap.connectivity.AylaConnectivityManager;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.NetworkUtils;
import com.blankj.utilcode.util.ToastUtils;

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

public class APWifiToGateWayPresenter extends BasePresenter<APwifiToGateWayView> {

    public AylaWiFiSetup aylaWiFiSetup;
    private Observable<AylaSetupDevice> mConnextNewDeviceobservable;
    private Observable<AylaSetupDevice> connectDeviceToServiceObservalble;
    private String randomString;


    public void connectToApDevice(Context context, String inputDsn, String homeWiFiSSid, String homeWiFiPwd) {
        final Boolean[] isNeedExit = {true};

        MutableLiveData apConfigResult = new MutableLiveData<MediaBrowserServiceCompat.Result<String>>();
        try {
            aylaWiFiSetup = new AylaWiFiSetup(context, AylaConnectivityManager.from(context, false));
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
                            emitter.onError(new Exception("找不到指定的网关设备,请重试..."));

                        }
                    });
                }
            }).flatMap(new Function<String, ObservableSource<AylaSetupDevice>>() {

                @Override
                public ObservableSource<AylaSetupDevice> apply(String apSSid) throws Exception {
                    if (!NetworkUtils.getWifiEnabled()) {
                        return Observable.error(new Exception("没有网络，请检查网络后再试"));
                    }
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
                    });
                    return mConnextNewDeviceobservable;
                }
            });
            Disposable disposable = objectObservable.flatMap(new Function<AylaSetupDevice, ObservableSource<AylaSetupDevice>>() {
                @Override
                public ObservableSource<AylaSetupDevice> apply(AylaSetupDevice aylaSetupDevice) throws Exception {
                    if (!inputDsn.equals(aylaSetupDevice.getDsn())) {
                        throw new RuntimeException("当前连接设备和所选设备不一致,请确认后重试");
                    } else {
                        connectDeviceToServiceObservalble = Observable.create(new ObservableOnSubscribe<AylaSetupDevice>() {
                            @Override
                            public void subscribe(ObservableEmitter<AylaSetupDevice> emitter) throws Exception {
                                randomString = Constance.getRandomString(8);
                                //ap设备，如A2网关去链接到路由器
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
                                        emitter.onError(new Exception("AP设备连接到家庭WiFi热点失败"));//此处正常处理
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
                            mView.showProgress("连接中...");
                        }
                    })
                    .doFinally(new Action() {
                        @Override
                        public void run() throws Exception {
                            mView.hideProgress();
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
            addSubscrebe(disposable);
        } catch (Exception e) {
            apConfigResult.setValue(e.toString());
            e.printStackTrace();
            if (aylaWiFiSetup == null) {
                CustomToast.makeText(context, "连接网关 Wi-Fi 失败，请重试..", R.drawable.ic_toast_warming);
            } else {
                aylaWiFiSetup.exitSetup();
            }
        }
    }

}
