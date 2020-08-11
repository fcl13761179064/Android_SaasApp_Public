package com.ayla.hotelsaas.mvp.view;

import com.ayla.hotelsaas.base.BaseView;

public interface ZigBeeAddView extends BaseView {
    /**
     * 节点绑定流程结束
     */
    void progressSuccess();

    /**
     * 节点绑定流程失败
     *
     * @param throwable
     */
    void progressFailed(Throwable throwable);

    /**
     * 5.绑定节点成功
     */
    void bindZigBeeDeviceSuccess();

    /**
     * 5.开始绑定节点
     */
    void bindZigBeeDeviceStart();
    /**
     * 4.候选节点查找成功
     */
    void fetchCandidatesSuccess();

    /**
     * 3.开始查找候选节点
     */
    void fetchCandidatesStart();

    /**
     * 2.连接网关成功
     */
    void gatewayConnectSuccess();

    /**
     * 1.开始连接网关
     */
    void gatewayConnectStart();

}
