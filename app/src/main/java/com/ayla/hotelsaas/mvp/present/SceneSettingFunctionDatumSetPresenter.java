package com.ayla.hotelsaas.mvp.present;

import android.text.TextUtils;
import android.util.Log;

import com.ayla.hotelsaas.application.MyApplication;
import com.ayla.hotelsaas.base.BasePresenter;
import com.ayla.hotelsaas.bean.BaseResult;
import com.ayla.hotelsaas.bean.DeviceListBean;
import com.ayla.hotelsaas.bean.DeviceTemplateBean;
import com.ayla.hotelsaas.bean.NewGroupAbility;
import com.ayla.hotelsaas.mvp.model.RequestModel;
import com.ayla.hotelsaas.mvp.view.SceneSettingFunctionDatumSetView;

import java.util.ArrayList;
import java.util.List;

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

                        for (DeviceTemplateBean.EventbutesBean attribute : deviceTemplateBean.getEvents()) {
                            if (property.endsWith(".")) {
                                String[] split = property.split("\\.");
                                if (TextUtils.equals(attribute.getCode(), split[0])) {
                                    attribute.setCode(property);
                                    return attribute;
                                }
                            } else {
                                DeviceTemplateBean.AttributesBean attributesBean = new DeviceTemplateBean.AttributesBean();
                                String[] spiltCode = property.split("\\.");
                                if (attribute.getCode().equals(spiltCode[0])) {
                                    String parentName = attribute.getDisplayName();
                                    for (DeviceTemplateBean.AttributesBean outparam : attribute.getOutParams()) {
                                        if (TextUtils.equals(outparam.getCode(), spiltCode[1])) {
                                            outparam.setDisplayName(parentName + "-" + outparam.getDisplayName());
                                            outparam.setCode(property);
                                            return outparam;
                                        }
                                    }
                                }
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

    public void loadGroupFunction(String groupId, String code) {
        if (mView != null) {
            mView.showProgress("加载中");
        }
        Disposable subscribe = RequestModel.getInstance().getGroupAbility(groupId).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(groupAbilities -> {
            if (mView != null) {
                for (int j = 0; j < groupAbilities.size(); j++) {
                    NewGroupAbility newGroupAbility = groupAbilities.get(j);
                    if (TextUtils.equals(newGroupAbility.getAbilityCode(), code)) {
                        DeviceTemplateBean.AttributesBean attributesBean = new DeviceTemplateBean.AttributesBean();
                        attributesBean.setCode(newGroupAbility.getAbilityCode());
                        attributesBean.setDisplayName(newGroupAbility.getDisplayName());
                        List<DeviceTemplateBean.AttributesBean.ValueBean> valueBeans = new ArrayList<>();
                        for (int k = 0; k < newGroupAbility.getAbilityValues().size(); k++) {
                            NewGroupAbility.AbilityValues abilityValue = newGroupAbility.getAbilityValues().get(k);
                            DeviceTemplateBean.AttributesBean.ValueBean valueBean = new DeviceTemplateBean.AttributesBean.ValueBean();
                            valueBean.setDataType(abilityValue.getDataType());
                            valueBean.setDisplayName(abilityValue.getDisplayName());
                            valueBean.setValue(abilityValue.getValue());
                            valueBean.setAbilitySubCode(abilityValue.getAbilitySubCode());
                            valueBean.setVersion(newGroupAbility.getVersion());
                            NewGroupAbility.Setup setup = abilityValue.getSetup();
                            if (setup != null) {
                                DeviceTemplateBean.AttributesBean.SetupBean setupBean = new DeviceTemplateBean.AttributesBean.SetupBean();
                                try {
                                    setupBean.setMax(Double.parseDouble(setup.getMax()));
                                    setupBean.setMin(Double.parseDouble(setup.getMin()));
                                    setupBean.setStep(Double.parseDouble(setup.getStep()));
                                    setupBean.setUnit(setup.getUnit());
                                    valueBean.setSetupBean(setupBean);
                                } catch (Exception e) {
                                    Log.e("S", "setUp数据类型异常");
                                }
                            }
                            valueBeans.add(valueBean);
                        }
                        attributesBean.setValue(valueBeans);
                        mView.getGroupFunctionsSuccess(attributesBean);
                        break;
                    }
                }
            }
            mView.hideProgress();
        }, throwable -> {
            mView.hideProgress();
        });
        addSubscrebe(subscribe);
    }
}
