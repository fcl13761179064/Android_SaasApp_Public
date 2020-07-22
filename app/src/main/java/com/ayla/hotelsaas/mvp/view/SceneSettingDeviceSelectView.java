package com.ayla.hotelsaas.mvp.view;

import com.ayla.hotelsaas.base.BaseView;
import com.ayla.hotelsaas.bean.Device;
import com.ayla.hotelsaas.bean.DeviceListBean;

import java.util.List;

public interface SceneSettingDeviceSelectView extends BaseView {
    void showDevices(List<DeviceListBean.DevicesBean> devices);
}
