package com.ayla.hotelsaas.mvp.view;

import com.ayla.hotelsaas.base.BaseView;
import com.ayla.hotelsaas.bean.DeviceListBean;

public interface MultiDeviceAddView extends BaseView {
    /**
     * 节点绑定流程结束
     */
    void bindSuccess(DeviceListBean.DevicesBean devicesBean);

    /**
     * 5.节点绑定流程失败
     *
     * @param throwable
     */
    void bindFailed(Throwable throwable);

    /**
     * 4.绑定节点成功
     */
    void step3Finish();

    /**
     * 3.开始绑定节点
     */
    void step3Start();
    /**
     * 2.候选节点查找成功
     */
    void step2Finish();

    /**
     *1.开始查找候选节点
     */
    void step2Start();

}
