package com.ayla.hotelsaas.mvp.view;

import com.ayla.hotelsaas.base.BaseView;

public interface AylaWifiAddView extends BaseView {
    /**
     * 流程结束
     */
    void progressSuccess();

    /**
     * 流程失败
     *
     * @param throwable
     */
    void progressFailed(Throwable throwable);

    /**
     * 步骤二 完成
     */
    void bindSuccess();

    /**
     * 步骤二 开始
     */
    void startBind();

    /**
     * 步骤一 完成
     */
    void airkissSuccess();

    /**
     * 步骤一 开始
     */
    void startAirkiss();
}
