package com.ayla.hotelsaas.mvp.present;

import android.text.TextUtils;

import com.ayla.hotelsaas.base.BasePresenter;
import com.ayla.hotelsaas.bean.BaseResult;
import com.ayla.hotelsaas.bean.DeviceCategoryDetailBean;
import com.ayla.hotelsaas.bean.DeviceTemplateBean;
import com.ayla.hotelsaas.bean.PropertyNicknameBean;
import com.ayla.hotelsaas.mvp.model.RequestModel;
import com.ayla.hotelsaas.mvp.view.FunctionRenameView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
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

    public void getRenameAbleFunctions(int cuId, String pid, String deviceId) {
        Disposable subscribe = RequestModel.getInstance()
                .getDeviceCategoryDetail(pid)//首先查询出改设备的品类支持功能详情。
                .flatMap(new Function<DeviceCategoryDetailBean, ObservableSource<List<DeviceTemplateBean.AttributesBean>>>() {
                    @Override
                    public ObservableSource<List<DeviceTemplateBean.AttributesBean>> apply(DeviceCategoryDetailBean deviceCategoryDetailBean) throws Exception {
                        return RequestModel.getInstance()
                                .fetchDeviceTemplate(pid)
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
                .zipWith(RequestModel.getInstance().fetchPropertyNickname(cuId, deviceId), new BiFunction<List<DeviceTemplateBean.AttributesBean>, List<PropertyNicknameBean>, List<Map<String, String>>>() {
                    @Override
                    public List<Map<String, String>> apply(List<DeviceTemplateBean.AttributesBean> attributesBeans, List<PropertyNicknameBean> touchPanelDataBeans) throws Exception {
                        List<Map<String, String>> result = new ArrayList<>();
                        for (DeviceTemplateBean.AttributesBean attributesBean : attributesBeans) {
                            Map<String, String> bean = new HashMap<>();
                            result.add(bean);
                            String code = attributesBean.getCode();
                            bean.put("propertyCode", attributesBean.getCode());
                            bean.put("propertyName", attributesBean.getDisplayName());
                            for (PropertyNicknameBean touchPanelDataBean : touchPanelDataBeans) {
                                if ("nickName".equals(touchPanelDataBean.getPropertyType()) &&
                                        TextUtils.equals(code, touchPanelDataBean.getPropertyName())) {
                                    bean.put("propertyNickname", touchPanelDataBean.getPropertyValue());
                                    bean.put("nickNameId", String.valueOf(touchPanelDataBean.getId()));
                                    break;
                                }
                            }
                        }
                        return result;
                    }
                })//查出设置的别名、别名id，方便后面的更新使用。
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
                .subscribe(new Consumer<List<Map<String, String>>>() {
                    @Override
                    public void accept(List<Map<String, String>> beans) throws Exception {
                        mView.showFunctions(beans);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        mView.showError(throwable);
                    }
                });
        addSubscrebe(subscribe);

    }

    public void renameFunction(int cuId, String deviceId, String nickNameId, String property, String propertyNickName) {
        Disposable subscribe = RequestModel.getInstance()
                .updatePropertyNickName(nickNameId, deviceId, cuId, property, propertyNickName)
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
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        mView.renameSuccess();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        mView.renameFailed(throwable);
                    }
                });
        addSubscrebe(subscribe);
    }
}
