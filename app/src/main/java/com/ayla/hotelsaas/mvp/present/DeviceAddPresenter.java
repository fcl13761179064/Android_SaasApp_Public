package com.ayla.hotelsaas.mvp.present;

import android.content.Context;
import android.text.TextUtils;

import com.aliyun.iot.aep.sdk.framework.AApplication;
import com.ayla.hotelsaas.base.BasePresenter;
import com.ayla.hotelsaas.bean.BaseResult;
import com.ayla.hotelsaas.bean.DeviceListBean;
import com.ayla.hotelsaas.data.net.SelfMsgException;
import com.ayla.hotelsaas.mvp.model.RequestModel;
import com.ayla.hotelsaas.mvp.view.DeviceAddView;
import com.sunseaiot.larkairkiss.LarkConfigCallback;
import com.sunseaiot.larkairkiss.LarkConfigDefines;
import com.sunseaiot.larkairkiss.LarkSmartConfigManager;

import java.util.List;
import java.util.concurrent.TimeUnit;

import carlwu.top.lib_device_add.GatewayHelper;
import carlwu.top.lib_device_add.NodeHelper;
import carlwu.top.lib_device_add.exceptions.AlreadyBoundException;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class DeviceAddPresenter extends BasePresenter<DeviceAddView> {
    public void bindAylaGateway(String dsn, long cuId, long scopeId, String deviceCategory, String deviceName, String nickname, String waitBindDeviceId) {
        if (TextUtils.isEmpty(nickname)) {
            nickname = generateNickName(dsn, deviceName);
        }
        Disposable subscribe = RequestModel.getInstance()
                .bindDeviceWithDSN(dsn, waitBindDeviceId, cuId, scopeId, 2, deviceCategory, deviceName, nickname)
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

    public void bindHongYanGateway(AApplication application, long cuId, long scopeId, String deviceCategory, String deviceName, String nickname, String HYproductkey, String HYdeviceName, String waitBindDeviceId) {
        Disposable subscribe = RequestModel.getInstance()
                .getAuthCode(String.valueOf(scopeId))//获取授权码
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        mView.step1Finish();
                        mView.step2Start();
                    }
                })
                .observeOn(Schedulers.io())
                .flatMap(new Function<String, ObservableSource<String[]>>() {
                    GatewayHelper.BindHelper bindHelper;

                    @Override
                    public ObservableSource<String[]> apply(String authCode) throws Exception {
                        return Observable
                                .create(new ObservableOnSubscribe<String[]>() {
                                    @Override
                                    public void subscribe(ObservableEmitter<String[]> emitter) throws Exception {
                                        bindHelper = new GatewayHelper.BindHelper(application, new GatewayHelper.BindCallback() {
                                            @Override
                                            public void onFailure(Exception e) {
                                                if (!emitter.isDisposed()) {
                                                    if (e instanceof AlreadyBoundException) {
                                                        e = new SelfMsgException("该设备已在别处绑定，请先解绑后再重试", e);
                                                    }
                                                    emitter.onError(e);
                                                }
                                            }

                                            @Override
                                            public void onBindSuccess(String iotId, String productKey, String deviceName) {
                                                if (!emitter.isDisposed()) {
                                                    emitter.onNext(new String[]{iotId, productKey, deviceName});
                                                    emitter.onComplete();
                                                }
                                            }
                                        });
                                        bindHelper.startBind(authCode, HYproductkey, HYdeviceName, 100);
                                    }
                                })
                                .doFinally(new Action() {
                                    @Override
                                    public void run() throws Exception {
                                        if (bindHelper != null) {
                                            bindHelper.stopBind();
                                        }
                                    }
                                });
                    }
                })//调用 配网SDK
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Consumer<String[]>() {
                    @Override
                    public void accept(String[] strings) throws Exception {
                        mView.step2Finish();
                        mView.step3Start();
                    }
                })
                .observeOn(Schedulers.io())
                .flatMap(new Function<String[], ObservableSource<DeviceListBean.DevicesBean>>() {
                    @Override
                    public ObservableSource<DeviceListBean.DevicesBean> apply(String[] deviceInfo) throws Exception {
                        String deviceId = deviceInfo[0];
                        String newNickName = nickname;
                        if (TextUtils.isEmpty(newNickName)) {
                            newNickName = generateNickName(deviceInfo[2], deviceName);
                        }
                        return RequestModel.getInstance()
                                .bindDeviceWithDSN(deviceId, waitBindDeviceId, cuId, scopeId, 2, deviceCategory, deviceName, newNickName);
                    }
                })//通知中台绑定
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Consumer<DeviceListBean.DevicesBean>() {
                    @Override
                    public void accept(DeviceListBean.DevicesBean devicesBean) throws Exception {
                        mView.step3Finish();
                    }
                })
                .observeOn(Schedulers.io())
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

    public void bindAylaNode(String dsn, long cuId, long scopeId, String deviceCategory, String deviceName, String nickname, String waitBindDeviceId) {
        Disposable subscribe = Observable.just(dsn)
                .doOnNext(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        mView.step1Start();
                    }
                })//通知开始连接网关
                .observeOn(Schedulers.io())
                .flatMap(new Function<String, ObservableSource<?>>() {
                    @Override
                    public ObservableSource<?> apply(String s) throws Exception {
                        return RequestModel.getInstance()
                                .updateProperty(dsn, "zb_join_enable", "100");
                    }
                })//通知网关进入配网模式
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
                        mView.step1Finish();
                        mView.step2Start();
                    }
                })//通知网关连接成功
                .observeOn(Schedulers.io())
                .flatMap(new Function<Object, ObservableSource<List<DeviceListBean.DevicesBean>>>() {
                    @Override
                    public ObservableSource<List<DeviceListBean.DevicesBean>> apply(Object o) throws Exception {
                        long startTime = System.currentTimeMillis();
                        return RequestModel.getInstance()
                                .fetchCandidateNodes(dsn, deviceCategory)
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
                                });
                    }
                })//轮询候选节点，然后绑定候选节点
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
                        mView.step2Finish();
                        mView.step3Start();
                    }
                })//通知候选节点查找成功
                .observeOn(Schedulers.io())
                .flatMap(new Function<List<DeviceListBean.DevicesBean>, ObservableSource<DeviceListBean.DevicesBean>>() {
                    @Override
                    public ObservableSource<DeviceListBean.DevicesBean> apply(List<DeviceListBean.DevicesBean> devices) throws Exception {
                        if (devices.size() == 0) {
                            return Observable.error(new Throwable("没有候选节点"));
                        } else {
                            DeviceListBean.DevicesBean device = devices.get(0);
                            String newNickName = nickname;
                            if (TextUtils.isEmpty(newNickName)) {
                                newNickName = generateNickName(device.getDeviceId(), deviceName);
                            }
                            return RequestModel.getInstance()
                                    .bindDeviceWithDSN(device.getDeviceId(), waitBindDeviceId, cuId, scopeId, 2, deviceCategory, deviceName, newNickName);
                        }
                    }
                })//绑定候选节点，只返回出来第一个绑定的节点
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
                        mView.step3Finish();
                    }
                })//通知子节点绑定成功
                .observeOn(Schedulers.io())
                .doOnError(new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Disposable subscribe1 = RequestModel.getInstance()
                                .updateProperty(dsn, "zb_join_enable", "0")
                                .subscribeOn(Schedulers.io())
                                .subscribe(new Consumer<BaseResult<Boolean>>() {
                                    @Override
                                    public void accept(BaseResult<Boolean> booleanBaseResult) throws Exception {

                                    }
                                }, new Consumer<Throwable>() {
                                    @Override
                                    public void accept(Throwable throwable) throws Exception {

                                    }
                                });
                    }
                })//前面步骤遇错时，通知网关退出配网模式。
                .doOnDispose(new Action() {
                    @Override
                    public void run() throws Exception {
                        Disposable subscribe1 = RequestModel.getInstance()
                                .updateProperty(dsn, "zb_join_enable", "0")
                                .subscribeOn(Schedulers.io())
                                .subscribe(new Consumer<BaseResult<Boolean>>() {
                                    @Override
                                    public void accept(BaseResult<Boolean> booleanBaseResult) throws Exception {

                                    }
                                }, new Consumer<Throwable>() {
                                    @Override
                                    public void accept(Throwable throwable) throws Exception {

                                    }
                                });
                    }
                })//当直接退出时，通知网关退出配网模式。
                .concatMap(new Function<DeviceListBean.DevicesBean, ObservableSource<DeviceListBean.DevicesBean>>() {
                    @Override
                    public ObservableSource<DeviceListBean.DevicesBean> apply(@NonNull DeviceListBean.DevicesBean devicesBean) throws Exception {
                        return RequestModel.getInstance()
                                .updateProperty(dsn, "zb_join_enable", "0")
                                .map(new Function<BaseResult<Boolean>, DeviceListBean.DevicesBean>() {
                                    @Override
                                    public DeviceListBean.DevicesBean apply(BaseResult<Boolean> booleanBaseResult) throws Exception {
                                        return devicesBean;
                                    }
                                })
                                .onErrorReturnItem(devicesBean);
                    }
                })//前面步骤正常时，通知网关退出配网模式。
                .observeOn(AndroidSchedulers.mainThread())
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

    public void bindHongYanNode(String dsn, long cuId, long scopeId, String deviceCategory, String deviceName, String nickname, String waitBindDeviceId) {
        final NodeHelper[] nodeHelper = new NodeHelper[1];
        Disposable subscribe = RequestModel.getInstance()
                .getAuthCode(String.valueOf(scopeId))
                .flatMap(new Function<String, ObservableSource<String[]>>() {
                    @Override
                    public ObservableSource<String[]> apply(String authCode) throws Exception {
                        return Observable.create(new ObservableOnSubscribe<String[]>() {
                            @Override
                            public void subscribe(ObservableEmitter<String[]> emitter) throws Exception {
                                nodeHelper[0] = new NodeHelper(new NodeHelper.BindCallback() {
                                    @Override
                                    public boolean isUnbindRelation(String subIotId, String subProductKey, String subDeviceName) {
                                        try {
                                            Boolean aBoolean = RequestModel.getInstance()
                                                    .removeDeviceAllReleate(subProductKey, subDeviceName)
                                                    .subscribeOn(Schedulers.io())
                                                    .map(new Function<BaseResult<String>, Boolean>() {
                                                        @Override
                                                        public Boolean apply(BaseResult<String> stringBaseResult) throws Exception {
                                                            return true;
                                                        }
                                                    })
                                                    .onErrorReturnItem(true)
                                                    .toFuture().get();
                                            return aBoolean;
                                        } catch (Exception e) {
                                            return true;
                                        }
                                    }

                                    @Override
                                    public void onFailure(Exception e) {
                                        if (e instanceof AlreadyBoundException) {
                                            e = new SelfMsgException("该设备已在别处绑定，请先解绑后再重试", e);
                                        }
                                        emitter.onError(e);
                                    }

                                    @Override
                                    public void onSuccess(String subIotId, String subProductKey, String subDeviceName) {
                                        emitter.onNext(new String[]{subIotId, subProductKey, subDeviceName});
                                        emitter.onComplete();
                                    }
                                });
                                nodeHelper[0].startBind(authCode, dsn, deviceCategory, 60);
                            }
                        });
                    }
                })
                .doFinally(new Action() {
                    @Override
                    public void run() throws Exception {
                        NodeHelper helper = nodeHelper[0];
                        if (helper != null) {
                            helper.stopBind();
                        }
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        mView.step1Start();
                    }
                })//通知开始连接网关
                .doOnNext(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
                        mView.step1Finish();
                        mView.step2Start();
                    }
                })//通知网关连接成功
                .observeOn(Schedulers.io())
                .flatMap(new Function<String[], ObservableSource<DeviceListBean.DevicesBean>>() {
                    @Override
                    public ObservableSource<DeviceListBean.DevicesBean> apply(String subDeviceInfo[]) throws Exception {
                        String subIotId = subDeviceInfo[0];
                        String subDeviceName = subDeviceInfo[2];
                        if (subDeviceName != null && subDeviceName.length() > 4) {
                            subDeviceName = subDeviceName.substring(subDeviceName.length() - 4);
                        }
                        String newNickName = nickname;
                        if (TextUtils.isEmpty(newNickName)) {
                            newNickName = generateNickName(subDeviceName, deviceName);
                        }
                        return RequestModel.getInstance()
                                .bindDeviceWithDSN(subIotId, waitBindDeviceId, cuId, scopeId, 2, deviceCategory, deviceName, newNickName);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
                        mView.step2Finish();
                    }
                })//通知子节点绑定成功
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

    public void bindAylaWiFi(String wifiName, String wifiPassword, long cuId, long scopeId, String deviceCategory, String deviceName, String nickname, String waitBindDeviceId) {
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
                .flatMap(new Function<String[], ObservableSource<DeviceListBean.DevicesBean>>() {
                    @Override
                    public ObservableSource<DeviceListBean.DevicesBean> apply(String[] strings) throws Exception {
                        String deviceId = strings[0];
                        String newNickName = nickname;
                        if (TextUtils.isEmpty(newNickName)) {
                            newNickName = generateNickName(deviceId, deviceName);
                        }
                        return RequestModel.getInstance()
                                .bindDeviceWithDSN(deviceId, waitBindDeviceId, cuId, scopeId, 2, deviceCategory, deviceName, newNickName);
                    }
                })//绑定设备
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Consumer<DeviceListBean.DevicesBean>() {
                    @Override
                    public void accept(DeviceListBean.DevicesBean devicesBean) throws Exception {
                        mView.step2Finish();
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
        String newNickname;
        if (dsn.length() > 4) {
            newNickname = deviceName + "_" + dsn.substring(dsn.length() - 4);
        } else {
            newNickname = deviceName + "_" + dsn;
        }
        return newNickname;
    }
}
