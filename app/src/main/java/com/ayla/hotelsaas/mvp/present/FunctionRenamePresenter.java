package com.ayla.hotelsaas.mvp.present;

import android.text.TextUtils;

import com.ayla.hotelsaas.adapter.FunctionRenameListAdapter;
import com.ayla.hotelsaas.base.BasePresenter;
import com.ayla.hotelsaas.bean.BaseResult;
import com.ayla.hotelsaas.bean.DeviceCategoryDetailBean;
import com.ayla.hotelsaas.bean.DeviceTemplateBean;
import com.ayla.hotelsaas.bean.TouchPanelDataBean;
import com.ayla.hotelsaas.mvp.model.RequestModel;
import com.ayla.hotelsaas.mvp.view.FunctionRenameView;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class FunctionRenamePresenter extends BasePresenter<FunctionRenameView> {

    public void getRenameAbleFunctions(int cuId, String deviceName, String deviceId) {
        Disposable subscribe = RequestModel.getInstance()
                .getDeviceCategoryDetail()
                .map(new Function<BaseResult<List<DeviceCategoryDetailBean>>, DeviceCategoryDetailBean>() {
                    @Override
                    public DeviceCategoryDetailBean apply(BaseResult<List<DeviceCategoryDetailBean>> listBaseResult) throws Exception {
                        for (DeviceCategoryDetailBean deviceCategoryDetailBean : listBaseResult.data) {
                            if (cuId == deviceCategoryDetailBean.getCuId() && TextUtils.equals(deviceName, deviceCategoryDetailBean.getDeviceName())) {
                                return deviceCategoryDetailBean;
                            }
                        }
                        return null;
                    }
                })//首先查询出改设备的品类支持功能详情。
                .flatMap(new Function<DeviceCategoryDetailBean, ObservableSource<List<DeviceTemplateBean.AttributesBean>>>() {
                    @Override
                    public ObservableSource<List<DeviceTemplateBean.AttributesBean>> apply(DeviceCategoryDetailBean deviceCategoryDetailBean) throws Exception {
                        return RequestModel.getInstance()
                                .fetchDeviceTemplate(deviceCategoryDetailBean.getOemModel())
                                .map(new Function<BaseResult<DeviceTemplateBean>, DeviceTemplateBean>() {
                                    @Override
                                    public DeviceTemplateBean apply(BaseResult<DeviceTemplateBean> deviceTemplateBeanBaseResult) throws Exception {
                                        return deviceTemplateBeanBaseResult.data;
                                    }
                                })
                                .map(new Function<DeviceTemplateBean, List<DeviceTemplateBean.AttributesBean>>() {
                                    @Override
                                    public List<DeviceTemplateBean.AttributesBean> apply(DeviceTemplateBean deviceTemplateBean) throws Exception {
                                        List<DeviceTemplateBean.AttributesBean> attributesBeans = new ArrayList<>();
                                        Set<String> functions = new LinkedHashSet<>();
                                        functions.addAll(deviceCategoryDetailBean.getConditionProperties());
                                        functions.addAll(deviceCategoryDetailBean.getActionProperties());
                                        for (String function : functions) {
                                            for (DeviceTemplateBean.AttributesBean attribute : deviceTemplateBean.getAttributes()) {
                                                if (TextUtils.equals(function, attribute.getCode())) {
                                                    attributesBeans.add(attribute);
                                                    break;
                                                }
                                            }
                                        }
                                        return attributesBeans;
                                    }
                                });
                    }
                })//然后结合物模板查询出功能默认名称
                .zipWith(RequestModel.getInstance().getALlTouchPanelDeviceInfo(cuId, deviceId).map(new Function<BaseResult<List<TouchPanelDataBean>>, List<TouchPanelDataBean>>() {
                    @Override
                    public List<TouchPanelDataBean> apply(BaseResult<List<TouchPanelDataBean>> listBaseResult) throws Exception {
                        return listBaseResult.data;
                    }
                }), new BiFunction<List<DeviceTemplateBean.AttributesBean>, List<TouchPanelDataBean>, List<FunctionRenameListAdapter.Bean>>() {
                    @Override
                    public List<FunctionRenameListAdapter.Bean> apply(List<DeviceTemplateBean.AttributesBean> attributesBeans, List<TouchPanelDataBean> touchPanelDataBeans) throws Exception {
                        List<FunctionRenameListAdapter.Bean> result = new ArrayList<>();
                        for (DeviceTemplateBean.AttributesBean attributesBean : attributesBeans) {
                            FunctionRenameListAdapter.Bean bean = new FunctionRenameListAdapter.Bean();
                            result.add(bean);
                            String code = attributesBean.getCode();
                            String displayName = attributesBean.getDisplayName();
                            bean.setCode(code);
                            bean.setDisplayName(displayName);
                            bean.setPropertyValue(attributesBean.getDisplayName());
                            for (TouchPanelDataBean touchPanelDataBean : touchPanelDataBeans) {
                                if (TextUtils.equals(code, touchPanelDataBean.getPropertyName())) {
                                    bean.setPropertyValue(touchPanelDataBean.getPropertyValue());
                                    bean.setId(touchPanelDataBean.getId());
                                    break;
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
                        mView.showProgress("加载中...");
                    }
                })
                .doFinally(new Action() {
                    @Override
                    public void run() throws Exception {
                        mView.hideProgress();
                    }
                })
                .subscribe(new Consumer<List<FunctionRenameListAdapter.Bean>>() {
                    @Override
                    public void accept(List<FunctionRenameListAdapter.Bean> beans) throws Exception {
                        mView.showFunctions(beans);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        mView.showFunctions(null);
                    }
                });
        addSubscrebe(subscribe);

    }

    public void renameFunction(int cuId, String deviceId, int id, String propertyName, String propertyType, String propertyValue) {
        Disposable subscribe = RequestModel.getInstance()
                .tourchPanelRenameMethod(id, deviceId, cuId, propertyName, propertyType, propertyValue)
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
                .subscribe(new Consumer<BaseResult<Boolean>>() {
                    @Override
                    public void accept(BaseResult<Boolean> booleanBaseResult) throws Exception {
                        mView.renameSuccess();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        mView.renameFailed();
                    }
                });
        addSubscrebe(subscribe);
    }
}
