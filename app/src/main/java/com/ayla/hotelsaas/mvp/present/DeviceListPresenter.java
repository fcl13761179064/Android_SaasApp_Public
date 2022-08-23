package com.ayla.hotelsaas.mvp.present;


import com.ayla.hotelsaas.application.MyApplication;
import com.ayla.hotelsaas.base.BasePresenter;
import com.ayla.hotelsaas.bean.AllListData;
import com.ayla.hotelsaas.bean.BaseDevice;
import com.ayla.hotelsaas.bean.DeviceCategoryBean;
import com.ayla.hotelsaas.bean.DeviceGroupData;
import com.ayla.hotelsaas.bean.DeviceItem;
import com.ayla.hotelsaas.bean.DeviceListBean;
import com.ayla.hotelsaas.bean.GroupItem;
import com.ayla.hotelsaas.mvp.model.RequestModel;
import com.ayla.hotelsaas.mvp.view.DeviceListView;
import com.blankj.utilcode.util.GsonUtils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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

    public void loadCategory(DeviceItem devicesBean, String pid) {
        Disposable subscribe = Observable.zip(RequestModel.getInstance().getDeviceCategory(), RequestModel.getInstance().getDevicePid(pid), new BiFunction<List<DeviceCategoryBean>, DeviceCategoryBean.SubBean.NodeBean, List<Object>>() {
            @NonNull
            @Override
            public List<Object> apply(@NonNull List<DeviceCategoryBean> deviceCategoryBeans, DeviceCategoryBean.SubBean.NodeBean deviceNodeBean) throws Exception {

                return zipdatas(deviceCategoryBeans, deviceNodeBean);
            }

            private List<Object> zipdatas(List<DeviceCategoryBean> mListDeviceCategoryBean, DeviceCategoryBean.SubBean.NodeBean s) {
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
                        DeviceCategoryBean.SubBean.NodeBean deviceNodeBean = (DeviceCategoryBean.SubBean.NodeBean) objects.get(1);
                        mView.loadDataSuccess(devicesBean, mListDeviceCategoryBean, deviceNodeBean);
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
     */
    public void loadData(long scopeId, Long maxDeviceId, Long maxGroupId, Long regionId) {
        Disposable subscribe = RequestModel.getInstance().getDeviceGroupData(scopeId, maxDeviceId, maxGroupId, regionId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(devices -> {
                    List<BaseDevice> allData = new ArrayList<>();
                    for (int i = 0; i < devices.size(); i++) {
                        DeviceGroupData deviceGroupData = devices.get(i);
                        if (deviceGroupData.deviceGroupFlag == 0) {
                            //设备
                            DeviceItem deviceItem = GsonUtils.fromJson(deviceGroupData.deviceGroupJson, DeviceItem.class);
                            allData.add(deviceItem);
                        } else if (deviceGroupData.deviceGroupFlag == 1) {
                            //群组
                            GroupItem groupItem = GsonUtils.fromJson(deviceGroupData.deviceGroupJson, GroupItem.class);
                            allData.add(groupItem);
                        }
                    }
//                    Collections.sort(allData, (Comparator<BaseDevice>) (o1, o2) -> o2.createTime.compareTo(o1.createTime));
                    mView.loadDeviceDataSuccess(allData);
                }, throwable -> {
                    mView.loadDataFailed(throwable);
                });
        addSubscrebe(subscribe);
    }

    public void refreshDeviceAndGroup(long scopeId, Long maxDeviceId, Long maxGroupId, Long regionId) {
        Observable<AllListData> observable = Observable.zip(RequestModel.getInstance().getDeviceGroupData(scopeId, maxDeviceId, maxGroupId, regionId), RequestModel.getInstance()
                .getAllDeviceList(scopeId, 1, Integer.MAX_VALUE), (deviceGroupData, deviceListBean) -> {
            AllListData data = new AllListData();
            data.setDeviceGroupData(deviceGroupData);
            data.setDeviceListBean(deviceListBean);
            return data;
        }).subscribeOn(Schedulers.io());
        Disposable subscribe = Observable.zip(observable, RequestModel.getInstance().getAllGroup(scopeId), new BiFunction<AllListData, List<GroupItem>, AllListData>() {
                    @NotNull
                    @Override
                    public AllListData apply(@NotNull AllListData allListData, @NotNull List<GroupItem> groupItems) throws Exception {
                        allListData.setAllGroup(groupItems);
                        return allListData;
                    }
                }
        ).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(data -> {
                    List<DeviceGroupData> deviceAndGroupData = data.getDeviceGroupData();
                    MyApplication.getInstance().setDevicesBean(data.getDeviceListBean());
                    MyApplication.getInstance().setAllGroupData(data.getAllGroup());
                    List<BaseDevice> allData = new ArrayList<>();
                    if (deviceAndGroupData != null) {
                        for (int i = 0; i < deviceAndGroupData.size(); i++) {
                            DeviceGroupData deviceGroupData = deviceAndGroupData.get(i);
                            if (deviceGroupData.deviceGroupFlag == 0) {
                                //设备
                                DeviceItem deviceItem = GsonUtils.fromJson(deviceGroupData.deviceGroupJson, DeviceItem.class);
                                allData.add(deviceItem);
                            } else if (deviceGroupData.deviceGroupFlag == 1) {
                                //群组
                                GroupItem groupItem = GsonUtils.fromJson(deviceGroupData.deviceGroupJson, GroupItem.class);
                                allData.add(groupItem);
                            }
                        }
//                        Collections.sort(allData, (Comparator<BaseDevice>) (o1, o2) -> o2.createTime.compareTo(o1.createTime));
                    }
                    mView.loadDeviceDataSuccess(allData);

                }, throwable -> {
                    mView.loadDataFailed(throwable);
                });
        addSubscrebe(subscribe);
    }

}
