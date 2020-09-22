package com.ayla.hotelsaas.mvp.present;

import com.aliyun.iot.aep.sdk.framework.AApplication;
import com.ayla.hotelsaas.base.BasePresenter;
import com.ayla.hotelsaas.bean.BaseResult;
import com.ayla.hotelsaas.mvp.model.RequestModel;
import com.ayla.hotelsaas.mvp.view.HongyanGatewayAddGuideView;

import carlwu.top.lib_device_add.GatewayHelper;
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

public class HongyanGatewayAddGuidePresenter extends BasePresenter<HongyanGatewayAddGuideView> {

    public void startBind(AApplication application, long cuId, long scopeId, String deviceCategory, String deviceName, String HYproductkey, String HYdeviceName) {
        final GatewayHelper.BindHelper[] bindHelper = new GatewayHelper.BindHelper[1];
        Disposable subscribe = RequestModel.getInstance().getAuthCode(String.valueOf(scopeId))
                .map(new Function<BaseResult<String>, String>() {
                    @Override
                    public String apply(BaseResult<String> stringBaseResult) throws Exception {
                        return stringBaseResult.data;
                    }
                })
                .subscribeOn(Schedulers.io())
                .flatMap(new Function<String, ObservableSource<String>>() {
                    @Override
                    public ObservableSource<String> apply(String authCode) throws Exception {
                        return Observable.create(new ObservableOnSubscribe<String>() {
                            @Override
                            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                                bindHelper[0] = new GatewayHelper.BindHelper(application, new GatewayHelper.BindCallback() {
                                    @Override
                                    public void onGetTokenSuccess() {

                                    }

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
                                bindHelper[0].startBind(authCode, HYproductkey, HYdeviceName, 60);
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
                .observeOn(Schedulers.io())
                .flatMap(new Function<String, ObservableSource<?>>() {
                    @Override
                    public ObservableSource<?> apply(String s) throws Exception {
                        return RequestModel.getInstance()
                                .bindDeviceWithDSN(s, cuId, scopeId, 2, deviceCategory, deviceName, deviceName);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
                        mView.bindSuccess();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        mView.bindFailed();
                    }
                });
        addSubscrebe(subscribe);
    }

    /**
     * 通过设备和用户，阿里云的关系
     *
     * @param mHongyanproductKey,
     * @param mHongyandeviceName
     */
//    public void removeDeviceAllReleate(String mHongyanproductKey, String mHongyandeviceName) {
//        long startTime = System.currentTimeMillis();
//        RequestModel.getInstance().removeDeviceAllReleate(mHongyanproductKey, mHongyandeviceName)
//                .retryWhen(new Function<Observable<Throwable>, ObservableSource<?>>() {
//                    @Override
//                    public ObservableSource<?> apply(Observable<Throwable> throwableObservable) throws Exception {
//                        return throwableObservable.flatMap(new Function<Throwable, ObservableSource<?>>() {
//                            @Override
//                            public ObservableSource<?> apply(Throwable throwable) throws Exception {
//                                long currentTime = System.currentTimeMillis();
//                                long s = currentTime - startTime;
//                                if (s > 60_000) {
//                                    return Observable.error(throwable);
//                                } else {
//                                    return Observable.timer(3, TimeUnit.SECONDS);
//                                }
//                            }
//                        });
//                    }
//                })
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new RxjavaObserver<String>() {
//                    @Override
//                    public void onSubscribe(Disposable d) {
//                        addSubscrebe(d);
//                    }
//
//                    @Override
//                    public void _onNext(String data) {
//
//                        mView.removeReleteSuccess(data);
//                    }
//
//                    @Override
//                    public void _onError(String code, String msg) {
//
//                        mView.removeReleteFail(code, msg);
//                    }
//                });
//    }
}
