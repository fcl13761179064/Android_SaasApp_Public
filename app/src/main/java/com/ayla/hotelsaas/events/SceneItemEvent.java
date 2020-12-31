package com.ayla.hotelsaas.events;

import com.ayla.hotelsaas.bean.DeviceTemplateBean;
import com.ayla.hotelsaas.ui.ISceneSettingFunctionDatumSet;

public class SceneItemEvent {
    public boolean condition;//标记是否为条件
    public String deviceId;
    public DeviceTemplateBean.AttributesBean attributesBean;
    public ISceneSettingFunctionDatumSet.CallBackBean callBackBean;
    public boolean editMode;
    public int editPosition = -1;

    public SceneItemEvent(boolean condition, String deviceId, DeviceTemplateBean.AttributesBean attributesBean, ISceneSettingFunctionDatumSet.CallBackBean callBackBean) {
        this.condition = condition;
        this.deviceId = deviceId;
        this.attributesBean = attributesBean;
        this.callBackBean = callBackBean;
    }
}
