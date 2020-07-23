package com.ayla.hotelsaas.mvp.present;

import com.ayla.hotelsaas.application.MyApplication;
import com.ayla.hotelsaas.base.BasePresenter;
import com.ayla.hotelsaas.mvp.view.SceneSettingDeviceSelectView;

public class SceneSettingDeviceSelectPresenter extends BasePresenter<SceneSettingDeviceSelectView> {

    public void loadDevice() {
        mView.showDevices(MyApplication.getInstance().getDevicesBean());
    }
}
