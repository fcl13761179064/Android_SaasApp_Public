package com.ayla.hotelsaas.mvp.view;

import com.ayla.hotelsaas.adapter.CheckableSupport;
import com.ayla.hotelsaas.adapter.SceneSettingFunctionDatumSetAdapter;
import com.ayla.hotelsaas.base.BaseView;

import java.util.List;

public interface SceneSettingFunctionDatumSetView extends BaseView {
    void showFunctions(List<CheckableSupport<SceneSettingFunctionDatumSetAdapter.DatumBean>> devices);
}
