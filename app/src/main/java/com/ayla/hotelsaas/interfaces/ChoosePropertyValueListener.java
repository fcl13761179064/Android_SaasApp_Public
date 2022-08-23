package com.ayla.hotelsaas.interfaces;

import com.ayla.hotelsaas.ui.activities.ISceneSettingFunctionDatumSet;

public interface ChoosePropertyValueListener {
    void onUpdate(int currentPos, ISceneSettingFunctionDatumSet.CallBackBean callBackBean);

    void onToastContent(String content);

    void onProgress();

    void onFinish();
}
