package com.ayla.hotelsaas.mvp.view;

import com.ayla.hotelsaas.base.BaseView;
import com.ayla.hotelsaas.bean.DeviceListBean;
import com.ayla.hotelsaas.bean.DeviceLocationBean;

import java.util.List;

public interface DeviceListContainerView extends BaseView {

    void loadDataSuccess(DeviceListBean data);

    void loadDeviceLocationSuccess(List<DeviceLocationBean> data);

    void loadDataFinish(Throwable throwable) ;

}
