package com.ayla.hotelsaas.mvp.present;


import com.ayla.hotelsaas.base.BasePresenter;
import com.ayla.hotelsaas.bean.DeviceListBean;
import com.ayla.hotelsaas.mvp.model.RequestModel;
import com.ayla.hotelsaas.mvp.view.DeviceListContainerView;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * @描述
 * @作者 fanchunlei
 * @时间 2020/7/14
 */
public class DeviceListContainerPresenter extends BasePresenter<DeviceListContainerView> {

    /**
     * 加载列表
     *
     * @param resourceRoomId
     */
    public void loadData(long resourceRoomId) {
        Disposable subscribe = RequestModel.getInstance()
                .getDeviceList(resourceRoomId, 1, Integer.MAX_VALUE)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<DeviceListBean>() {
                    @Override
                    public void accept(DeviceListBean deviceListBean) throws Exception {
                        mView.loadDataSuccess(deviceListBean);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        mView.loadDataFinish(throwable);
                    }
                });
        addSubscrebe(subscribe);
    }

}
