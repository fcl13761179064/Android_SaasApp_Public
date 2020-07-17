package com.ayla.hotelsaas.mvp.present;

import com.ayla.hotelsaas.base.BasePresenter;
import com.ayla.hotelsaas.mvp.view.ZigBeeAddView;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class ZigBeeAddPresenter extends BasePresenter<ZigBeeAddView> {
    /**
     * 通过网关DSN注册一个ZigBee设备
     *
     * @param dsn
     */
    public void bindZigBeeNodeWithGatewayDSN(String dsn) {

        Observable.just(dsn)
                .doOnNext(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        mView.gatewayConnectStart();
                    }
                })//通知开始连接网关
                .subscribeOn(Schedulers.io())
                .flatMap(new Function<String, ObservableSource<?>>() {
                    @Override
                    public ObservableSource<?> apply(String s) throws Exception {
                        return Observable
                                .timer(3, TimeUnit.SECONDS)
                                .flatMap(new Function<Long, ObservableSource<?>>() {
                                    @Override
                                    public ObservableSource<?> apply(Long aLong) throws Exception {
                                        if (new Random().nextBoolean()) {
                                            return Observable.just(s);
                                        } else {
                                            return Observable.error(new Exception());
                                        }
                                    }
                                });
                    }
                })//通知网关进入配网模式
                .doOnNext(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
                        mView.gatewayConnectSuccess();
                        mView.zigBeeDeviceBindStart();
                    }
                })//通知网关连接成功
                .subscribeOn(Schedulers.io())
                .flatMap(new Function<Object, ObservableSource<?>>() {
                    @Override
                    public ObservableSource<?> apply(Object o) throws Exception {
                        return Observable
                                .timer(3, TimeUnit.SECONDS)
                                .flatMap(new Function<Long, ObservableSource<?>>() {
                                    @Override
                                    public ObservableSource<?> apply(Long aLong) throws Exception {
                                        if (new Random().nextBoolean()) {
                                            return Observable.just(o);
                                        } else {
                                            return Observable.error(new Exception());
                                        }
                                    }
                                });
                    }
                })//轮询候选节点，然后绑定候选节点
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
                        mView.zigBeeDeviceBindSuccess();
                    }
                })//通知子节点绑定成功
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
                        mView.zigBeeDeviceBindFinished();
                    }
                })
                .doOnError(new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        mView.zigBeeDeviceBindFailed(throwable);
                    }
                })
                .subscribe(new Observer<Object>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addSubscrebe(d);
                    }

                    @Override
                    public void onNext(Object o) {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
}
