package com.ayla.hotelsaas.mvp.view;

import com.ayla.hotelsaas.base.BaseView;

public interface AylaWifiAddView extends BaseView {
    /**
     * 流程结束
     */
    void bindSuccess(String deviceId, String deviceName);

    /**
     * 流程失败
     *
     * @param msg
     */
    void bindFailed(String msg);

    /**
     * 步骤二 完成
     */
    void step2Finish();

    /**
     * 步骤二 开始
     */
    void step2Start();

    /**
     * 步骤一 完成
     */
    void step1Finish();

    /**
     * 步骤一 开始
     */
    void step1Start();

    void renameSuccess();

    void renameFailed();
}
