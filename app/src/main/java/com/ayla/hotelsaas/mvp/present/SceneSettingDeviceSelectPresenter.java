package com.ayla.hotelsaas.mvp.present;

import com.ayla.hotelsaas.application.MyApplication;
import com.ayla.hotelsaas.base.BasePresenter;
import com.ayla.hotelsaas.bean.BaseResult;
import com.ayla.hotelsaas.bean.DeviceCategoryDetailBean;
import com.ayla.hotelsaas.bean.DeviceListBean;
import com.ayla.hotelsaas.mvp.model.RequestModel;
import com.ayla.hotelsaas.mvp.view.SceneSettingDeviceSelectView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class SceneSettingDeviceSelectPresenter extends BasePresenter<SceneSettingDeviceSelectView> {

    public void loadDevice(boolean condition) {
        Disposable subscribe = RequestModel.getInstance().getDeviceCategoryDetail()
                .subscribeOn(Schedulers.io())
                .map(new Function<BaseResult<List<DeviceCategoryDetailBean>>, List<DeviceCategoryDetailBean>>() {
                    @Override
                    public List<DeviceCategoryDetailBean> apply(BaseResult<List<DeviceCategoryDetailBean>> listBaseResult) throws Exception {
                        return listBaseResult.data;
                    }
                })//查询出设备对条件、动作的支持情况
                .map(new Function<List<DeviceCategoryDetailBean>, Object[]>() {
                    @Override
                    public Object[] apply(List<DeviceCategoryDetailBean> deviceCategoryDetailBeans) throws Exception {
                        List<DeviceListBean.DevicesBean> enableDevices = new ArrayList<>();//可以显示在列表里面的设备
                        List<List<String>> others = new ArrayList<>();
                        List<String> oemModels = new ArrayList<>();
                        List<DeviceListBean.DevicesBean> devicesBeans = MyApplication.getInstance().getDevicesBean();
                        for (DeviceCategoryDetailBean categoryDetailBean : deviceCategoryDetailBeans) {
                            for (DeviceListBean.DevicesBean devicesBean : devicesBeans) {
                                if (categoryDetailBean.getCuId() == devicesBean.getCuId()
                                        && categoryDetailBean.getDeviceName().equals(devicesBean.getDeviceName())) {//找到已绑定的设备的条件、动作描述信息
                                    if (condition) {
                                        List<String> conditionProperties = categoryDetailBean.getConditionProperties();
                                        if (conditionProperties != null && conditionProperties.size() != 0) {
                                            enableDevices.add(devicesBean);
                                            others.add(categoryDetailBean.getConditionProperties());
                                            oemModels.add(categoryDetailBean.getOemModel());
                                        }
                                    } else {
                                        List<String> actionProperties = categoryDetailBean.getActionProperties();
                                        if (actionProperties != null && actionProperties.size() != 0) {
                                            enableDevices.add(devicesBean);
                                            others.add(categoryDetailBean.getActionProperties());
                                            oemModels.add(categoryDetailBean.getOemModel());
                                        }
                                    }
                                }
                            }
                        }
                        return new Object[]{enableDevices, others, oemModels};
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        mView.showProgress("加载中...");
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
                    public void accept(Object[] data) throws Exception {
                        mView.showDevices((List<DeviceListBean.DevicesBean>) data[0], (List<List<String>>) data[1], (List<String>) data[2]);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        mView.showDevices(null, null, null);
                    }
                });
        addSubscrebe(subscribe);

    }
}
