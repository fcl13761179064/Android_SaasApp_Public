package com.ayla.hotelsaas.mvp.view;

import com.ayla.hotelsaas.base.BaseView;
import com.ayla.hotelsaas.bean.DeviceLocationBean;

import java.util.List;

public interface PointAndRegionView extends BaseView {
    void modifySuccess(String pointName);
    void modifyRegionSuccess(String pointName);
    void loadDeviceLocationSuccess(List<DeviceLocationBean> deviceListBean);
}
