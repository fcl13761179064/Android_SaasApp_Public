package com.ayla.hotelsaas.mvp.present;


import com.ayla.hotelsaas.base.BasePresenter;
import com.ayla.hotelsaas.bean.DeviceCategoryBean;
import com.ayla.hotelsaas.bean.DeviceListBean;
import com.ayla.hotelsaas.bean.DeviceLocationBean;
import com.ayla.hotelsaas.mvp.model.RequestModel;
import com.ayla.hotelsaas.mvp.view.DeviceListView;

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
 * @时间 2020/7/14
 */
public class DeviceListPresenter extends BasePresenter<DeviceListView> {

    public void loadCategory(DeviceListBean.DevicesBean devicesBean, String pid) {
        Disposable subscribe = Observable.zip(RequestModel.getInstance().getDeviceCategory(), RequestModel.getInstance().getDevicePid(pid), new BiFunction<List<DeviceCategoryBean>, Object, List<Object>>() {
            @NonNull
            @Override
            public List<Object> apply(@NonNull List<DeviceCategoryBean> deviceCategoryBeans,  Object o) throws Exception {

                return zipdatas(deviceCategoryBeans,o);
            }
            private List<Object> zipdatas(List<DeviceCategoryBean> mListDeviceCategoryBean , Object s) {
                List<Object> listData = new ArrayList<>();
                listData.add(mListDeviceCategoryBean);
                listData.add(s);
                return listData;
            }
        }).subscribeOn(Schedulers.io())
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
                .subscribe(new Consumer<List<Object>>() {
                    @Override
                    public void accept(List<Object> objects) throws Exception {
                        List<DeviceCategoryBean> mListDeviceCategoryBean = (List<DeviceCategoryBean>) objects.get(0);
                        Object o = objects.get(1);
                        mView.loadDataSuccess(devicesBean, mListDeviceCategoryBean,o);
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
    public void loadData(long resourceRoomId, long regionId) {
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
