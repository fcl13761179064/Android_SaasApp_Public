package com.ayla.hotelsaas.mvp.view;

import com.ayla.hotelsaas.base.BaseView;

public interface SwitchDefaultSettingView extends BaseView {
    void showData(String propertyValue);

    void updateSuccess();
}
