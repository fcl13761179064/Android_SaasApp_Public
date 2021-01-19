package com.ayla.hotelsaas.mvp.present;

import android.text.TextUtils;

import com.ayla.hotelsaas.application.MyApplication;
import com.ayla.hotelsaas.base.BasePresenter;
import com.ayla.hotelsaas.bean.BaseResult;
import com.ayla.hotelsaas.bean.DeviceListBean;
import com.ayla.hotelsaas.bean.DeviceTemplateBean;
import com.ayla.hotelsaas.mvp.model.RequestModel;
import com.ayla.hotelsaas.mvp.view.SceneSettingFunctionDatumSetView;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class SceneSettingFunctionDatumSetPresenter extends BasePresenter<SceneSettingFunctionDatumSetView> {
    public void loadFunction(String deviceId, String property) {
        final DeviceListBean.DevicesBean devicesBean = MyApplication.getInstance().getDevicesBean(deviceId);
        Disposable subscribe = RequestModel.getInstance()
                .fetchDeviceTemplate(devicesBean.getPid())
                .map(new Function<BaseResult<DeviceTemplateBean>, DeviceTemplateBean>() {
                    @Override
                    public DeviceTemplateBean apply(@NonNull BaseResult<DeviceTemplateBean> deviceTemplateBeanBaseResult) throws Exception {
                        return deviceTemplateBeanBaseResult.data;
                    }
                })
                .compose(RequestModel.getInstance().modifyTemplateDisplayName(deviceId))
                .map(new Function<DeviceTemplateBean, DeviceTemplateBean.AttributesBean>() {
                    @Override
                    public DeviceTemplateBean.AttributesBean apply(@NonNull DeviceTemplateBean deviceTemplateBean) throws Exception {
                        for (DeviceTemplateBean.AttributesBean attribute : deviceTemplateBean.getAttributes()) {
                            if (TextUtils.equals(attribute.getCode(), property)) {
                                return attribute;
                            }
                        }
                        return null;
                    }
                })
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
                .subscribe(new Consumer<DeviceTemplateBean.AttributesBean>() {
                    @Override
                    public void accept(DeviceTemplateBean.AttributesBean attributesBean) throws Exception {
                        mView.showFunctions(attributesBean);
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
