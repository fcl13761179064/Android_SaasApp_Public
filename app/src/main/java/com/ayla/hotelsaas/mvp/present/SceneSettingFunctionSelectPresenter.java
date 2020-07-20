package com.ayla.hotelsaas.mvp.present;

import com.ayla.hotelsaas.base.BasePresenter;
import com.ayla.hotelsaas.mvp.view.SceneSettingFunctionSelectView;

import java.util.ArrayList;
import java.util.List;

public class SceneSettingFunctionSelectPresenter extends BasePresenter<SceneSettingFunctionSelectView> {

    public void loadFunction() {
        List<String> devices = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
           devices.add("开关"+i);
        }
        mView.showFunctions(devices);
    }
}
