package com.ayla.hotelsaas.mvp.present;

import com.aliyun.iot.aep.sdk.framework.AApplication;
import com.ayla.hotelsaas.base.BasePresenter;
import com.ayla.hotelsaas.bean.BaseResult;
import com.ayla.hotelsaas.bean.DeviceListBean;
import com.ayla.hotelsaas.data.net.RxjavaFlatmapThrowable;
import com.ayla.hotelsaas.data.net.RxjavaObserver;
import com.ayla.hotelsaas.mvp.model.RequestModel;
import com.ayla.hotelsaas.mvp.view.GatewayAddView;

import carlwu.top.lib_device_add.GatewayHelper;
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

public class GatewayAddPresenter extends BasePresenter<GatewayAddView> {
    /**
     * 通过DSN注册一个设备
     *
     * @param dsn
     * @param cuId
     * @param scopeId
     */
    public void
    bindAylaGateway(String dsn, long cuId, long scopeId, String deviceCategory, String deviceName) {
        RequestModel.getInstance().bindDeviceWithDSN(dsn, cuId, scopeId, 2, deviceCategory, deviceName, deviceName)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new RxjavaObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addSubscrebe(d);
                    }

                    @Override
                    public void _onNext(Object data) {
                        mView.bindSuccess(dsn, deviceName);
                    }

                    @Override
                    public void _onError(String code, String msg) {
                        mView.bindFailed(null);
                    }
                });
    }

    public void bindHongYanGateway(AApplication application, long cuId, long scopeId, String deviceCategory, String deviceName, String HYproductkey, String HYdeviceName) {
        final GatewayHelper.BindHelper[] bindHelper = new GatewayHelper.BindHelper[1];
        Disposable subscribe = RequestModel.getInstance()
                .getAuthCode(String.valueOf(scopeId))
                .map(new Function<BaseResult<String>, String>() {
                    @Override
                    public String apply(BaseResult<String> stringBaseResult) throws Exception {
                        return stringBaseResult.data;
                    }
                })
                .flatMap(new Function<String, ObservableSource<String>>() {
                    @Override
                    public ObservableSource<String> apply(String authCode) throws Exception {
                        return Observable.create(new ObservableOnSubscribe<String>() {
                            @Override
                            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                                bindHelper[0] = new GatewayHelper.BindHelper(application, new GatewayHelper.BindCallback() {
                                    @Override
                                    public void onFailure(Exception e) {
                                        emitter.onError(e);
                                    }

                                    @Override
                                    public void onBindSuccess(String iotId) {
                                        emitter.onNext(iotId);
                                        emitter.onComplete();
                                    }
                                });
                                bindHelper[0].startBind(authCode, HYproductkey, HYdeviceName, 100);
                            }
                        });
                    }
                })
                .doFinally(new Action() {
                    @Override
                    public void run() throws Exception {
                        GatewayHelper.BindHelper helper = bindHelper[0];
                        if (helper != null) {
                            helper.stopBind();
                        }
                    }
                })
                .flatMap(new Function<String, ObservableSource<DeviceListBean.DevicesBean>>() {
                    @Override
                    public ObservableSource<DeviceListBean.DevicesBean> apply(String s) throws Exception {
                        return RequestModel.getInstance()
                                .bindDeviceWithDSN(s, cuId, scopeId, 2, deviceCategory, deviceName, deviceName)
                                .map(new Function<BaseResult<DeviceListBean.DevicesBean>, DeviceListBean.DevicesBean>() {
                                    @Override
                                    public DeviceListBean.DevicesBean apply(BaseResult<DeviceListBean.DevicesBean> devicesBeanBaseResult) throws Exception {
                                        return devicesBeanBaseResult.data;
                                    }
                                });
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<DeviceListBean.DevicesBean>() {
                    @Override
                    public void accept(DeviceListBean.DevicesBean bean) throws Exception {
                        mView.bindSuccess(bean.getDeviceId(), deviceName);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        if (throwable instanceof AlreadyBoundException) {
                            mView.bindFailed("该设备已在别处绑定，请先解绑后再重试");
                        } else {
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
