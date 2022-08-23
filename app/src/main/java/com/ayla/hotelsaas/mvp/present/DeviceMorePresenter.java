package com.ayla.hotelsaas.mvp.present;

import android.text.TextUtils;

import com.ayla.hotelsaas.base.BasePresenter;
import com.ayla.hotelsaas.bean.BaseResult;
import com.ayla.hotelsaas.bean.DeviceCategoryDetailBean;
import com.ayla.hotelsaas.bean.DeviceFirmwareVersionBean;
import com.ayla.hotelsaas.bean.DeviceListBean;
import com.ayla.hotelsaas.bean.DeviceTemplateBean;
import com.ayla.hotelsaas.bean.PurposeCategoryBean;
import com.ayla.hotelsaas.constant.ShowWay;
import com.ayla.hotelsaas.data.net.ExceptionCode;
import com.ayla.hotelsaas.data.net.SpecialException;
import com.ayla.hotelsaas.mvp.model.RequestModel;
import com.ayla.hotelsaas.mvp.view.DeviceMoreView;
import com.ayla.hotelsaas.utils.CommonUtils;
import com.ayla.hotelsaas.utils.TempUtils;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
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

    public void deviceRemove(String deviceId, long scopeId, String scopeType, String pid) {
        Disposable subscribe = RequestModel.getInstance().deviceRemove(deviceId, scopeId, scopeType, pid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        mView.removeSuccess(aBoolean);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        mView.hideProgress();
                        mView.removeFailed(throwable);
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
    private Observable<Boolean> getRenameAbleFunctions(String pid) {
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
    private Observable<Boolean> getSwitchDefaultSetting2(String deviceId, String pid) {
        return RequestModel.getInstance().fetchDeviceTemplate(pid)
                .map(new Function<BaseResult<DeviceTemplateBean>, DeviceTemplateBean>() {
                    @Override
                    public DeviceTemplateBean apply(@NonNull BaseResult<DeviceTemplateBean> deviceTemplateBeanBaseResult) throws Exception {
                        return deviceTemplateBeanBaseResult.data;
                    }
                })
                .map(new Function<DeviceTemplateBean, List<DeviceTemplateBean.AttributesBean>>() {
                    @Override
                    public List<DeviceTemplateBean.AttributesBean> apply(@NonNull DeviceTemplateBean deviceTemplateBean) throws Exception {
                        List<DeviceTemplateBean.AttributesBean> defaultAttributesBeans = new ArrayList<>();
                        for (DeviceTemplateBean.AttributesBean attribute : deviceTemplateBean.getAttributes()) {
                            if (attribute.getCode().endsWith(":OnoffDefault")) {//是默认开关的设置属性格式
                                if (!"ZBSW0-A000001".equalsIgnoreCase(pid) && !"ZBSW0-A000002".equalsIgnoreCase(pid) && !"ZBSW0-A000003".equalsIgnoreCase(pid) && !"ZBSW0-A000004".equalsIgnoreCase(pid)
                                        && !"ZBSW0-A000008".equalsIgnoreCase(pid) && !"ZBSW0-A000006".equalsIgnoreCase(pid) && !"ZBSW0-A000007".equalsIgnoreCase(pid)) {
                                    defaultAttributesBeans.add(attribute);
                                }
                            }
                        }
                        List<DeviceTemplateBean.AttributesBean> attributesBeans = new ArrayList<>();
                        for (DeviceTemplateBean.AttributesBean attribute : deviceTemplateBean.getAttributes()) {
                            for (DeviceTemplateBean.AttributesBean defaultAttributesBean : defaultAttributesBeans) {
                                if (attribute.getCode().equals(defaultAttributesBean.getCode().substring(0, defaultAttributesBean.getCode().length() - 7))) {
                                    attributesBeans.add(attribute);
                                }
                            }
                        }
                        return attributesBeans;
                    }
                })
                .map(new Function<List<DeviceTemplateBean.AttributesBean>, Boolean>() {
                    @Override
                    public Boolean apply(@NonNull List<DeviceTemplateBean.AttributesBean> attributesBeans) throws Exception {
                        return !attributesBeans.isEmpty();
                    }
                });
    }

    public void functionLoad(String deviceId, String pid) {
        Disposable subscribe = Observable
                .zip(getRenameAbleFunctions(pid).observeOn(AndroidSchedulers.mainThread())
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
                        getSwitchDefaultSetting2(deviceId, pid).observeOn(AndroidSchedulers.mainThread())
                                .doOnNext(new Consumer<Boolean>() {
                                    @Override
                                    public void accept(Boolean aBoolean) throws Exception {
                                        if (aBoolean) {
                                            mView.canSetSwitchDefault();
                                        } else {
                                            mView.cannotSetSwitchDefault();
                                        }
                                    }
                                })
                                .doOnError(new Consumer<Throwable>() {
                                    @Override
                                    public void accept(Throwable throwable) throws Exception {
                                        mView.cannotSetSwitchDefault();
                                    }
                                }),
                        new BiFunction<Boolean, Boolean, Object>() {
                            @NonNull
                            @Override
                            public Object apply(@NonNull Boolean aBoolean, @NonNull Boolean aBoolean2) throws Exception {
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

    /**
     * 获取允许编组的pids ，是否显示设备编组
     */
    public void getDeviceMarShallPidData() {
        Disposable subscribe = RequestModel.getInstance().getDeviceMarShallPidData()
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
                .subscribe(new Consumer<List<String>>() {
                    @Override
                    public void accept(List<String> baseResultBaseResp) throws Exception {
                        mView.MarshallEntryPidSuccess(baseResultBaseResp);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        mView.MarshallEntryPidFail(throwable);
                    }
                });
        addSubscrebe(subscribe);
    }


    /**
     * A6网关和设备移交到智家
     *
     * @param deviceId
     * @param mScopeId
     * @param businessId
     * @param mRoom_name
     * @param regToken
     * @param tempToken
     */
    public void A6TransferToZj(String deviceId, long mScopeId, long businessId, String mRoom_name, String regToken, String tempToken) {
        Disposable subscribe = RequestModel.getInstance().transferRoomToZj(deviceId, mScopeId, businessId, mRoom_name, regToken, tempToken)
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
                .subscribe(new Consumer<BaseResult>() {
                    @Override
                    public void accept(BaseResult result) throws Exception {
                        mView.transferSuccess(result);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        mView.transferFailed(throwable);
                    }
                });
        addSubscrebe(subscribe);
    }


    /**
     * A6网关和设备移交到智家
     *
     * @param mScopeId
     * @param businessId
     * @param mRoom_name
     */
    public void A6UpdateTransferToZj(long mScopeId, long businessId, String mRoom_name) {
        Disposable subscribe = RequestModel.getInstance().transferUpdateRoomToZj(mScopeId, businessId, mRoom_name)
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
                .subscribe(new Consumer<BaseResult>() {
                    @Override
                    public void accept(BaseResult result) throws Exception {
                        mView.updatetransferSuccess(result);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        mView.updatetransferFailed(throwable);
                    }
                });
        addSubscrebe(subscribe);
    }

    public void loadFirmwareVersion(String deviceId) {
        Disposable subscribe = RequestModel.getInstance()
                .fetchDeviceDetail(deviceId)
                .map(deviceFirmwareVersionBeanBaseResult -> deviceFirmwareVersionBeanBaseResult.data)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(deviceFirmwareVersionBean -> mView.getCurrentFirmwareVersion(deviceFirmwareVersionBean.getFirmwareVersion()), throwable -> {
                    mView.getFirmwareVersionFail(throwable);
                });
        addSubscrebe(subscribe);
    }

    public void getDeviceFirmwareVersion(String dsn, String pid) {
        mView.showProgress("请稍后...");
        Disposable subscribe = RequestModel.getInstance().getDeviceFirmwareVersion(dsn, pid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {
                            mView.getFirmwareNewVersion(result);
                            mView.hideProgress();
                        },
                        throwable -> {
                            mView.getFirmwareNewVersionFail(throwable);
                            mView.hideProgress();
                        });
        addSubscrebe(subscribe);
    }

    public void getDeviceDetail(String deviceId) {
        mView.showProgress("请稍后...");
        Disposable subscribe = RequestModel.getInstance().getDeviceDetail(deviceId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {
                            mView.onDeviceStatus(TempUtils.isDeviceOnline(result));
                        },
                        throwable -> {
                            mView.onDeviceStatus(false);
                            mView.hideProgress();
                        });
        addSubscrebe(subscribe);
    }

    public void getDeviceDisplay(long scopeId, String deviceId) {
        Disposable subscribe = RequestModel.getInstance().getDeviceDisplay(scopeId, deviceId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(type -> {
                    mView.getDisplaySuccess(CommonUtils.INSTANCE.getShowWay(type));
                }, throwable -> {
                    mView.onDisplayFail(throwable);
                });
        addSubscrebe(subscribe);
    }

    public void saveDeviceDisplay(ShowWay showWay, String deviceId, long scopeId) {
        mView.showProgress("请稍后...");
        Disposable subscribe = RequestModel.getInstance().saveDeviceDisplay(showWay, deviceId, scopeId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {
                    mView.hideProgress();
                    if (result.isSuccess())
                        mView.onSaveDisplaySuccess(showWay);
                    else
                        mView.onDisplayFail(new SpecialException(1022, "保存失败,请重试", null));
                }, throwable -> {
                    mView.hideProgress();
                    mView.onDisplayFail(throwable);
                });
        addSubscrebe(subscribe);
    }

    public void initSwitchMode(String deviceId, String json) {
        //验证是否物理退网
        Disposable subscribe = RequestModel.getInstance().deviceIsExitNet(deviceId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {
                    //物理退网了，直接移除，不初始化开关模式
                    if (TextUtils.equals(result.code, "280404"))
                        mView.initSwitchModeResult(true, null);
                    else
                        setSwitchMode(json);
                }, throwable -> {
                    mView.hideProgress();
                    mView.initSwitchModeResult(false, throwable);
                });
        addSubscrebe(subscribe);
    }

    private void setSwitchMode(String json) {
        Disposable subscribe = RequestModel.getInstance().batchUpdateSwitchProperty(json)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {
                    if (result.isSuccess())
                        mView.initSwitchModeResult(true, null);
                    else
                        mView.initSwitchModeResult(false, new SpecialException(1022, "初始化开关模式失败", null));
                }, throwable -> {
                    mView.hideProgress();
                    mView.initSwitchModeResult(false, throwable);
                });
        addSubscrebe(subscribe);
    }
}
