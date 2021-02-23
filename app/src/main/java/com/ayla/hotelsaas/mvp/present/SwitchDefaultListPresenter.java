package com.ayla.hotelsaas.mvp.present;

import com.ayla.hotelsaas.base.BasePresenter;
import com.ayla.hotelsaas.bean.BaseResult;
import com.ayla.hotelsaas.bean.DeviceTemplateBean;
import com.ayla.hotelsaas.mvp.model.RequestModel;
import com.ayla.hotelsaas.mvp.view.SwitchDefaultListView;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * @描述
 * @作者 fanchunlei
 * @时间 2020/7/29
 */
public class SwitchDefaultListPresenter extends BasePresenter<SwitchDefaultListView> {

    /**
     * 查询默认开关设置的情况
     *
     * @param deviceId
     * @param pid
     * @return
     */
    public void getSwitchDefaultSetting(String deviceId, String pid) {
        Disposable subscribe = RequestModel.getInstance().fetchDeviceTemplate(pid)
                .map(new Function<BaseResult<DeviceTemplateBean>, DeviceTemplateBean>() {
                    @Override
                    public DeviceTemplateBean apply(@NonNull BaseResult<DeviceTemplateBean> deviceTemplateBeanBaseResult) throws Exception {
                        return deviceTemplateBeanBaseResult.data;
                    }
                })
                .compose(RequestModel.getInstance().modifyTemplateDisplayName(deviceId))
                .map(new Function<DeviceTemplateBean, List<String[]>>() {
                    @Override
                    public List<String[]> apply(@NonNull DeviceTemplateBean deviceTemplateBean) throws Exception {
                        List<DeviceTemplateBean.AttributesBean> defaultAttributesBeans = new ArrayList<>();
                        for (DeviceTemplateBean.AttributesBean attribute : deviceTemplateBean.getAttributes()) {
                            if (attribute.getCode().endsWith(":OnoffDefault")) {//是默认开关的设置属性格式
                                defaultAttributesBeans.add(attribute);
                            }
                        }
                        List<String[]> result = new ArrayList<>();
                        for (DeviceTemplateBean.AttributesBean attribute : deviceTemplateBean.getAttributes()) {
                            for (DeviceTemplateBean.AttributesBean defaultAttributesBean : defaultAttributesBeans) {
                                if (attribute.getCode().equals(defaultAttributesBean.getCode().substring(0, defaultAttributesBean.getCode().length() - 7))) {
                                    result.add(new String[]{defaultAttributesBean.getCode(), attribute.getDisplayName()});
                                }
                            }
                        }
                        return result;
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
                .subscribe(new Consumer<List<String[]>>() {
                    @Override
                    public void accept(List<String[]> result) throws Exception {
                        mView.showData(result);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        mView.showError(throwable);
                    }
                });
        addSubscrebe(subscribe);
    }
}
