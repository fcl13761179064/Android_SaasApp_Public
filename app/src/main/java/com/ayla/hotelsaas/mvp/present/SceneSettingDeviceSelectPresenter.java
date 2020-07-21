package com.ayla.hotelsaas.mvp.present;

import com.ayla.hotelsaas.base.BasePresenter;
import com.ayla.hotelsaas.bean.Device;
import com.ayla.hotelsaas.mvp.view.SceneSettingDeviceSelectView;

import java.util.ArrayList;
import java.util.List;

public class SceneSettingDeviceSelectPresenter extends BasePresenter<SceneSettingDeviceSelectView> {

    public void loadDevice() {
        List<Device> devices = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            Device device = new Device();
            device.setName("设备" + i);
            device.setId("dsn" + i);
            device.setOnlineStatus(i % 2 == 0 ? "online" : "offline");
            devices.add(device);
        }
        mView.showDevices(devices);
    }
}
