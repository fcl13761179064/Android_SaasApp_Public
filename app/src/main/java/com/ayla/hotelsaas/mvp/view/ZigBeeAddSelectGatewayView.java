package com.ayla.hotelsaas.mvp.view;

import com.ayla.hotelsaas.base.BaseView;
import com.ayla.hotelsaas.bean.DeviceListBean;

import java.util.List;

public interface ZigBeeAddSelectGatewayView extends BaseView {
    void showGateways(List<DeviceListBean.DevicesBean> devices);
}
