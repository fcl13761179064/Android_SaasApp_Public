package com.ayla.hotelsaas.mvp.present;


import com.ayla.hotelsaas.base.BasePresenter;
import com.ayla.hotelsaas.bean.RoomOrderBean;
import com.ayla.hotelsaas.data.net.RxjavaObserver;
import com.ayla.hotelsaas.mvp.model.RequestModel;
import com.ayla.hotelsaas.mvp.view.MainView;
import com.ayla.hotelsaas.mvp.view.RoomOrderView;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * @描述
 * @作者 fanchunlei
 * @时间 2020/7/14
 */
public class MainPresenter extends BasePresenter<MainView> {

    public void getAuthCode(String RoomId) {
        long startTime = System.currentTimeMillis();
        RequestModel.getInstance().getAuthCode(RoomId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
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
                .subscribe(new RxjavaObserver<String>() {
                    @Override
                    public void _onNext(String data) {

                        mView.getAuthCodeSuccess(data);
                    }

                    @Override
                    public void _onError(String code, String msg) {

                        mView.getAuthCodeFail(code, msg);
                    }

                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        addSubscrebe(d);
                    }
                });
    }
}
