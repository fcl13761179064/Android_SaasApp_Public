package com.ayla.hotelsaas.mvp.present;

import android.text.TextUtils;

import com.ayla.hotelsaas.base.BasePresenter;
import com.ayla.hotelsaas.bean.BaseResult;
import com.ayla.hotelsaas.bean.DeviceCategoryDetailBean;
import com.ayla.hotelsaas.bean.DeviceTemplateBean;
import com.ayla.hotelsaas.data.net.RxjavaFlatmapThrowable;
import com.ayla.hotelsaas.data.net.RxjavaObserver;
import com.ayla.hotelsaas.mvp.model.RequestModel;
import com.ayla.hotelsaas.mvp.view.DeviceMoreView;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
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
public class DeviceMorePresenter extends BasePresenter<DeviceMoreView> {

    public void deviceRenameMethod(String dsn, String nickName) {
        Disposable subscribe = RequestModel.getInstance().deviceRename(dsn, nickName)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        mView.showProgress("修改中...");
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
                        mView.renameSuccess(nickName);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        if (throwable instanceof RxjavaFlatmapThrowable) {
                            mView.renameFailed(((RxjavaFlatmapThrowable) throwable).getCode(), ((RxjavaFlatmapThrowable) throwable).getMsg());
                        } else {
                            mView.renameFailed(null, throwable.getMessage());
                        }
                    }
                });
        addSubscrebe(subscribe);
    }


    public void deviceRemove(String deviceId, long scopeId, String scopeType) {
        RequestModel.getInstance().deviceRemove(deviceId, scopeId, scopeType)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        mView.showProgress("移除中...");
                    }
                })
                .subscribe(new RxjavaObserver<Boolean>() {

                    @Override
                    public void onSubscribe(Disposable d) {
                        addSubscrebe(d);

                    }

                    @Override
                    public void _onNext(Boolean data) {
                        mView.hideProgress();
                        mView.removeSuccess(data);
                    }

                    @Override
                    public void _onError(String code, String msg) {
                        mView.hideProgress();
                        mView.removeFailed(code, msg);
                    }
                });
    }

    public void getRenameAbleFunctions(int cuId, String deviceCategory, String deviceId) {
        Disposable subscribe = RequestModel.getInstance()
                .getDeviceCategoryDetail()
                .map(new Function<BaseResult<List<DeviceCategoryDetailBean>>, DeviceCategoryDetailBean>() {
                    @Override
                    public DeviceCategoryDetailBean apply(BaseResult<List<DeviceCategoryDetailBean>> listBaseResult) throws Exception {
                        for (DeviceCategoryDetailBean deviceCategoryDetailBean : listBaseResult.data) {
                            if (cuId == deviceCategoryDetailBean.getCuId() && TextUtils.equals(deviceCategory, deviceCategoryDetailBean.getOemModel())) {
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
                        if (attributesBeans.size() == 0) {
                            mView.cannotRenameFunction();
                        } else {
                            mView.canRenameFunction();
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        mView.cannotRenameFunction();
                    }
                });
        addSubscrebe(subscribe);

    }
}
