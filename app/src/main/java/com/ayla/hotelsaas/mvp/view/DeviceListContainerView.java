package com.ayla.hotelsaas.mvp.view;

import com.ayla.hotelsaas.base.BaseView;
import com.ayla.hotelsaas.bean.BaseDevice;
import com.ayla.hotelsaas.bean.DeviceListBean;
import com.ayla.hotelsaas.bean.DeviceLocationBean;
import com.ayla.hotelsaas.bean.GroupItem;

import java.util.List;

public interface DeviceListContainerView extends BaseView {

    void loadDataSuccess(List<BaseDevice> data);

    void loadAllDeviceDataSuccess(DeviceListBean data);

    void loadAllDeviceDataFail(Throwable throwable);

    void loadDeviceLocationSuccess(List<DeviceLocationBean> data);

    void loadDataFinish(Throwable throwable);

}
