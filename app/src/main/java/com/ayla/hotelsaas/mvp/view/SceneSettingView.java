package com.ayla.hotelsaas.mvp.view;

import com.ayla.hotelsaas.adapter.SceneSettingFunctionDatumSetAdapter;
import com.ayla.hotelsaas.base.BaseView;

import java.util.List;

public interface SceneSettingView extends BaseView {
    void saveSuccess();

    void saveFailed();

    void deleteSuccess();

    void deleteFailed();

    void showData(List<SceneSettingFunctionDatumSetAdapter.DatumBean> datum, List<SceneSettingFunctionDatumSetAdapter.DatumBean> datum1);
}
