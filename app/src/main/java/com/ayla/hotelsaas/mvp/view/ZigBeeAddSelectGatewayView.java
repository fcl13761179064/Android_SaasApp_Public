package com.ayla.hotelsaas.mvp.view;

import com.ayla.hotelsaas.base.OrderListView;
import com.ayla.hotelsaas.bean.Device;

import java.util.List;

public interface ZigBeeAddSelectGatewayView extends OrderListView {
    void showGateways(List<Device> devices);
}
