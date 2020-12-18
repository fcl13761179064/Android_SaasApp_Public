package com.ayla.hotelsaas.mvp.present;

import android.content.Context;

import com.ayla.hotelsaas.base.BasePresenter;
import com.ayla.hotelsaas.bean.BaseResult;
import com.ayla.hotelsaas.bean.DeviceListBean;
import com.ayla.hotelsaas.data.net.RxjavaFlatmapThrowable;
import com.ayla.hotelsaas.mvp.model.RequestModel;
import com.ayla.hotelsaas.mvp.view.AylaWifiAddView;
import com.sunseaiot.larkairkiss.LarkConfigCallback;
import com.sunseaiot.larkairkiss.LarkConfigDefines;
import com.sunseaiot.larkairkiss.LarkSmartConfigManager;

import carlwu.top.lib_device_add.exceptions.AlreadyBoundException;
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
                        mView.step1Start();
                    }
                })//准备阶段
                .observeOn(Schedulers.io())
                .flatMap(new Function<LarkSmartConfigManager, ObservableSource<String[]>>() {
                    @Override
                    public ObservableSource<String[]> apply(LarkSmartConfigManager larkSmartConfigManager) throws Exception {
                        return Observable.create(new ObservableOnSubscribe<String[]>() {
                            @Override
                            public void subscribe(ObservableEmitter<String[]> emitter) throws Exception {
                                configManager[0].start((Context) mView, wifiName, wifiPassword, 100_000, new LarkConfigCallback() {
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
                        mView.step1Finish();
                        mView.step2Start();
                    }
                })//设备airkiss过程完成
                .observeOn(Schedulers.io())
                .flatMap(new Function<String[], ObservableSource<String[]>>() {
                    @Override
                    public ObservableSource<String[]> apply(String[] strings) throws Exception {
                        String deviceId = strings[0];
                        String newNickname;
                        if (deviceId.length() > 4) {
                            newNickname = deviceName + "_" + deviceId.substring(deviceId.length() - 4);
                        } else {
                            newNickname = deviceName + "_" + deviceId;
                        }
                        return RequestModel.getInstance()
                                .bindDeviceWithDSN(deviceId, cuId, scopeId, 2,
                                        deviceCategory, deviceName, newNickname)
                                .map(new Function<DeviceListBean.DevicesBean, String[]>() {
                                    @Override
                                    public String[] apply(DeviceListBean.DevicesBean devicesBeanBaseResult) throws Exception {
                                        return new String[]{deviceId, newNickname};
                                    }
                                });
                    }
                })//绑定设备
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Consumer<String[]>() {
                    @Override
                    public void accept(String[] s) throws Exception {
                        mView.step2Finish();
                    }
                })
                .subscribe(new Consumer<String[]>() {
                    @Override
                    public void accept(String[] s) throws Exception {
                        mView.bindSuccess(s[0], s[1]);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        if(throwable instanceof AlreadyBoundException){
                            mView.bindFailed("该设备已在别处绑定，请先解绑后再重试");
                        }else{
                            mView.bindFailed(null);
                        }
                    }
                });
        addSubscrebe(subscribe);
    }

    public void deviceRenameMethod(String dsn, String nickName) {
        Disposable subscribe = RequestModel.getInstance().deviceRename(dsn, nickName)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        mView.showProgress("修改中...");
                    }
                })
                .doFinally(new Action() {
                    @Override
                    public void run() throws Exception {
                        mView.hideProgress();
                    }
                })
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        mView.renameSuccess(nickName);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        if (throwable instanceof RxjavaFlatmapThrowable) {
                            mView.renameFailed(((RxjavaFlatmapThrowable) throwable).getCode(), ((RxjavaFlatmapThrowable) throwable).getMsg());
                        } else {
                            mView.renameFailed(null, throwable.getMessage());
                        }
                    }
                });
        addSubscrebe(subscribe);
    }
}
