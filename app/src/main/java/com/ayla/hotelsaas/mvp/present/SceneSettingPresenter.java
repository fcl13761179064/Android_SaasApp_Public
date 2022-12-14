package com.ayla.hotelsaas.mvp.present;

import android.text.TextUtils;
import android.util.Log;

import com.ayla.hotelsaas.constant.ConstantValue;
import com.ayla.hotelsaas.application.MyApplication;
import com.ayla.hotelsaas.base.BasePresenter;
import com.ayla.hotelsaas.bean.BaseResult;
import com.ayla.hotelsaas.bean.DeviceCategoryDetailBean;
import com.ayla.hotelsaas.bean.DeviceListBean;
import com.ayla.hotelsaas.bean.DeviceTemplateBean;
import com.ayla.hotelsaas.bean.RuleEngineBean;
import com.ayla.hotelsaas.data.net.UnifiedErrorConsumer;
import com.ayla.hotelsaas.bean.scene_bean.BaseSceneBean;
import com.ayla.hotelsaas.mvp.model.RequestModel;
import com.ayla.hotelsaas.mvp.view.SceneSettingView;
import com.ayla.hotelsaas.utils.BeanObtainCompactUtil;
import com.ayla.hotelsaas.utils.TempUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class SceneSettingPresenter extends BasePresenter<SceneSettingView> {
    public void saveOrUpdateRuleEngine(BaseSceneBean mRuleEngineBean) {
        Observable<Boolean> observable;
        RuleEngineBean ruleEngineBean = BeanObtainCompactUtil.obtainRuleEngineBean(mRuleEngineBean);
        if (mRuleEngineBean.getRuleId() == 0) {
            observable = RequestModel.getInstance().saveRuleEngine(ruleEngineBean);
        } else {
            observable = RequestModel.getInstance().updateRuleEngine(ruleEngineBean);
        }
        Disposable subscribe = observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        mView.showProgress("?????????");
                    }
                })
                .doFinally(new Action() {
                    @Override
                    public void run() throws Exception {
                        mView.hideProgress();
                    }
                })
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean baseResult) throws Exception {
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

    public void deleteScene(long ruleId) {
        Disposable subscribe = RequestModel.getInstance()
                .deleteRuleEngine(ruleId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        mView.showProgress("?????????");
                    }
                })
                .doFinally(new Action() {
                    @Override
                    public void run() throws Exception {
                        mView.hideProgress();
                    }
                })
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        mView.deleteSuccess();
                    }
                }, new UnifiedErrorConsumer() {
                    @Override
                    public void handle(Throwable throwable) throws Exception {
                    }

                    @Override
                    public String getDefaultErrorMsg() {
                        return "????????????";
                    }
                });
        addSubscrebe(subscribe);
    }

    public void loadFunctionDetail(long roomId, List<BaseSceneBean.DeviceCondition> conditionItems, List<BaseSceneBean.DeviceAction> actionItems) {
        Set<DeviceListBean.DevicesBean> enableDevices = new HashSet<>();//??????????????????????????????????????????
        for (BaseSceneBean.DeviceAction actionItem : actionItems) {
            DeviceListBean.DevicesBean devicesBean = MyApplication.getInstance().getDevicesBean(actionItem.getTargetDeviceId());
            if (devicesBean != null) {
                enableDevices.add(devicesBean);
            }
        }
        for (BaseSceneBean.DeviceCondition conditionItem : conditionItems) {
            DeviceListBean.DevicesBean devicesBean = MyApplication.getInstance().getDevicesBean(conditionItem.getSourceDeviceId());
            if (devicesBean != null) {
                enableDevices.add(devicesBean);
            }
        }
        Observable<List<DeviceCategoryDetailBean>> observable;
        if (enableDevices.size() == 0) {
            observable = Observable.just(new ArrayList<DeviceCategoryDetailBean>());
        } else {
            observable = RequestModel.getInstance().getDeviceCategoryDetail(roomId);//????????????????????????
        }
        Disposable subscribe = observable
                .flatMap(new Function<List<DeviceCategoryDetailBean>, ObservableSource<DeviceTemplateBean[]>>() {
                    @Override
                    public ObservableSource<DeviceTemplateBean[]> apply(List<DeviceCategoryDetailBean> deviceCategoryDetailBeans) throws Exception {
                        List<Observable<DeviceTemplateBean>> tasks = new ArrayList<>();
                        for (DeviceListBean.DevicesBean enableDevice : enableDevices) {
                            if (TempUtils.isINFRARED_VIRTUAL_SUB_DEVICE(enableDevice)) {//?????????????????????(??????????????????)????????????????????????????????????????????????????????????????????????
                                tasks.add(RequestModel.getInstance()
                                        .fetchDeviceTemplate(enableDevice.getPid())
                                        .map(new Function<BaseResult<DeviceTemplateBean>, DeviceTemplateBean>() {
                                            @Override
                                            public DeviceTemplateBean apply(BaseResult<DeviceTemplateBean> deviceTemplateBeanBaseResult) throws Exception {
                                                return deviceTemplateBeanBaseResult.data;
                                            }
                                        })
                                        .compose(RequestModel.getInstance().modifyTemplateDisplayName(enableDevice.getDeviceId()))
                                );
                                continue;
                            }
                            for (DeviceCategoryDetailBean deviceCategoryDetailBean : deviceCategoryDetailBeans) {
                                if (TextUtils.equals(deviceCategoryDetailBean.getDeviceId(), enableDevice.getDeviceId())) {
                                    tasks.add(RequestModel.getInstance().fetchDeviceTemplate(enableDevice.getPid()).map(new Function<BaseResult<DeviceTemplateBean>, DeviceTemplateBean>() {
                                        @Override
                                        public DeviceTemplateBean apply(@NonNull BaseResult<DeviceTemplateBean> deviceTemplateBeanBaseResult) throws Exception {
                                            return deviceTemplateBeanBaseResult.data;
                                        }
                                    }).compose(RequestModel.getInstance().modifyTemplateDisplayName(enableDevice.getDeviceId())));
                                    break;
                                }
                            }
                        }
                        if (tasks.size() == 0) {
                            return Observable.just(new DeviceTemplateBean[]{});
                        } else {
                            return Observable.zip(tasks, new Function<Object[], DeviceTemplateBean[]>() {
                                @Override
                                public DeviceTemplateBean[] apply(Object[] objects) throws Exception {
                                    DeviceTemplateBean[] data = new DeviceTemplateBean[objects.length];
                                    for (int i = 0; i < objects.length; i++) {
                                        data[i] = (DeviceTemplateBean) objects[i];
                                    }
                                    return data;
                                }
                            });
                        }
                    }
                })//????????????????????????????????? ???????????????????????????
                .doOnNext(new Consumer<DeviceTemplateBean[]>() {
                    @Override
                    public void accept(DeviceTemplateBean[] deviceTemplateBeans) throws Exception {
                        for (BaseSceneBean.DeviceAction actionItem : actionItems) {
                            DeviceListBean.DevicesBean devicesBean = MyApplication.getInstance().getDevicesBean(actionItem.getTargetDeviceId());
                            if (devicesBean != null) {
                                for (DeviceTemplateBean deviceTemplateBean : deviceTemplateBeans) {
                                    for (DeviceTemplateBean.AttributesBean attribute : deviceTemplateBean.getAttributes()) {
                                        if (TextUtils.equals(devicesBean.getDeviceId(), deviceTemplateBean.getDeviceId())) {//???????????????????????????
                                            if (TextUtils.equals(attribute.getCode(), actionItem.getLeftValue())) {
                                                actionItem.setFunctionName(attribute.getDisplayName());

                                                List<DeviceTemplateBean.AttributesBean.ValueBean> attributeValue = attribute.getValue();
                                                DeviceTemplateBean.AttributesBean.SetupBean setupBean = attribute.getSetup();
                                                if (attributeValue != null) {
                                                    for (DeviceTemplateBean.AttributesBean.ValueBean valueBean : attributeValue) {
                                                        if (TextUtils.equals(valueBean.getValue(), actionItem.getRightValue())) {
                                                            actionItem.setValueName(valueBean.getDisplayName());
                                                        }
                                                    }
                                                } else if (setupBean != null) {
                                                    String unit = setupBean.getUnit();
                                                    actionItem.setValueName(String.format("%s%s", actionItem.getRightValue(), TextUtils.isEmpty(unit) ? "" : unit));
                                                }
                                            }
                                        }
                                    }
                                }
                            }else {
                                String rulename = MyApplication.getInstance().getmOneKeyRelueName(actionItem.getTargetDeviceId());
                                actionItem.setValueName(String.format("%s",rulename));
                            }
                        }
                        for (BaseSceneBean.DeviceCondition conditionItem : conditionItems) {
                            DeviceListBean.DevicesBean devicesBean = MyApplication.getInstance().getDevicesBean(conditionItem.getSourceDeviceId());
                            if (devicesBean != null) {
                                for (DeviceTemplateBean deviceTemplateBean : deviceTemplateBeans) {
                                    for (DeviceTemplateBean.AttributesBean attribute : deviceTemplateBean.getAttributes()) {
                                        if (TextUtils.equals(devicesBean.getDeviceId(), deviceTemplateBean.getDeviceId())) {//???????????????????????????
                                            if (TextUtils.equals(attribute.getCode(), conditionItem.getLeftValue())) {
                                                conditionItem.setFunctionName(attribute.getDisplayName());

                                                List<DeviceTemplateBean.AttributesBean.ValueBean> attributeValue = attribute.getValue();
                                                DeviceTemplateBean.AttributesBean.SetupBean setupBean = attribute.getSetup();
                                                List<DeviceTemplateBean.AttributesBean.BitValueBean> bitValue = attribute.getBitValue();
                                                if (attributeValue != null) {
                                                    for (DeviceTemplateBean.AttributesBean.ValueBean valueBean : attributeValue) {
                                                        if (TextUtils.equals(valueBean.getValue(), conditionItem.getRightValue())) {
                                                            conditionItem.setValueName(valueBean.getDisplayName());
                                                        }
                                                    }
                                                } else if (setupBean != null) {
                                                    String unit = setupBean.getUnit();
                                                    conditionItem.setValueName(String.format("%s%s", conditionItem.getRightValue(), TextUtils.isEmpty(unit) ? "" : unit));
                                                } else if (bitValue != null) {
                                                    for (DeviceTemplateBean.AttributesBean.BitValueBean bitValueBean : bitValue) {
                                                        if (bitValueBean.getBit() == conditionItem.getBit() &&
                                                                bitValueBean.getCompareValue() == conditionItem.getCompareValue() &&
                                                                TextUtils.equals(conditionItem.getRightValue(), String.valueOf(bitValueBean.getValue()))) {
                                                            conditionItem.setValueName(bitValueBean.getDisplayName());
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    if (deviceTemplateBean != null && deviceTemplateBean.getEvents() != null) {
                                        if (!conditionItem.getLeftValue().equals(ConstantValue.SCENE_TEMPLATE_CODE)) {//event????????????
                                            for (DeviceTemplateBean.EventbutesBean attribute : deviceTemplateBean.getEvents()) {
                                                if (TextUtils.equals(devicesBean.getDeviceId(), deviceTemplateBean.getDeviceId())) {//???????????????????????????
                                                    if (conditionItem.getLeftValue().endsWith(".")) {
                                                        String[] leftvalue = conditionItem.getLeftValue().split("\\.");
                                                        if (TextUtils.equals(attribute.getCode(), leftvalue[0])) {
                                                            conditionItem.setFunctionName(attribute.getDisplayName());
                                                            conditionItem.setValueName("");
                                                        }
                                                    } else {
                                                        //A.B
                                                        String[] leftCode = conditionItem.getLeftValue().split("\\.");
                                                        if (TextUtils.equals(attribute.getCode(), leftCode[0])) {
                                                            for (DeviceTemplateBean.AttributesBean outparam : attribute.getOutParams()) {
                                                                if (TextUtils.equals(outparam.getCode(), leftCode[1])) {
                                                                    String parentName = attribute.getDisplayName();
                                                                    if (outparam.getValue() != null) {
                                                                        for (DeviceTemplateBean.AttributesBean.ValueBean valueBean : outparam.getValue()) {
                                                                            if (TextUtils.equals(valueBean.getValue(), conditionItem.getRightValue())) {
                                                                                conditionItem.setFunctionName(parentName + "-" + outparam.getDisplayName());
                                                                                conditionItem.setValueName(valueBean.getDisplayName());
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        mView.showProgress("?????????...");
                    }
                })
                .doFinally(new Action() {
                    @Override
                    public void run() throws Exception {
                        mView.hideProgress();
                    }
                })
                .subscribe(new Consumer<DeviceTemplateBean[]>() {
                    @Override
                    public void accept(DeviceTemplateBean[] deviceTemplateBeans) throws Exception {
                        mView.showData();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.e("1111", "accept: ", throwable);
                    }
                });
        addSubscrebe(subscribe);
    }
}
