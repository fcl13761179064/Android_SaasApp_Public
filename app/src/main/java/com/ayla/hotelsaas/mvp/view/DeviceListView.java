package com.ayla.hotelsaas.mvp.view;

import com.ayla.hotelsaas.base.BaseView;
import com.ayla.hotelsaas.bean.DeviceCategoryBean;
import com.ayla.hotelsaas.bean.DeviceListBean;
import com.ayla.hotelsaas.bean.DeviceNodeBean;

import java.util.List;

public interface DeviceListView extends BaseView {

    void loadDataSuccess(DeviceListBean.DevicesBean devicesBean, List<DeviceCategoryBean> data, DeviceCategoryBean.SubBean.NodeBean deviceNodeBean);
    void loadDeviceDataSuccess(DeviceListBean data);
    void loadDataFailed(Throwable throwable) ;

}
