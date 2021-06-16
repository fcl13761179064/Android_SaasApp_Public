package com.ayla.hotelsaas.mvp.present;

import android.text.TextUtils;

import com.ayla.hotelsaas.application.Constance;
import com.ayla.hotelsaas.application.MyApplication;
import com.ayla.hotelsaas.base.BasePresenter;
import com.ayla.hotelsaas.bean.BaseResult;
import com.ayla.hotelsaas.bean.DeviceCategoryDetailBean;
import com.ayla.hotelsaas.bean.DeviceListBean;
import com.ayla.hotelsaas.bean.DeviceTemplateBean;
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
     * @param deviceId
     */
    public void
    loadFunction(boolean condition, String deviceId, String pid) {
        Disposable subscribe = RequestModel.getInstance()
                .getDeviceCategoryDetail(pid)
                .map(new Function<DeviceCategoryDetailBean, List<String>>() {
                    @Override
                    public List<String> apply(@NonNull DeviceCategoryDetailBean deviceCategoryDetailBean) throws Exception {
                        if (condition) {
                            return deviceCategoryDetailBean.getConditionProperties();
                        } else {
                            return deviceCategoryDetailBean.getActionProperties();
                        }
                    }
                })//查询出设备对条件、动作的支持情况
                .zipWith(RequestModel.getInstance().fetchDeviceTemplate(pid)
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

                        /**
                         * @update 2021年6月8日 新增支持event类型
                         * 1.A.事件情况
                         * 2.A.B事件情况
                         */
                        for (String property : properties) {
                            if (property != Constance.SCENE_TEMPLATE_CODE && property.contains(".")) {//event事件类型
                                if (property.endsWith(".")) {//A.情况
                                    String[] spiltCode = property.split("\\.");
                                    for (DeviceTemplateBean.EventbutesBean eventbuesBean : deviceTemplateBean.getEvents()) {
                                        if (eventbuesBean.getCode().equals(spiltCode[0])) {
                                            data.add(eventbuesBean);
                                        }
                                    }
                                } else {//A.B情况
                                    String[] spiltCode = property.split("\\.");
                                    for (DeviceTemplateBean.EventbutesBean eventbuesBean : deviceTemplateBean.getEvents()) {
                                        if (eventbuesBean.getCode().equals(spiltCode[0])) {
                                            String parentName = eventbuesBean.getDisplayName();
                                            for (DeviceTemplateBean.AttributesBean outparam : eventbuesBean.getOutParams()) {
                                                if (TextUtils.equals(outparam.getCode(), spiltCode[1])) {
                                                    outparam.setDisplayName(parentName + "-" + outparam.getDisplayName());
                                                    outparam.setCode(property);
                                                    data.add(outparam);
                                                }
                                            }
                                        }
                                    }
                                }
                            } else {
                                if (TempUtils.isINFRARED_VIRTUAL_SUB_DEVICE(devicesBean) && !condition) {//如果是用途设备(红外遥控家电)，就直接套用物模型作为联动动作，不走品类中心过滤
                                    data.addAll(deviceTemplateBean.getAttributes());
                                } else {
                                        for (DeviceTemplateBean.AttributesBean attribute : deviceTemplateBean.getAttributes()) {
                                            if (TextUtils.equals(attribute.getCode(), property)) {
                                                data.add(attribute);
                                            }
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
