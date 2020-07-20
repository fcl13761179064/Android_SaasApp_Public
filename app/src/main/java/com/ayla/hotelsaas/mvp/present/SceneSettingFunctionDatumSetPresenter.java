package com.ayla.hotelsaas.mvp.present;

import com.ayla.hotelsaas.adapter.CheckableSupport;
import com.ayla.hotelsaas.base.BasePresenter;
import com.ayla.hotelsaas.mvp.view.SceneSettingFunctionDatumSetView;

import java.util.ArrayList;
import java.util.List;

public class SceneSettingFunctionDatumSetPresenter extends BasePresenter<SceneSettingFunctionDatumSetView> {

    public void loadFunction() {
        List<CheckableSupport<String>> devices = new ArrayList<>();
        {
            CheckableSupport<String> bean = new CheckableSupport<>("开启");
            bean.setChecked(true);
            devices.add(bean);
        }
        {
            CheckableSupport<String> bean = new CheckableSupport<>("关闭");
            devices.add(bean);
        }
        mView.showFunctions(devices);
    }
}
