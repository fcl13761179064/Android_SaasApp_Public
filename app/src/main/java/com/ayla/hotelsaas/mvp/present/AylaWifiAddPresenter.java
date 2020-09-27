package com.ayla.hotelsaas.mvp.present;

import android.content.Context;

import com.ayla.hotelsaas.base.BasePresenter;
import com.ayla.hotelsaas.mvp.model.RequestModel;
import com.ayla.hotelsaas.mvp.view.AylaWifiAddView;
import com.sunseaiot.larkairkiss.LarkConfigCallback;
import com.sunseaiot.larkairkiss.LarkConfigDefines;
import com.sunseaiot.larkairkiss.LarkSmartConfigManager;

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

public class AylaWifiAddPresenter extends BasePresenter<AylaWifiAddView> {
    /**
     * 通过网关DSN注册一个ZigBee设备
     */
    public void bindZigBeeNodeWithGatewayDSN(String wifiName, String wifiPassword, long cuId, long scopeId, String deviceCategory, String deviceName) {
        final LarkSmartConfigManager[] configManager = new LarkSmartConfigManager[1];
        Disposable subscribe = Observable.just(LarkSmartConfigManager.initWithSmartConfigType(LarkConfigDefines.LarkSmartConfigType.LarkSmartConfigTypeForAirkissEasy))
                .doOnNext(new Consumer<LarkSmartConfigManager>() {
                    @Override
                    public void accept(LarkSmartConfigManager larkSmartConfigManager) throws Exception {
                        configManager[0] = larkSmartConfigManager;
                        mView.startAirkiss();
                    }
                })//准备阶段
                .observeOn(Schedulers.io())
                .flatMap(new Function<LarkSmartConfigManager, ObservableSource<String[]>>() {
                    @Override
                    public ObservableSource<String[]> apply(LarkSmartConfigManager larkSmartConfigManager) throws Exception {
                        return Observable.create(new ObservableOnSubscribe<String[]>() {
                            @Override
                            public void subscribe(ObservableEmitter<String[]> emitter) throws Exception {
                                configManager[0].start((Context) mView, wifiName, wifiPassword, 60_000, new LarkConfigCallback() {
                                    @Override
                                    public void configSuccess(int i, String s, String s1) {
                                        emitter.onNext(new String[]{s, s1});
                                        emitter.onComplete();
                                    }

                                    @Override
                                    public void configFailed(LarkConfigDefines.LarkResutCode larkResutCode, String s) {
                                        emitter.onError(new Exception(s));
                                    }
                                });
                            }
                        }).doFinally(new Action() {
                            @Override
                            public void run() throws Exception {
                                configManager[0].stop();
                            }
                        });
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
                        mView.airkissSuccess();
                        mView.startBind();
                    }
                })//设备airkiss过程完成
                .observeOn(Schedulers.io())
                .flatMap(new Function<String[], ObservableSource<?>>() {
                    @Override
                    public ObservableSource<?> apply(String[] strings) throws Exception {
                        String dsn = strings[0];
                        return RequestModel.getInstance()
                                .bindDeviceWithDSN(dsn, cuId, scopeId, 2,
                                        deviceCategory, deviceName, deviceName);
                    }
                })//绑定设备
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
                        mView.bindSuccess();
                    }
                })//通知子节点绑定成功
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
                        mView.progressSuccess();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        mView.progressFailed(throwable);
                    }
                });
        addSubscrebe(subscribe);
    }
}
