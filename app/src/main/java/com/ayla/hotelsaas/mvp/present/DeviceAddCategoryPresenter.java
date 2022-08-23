package com.ayla.hotelsaas.mvp.present;

import com.ayla.hotelsaas.base.BasePresenter;
import com.ayla.hotelsaas.bean.DeviceCategoryBean;
import com.ayla.hotelsaas.mvp.model.RequestModel;
import com.ayla.hotelsaas.mvp.view.DeviceAddCategoryView;
import com.blankj.utilcode.constant.TimeConstants;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class DeviceAddCategoryPresenter extends BasePresenter<DeviceAddCategoryView> {

    public void loadCategory() {
        Disposable subscribe = RequestModel.getInstance()
                .getDeviceCategory()
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
                        mView.showCategory(deviceCategoryBeans);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        mView.categoryLoadFail(throwable);
                    }
                });
        addSubscrebe(subscribe);
    }

    /**
     * 加载列表
     *
     * @param resourceRoomId
     */
    public void loadData(long resourceRoomId) {
        Disposable subscribe = RequestModel.getInstance()
                .getAllDeviceList(resourceRoomId, 1, Integer.MAX_VALUE)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(deviceListBean -> mView.loadAllDeviceDataSuccess(deviceListBean), throwable -> mView.loadAllDeviceDataFail(throwable));
        addSubscrebe(subscribe);
    }
}
