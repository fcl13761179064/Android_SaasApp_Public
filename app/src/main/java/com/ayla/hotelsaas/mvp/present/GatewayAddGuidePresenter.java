package com.ayla.hotelsaas.mvp.present;

import com.ayla.hotelsaas.base.BasePresenter;
import com.ayla.hotelsaas.data.net.RxjavaObserver;
import com.ayla.hotelsaas.mvp.model.RequestModel;
import com.ayla.hotelsaas.mvp.view.GatewayAddGuideView;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class GatewayAddGuidePresenter extends BasePresenter<GatewayAddGuideView> {
    /**
     * 通过DSN注册一个设备
     *
     * @param dsn
     * @param cuId
     * @param scopeId
     */
    public void registerDeviceWithDSN(String dsn, long cuId, long scopeId, String deviceCategory, String deviceName) {
        long startTime = System.currentTimeMillis();
        RequestModel.getInstance().bindDeviceWithDSN(dsn, cuId, scopeId, 2, deviceCategory, deviceName, deviceName + "_" + dsn)
                .retryWhen(new Function<Observable<Throwable>, ObservableSource<?>>() {
                    @Override
                    public ObservableSource<?> apply(Observable<Throwable> throwableObservable) throws Exception {
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
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new RxjavaObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addSubscrebe(d);
                    }

                    @Override
                    public void _onNext(Object data) {
                        mView.bindSuccess();
                    }

                    @Override
                    public void _onError(String code, String msg) {
                        mView.bindFailed();
                    }
                });
    }
}
