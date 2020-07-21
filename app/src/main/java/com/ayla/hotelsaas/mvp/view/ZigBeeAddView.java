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
     * 5.通知网关退出配网模式成功
     */
    void gatewayDisconnectSuccess();

    /**
     * 5.开始通知网关退出配网模式
     */
    void gatewayDisconnectStart();
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
