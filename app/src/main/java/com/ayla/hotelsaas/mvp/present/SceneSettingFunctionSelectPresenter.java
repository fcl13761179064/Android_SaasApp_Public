package com.ayla.hotelsaas.mvp.present;

import com.ayla.hotelsaas.base.BasePresenter;
import com.ayla.hotelsaas.mvp.view.SceneSettingFunctionSelectView;

import java.util.ArrayList;
import java.util.List;

public class SceneSettingFunctionSelectPresenter extends BasePresenter<SceneSettingFunctionSelectView> {

    public void loadFunction() {
        List<String> devices = new ArrayList<>();
        devices.add("开关");
        mView.showFunctions(devices);
    }
}
