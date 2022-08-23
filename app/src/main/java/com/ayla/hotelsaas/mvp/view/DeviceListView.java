package com.ayla.hotelsaas.mvp.view;

import com.ayla.hotelsaas.base.BaseView;
import com.ayla.hotelsaas.bean.BaseDevice;
import com.ayla.hotelsaas.bean.DeviceCategoryBean;
import com.ayla.hotelsaas.bean.DeviceItem;
import com.ayla.hotelsaas.bean.DeviceListBean;

import java.util.List;

public interface DeviceListView extends BaseView {
    void loadDataSuccess(DeviceItem devicesBean, List<DeviceCategoryBean> data, DeviceCategoryBean.SubBean.NodeBean deviceNodeBean);
    void loadDeviceDataSuccess(List<BaseDevice> data);
    void loadDataFailed(Throwable throwable) ;

}
