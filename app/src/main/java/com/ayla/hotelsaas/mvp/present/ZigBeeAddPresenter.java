package com.ayla.hotelsaas.mvp.present;

import android.util.Log;

import com.ayla.hotelsaas.base.BasePresenter;
import com.ayla.hotelsaas.bean.BaseResult;
import com.ayla.hotelsaas.bean.DeviceListBean;
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
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class ZigBeeAddPresenter extends BasePresenter<ZigBeeAddView> {
    /**
     * 通过网关DSN注册一个ZigBee设备
     *
     * @param dsn
     */
    public void bindZigBeeNodeWithGatewayDSN(String dsn, long cuId, long scopeId, String deviceCategory, String deviceName) {
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
                                .updateProperty(dsn, "zb_join_enable", "100");
                    }
                })//通知网关进入配网模式
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
                        mView.gatewayConnectSuccess();
                        mView.fetchCandidatesStart();
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
                                })
                                .map(new Function<BaseResult<List<DeviceListBean.DevicesBean>>, List<DeviceListBean.DevicesBean>>() {
                                    @Override
                                    public List<DeviceListBean.DevicesBean> apply(BaseResult<List<DeviceListBean.DevicesBean>> listBaseResult) throws Exception {
                                        return listBaseResult.data;
                                    }
                                });
                    }
                })//轮询候选节点，然后绑定候选节点
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
                        mView.fetchCandidatesSuccess();
                        mView.bindZigBeeDeviceStart();
                    }
                })//通知候选节点查找成功
                .observeOn(Schedulers.io())
                .flatMap(new Function<List<DeviceListBean.DevicesBean>, ObservableSource<?>>() {
                    @Override
                    public ObservableSource<?> apply(List<DeviceListBean.DevicesBean> devices) throws Exception {
                        List<Observable<?>> tasks = new ArrayList<>();
                        for (DeviceListBean.DevicesBean device : devices) {
                            Observable<?> task = RequestModel.getInstance()
                                    .bindDeviceWithDSN(device.getDeviceId(), cuId, scopeId, 2, deviceCategory, deviceName, deviceName + "_" + device.getDeviceId())
                                    .doOnError(new Consumer<Throwable>() {
                                        @Override
                                        public void accept(Throwable throwable) throws Exception {
                                            Log.d("候选节点绑定失败", "accept: " + throwable + " " + device.getDeviceId());
                                        }
                                    })
                                    .doOnNext(new Consumer<BaseResult>() {
                                        @Override
                                        public void accept(BaseResult baseResult) throws Exception {
                                            Log.d("候选节点绑定成功", "accept: " + device.getDeviceId());
                                        }
                                    });
                            tasks.add(task);
                        }
                        if (tasks.size() == 0) {
                            return Observable.error(new Throwable("没有候选节点"));
                        } else {
                            return Observable.zip(tasks, new Function<Object[], Object>() {
                                @Override
                                public Object apply(Object[] objects) throws Exception {
                                    return objects;
                                }
                            });
                        }
                    }
                })//绑定候选节点
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
                        mView.bindZigBeeDeviceSuccess();
                    }
                })//通知子节点绑定成功
                .observeOn(Schedulers.io())
                .onErrorResumeNext(new Function<Throwable, ObservableSource<?>>() {
                    @Override
                    public ObservableSource<?> apply(Throwable throwable) throws Exception {
                        return RequestModel.getInstance()
                                .updateProperty(dsn, "zb_join_enable", "0")
                                .flatMap(new Function<BaseResult<Boolean>, ObservableSource<?>>() {
                                    @Override
                                    public ObservableSource<?> apply(BaseResult<Boolean> booleanBaseResult) throws Exception {
                                        return Observable.error(throwable);
                                    }
                                });
                    }
                })//前面步骤遇错时，通知网关退出配网模式，然后重新抛出错误。
                .flatMap(new Function<Object, ObservableSource<?>>() {
                    @Override
                    public ObservableSource<?> apply(Object o) throws Exception {
                        return RequestModel.getInstance()
                                .updateProperty(dsn, "zb_join_enable", "0");
                    }
                })//前面步骤正常时，通知网关退出配网模式。
                .doOnDispose(new Action() {
                    @Override
                    public void run() throws Exception {
                        RequestModel.getInstance()
                                .updateProperty(dsn, "zb_join_enable", "0")
                                .subscribeOn(Schedulers.io())
                                .subscribe(new Observer<BaseResult<Boolean>>() {
                                    @Override
                                    public void onSubscribe(Disposable d) {

                                    }

                                    @Override
                                    public void onNext(BaseResult<Boolean> booleanBaseResult) {

                                    }

                                    @Override
                                    public void onError(Throwable e) {

                                    }

                                    @Override
                                    public void onComplete() {

                                    }
                                });
                    }
                })//当直接退出时，通知网关退出配网模式。
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
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
