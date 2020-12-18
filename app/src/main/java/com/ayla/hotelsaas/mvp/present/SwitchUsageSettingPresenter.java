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

    //PowerSwitch_3
    public void handleSave(long scopeId, String deviceId, String[] selfNames, PurposeCategoryBean[] selfPurposeCategories) {
        Observable<String[]> purposeIdObservable = Observable
                .fromCallable(new Callable<DeviceListBean.DevicesBean>() {
                    @Override
                    public DeviceListBean.DevicesBean call() throws Exception {
                        return MyApplication.getInstance().getDevicesBean(deviceId);
                    }
                })
                .flatMap(new Function<DeviceListBean.DevicesBean, ObservableSource<String[]>>() {
                    @Override
                    public ObservableSource<String[]> apply(@NonNull DeviceListBean.DevicesBean devicesBean) throws Exception {
                        if (devicesBean.getCuId() == 0) {//艾拉设备
                            return RequestModel.getInstance()
                                    .fetchDeviceTemplate(devicesBean.getDeviceCategory())
                                    .compose(new BaseResultTransformer<BaseResult<DeviceTemplateBean>, DeviceTemplateBean>() {
                                    })
                                    .flatMap(new Function<DeviceTemplateBean, ObservableSource<String[]>>() {
                                        @Override
                                        public ObservableSource<String[]> apply(@NonNull DeviceTemplateBean deviceTemplateBean) throws Exception {
                                            String[] purposeId = new String[selfNames.length];
                                            for (int i = 0; i < purposeId.length; i++) {
                                                for (DeviceTemplateBean.AttributesBean attribute : deviceTemplateBean.getAttributes()) {
                                                    if (attribute.getCode().endsWith(":Onoff") && attribute.getCode().startsWith(String.valueOf(i + 1) + ":")) {
                                                        purposeId[i] = attribute.getCode();
                                                        break;
                                                    }
                                                }
                                            }
                                            return Observable.just(purposeId);
                                        }
                                    });
                        } else if (devicesBean.getCuId() == 1) {//阿里设备
                            String[] purposeId = new String[selfNames.length];
                            for (int i = 0; i < purposeId.length; i++) {
                                purposeId[i] = "PowerSwitch_" + (i + 1);
                            }
                            return Observable.just(purposeId);
                        }
                        return Observable.error(new Exception("不支持的设备"));
                    }
                });//查询出需要设置的 String[] purposeId

        Disposable subscribe = purposeIdObservable
                .flatMap(new Function<String[], ObservableSource<?>>() {
                    @Override
                    public ObservableSource<?> apply(@NonNull String[] purposeId) throws Exception {
                        int[] purposeCategory = new int[selfNames.length];
                        for (int i = 0; i < selfPurposeCategories.length; i++) {
                            purposeCategory[i] = selfPurposeCategories[i].getId();
                        }
                        return RequestModel.getInstance().purposeBatchSave(scopeId, deviceId, selfNames, purposeId, purposeCategory);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
                        mView.saveSuccess();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        mView.saveFailed(throwable);
                    }
                });
        addSubscrebe(subscribe);
    }
}
