package com.ayla.hotelsaas.mvp.view;

import com.ayla.hotelsaas.base.BaseView;
import com.ayla.hotelsaas.bean.DeviceListBean;

import java.util.List;

public interface SceneSettingDeviceSelectView extends BaseView {
    /**
     * Object[0] = {@link com.ayla.hotelsaas.bean.DeviceListBean.DevicesBean}
     * Object[1] = {@link List<List<String>>}
     *
     * @param devices
     */
    void showDevices(List<DeviceListBean.DevicesBean> devices, List<List<String>> properties);
}
