package com.ayla.hotelsaas.mvp.present;

import com.ayla.hotelsaas.base.BasePresenter;
import com.ayla.hotelsaas.bean.BaseResult;
import com.ayla.hotelsaas.bean.Device;
import com.ayla.hotelsaas.mvp.model.RequestModel;
import com.ayla.hotelsaas.mvp.view.ZigBeeAddView;

import java.util.ArrayList;
import java.util.List;
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
    public void bindZigBeeNodeWithGatewayDSN(String dsn, int cuid, int scopeId) {
        Observable.just(dsn)
                .doOnNext(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        mView.gatewayConnectStart();
                    }
                })//通知开始连接网关
                .observeOn(Schedulers.io())
                .flatMap(new Function<String, ObservableSource<?>>() {
                    @Override
                    public ObservableSource<?> apply(String s) throws Exception {
                        return RequestModel.getInstance()
                                .notifyGatewayBeginConfig(dsn, cuid)
                                .delay(3, TimeUnit.SECONDS)
                                .subscribeOn(Schedulers.io());
                    }
                })//通知网关进入配网模式
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
                        mView.gatewayConnectSuccess();
                        mView.zigBeeDeviceBindStart();
                    }
                })//通知网关连接成功
                .observeOn(Schedulers.io())
                .flatMap(new Function<Object, ObservableSource<List<Device>>>() {
                    @Override
                    public ObservableSource<List<Device>> apply(Object o) throws Exception {
                        return RequestModel.getInstance()
                                .fetchCandidateNodes(dsn)
                                .delay(3, TimeUnit.SECONDS)
                                .map(new Function<BaseResult<List<Device>>, List<Device>>() {
                                    @Override
                                    public List<Device> apply(BaseResult<List<Device>> listBaseResult) throws Exception {
                                        return listBaseResult.data;
                                    }
                                });
                    }
                })//轮询候选节点，然后绑定候选节点
                .observeOn(Schedulers.io())
                .flatMap(new Function<List<Device>, ObservableSource<?>>() {
                    @Override
                    public ObservableSource<?> apply(List<Device> devices) throws Exception {
                        List<Observable<Boolean>> tasks = new ArrayList<>();
                        for (Device device : devices) {
                            Observable<Boolean> task = RequestModel.getInstance()
                                    .bindDeviceWithDSN(device.getId(), scopeId, 1, 1)
                                    .map(new Function<BaseResult<Boolean>, Boolean>() {
                                        @Override
                                        public Boolean apply(BaseResult<Boolean> booleanBaseResult) throws Exception {
                                            return booleanBaseResult.data;
                                        }
                                    });
                        }
                        if (tasks.size() == 0) {
                            return Observable.just(true);
                        } else {
                            return Observable.zip(tasks, new Function<Object[], Object>() {
                                @Override
                                public Object apply(Object[] objects) throws Exception {
                                    return true;
                                }
                            });
                        }
                    }
                })//绑定候选节点
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
                        mView.zigBeeDeviceBindSuccess();
                        mView.gatewayDisconnectStart();
                    }
                })//通知子节点绑定成功
                .observeOn(Schedulers.io())
                .flatMap(new Function<Object, ObservableSource<?>>() {
                    @Override
                    public ObservableSource<?> apply(Object o) throws Exception {
                        return RequestModel.getInstance()
                                .notifyGatewayFinishConfig(dsn, cuid)
                                .delay(3, TimeUnit.SECONDS)
                                .subscribeOn(Schedulers.io());
                    }
                })//通知网关退出配网模式
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
                        mView.gatewayDisconnectSuccess();
                        mView.progressSuccess();
                    }
                })//通知子节点绑定成功
                .doOnError(new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        mView.progressFailed(throwable);
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
