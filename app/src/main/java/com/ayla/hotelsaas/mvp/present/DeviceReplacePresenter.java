package com.ayla.hotelsaas.mvp.present;

import com.ayla.hotelsaas.base.BasePresenter;
import com.ayla.hotelsaas.bean.DeviceCategoryBean;
import com.ayla.hotelsaas.mvp.model.RequestModel;
import com.ayla.hotelsaas.mvp.view.DeviceReplaceView;

import java.util.List;

import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class DeviceReplacePresenter extends BasePresenter<DeviceReplaceView> {

    public void getGatewayId(String deviceId) {
        Disposable subscribe = RequestModel.getInstance().getNodeGateway(deviceId)
                .flatMap(new Function<String, ObservableSource<Object[]>>() {
                    @Override
                    public ObservableSource<Object[]> apply(@NonNull String s) throws Exception {
                        return RequestModel.getInstance()
                                .getDeviceCategory()
                                .map(new Function<List<DeviceCategoryBean>, Object[]>() {
                                    @Override
                                    public Object[] apply(@NonNull List<DeviceCategoryBean> deviceCategoryBeans) throws Exception {
                                        return new Object[]{s, deviceCategoryBeans};
                                    }
                                });
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        mView.showProgress();
                    }
                })
                .doFinally(new Action() {
                    @Override
                    public void run() throws Exception {
                        mView.hideProgress();
                    }
                })
                .subscribe(new Consumer<Object[]>() {
                    @Override
                    public void accept(Object[] o) throws Exception {
                        String gatewayId = (String) o[0];
                        List<DeviceCategoryBean> deviceCategoryBeans = (List<DeviceCategoryBean>) o[1];
                        mView.canReplace(gatewayId, deviceCategoryBeans);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        mView.cannotReplace(throwable);
                    }
                });
        addSubscrebe(subscribe);
    }
}
