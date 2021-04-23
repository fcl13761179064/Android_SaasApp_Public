package com.ayla.hotelsaas.mvp.present;

import com.ayla.hotelsaas.application.MyApplication;
import com.ayla.hotelsaas.base.BasePresenter;
import com.ayla.hotelsaas.bean.DeviceListBean;
import com.ayla.hotelsaas.mvp.view.ZigBeeAddSelectGatewayView;
import com.ayla.hotelsaas.utils.TempUtils;

import java.util.ArrayList;
import java.util.List;

public class ZigBeeAddSelectGatewayPresenter extends BasePresenter<ZigBeeAddSelectGatewayView> {
    /**
     * 过滤出指定的网关
     * @param sourceId  <0时 ，不区分网关所属云。
     */
    public void loadGateway(int sourceId) {
        List<DeviceListBean.DevicesBean> gateways = new ArrayList<>();
        List<DeviceListBean.DevicesBean> devicesBean = MyApplication.getInstance().getDevicesBean();
        if (devicesBean != null) {
            for (DeviceListBean.DevicesBean device : devicesBean) {
                if (TempUtils.isDeviceGateway(device)) {
                    if (sourceId < 0) {
                        gateways.add(device);
                    } else if (device.getCuId() == sourceId) {
                        gateways.add(device);
                    }
                }
            }
        }
        mView.showGateways(gateways);
    }

    /**
     * 过滤出指定的网关
     * @param sourceId  <0时 ，不区分网关所属云。
     */
    public void loadA2Gateway(int sourceId) {
        List<DeviceListBean.DevicesBean> gateways = new ArrayList<>();
        List<DeviceListBean.DevicesBean> devicesBean = MyApplication.getInstance().getDevicesBean();
        if (devicesBean != null) {
            for (DeviceListBean.DevicesBean device : devicesBean) {
                if (TempUtils.isDeviceGateway(device)) {
                    if (sourceId < 0) {
                        gateways.add(device);
                    } else if (device.getCuId() == sourceId || device.getPid().equalsIgnoreCase("ZBGW0-A000002")) {
                        gateways.add(device);
                    }
                }
            }
        }
        mView.showGateways(gateways);
    }
}
