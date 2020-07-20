package com.ayla.hotelsaas.mvp.view;

import com.ayla.hotelsaas.base.BaseView;
import com.ayla.hotelsaas.bean.Device;

import java.util.List;

public interface SceneSettingDeviceSelectView extends BaseView {
    void showGateways(List<Device> devices);
}
