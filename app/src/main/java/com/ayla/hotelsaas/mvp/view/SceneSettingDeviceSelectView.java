package com.ayla.hotelsaas.mvp.view;

import com.ayla.hotelsaas.base.BaseView;
import com.ayla.hotelsaas.bean.DeviceListBean;

import java.util.List;

public interface SceneSettingDeviceSelectView extends BaseView {
    /**
     * Object[0] = {@link com.ayla.hotelsaas.bean.DeviceListBean.DevicesBean}
     * Object[1] = {@link List<String>}  支持条件、动作的属性名称列表 ，如果是全部支持（红外家电、用途设备），则为 null
     *
     * @param devices
     */
    void showDevices(List<DeviceListBean.DevicesBean> devices, List<List<String>> properties);
}
