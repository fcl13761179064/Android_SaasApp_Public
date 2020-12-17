package com.ayla.hotelsaas.mvp.present;

import com.ayla.hotelsaas.application.MyApplication;
import com.ayla.hotelsaas.base.BasePresenter;
import com.ayla.hotelsaas.bean.BaseResult;
import com.ayla.hotelsaas.bean.DeviceListBean;
import com.ayla.hotelsaas.bean.DeviceTemplateBean;
import com.ayla.hotelsaas.bean.PurposeCategoryBean;
import com.ayla.hotelsaas.data.net.BaseResultTransformer;
import com.ayla.hotelsaas.mvp.model.RequestModel;
import com.ayla.hotelsaas.mvp.view.SwitchUsageSettingView;

import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class SwitchUsageSettingPresenter extends BasePresenter<SwitchUsageSettingView> {
    public void getPurposeCategory() {
        Disposable subscribe = RequestModel.getInstance().getPurposeCategory()
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
                .subscribe(new Consumer<List<PurposeCategoryBean>>() {
                    @Override
                    public void accept(List<PurposeCategoryBean> purposeCategoryBeans) throws Exception {
                        mView.showPurposeCategory(purposeCategoryBeans);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                });
        addSubscrebe(subscribe);
    }

    public void handleSave(long scopeId, String deviceId, String[] selfNames, PurposeCategoryBean[] selfPurposeCategories) {
        Disposable subscribe = Observable
                .fromCallable(new Callable<String>() {
                    @Override
                    public String call() throws Exception {
                        DeviceListBean.DevicesBean devicesBean = MyApplication.getInstance().getDevicesBean(deviceId);
                        return devicesBean.getDeviceCategory();
                    }
                })
                .flatMap(new Function<String, ObservableSource<DeviceTemplateBean>>() {
                    @Override
                    public ObservableSource<DeviceTemplateBean> apply(@NonNull String s) throws Exception {
                        return RequestModel.getInstance().fetchDeviceTemplate(s)
                                .compose(new BaseResultTransformer<BaseResult<DeviceTemplateBean>, DeviceTemplateBean>() {
                                });
                    }
                })//查询出物模板信息
                .flatMap(new Function<DeviceTemplateBean, ObservableSource<?>>() {
                    @Override
                    public ObservableSource<?> apply(@NonNull DeviceTemplateBean deviceTemplateBean) throws Exception {
                        int[] purposeCategory = new int[selfNames.length];
                        for (int i = 0; i < selfPurposeCategories.length; i++) {
                            purposeCategory[i] = selfPurposeCategories[i].getId();
                        }
                        return RequestModel.getInstance().purposeBatchSave(scopeId, deviceId, selfNames, selfNames, purposeCategory);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
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
