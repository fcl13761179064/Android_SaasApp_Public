package com.ayla.hotelsaas.mvp.present;

import android.text.TextUtils;

import com.ayla.hotelsaas.application.MyApplication;
import com.ayla.hotelsaas.base.BasePresenter;
import com.ayla.hotelsaas.bean.BaseResult;
import com.ayla.hotelsaas.bean.DeviceCategoryDetailBean;
import com.ayla.hotelsaas.bean.DeviceListBean;
import com.ayla.hotelsaas.bean.DeviceTemplateBean;
import com.ayla.hotelsaas.bean.TouchPanelDataBean;
import com.ayla.hotelsaas.mvp.model.RequestModel;
import com.ayla.hotelsaas.mvp.view.SceneSettingFunctionSelectView;
import com.ayla.hotelsaas.utils.TempUtils;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class SceneSettingFunctionSelectPresenter extends BasePresenter<SceneSettingFunctionSelectView> {
    /**
     * @param cuId
     * @param deviceId
     * @param oemModel
     */
    public void loadFunction(boolean condition, int cuId, String deviceId, String oemModel) {
        Disposable subscribe = RequestModel.getInstance()
                .getDeviceCategoryDetail()
                .map(new Function<BaseResult<List<DeviceCategoryDetailBean>>, List<String>>() {
                    @Override
                    public List<String> apply(BaseResult<List<DeviceCategoryDetailBean>> listBaseResult) throws Exception {
                        for (DeviceCategoryDetailBean deviceCategoryDetailBean : listBaseResult.data) {
                            if (TextUtils.equals(deviceCategoryDetailBean.getOemModel(), oemModel)) {
                                if (condition) {
                                    return deviceCategoryDetailBean.getConditionProperties();
                                } else {
                                    return deviceCategoryDetailBean.getActionProperties();
                                }
                            }
                        }
                        return new ArrayList<>();
                    }
                })//查询出设备对条件、动作的支持情况
                .zipWith(RequestModel.getInstance().fetchDeviceTemplate(oemModel)
                        .map(new Function<BaseResult<DeviceTemplateBean>, DeviceTemplateBean>() {
                            @Override
                            public DeviceTemplateBean apply(@NonNull BaseResult<DeviceTemplateBean> deviceTemplateBeanBaseResult) throws Exception {
                                return deviceTemplateBeanBaseResult.data;
                            }
                        })
                        .compose(RequestModel.getInstance().modifyTemplateDisplayName(deviceId)), new BiFunction<List<String>, DeviceTemplateBean, List<DeviceTemplateBean.AttributesBean>>() {
                    @NonNull
                    @Override
                    public List<DeviceTemplateBean.AttributesBean> apply(@NonNull List<String> properties, @NonNull DeviceTemplateBean deviceTemplateBean) throws Exception {
                        DeviceListBean.DevicesBean devicesBean = MyApplication.getInstance().getDevicesBean(deviceId);
                        List<DeviceTemplateBean.AttributesBean> data = new ArrayList<>();
                        if (TempUtils.isINFRARED_VIRTUAL_SUB_DEVICE(devicesBean) && !condition) {//如果是用途设备(红外遥控家电)，就直接套用物模型作为联动动作，不走品类中心过滤
                            data.addAll(deviceTemplateBean.getAttributes());
                        } else {
                            for (String property : properties) {
                                for (DeviceTemplateBean.AttributesBean attribute : deviceTemplateBean.getAttributes()) {
                                    if (TextUtils.equals(attribute.getCode(), property)) {
                                        data.add(attribute);
                                    }
                                }
                            }
                        }
                        return data;
                    }
                })//过滤出需要展示的内容
                .subscribeOn(Schedulers.io())
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
                .subscribe(new Consumer<List<DeviceTemplateBean.AttributesBean>>() {
                    @Override
                    public void accept(List<DeviceTemplateBean.AttributesBean> attributesBeans) throws Exception {
                        mView.showFunctions(attributesBeans);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        mView.showFunctions(null);
                    }
                });
        addSubscrebe(subscribe);
    }
}
