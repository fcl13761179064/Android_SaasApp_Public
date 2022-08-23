package com.ayla.hotelsaas.mvp.view;

import com.ayla.hotelsaas.base.BaseView;
import com.ayla.hotelsaas.bean.DeviceTemplateBean;
import com.ayla.hotelsaas.bean.NewGroupAbility;

import java.util.List;

public interface SceneSettingFunctionSelectView extends BaseView {
    void showAllProperty(List<DeviceTemplateBean.AttributesBean> devices);

    void loadGroupAbilitySuccess(List<NewGroupAbility> data);

    void loadGroupAbilityFail(Throwable throwable);
}
