package com.ayla.hotelsaas.mvp.view;

import com.ayla.hotelsaas.base.BaseView;
import com.ayla.hotelsaas.bean.DeviceListBean;

public interface ApDeviceAddView extends BaseView {
    /**
     * 节点绑定流程结束
     */
    void bindSuccess(Object devicesBean);

    /**
     * 节点绑定流程失败
     *
     * @param throwable
     */
    void bindFailed(Throwable throwable);

}
