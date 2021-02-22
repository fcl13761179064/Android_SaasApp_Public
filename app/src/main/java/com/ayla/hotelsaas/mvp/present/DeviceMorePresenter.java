package com.ayla.hotelsaas.mvp.present;

import com.ayla.hotelsaas.base.BasePresenter;
import com.ayla.hotelsaas.bean.BaseResult;
import com.ayla.hotelsaas.bean.DeviceCategoryDetailBean;
import com.ayla.hotelsaas.bean.DeviceTemplateBean;
import com.ayla.hotelsaas.bean.PurposeCategoryBean;
import com.ayla.hotelsaas.mvp.model.RequestModel;
import com.ayla.hotelsaas.mvp.view.DeviceMoreView;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * @描述
 * @作者 fanchunlei
 * @时间 2020/7/29
 */
public class DeviceMorePresenter extends BasePresenter<DeviceMoreView> {

    public void deviceRenameMethod(String deviceId, String nickName, String pointName, long regionId, String regionName) {
        Disposable subscribe = RequestModel.getInstance().deviceRename(deviceId, nickName, pointName, regionId, regionName)
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
                        mView.renameFailed(throwable);
                    }
                });
        addSubscrebe(subscribe);
    }

    public void deviceRemove(String deviceId, long scopeId, String scopeType) {
        Disposable subscribe = RequestModel.getInstance().deviceRemove(deviceId, scopeId, scopeType)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        mView.showProgress("移除中...");
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
                        mView.removeSuccess(aBoolean);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        mView.removeFailed(throwable);
                    }
                });
        addSubscrebe(subscribe);
    }

    public void getRenameAbleFunctions(String pid) {
        Disposable subscribe = RequestModel.getInstance()
                .getDeviceCategoryDetail(pid)//找到了指定的设备，存在可以配置为联动条件、动作的属性。
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
                .subscribe(new Consumer<DeviceCategoryDetailBean>() {
                    @Override
                    public void accept(DeviceCategoryDetailBean deviceCategoryDetailBean) throws Exception {
                        int count = deviceCategoryDetailBean.getConditionProperties().size() + deviceCategoryDetailBean.getActionProperties().size();
                        if (count > 0) {
                            mView.canRenameFunction();
                        } else {
                            mView.cannotRenameFunction();
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

    /**
     * 查询是否支持开关重命名
     *
     * @param pid
     * @return
     */
    private Observable<Boolean> getRenameAbleFunctions2(String pid) {
        return RequestModel.getInstance()
                .getDeviceCategoryDetail(pid)//找到了指定的设备，存在可以配置为联动条件、动作的属性。
                .map(new Function<DeviceCategoryDetailBean, Boolean>() {
                    @Override
                    public Boolean apply(@NonNull DeviceCategoryDetailBean deviceCategoryDetailBean) throws Exception {
                        int count = deviceCategoryDetailBean.getConditionProperties().size() + deviceCategoryDetailBean.getActionProperties().size();
                        return count > 0;
                    }
                });
    }

    /**
     * 查询默认开关设置的情况
     *
     * @param deviceId
     * @param pid
     * @return
     */
    private Observable<List<DeviceTemplateBean.AttributesBean>> getSwitchDefaultSetting(String deviceId, String pid) {
        return RequestModel.getInstance().fetchDeviceTemplate(pid)
                .map(new Function<BaseResult<DeviceTemplateBean>, DeviceTemplateBean>() {
                    @Override
                    public DeviceTemplateBean apply(@NonNull BaseResult<DeviceTemplateBean> deviceTemplateBeanBaseResult) throws Exception {
                        return deviceTemplateBeanBaseResult.data;
                    }
                })
                .compose(RequestModel.getInstance().modifyTemplateDisplayName(deviceId))
                .map(new Function<DeviceTemplateBean, List<DeviceTemplateBean.AttributesBean>>() {
                    @Override
                    public List<DeviceTemplateBean.AttributesBean> apply(@NonNull DeviceTemplateBean deviceTemplateBean) throws Exception {
                        List<DeviceTemplateBean.AttributesBean> attributesBeans = new ArrayList<>();
                        for (DeviceTemplateBean.AttributesBean attribute : deviceTemplateBean.getAttributes()) {
                            if (attribute.getCode().endsWith(":OnoffDefault")) {//是默认开关的设置属性格式
                                attributesBeans.add(attribute);
                            }
                        }
                        return attributesBeans;
                    }
                });
    }

    public void functionLoad(String deviceId, String pid) {
        Disposable subscribe = Observable
                .zip(getRenameAbleFunctions2(pid).observeOn(AndroidSchedulers.mainThread())
                                .doOnNext(new Consumer<Boolean>() {
                                    @Override
                                    public void accept(Boolean aBoolean) throws Exception {
                                        if (aBoolean) {
                                            mView.canRenameFunction();
                                        } else {
                                            mView.cannotRenameFunction();
                                        }
                                    }
                                })
                                .doOnError(new Consumer<Throwable>() {
                                    @Override
                                    public void accept(Throwable throwable) throws Exception {
                                        mView.cannotRenameFunction();
                                    }
                                }),
                        getSwitchDefaultSetting(deviceId, pid).observeOn(AndroidSchedulers.mainThread())
                                .doOnNext(new Consumer<List<DeviceTemplateBean.AttributesBean>>() {
                                    @Override
                                    public void accept(List<DeviceTemplateBean.AttributesBean> attributesBeans) throws Exception {
                                        if (attributesBeans.size() == 0) {
                                            mView.cannotSetSwitchDefault();
                                        } else {
                                            mView.canSetSwitchDefault(attributesBeans);
                                        }
                                    }
                                })
                                .doOnError(new Consumer<Throwable>() {
                                    @Override
                                    public void accept(Throwable throwable) throws Exception {
                                        mView.cannotSetSwitchDefault();
                                    }
                                }),
                        new BiFunction<Boolean, List<DeviceTemplateBean.AttributesBean>, Object>() {
                            @NonNull
                            @Override
                            public Object apply(@NonNull Boolean aBoolean, @NonNull List<DeviceTemplateBean.AttributesBean> attributesBeans) throws Exception {
                                return 1;
                            }
                        }, true)
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
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                });
        addSubscrebe(subscribe);

    }

    public void getPurposeCategory() {
        Disposable subscribe = RequestModel.getInstance().getPurposeCategory()
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
                .subscribe(new Consumer<List<PurposeCategoryBean>>() {
                    @Override
                    public void accept(List<PurposeCategoryBean> purposeCategoryBeans) throws Exception {
                        mView.showPurposeCategory(purposeCategoryBeans);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                });
        addSubscrebe(subscribe);
    }

    public void updatePurpose(String deviceId, int purposeCategory) {
        Disposable subscribe = RequestModel.getInstance().updatePurpose(deviceId, purposeCategory)
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
                .subscribe(new Consumer() {
                    @Override
                    public void accept(Object o) throws Exception {
                        mView.updatePurposeSuccess();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        mView.updatePurposeFailed(throwable);
                    }
                });
        addSubscrebe(subscribe);
    }
}
