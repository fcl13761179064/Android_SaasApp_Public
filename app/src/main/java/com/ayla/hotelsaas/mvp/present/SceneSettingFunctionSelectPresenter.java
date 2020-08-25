package com.ayla.hotelsaas.mvp.present;

import android.text.TextUtils;

import com.ayla.hotelsaas.base.BasePresenter;
import com.ayla.hotelsaas.bean.BaseResult;
import com.ayla.hotelsaas.bean.DeviceTemplateBean;
import com.ayla.hotelsaas.bean.TouchPanelDataBean;
import com.ayla.hotelsaas.mvp.model.RequestModel;
import com.ayla.hotelsaas.mvp.view.SceneSettingFunctionSelectView;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class SceneSettingFunctionSelectPresenter extends BasePresenter<SceneSettingFunctionSelectView> {

    public void loadFunction(int cuId, String deviceId, String oemModel, List<String> properties) {
        Disposable subscribe = RequestModel.getInstance()
                .fetchDeviceTemplate(oemModel)//拉取物模板
                .map(new Function<BaseResult<DeviceTemplateBean>, List<DeviceTemplateBean.AttributesBean>>() {
                    @Override
                    public List<DeviceTemplateBean.AttributesBean> apply(BaseResult<DeviceTemplateBean> deviceTemplateBeanBaseResult) throws Exception {
                        List<DeviceTemplateBean.AttributesBean> data = new ArrayList<>();
                        DeviceTemplateBean deviceTemplateBean = deviceTemplateBeanBaseResult.data;
                        for (String property : properties) {
                            for (DeviceTemplateBean.AttributesBean attribute : deviceTemplateBean.getAttributes()) {
                                if (property.equals(attribute.getCode())) {
                                    data.add(attribute);
                                }
                            }
                        }
                        return data;
                    }
                })//过滤出需要展示的内容
                .zipWith(RequestModel.getInstance()
                        .getALlTouchPanelDeviceInfo(cuId, deviceId).map(new Function<BaseResult<List<TouchPanelDataBean>>, List<TouchPanelDataBean>>() {
                            @Override
                            public List<TouchPanelDataBean> apply(BaseResult<List<TouchPanelDataBean>> listBaseResult) throws Exception {
                                return listBaseResult.data;
                            }
                        }), new BiFunction<List<DeviceTemplateBean.AttributesBean>, List<TouchPanelDataBean>, List<DeviceTemplateBean.AttributesBean>>() {
                    @Override
                    public List<DeviceTemplateBean.AttributesBean> apply(List<DeviceTemplateBean.AttributesBean> attributesBeans, List<TouchPanelDataBean> touchPanelDataBeans) throws Exception {
                        for (DeviceTemplateBean.AttributesBean attributesBean : attributesBeans) {
                            for (TouchPanelDataBean touchPanelDataBean : touchPanelDataBeans) {
                                if (TextUtils.equals(attributesBean.getCode(), touchPanelDataBean.getPropertyName())) {
                                    attributesBean.setDisplayName(touchPanelDataBean.getPropertyValue());
                                    break;
                                }
                            }
                        }
                        return attributesBeans;
                    }
                })//根据设置的功能别名，篡改displayname。
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
