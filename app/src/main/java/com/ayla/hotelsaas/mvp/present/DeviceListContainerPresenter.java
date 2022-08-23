package com.ayla.hotelsaas.mvp.present;


import com.ayla.hotelsaas.base.BasePresenter;
import com.ayla.hotelsaas.bean.BaseDevice;
import com.ayla.hotelsaas.bean.BaseResult;
import com.ayla.hotelsaas.bean.DeviceGroupData;
import com.ayla.hotelsaas.bean.DeviceItem;
import com.ayla.hotelsaas.bean.DeviceListBean;
import com.ayla.hotelsaas.bean.DeviceLocationBean;
import com.ayla.hotelsaas.bean.GroupItem;
import com.ayla.hotelsaas.mvp.model.RequestModel;
import com.ayla.hotelsaas.mvp.view.DeviceListContainerView;
import com.blankj.utilcode.util.GsonUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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
                .getAllDeviceList(resourceRoomId, 1, Integer.MAX_VALUE)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(deviceListBean -> mView.loadAllDeviceDataSuccess(deviceListBean), throwable -> mView.loadAllDeviceDataFail(throwable));
        addSubscrebe(subscribe);
    }

    /**
     * 加载编组和设备列表
     */
    public void loadGroupDeviceData(long scopeId, Long maxDeviceId, Long maxGroupId, Long regionId) {
        Disposable subscribe = RequestModel.getInstance()
                .getDeviceGroupData(scopeId, maxDeviceId, maxGroupId, regionId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(listBaseResult -> {
                    List<BaseDevice> allData = new ArrayList<>();
                    for (int i = 0; i < listBaseResult.size(); i++) {
                        DeviceGroupData deviceGroupData = listBaseResult.get(i);
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
                    mView.loadDataSuccess(allData);
                }, throwable -> mView.loadDataFinish(throwable));
        addSubscrebe(subscribe);
    }

    /**
     * 加载区域位置
     *
     * @param
     * @param room_id
     */
    public void getAllDeviceLocation(Long room_id) {
        Disposable subscribe = RequestModel.getInstance()
                .getAllDeviceLocation(room_id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(deviceListBean -> mView.loadDeviceLocationSuccess(deviceListBean), throwable -> mView.loadDataFinish(throwable));
        addSubscrebe(subscribe);
    }

}
