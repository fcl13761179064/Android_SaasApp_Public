package com.ayla.hotelsaas.mvp.view;

import com.ayla.hotelsaas.base.BaseView;
import com.ayla.hotelsaas.bean.DeviceTemplateBean;

import java.util.List;

public interface SceneSettingFunctionSelectView extends BaseView {
    void showFunctions(List<DeviceTemplateBean.AttributesBean> devices);
}
