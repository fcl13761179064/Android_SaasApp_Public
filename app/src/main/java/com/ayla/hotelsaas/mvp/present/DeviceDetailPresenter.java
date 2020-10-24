package com.ayla.hotelsaas.mvp.present;

import com.ayla.hotelsaas.base.BasePresenter;
import com.ayla.hotelsaas.bean.BaseResult;
import com.ayla.hotelsaas.bean.DeviceFirmwareVersionBean;
import com.ayla.hotelsaas.mvp.model.RequestModel;
import com.ayla.hotelsaas.mvp.view.DeviceDetailView;

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
public class DeviceDetailPresenter extends BasePresenter<DeviceDetailView> {
    public void loadFirmwareVersion(String deviceId) {
        Disposable subscribe = RequestModel.getInstance()
                .fetchDeviceDetail(deviceId)
                .map(new Function<BaseResult<DeviceFirmwareVersionBean>, DeviceFirmwareVersionBean>() {
                    @Override
                    public DeviceFirmwareVersionBean apply(BaseResult<DeviceFirmwareVersionBean> deviceFirmwareVersionBeanBaseResult) throws Exception {
                        return deviceFirmwareVersionBeanBaseResult.data;
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
                .subscribe(new Consumer<DeviceFirmwareVersionBean>() {
                    @Override
                    public void accept(DeviceFirmwareVersionBean deviceFirmwareVersionBean) throws Exception {
                        mView.showFirmwareVersion(deviceFirmwareVersionBean.getFirmwareVersion());
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                });
        addSubscrebe(subscribe);
    }
}
