package com.ayla.hotelsaas.mvp.present;


import com.ayla.hotelsaas.base.BasePresenter;
import com.ayla.hotelsaas.bean.DeviceCategoryBean;
import com.ayla.hotelsaas.bean.DeviceListBean;
import com.ayla.hotelsaas.bean.DeviceLocationBean;
import com.ayla.hotelsaas.mvp.model.RequestModel;
import com.ayla.hotelsaas.mvp.view.DeviceListView;

import java.util.List;

import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * @描述
 * @作者 fanchunlei
 * @时间 2020/7/14
 */
public class DeviceListPresenter extends BasePresenter<DeviceListView> {

    public void loadCategory(DeviceListBean.DevicesBean devicesBean) {
        Disposable subscribe = RequestModel.getInstance().getDeviceCategory()
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
                .subscribe(new Consumer<List<DeviceCategoryBean>>() {
                    @Override
                    public void accept(List<DeviceCategoryBean> deviceCategoryBeans) throws Exception {
                        mView.loadDataSuccess(devicesBean,deviceCategoryBeans);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        mView.loadDataFailed(throwable);
                    }
                });
        addSubscrebe(subscribe);
    }


    public void getTagetPid(String pid) {
        Disposable subscribe = RequestModel.getInstance().getDevicePid(pid)
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
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object deviceCategoryBeans) throws Exception {
                        mView.loadDeviceDataSuccessssss(deviceCategoryBeans);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        mView.loadDataFailed(throwable);
                    }
                });
        addSubscrebe(subscribe);
    }

    /**
     * 加载列表
     *
     * @param resourceRoomId
     */
    public void loadData(long resourceRoomId,long regionId) {
        Disposable subscribe = RequestModel.getInstance().getDeviceList(resourceRoomId, 1, Integer.MAX_VALUE, regionId).subscribeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<DeviceListBean>() {
                    @Override
                    public void accept(DeviceListBean deviceListBean) throws Exception {
                        mView.loadDeviceDataSuccess(deviceListBean);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        mView.loadDataFailed(throwable);
                    }
                });
        addSubscrebe(subscribe);
    }
}
