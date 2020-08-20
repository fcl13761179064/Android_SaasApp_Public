package com.ayla.hotelsaas.mvp.present;

import android.util.Log;

import com.ayla.hotelsaas.adapter.SceneSettingFunctionDatumSetAdapter;
import com.ayla.hotelsaas.application.MyApplication;
import com.ayla.hotelsaas.base.BasePresenter;
import com.ayla.hotelsaas.bean.BaseResult;
import com.ayla.hotelsaas.bean.DeviceCategoryDetailBean;
import com.ayla.hotelsaas.bean.DeviceListBean;
import com.ayla.hotelsaas.bean.DeviceTemplateBean;
import com.ayla.hotelsaas.bean.RuleEngineBean;
import com.ayla.hotelsaas.data.net.RxjavaObserver;
import com.ayla.hotelsaas.mvp.model.RequestModel;
import com.ayla.hotelsaas.mvp.view.SceneSettingView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class SceneSettingPresenter extends BasePresenter<SceneSettingView> {
    public void saveOrUpdateRuleEngine(RuleEngineBean mRuleEngineBean) {
        Observable<BaseResult> observable;
        if (mRuleEngineBean.getRuleId() == null) {
            observable = RequestModel.getInstance().saveRuleEngine(mRuleEngineBean);
        } else {
            observable = RequestModel.getInstance().updateRuleEngine(mRuleEngineBean);
        }
        Disposable subscribe = observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        mView.showProgress("加载中");
                    }
                })
                .doFinally(new Action() {
                    @Override
                    public void run() throws Exception {
                        mView.hideProgress();
                    }
                })
                .subscribe(new Consumer<BaseResult>() {
                    @Override
                    public void accept(BaseResult baseResult) throws Exception {
                        mView.saveSuccess();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        mView.saveFailed();
                    }
                });
        addSubscrebe(subscribe);
    }

    public void deleteScene(long ruleId) {
        RequestModel.getInstance()
                .deleteRuleEngine(ruleId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        mView.showProgress("加载中");
                    }
                })
                .doFinally(new Action() {
                    @Override
                    public void run() throws Exception {
                        mView.hideProgress();
                    }
                })
                .subscribe(new RxjavaObserver<Boolean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addSubscrebe(d);
                    }

                    @Override
                    public void _onNext(Boolean data) {
                        mView.deleteSuccess();
                    }

                    @Override
                    public void _onError(String code, String msg) {
                        mView.deleteFailed();
                    }
                });
    }

    public void loadFunctionDetail(List<RuleEngineBean.Condition.ConditionItem> conditionItems, List<RuleEngineBean.Action.ActionItem> actionItems) {
        Set<DeviceListBean.DevicesBean> enableDevices = new HashSet<>();//条件动作里面用到了的设备集合
        List<DeviceCategoryDetailBean> categoryDetailBeans = new ArrayList<>();//记录品类描述信息
        for (DeviceListBean.DevicesBean devicesBean : MyApplication.getInstance().getDevicesBean()) {
            for (RuleEngineBean.Action.ActionItem actionItem : actionItems) {
                if (devicesBean.getDeviceId().equals(actionItem.getTargetDeviceId())) {
                    enableDevices.add(devicesBean);
                }
            }
            for (RuleEngineBean.Condition.ConditionItem conditionItem : conditionItems) {
                if (devicesBean.getDeviceId().equals(conditionItem.getSourceDeviceId())) {
                    enableDevices.add(devicesBean);
                }
            }
        }
        Observable<List<DeviceCategoryDetailBean>> observable;
        if (enableDevices.size() == 0) {
            observable = Observable.just(new ArrayList<DeviceCategoryDetailBean>());
        } else {
            observable = RequestModel.getInstance()
                    .getDeviceCategoryDetail()
                    .map(new Function<BaseResult<List<DeviceCategoryDetailBean>>, List<DeviceCategoryDetailBean>>() {
                        @Override
                        public List<DeviceCategoryDetailBean> apply(BaseResult<List<DeviceCategoryDetailBean>> listBaseResult) throws Exception {
                            categoryDetailBeans.addAll(listBaseResult.data);
                            return listBaseResult.data;
                        }
                    });
        }
        Disposable subscribe = observable
                .subscribeOn(Schedulers.io())
                .flatMap(new Function<List<DeviceCategoryDetailBean>, ObservableSource<DeviceTemplateBean[]>>() {
                    @Override
                    public ObservableSource<DeviceTemplateBean[]> apply(List<DeviceCategoryDetailBean> deviceCategoryDetailBeans) throws Exception {
                        List<Observable<DeviceTemplateBean>> tasks = new ArrayList<>();
                        for (DeviceListBean.DevicesBean enableDevice : enableDevices) {
                            for (DeviceCategoryDetailBean deviceCategoryDetailBean : deviceCategoryDetailBeans) {
                                if (enableDevice.getCuId() == deviceCategoryDetailBean.getCuId()
                                        && enableDevice.getDeviceName().equals(deviceCategoryDetailBean.getDeviceName())) {
                                    Observable<DeviceTemplateBean> task = RequestModel.getInstance().fetchDeviceTemplate(deviceCategoryDetailBean.getOemModel())
                                            .map(new Function<BaseResult<DeviceTemplateBean>, DeviceTemplateBean>() {
                                                @Override
                                                public DeviceTemplateBean apply(BaseResult<DeviceTemplateBean> deviceTemplateBeanBaseResult) throws Exception {
                                                    return deviceTemplateBeanBaseResult.data;
                                                }
                                            });
                                    tasks.add(task);
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
                })//查询出了所有动作、条件 要用到的物模板信息
                .map(new Function<DeviceTemplateBean[], List<SceneSettingFunctionDatumSetAdapter.DatumBean>[]>() {
                    @Override
                    public List<SceneSettingFunctionDatumSetAdapter.DatumBean>[] apply(DeviceTemplateBean[] deviceTemplateBeans) throws Exception {
                        List<SceneSettingFunctionDatumSetAdapter.DatumBean> actions = new ArrayList<>();

                        for (RuleEngineBean.Action.ActionItem actionItem : actionItems) {
                            SceneSettingFunctionDatumSetAdapter.DatumBean datumBean = new SceneSettingFunctionDatumSetAdapter.DatumBean();
                            datumBean.setLeftValue(actionItem.getLeftValue());
                            datumBean.setFunctionName(actionItem.getLeftValue());
                            datumBean.setValueName(actionItem.getRightValue());
                            datumBean.setDeviceId(actionItem.getTargetDeviceId());
                            datumBean.setDeviceType(actionItem.getTargetDeviceType());
                            datumBean.setRightValueType(actionItem.getRightValueType());
                            datumBean.setOperator(actionItem.getOperator());
                            datumBean.setLeftValue(actionItem.getLeftValue());
                            s1:
                            for (DeviceListBean.DevicesBean devicesBean : MyApplication.getInstance().getDevicesBean()) {
                                if (devicesBean.getDeviceId().equals(actionItem.getTargetDeviceId())) {
                                    String deviceName = devicesBean.getDeviceName();
                                    int cuId = devicesBean.getCuId();
                                    for (DeviceCategoryDetailBean categoryDetailBean : categoryDetailBeans) {
                                        if (deviceName.equals(categoryDetailBean.getDeviceName()) && cuId == categoryDetailBean.getCuId()) {
                                            String oemModel = categoryDetailBean.getOemModel();
                                            for (DeviceTemplateBean deviceTemplateBean : deviceTemplateBeans) {
                                                String deviceCategory = deviceTemplateBean.getDeviceCategory();
                                                if (oemModel.equals(deviceCategory)) {
                                                    for (DeviceTemplateBean.AttributesBean attribute : deviceTemplateBean.getAttributes()) {
                                                        datumBean.setFunctionName(attribute.getDisplayName());
                                                        if (attribute.getCode().equals(actionItem.getLeftValue())) {
                                                            List<DeviceTemplateBean.AttributesBean.ValueBean> attributeValue = attribute.getValue();
                                                            if (attributeValue != null) {
                                                                for (DeviceTemplateBean.AttributesBean.ValueBean valueBean : attributeValue) {
                                                                    if (valueBean.getValue().equals(actionItem.getRightValue())) {
                                                                        datumBean.setValueName(valueBean.getDisplayName());
                                                                        break s1;
                                                                    }
                                                                }
                                                            } else {
                                                                datumBean.setValueName(actionItem.getRightValue());
                                                                break s1;
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            actions.add(datumBean);
                        }

                        List<SceneSettingFunctionDatumSetAdapter.DatumBean> conditions = new ArrayList<>();
                        for (RuleEngineBean.Condition.ConditionItem conditionItem : conditionItems) {
                            SceneSettingFunctionDatumSetAdapter.DatumBean datumBean = new SceneSettingFunctionDatumSetAdapter.DatumBean();
                            datumBean.setLeftValue(conditionItem.getLeftValue());
                            datumBean.setFunctionName(conditionItem.getLeftValue());
                            datumBean.setValueName(conditionItem.getRightValue());
                            datumBean.setDeviceId(conditionItem.getSourceDeviceId());
                            datumBean.setDeviceType(conditionItem.getSourceDeviceType());
                            datumBean.setOperator(conditionItem.getOperator());
                            datumBean.setLeftValue(conditionItem.getLeftValue());
                            s1:
                            for (DeviceListBean.DevicesBean devicesBean : MyApplication.getInstance().getDevicesBean()) {
                                if (devicesBean.getDeviceId().equals(conditionItem.getSourceDeviceId())) {
                                    String deviceName = devicesBean.getDeviceName();
                                    int cuId = devicesBean.getCuId();
                                    for (DeviceCategoryDetailBean categoryDetailBean : categoryDetailBeans) {
                                        if (deviceName.equals(categoryDetailBean.getDeviceName()) && cuId == categoryDetailBean.getCuId()) {
                                            String oemModel = categoryDetailBean.getOemModel();
                                            for (DeviceTemplateBean deviceTemplateBean : deviceTemplateBeans) {
                                                String deviceCategory = deviceTemplateBean.getDeviceCategory();
                                                if (oemModel.equals(deviceCategory)) {
                                                    for (DeviceTemplateBean.AttributesBean attribute : deviceTemplateBean.getAttributes()) {
                                                        if (attribute.getCode().equals(conditionItem.getLeftValue())) {
                                                            List<DeviceTemplateBean.AttributesBean.ValueBean> attributeValue = attribute.getValue();
                                                            if (attributeValue != null) {
                                                                for (DeviceTemplateBean.AttributesBean.ValueBean valueBean : attributeValue) {
                                                                    if (valueBean.getValue().equals(conditionItem.getRightValue())) {
                                                                        datumBean.setValueName(valueBean.getDisplayName());
                                                                        break s1;
                                                                    }
                                                                }
                                                            } else {
                                                                datumBean.setValueName(conditionItem.getRightValue());
                                                                break s1;
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            conditions.add(datumBean);
                        }
                        for (DeviceListBean.DevicesBean devicesBean : MyApplication.getInstance().getDevicesBean()) {
                            for (SceneSettingFunctionDatumSetAdapter.DatumBean condition : conditions) {
                                if (devicesBean.getDeviceId().equals(condition.getDeviceId())) {
                                    condition.setIconUrl(devicesBean.getIconUrl());
                                }
                            }
                            for (SceneSettingFunctionDatumSetAdapter.DatumBean action : actions) {
                                if (devicesBean.getDeviceId().equals(action.getDeviceId())) {
                                    action.setIconUrl(devicesBean.getIconUrl());
                                }
                            }
                        }

                        return new List[]{conditions, actions};
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
                .subscribe(new Consumer<List<SceneSettingFunctionDatumSetAdapter.DatumBean>[]>() {
                    @Override
                    public void accept(List<SceneSettingFunctionDatumSetAdapter.DatumBean>[] data) throws Exception {
                        mView.showData(data[0], data[1]);
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
