package com.ayla.hotelsaas.mvp.present;

import com.ayla.hotelsaas.base.BasePresenter;
import com.ayla.hotelsaas.bean.DeviceListBean;
import com.ayla.hotelsaas.mvp.view.SceneSettingDeviceSelectView;

import java.util.ArrayList;
import java.util.List;

public class SceneSettingDeviceSelectPresenter extends BasePresenter<SceneSettingDeviceSelectView> {

    public void loadDevice() {
        List<DeviceListBean.DevicesBean> devices = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            DeviceListBean.DevicesBean device = new DeviceListBean.DevicesBean();
            device.setDeviceName("设备" + i);
            device.setDeviceId("GADw3NnUI4Xa54nsr5tYz20000");
            device.setDeviceStatus(i % 2 == 0 ? "online" : "offline");
            devices.add(device);
        }
        mView.showDevices(devices);
    }
}
