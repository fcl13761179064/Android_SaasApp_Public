package com.ayla.hotelsaas.mvp.view;

import com.ayla.hotelsaas.base.BaseView;

public interface ZigBeeAddView extends BaseView {
    /**
     * 节点绑定流程结束
     */
    void bindSuccess(String deviceId, String deviceName);

    /**
     * 节点绑定流程失败
     *
     * @param msg
     */
    void bindFailed(String msg);

    /**
     * 5.绑定节点成功
     */
    void step3Finish();

    /**
     * 5.开始绑定节点
     */
    void step3Start();
    /**
     * 4.候选节点查找成功
     */
    void step2Finish();

    /**
     * 3.开始查找候选节点
     */
    void step2Start();

    /**
     * 2.连接网关成功
     */
    void step1Finish();

    /**
     * 1.开始连接网关
     */
    void step1Start();

    void renameSuccess();

    void renameFailed();
}
