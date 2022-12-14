package com.ayla.hotelsaas.mvp.view;

import com.ayla.hotelsaas.base.BaseView;
import com.ayla.hotelsaas.bean.DeviceTemplateBean;

public interface SceneSettingView extends BaseView {
    void saveSuccess();

    void saveFailed(Throwable throwable);

    void deleteSuccess();

    void deleteFailed();

    void showData();
}
