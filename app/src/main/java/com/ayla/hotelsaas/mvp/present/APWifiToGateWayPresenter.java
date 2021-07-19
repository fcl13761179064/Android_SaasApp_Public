package com.ayla.hotelsaas.mvp.present;

import android.content.Context;
import androidx.annotation.NonNull;
import com.ayla.hotelsaas.application.Constance;
import com.ayla.hotelsaas.base.BasePresenter;
import com.ayla.hotelsaas.mvp.view.APwifiToGateWayView;
import com.ayla.hotelsaas.widget.DisposableObservable;
import com.ayla.hotelsaas.widget.DisposableObservableOnSubscribe;
import com.ayla.ng.lib.bootstrap.AylaSetupDevice;
import com.ayla.ng.lib.bootstrap.AylaWiFiSetup;
import com.ayla.ng.lib.bootstrap.common.AylaCallback;
import com.ayla.ng.lib.bootstrap.common.AylaDisposable;
import com.ayla.ng.lib.bootstrap.connectivity.AylaConnectivityManagerPreAndroid10Imp;
import com.blankj.utilcode.util.LogUtils;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

public class APWifiToGateWayPresenter extends BasePresenter<APwifiToGateWayView> {

    public AylaWiFiSetup aylaWiFiSetup;
    private String randomString;

    public void connectToApDevice(Context context, String inputDsn, String homeWiFiSSid, String homeWiFiPwd, String deviceSSid) {
        try {
            aylaWiFiSetup = new AylaWiFiSetup(context, new AylaConnectivityManagerPreAndroid10Imp(context));//使用AylaConnectivityManagerPreAndroid10Imp是妥协做法，产品要求
            Disposable subscribe = DisposableObservable
                    .create(new DisposableObservableOnSubscribe() {

                        /**
                         * Called for each Observer that subscribes.
                         *
                         * @param emitter the safe emitter instance, never null
                         * @throws Exception on error
                         */
                        @Override
                        public void subscribe(@io.reactivex.annotations.NonNull ObservableEmitter emitter) throws Exception {
                            try {
                                AylaDisposable aylaDisposable = aylaWiFiSetup.connectToNewDevice(deviceSSid, 20, new AylaCallback<AylaSetupDevice>() {
                                    @Override
                                    public void onSuccess(@NonNull AylaSetupDevice result) {
                                        LogUtils.d("connectToApDevice: 连接到AP设备WiFi热点成功");
                                        if (!emitter.isDisposed()) {
                                            emitter.onNext(result);
                                            emitter.onComplete();
                                        }
                                    }

                                    @Override
                                    public void onFailed(@NonNull Throwable throwable) {
                                        LogUtils.d("connectToApDevice: 连接到AP设备WiFi热点失败，" + throwable.getMessage());
                                        try {
                                            if (!emitter.isDisposed()) {
                                                emitter.onError(new Exception("连接网关 Wi-Fi 失败，请重试.."));
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                                addDisposable(aylaDisposable);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    })//连接到设备ap
                    .flatMap(new Function<AylaSetupDevice, ObservableSource<AylaSetupDevice>>() {
                        @Override
                        public ObservableSource<AylaSetupDevice> apply(AylaSetupDevice aylaSetupDevice) throws Exception {
                            if (!aylaSetupDevice.getDsn().equals(inputDsn)) {
                                return Observable.error(new Exception("当前连接设备和所选设备不一致,请确认后重试"));
                            } else {
                                return Observable.just(aylaSetupDevice);
                            }
                        }//是否是当前的dsn
                    }).flatMap(new Function<AylaSetupDevice, ObservableSource<AylaSetupDevice>>() {//
                        @Override
                        public ObservableSource<AylaSetupDevice> apply(AylaSetupDevice aylaSetupDevice) throws Exception {
                            return DisposableObservable
                                    .create(new DisposableObservableOnSubscribe() {
                                        /**
                                         * Called for each Observer that subscribes.
                                         *
                                         * @param emitter the safe emitter instance, never null
                                         * @throws Exception on error
                                         */
                                        @Override
                                        public void subscribe(ObservableEmitter emitter) throws Exception {
                                            try {
                                                randomString = Constance.getRandomString(8);
                                                //ap设备，如A2网关去链接到路由器
                                                AylaDisposable aylaDisposable = aylaWiFiSetup.connectDeviceToService(homeWiFiSSid, homeWiFiPwd, randomString, 20, new AylaCallback<Object>() {
                                                    @Override
                                                    public void onSuccess(@NonNull Object result) {
                                                        LogUtils.d("connectToApDevice: AP设备连接到家庭WiFi热点成功${result}");
                                                        if (!emitter.isDisposed()) {
                                                            emitter.onNext(aylaSetupDevice);
                                                            emitter.onComplete();
                                                        }
                                                    }

                                                    @Override
                                                    public void onFailed(@NonNull Throwable throwable) {
                                                        LogUtils.d("connectToApDevice: AP设备连接到家庭WiFi热点失败，${throwable.localizedMessage}");
                                                        try {
                                                            if (!emitter.isDisposed()) {
                                                                emitter.onError(new Exception("AP设备连接到家庭WiFi热点失败"));//此处正常处理
                                                            }
                                                        } catch (Exception e) {
                                                            e.printStackTrace();
                                                        }
                                                    }
                                                });
                                                addDisposable(aylaDisposable);
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });

                        }
                    }).flatMap(new Function<AylaSetupDevice, ObservableSource<AylaSetupDevice>>() {
                        @Override
                        public ObservableSource<AylaSetupDevice> apply(AylaSetupDevice aylaSetupDevice) throws Exception {

                            return DisposableObservable.create(new DisposableObservableOnSubscribe<AylaSetupDevice>() {
                                @Override
                                public void subscribe(ObservableEmitter<AylaSetupDevice> emitter) throws Exception {
                                    AylaDisposable aylaDisposable = aylaWiFiSetup.reconnectToOriginalNetwork(20, new AylaCallback() {
                                        @Override
                                        public void onSuccess(@NonNull Object result) {
                                            if (emitter.isDisposed()) {
                                                emitter.onNext(aylaSetupDevice);
                                                emitter.onComplete();
                                            }
                                        }

                                        @Override
                                        public void onFailed(@NonNull Throwable throwable) {
                                            if (emitter.isDisposed()) {
                                                emitter.onError(new Exception("AP设备连接到家庭WiFi热点失败"));//此处正常处理
                                            }
                                        }
                                    });
                                    addDisposable(aylaDisposable);
                                }
                            });
                        }
                    }).observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe(new Consumer<Disposable>() {
                        @Override
                        public void accept(Disposable disposable) throws Exception {
                            mView.showProgress("连接中...");
                        }
                    })
                    .doFinally(new Action() {
                        @Override
                        public void run() throws Exception {
                            try {
                                if (aylaWiFiSetup != null) {
                                    aylaWiFiSetup.exitSetup();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
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

            addSubscrebe(subscribe);
            ;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
