package com.ayla.hotelsaas.mvp.present;

import android.util.Log;

import com.ayla.hotelsaas.base.BasePresenter;
import com.ayla.hotelsaas.bean.BaseResult;
import com.ayla.hotelsaas.bean.DeviceListBean;
import com.ayla.hotelsaas.data.net.RxjavaFlatmapThrowable;
import com.ayla.hotelsaas.mvp.model.RequestModel;
import com.ayla.hotelsaas.mvp.view.ZigBeeAddView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import carlwu.top.lib_device_add.NodeHelper;
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

public class ZigBeeAddPresenter extends BasePresenter<ZigBeeAddView> {
    public void bindAylaNode(String dsn, long cuId, long scopeId, String deviceCategory, String deviceName) {
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
                        mView.step2Finish();
                        mView.step3Start();
                    }
                })//通知候选节点查找成功
                .observeOn(Schedulers.io())
                .flatMap(new Function<List<DeviceListBean.DevicesBean>, ObservableSource<DeviceListBean.DevicesBean>>() {
                    @Override
                    public ObservableSource<DeviceListBean.DevicesBean> apply(List<DeviceListBean.DevicesBean> devices) throws Exception {
                        List<Observable<DeviceListBean.DevicesBean>> tasks = new ArrayList<>();
                        for (DeviceListBean.DevicesBean device : devices) {
                            Observable<DeviceListBean.DevicesBean> task = RequestModel.getInstance()
                                    .bindDeviceWithDSN(device.getDeviceId(), cuId, scopeId, 2, deviceCategory, deviceName, deviceName)
                                    .map(new Function<BaseResult<DeviceListBean.DevicesBean>, DeviceListBean.DevicesBean>() {
                                        @Override
                                        public DeviceListBean.DevicesBean apply(BaseResult<DeviceListBean.DevicesBean> devicesBeanBaseResult) throws Exception {
                                            return devicesBeanBaseResult.data;
                                        }
                                    })
                                    .doOnError(new Consumer<Throwable>() {
                                        @Override
                                        public void accept(Throwable throwable) throws Exception {
                                            Log.d("候选节点绑定失败", "accept: " + throwable + " " + device.getDeviceId());
                                        }
                                    })
                                    .doOnNext(new Consumer<DeviceListBean.DevicesBean>() {
                                        @Override
                                        public void accept(DeviceListBean.DevicesBean baseResult) throws Exception {
                                            Log.d("候选节点绑定成功", "accept: " + device.getDeviceId());
                                        }
                                    });
                            tasks.add(task);
                        }
                        if (tasks.size() == 0) {
                            return Observable.error(new Throwable("没有候选节点"));
                        } else {
                            return Observable.zip(tasks, new Function<Object[], DeviceListBean.DevicesBean>() {
                                @Override
                                public DeviceListBean.DevicesBean apply(Object[] objects) throws Exception {
                                    return (DeviceListBean.DevicesBean) objects[0];
                                }
                            });
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
                    public ObservableSource<DeviceListBean.DevicesBean> apply(DeviceListBean.DevicesBean bean) throws Exception {
                        return RequestModel.getInstance()
                                .updateProperty(dsn, "zb_join_enable", "0")
                                .map(new Function<BaseResult<Boolean>, DeviceListBean.DevicesBean>() {
                                    @Override
                                    public DeviceListBean.DevicesBean apply(BaseResult<Boolean> booleanBaseResult) throws Exception {
                                        return bean;
                                    }
                                })
                                .onErrorReturnItem(bean);
                    }
                })//前面步骤正常时，通知网关退出配网模式。
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<DeviceListBean.DevicesBean>() {
                    @Override
                    public void accept(DeviceListBean.DevicesBean bean) throws Exception {
                        mView.bindSuccess(bean.getDeviceId(), deviceName);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        mView.bindFailed(null);
                    }
                });
        addSubscrebe(subscribe);
    }

    public void bindHongYanNode(String dsn, long cuId, long scopeId, String deviceCategory, String deviceName) {
        final NodeHelper[] nodeHelper = new NodeHelper[1];
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
                                            if (!emitter.isDisposed()) {
                                                emitter.onError(e);
                                            }
                                            return true;
                                        }
                                    }

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
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
                        mView.step2Finish();
                    }
                })//通知子节点绑定成功
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
