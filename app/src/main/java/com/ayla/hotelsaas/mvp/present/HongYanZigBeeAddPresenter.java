package com.ayla.hotelsaas.mvp.present;

import com.aliyun.iot.aep.sdk.framework.AApplication;
import com.ayla.hotelsaas.base.BasePresenter;
import com.ayla.hotelsaas.bean.BaseResult;
import com.ayla.hotelsaas.mvp.model.RequestModel;
import com.ayla.hotelsaas.mvp.view.HongYanZigBeeAddView;

import carlwu.top.lib_device_add.NodeHelper;
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

public class HongYanZigBeeAddPresenter extends BasePresenter<HongYanZigBeeAddView> {
    public void bind(AApplication application, String dsn, long cuId, long scopeId, String deviceCategory, String deviceName) {
        final NodeHelper[] nodeHelper = new NodeHelper[1];
        Disposable subscribe = RequestModel.getInstance().getAuthCode(String.valueOf(scopeId))
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
                                nodeHelper[0] = new NodeHelper(application, new NodeHelper.BindCallback() {
                                    @Override
                                    public void onFailure(Exception e) {
                                        emitter.onError(e);
                                    }

                                    @Override
                                    public void onSuccess(String iotId) {
                                        emitter.onNext(iotId);
                                        emitter.onComplete();
                                    }
                                });
                                nodeHelper[0].start(authCode, dsn, deviceCategory, 60);
                            }
                        });
                    }
                })
                .doFinally(new Action() {
                    @Override
                    public void run() throws Exception {
                        NodeHelper helper = nodeHelper[0];
                        if (helper != null) {
                            helper.stop();
                        }
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        mView.gatewayConnectStart();
                    }
                })//通知开始连接网关
                .doOnNext(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
                        mView.gatewayConnectSuccess();
                        mView.bindZigBeeDeviceStart();
                    }
                })//通知网关连接成功
                .observeOn(Schedulers.io())
                .flatMap(new Function<String, ObservableSource<?>>() {
                    @Override
                    public ObservableSource<?> apply(String s) throws Exception {
                        return RequestModel.getInstance().bindDeviceWithDSN(s, cuId, scopeId, 2, deviceCategory, deviceName, deviceName);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
                        mView.bindZigBeeDeviceSuccess();
                    }
                })//通知子节点绑定成功
                .doOnComplete(new Action() {
                    @Override
                    public void run() throws Exception {
                        mView.progressSuccess();
                    }
                })
                .doOnError(new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        mView.progressFailed(throwable);
                    }
                })
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                });
        addSubscrebe(subscribe);

    }
}
