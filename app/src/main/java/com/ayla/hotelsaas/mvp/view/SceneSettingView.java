package com.ayla.hotelsaas.mvp.view;

import com.ayla.hotelsaas.adapter.SceneSettingFunctionDatumSetAdapter;
import com.ayla.hotelsaas.base.BaseView;

import java.util.List;

public interface SceneSettingView extends BaseView {
    void saveSuccess();

    void saveFailed(String code);

    void deleteSuccess();

    void deleteFailed();

    void showData();
}
