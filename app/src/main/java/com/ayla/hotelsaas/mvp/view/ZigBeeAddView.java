package com.ayla.hotelsaas.mvp.view;

import com.ayla.hotelsaas.base.OrderListView;

public interface ZigBeeAddView extends OrderListView {
    /**
     * 节点绑定流程结束
     */
    void zigBeeDeviceBindFinished();

    /**
     * 节点绑定流程失败
     *
     * @param throwable
     */
    void zigBeeDeviceBindFailed(Throwable throwable);

    /**
     * 4.节点绑定成功
     */
    void zigBeeDeviceBindSuccess();

    /**
     * 3.开始绑定节点
     */
    void zigBeeDeviceBindStart();

    /**
     * 2.连接网关成功
     */
    void gatewayConnectSuccess();

    /**
     * 1.开始连接网关
     */
    void gatewayConnectStart();

}
