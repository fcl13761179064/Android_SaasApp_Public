package com.ayla.hotelsaas.mvp.view;

import com.ayla.hotelsaas.base.BaseView;
import com.ayla.hotelsaas.bean.DeviceCategoryBean;
import com.ayla.hotelsaas.bean.DeviceListBean;

import java.util.List;

public interface DeviceListView extends BaseView {

    void loadDataSuccess(DeviceListBean.DevicesBean devicesBean, List<DeviceCategoryBean> data, Object o);
    void loadDeviceDataSuccess(DeviceListBean data);
    void loadDataFailed(Throwable throwable) ;

}
