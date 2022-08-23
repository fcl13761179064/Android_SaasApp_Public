package com.ayla.hotelsaas.mvp.view;

import com.ayla.hotelsaas.base.BaseView;
import com.ayla.hotelsaas.bean.DeviceTemplateBean;

import java.util.List;

public interface SceneSettingFunctionDatumSetView extends BaseView {
    void showFunctions(DeviceTemplateBean.AttributesBean attributesBeans);
    void getGroupFunctionsSuccess(DeviceTemplateBean.AttributesBean attributesBeans);
}
