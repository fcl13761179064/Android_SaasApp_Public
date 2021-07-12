package com.ayla.hotelsaas.mvp.present;


import android.content.Context;
import android.text.TextUtils;

import com.ayla.hotelsaas.application.Constance;
import com.ayla.hotelsaas.base.BasePresenter;
import com.ayla.hotelsaas.bean.BaseResult;
import com.ayla.hotelsaas.bean.DeviceListBean;
import com.ayla.hotelsaas.data.net.ApiService;
import com.ayla.hotelsaas.data.net.UnifiedErrorConsumer;
import com.ayla.hotelsaas.mvp.model.RequestModel;
import com.ayla.hotelsaas.mvp.view.ApDeviceAddView;
import com.ayla.hotelsaas.mvp.view.RoomMoreView;
import com.ayla.hotelsaas.widget.DisposableObservable;
import com.ayla.hotelsaas.widget.DisposableObservableOnSubscribe;
import com.ayla.ng.lib.bootstrap.AylaSetupDevice;
import com.ayla.ng.lib.bootstrap.AylaWiFiSetup;
import com.ayla.ng.lib.bootstrap.common.AylaCallback;
import com.ayla.ng.lib.bootstrap.common.AylaDisposable;
import com.ayla.ng.lib.bootstrap.connectivity.AylaConnectivityManagerPreAndroid10Imp;
import com.blankj.utilcode.util.LogUtils;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.Subject;

/**
 * @描述
 * @作者 fanchunlei
 * @时间 2020/7/14
 */
public class ApNetworkPresenter extends BasePresenter<ApDeviceAddView> {

    public AylaWiFiSetup aylaWiFiSetup;
    private String randomString;

    public void connectToApDevice(Context context, String inputDsn, String homeWiFiSSid, String homeWiFiPwd, String deviceSSid, String deviceId, int cuId) {
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
                                    public void onSuccess(@androidx.annotation.NonNull AylaSetupDevice result) {
                                        LogUtils.d("connectToApDevice: 连接到AP设备WiFi热点成功");
                                        if (!emitter.isDisposed()) {
                                            emitter.onNext(result);
                                            emitter.onComplete();
                                        }
                                    }

                                    @Override
                                    public void onFailed(@androidx.annotation.NonNull Throwable throwable) {
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
                                                    public void onSuccess(@androidx.annotation.NonNull Object result) {
                                                        LogUtils.d("connectToApDevice: AP设备连接到家庭WiFi热点成功${result}");
                                                        if (!emitter.isDisposed()) {
                                                            emitter.onNext(aylaSetupDevice);
                                                            emitter.onComplete();
                                                        }
                                                    }

                                                    @Override
                                                    public void onFailed(@androidx.annotation.NonNull Throwable throwable) {
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
                    }).flatMap(new Function<AylaSetupDevice, Observable<Boolean>>() {
                        @Override
                        public Observable<Boolean> apply(AylaSetupDevice aylaSetupDevice) throws Exception {

                            return RequestModel.getInstance().Apnetwork(deviceId, cuId, randomString)
                                    .map(new Function<Boolean, Boolean>() {
                                        @Override
                                        public Boolean apply(@NonNull Boolean aBoolean) throws Exception {
                                            if (!aBoolean) {
                                                throw new RuntimeException("接口返回false");
                                            }
                                            return aBoolean;
                                        }
                                    })
                                    .observeOn(Schedulers.io())
                                    .retryWhen(new Function<Observable<Throwable>, ObservableSource<?>>() {
                                        @Override
                                        public ObservableSource<?> apply(@NonNull Observable<Throwable> throwableObservable) throws Exception {
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
                    })
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe(new Consumer<Disposable>() {
                        @Override
                        public void accept(Disposable disposable) throws Exception {
                            mView.step1Start();
                        }
                    })
                    .doFinally(new Action() {
                        @Override
                        public void run() throws Exception {
                            try {
                                if (aylaWiFiSetup != null) {
                                    aylaWiFiSetup.exitSetup();
                                }
                                mView.step1Finish();

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }).subscribe(new Consumer<Boolean>() {
                        /**
                         * Consume the given value.
                         *
                         * @param aBoolean the value
                         * @throws Exception on error
                         */
                        @Override
                        public void accept(Boolean aBoolean) throws Exception {
                            mView.confireApStatus(aBoolean, randomString);
                        }


                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            mView.bindFailed(throwable);
                        }
                    });

            addSubscrebe(subscribe);
            ;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void bindAylaNode(String dsn, long cuId, long scopeId, String deviceCategory, String pid, String productName, String nickname, String waitBindDeviceId, String replaceDeviceId, String randomNum) {
        if (TextUtils.isEmpty(nickname)) {
            nickname = generateNickName(dsn, productName);
        }
        Disposable subscribe = RequestModel.getInstance()
                .bindOrReplaceDeviceWithDSN(dsn, waitBindDeviceId, replaceDeviceId, cuId, scopeId, 2, deviceCategory, pid, nickname)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally(new Action() {
                    @Override
                    public void run() throws Exception {
                        mView.step1Finish();
                    }
                })
                .subscribe(new Consumer<DeviceListBean.DevicesBean>() {
                    @Override
                    public void accept(DeviceListBean.DevicesBean devicesBean) throws Exception {
                        mView.bindSuccess(devicesBean);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        mView.bindFailed(throwable);
                    }
                });
        addSubscrebe(subscribe);
    }

    public String generateNickName(String dsn, String deviceName) {
       /* if (dsn.length() > 4) { //产品优化，不需要dsn，默认名称为产品名称，后续依次+数字排序（+2/3/4……）
            newNickname = deviceName + "_" + dsn.substring(dsn.length() - 4);
        } else {
            newNickname = deviceName + "_" + dsn;
        }*/
        String newNickname = deviceName;
        return newNickname;
    }
}
