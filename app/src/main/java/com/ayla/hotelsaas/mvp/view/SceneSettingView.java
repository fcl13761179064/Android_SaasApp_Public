package com.ayla.hotelsaas.mvp.view;

import com.ayla.hotelsaas.base.BaseView;

public interface SceneSettingView extends BaseView {
    void saveSuccess();

    void saveFailed();

    void deleteSuccess();

    void deleteFailed();
}
