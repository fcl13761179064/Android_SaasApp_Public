package com.ayla.hotelsaas.mvp.present;


import android.text.TextUtils;

import com.ayla.hotelsaas.base.BasePresenter;
import com.ayla.hotelsaas.bean.BaseResult;
import com.ayla.hotelsaas.bean.DeviceListBean;
import com.ayla.hotelsaas.data.net.ApiService;
import com.ayla.hotelsaas.data.net.UnifiedErrorConsumer;
import com.ayla.hotelsaas.mvp.model.RequestModel;
import com.ayla.hotelsaas.mvp.view.ApDeviceAddView;
import com.ayla.hotelsaas.mvp.view.RoomMoreView;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * @描述
 * @作者 fanchunlei
 * @时间 2020/7/14
 */
public class ApNetworkPresenter extends BasePresenter<ApDeviceAddView> {

    public void Apnetwork(String deviceId, int cuId, String setupToken) {
        Disposable subscribe = RequestModel.getInstance().Apnetwork(deviceId, cuId,setupToken )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        mView.step1Start();
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
                })
                .doFinally(new Action() {
                    @Override
                    public void run() throws Exception {
                    }
                })
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean s) throws Exception {
                        mView.confireApStatus(s);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        mView.bindFailed(throwable);
                        mView.step1Finish();
                    }
                });
        addSubscrebe(subscribe);
    }

    public void bindAylaNode(String dsn, long cuId, long scopeId, String deviceCategory, String pid, String productName, String nickname, String waitBindDeviceId, String replaceDeviceId, String randomNum) {
        if (TextUtils.isEmpty(nickname)) {
            nickname = generateNickName(dsn, productName);
        }
        Disposable subscribe = RequestModel.getInstance()
                .bindOrReplaceDeviceWithDSN(dsn, waitBindDeviceId, replaceDeviceId, cuId, scopeId, 2, deviceCategory, pid, nickname)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        mView.step1Start();
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
        String  newNickname = deviceName;
        return newNickname;
    }
}
