package com.ayla.hotelsaas.mvp.present;

import com.ayla.hotelsaas.application.MyApplication;
import com.ayla.hotelsaas.base.BasePresenter;
import com.ayla.hotelsaas.bean.DeviceListBean;
import com.ayla.hotelsaas.constant.ConstantValue;
import com.ayla.hotelsaas.mvp.view.ZigBeeAddSelectGatewayView;
import com.ayla.hotelsaas.utils.TempUtils;

import java.util.ArrayList;
import java.util.List;

public class ZigBeeAddSelectGatewayPresenter extends BasePresenter<ZigBeeAddSelectGatewayView> {
    /**
     * 过滤出指定的网关
     *
     * @param sourceId <0时 ，不区分网关所属云。
     */
    public void loadGateway(int sourceId) {
        List<DeviceListBean.DevicesBean> gateways = new ArrayList<>();
        List<DeviceListBean.DevicesBean> devicesBean = MyApplication.getInstance().getDevicesBean().getDevices();
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
     *
     * @param sourceId <0时 ，不区分网关所属云。
     */
    public void LocalLoadGateway(int sourceId) {
        List<DeviceListBean.DevicesBean> gateways = new ArrayList<>();
        List<DeviceListBean.DevicesBean> devicesBean = MyApplication.getInstance().getDevicesBean().getDevices();
        if (devicesBean != null) {
            for (DeviceListBean.DevicesBean device : devicesBean) {
                if (TempUtils.isDeviceGateway(device)) {
                    if (ConstantValue.A6_GATEWAY_PID.equals(device.getPid())){
                        continue;
                    }
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
     * 过滤出指定的网关1
     *
     * @param sourceId <0时 ，不区分网关所属云。
     */
    public void loadA2Gateway(int sourceId) {
        List<DeviceListBean.DevicesBean> gateways = new ArrayList<>();
        List<DeviceListBean.DevicesBean> devicesBean = MyApplication.getInstance().getDevicesBean().getDevices();
        if (devicesBean != null) {
            for (DeviceListBean.DevicesBean device : devicesBean) {
                if (TempUtils.isDeviceGateway(device)) {
                    if (sourceId < 0) {
                        gateways.add(device);
                    } else if (device.getCuId() == sourceId || device.getPid().equalsIgnoreCase("ZBGW0-A000002") || device.getPid().equalsIgnoreCase("ZBGW0-A000003")) {
                        gateways.add(device);
                    }
                }
            }
        }
        mView.showGateways(gateways);
    }


    /**
     * 过滤出指定的网关2
     *
     * @param sourceId              <0时 ，不区分网关所属云。
     * @param targetGatewayDeviceId
     */
    public void loadA2Gatewaytwo(int sourceId, String targetGatewayDeviceId) {
        List<DeviceListBean.DevicesBean> gateways = new ArrayList<>();
        List<DeviceListBean.DevicesBean> devicesBean = MyApplication.getInstance().getDevicesBean().getDevices();
        if (devicesBean != null) {
            for (DeviceListBean.DevicesBean device : devicesBean) {
                if (TempUtils.isDeviceGateway(device)) {
                    if (sourceId < 0) {
                        gateways.add(device);
                    } else if (device.getCuId() == sourceId || device.getPid().equalsIgnoreCase("ZBGW0-A000002") || device.getPid().equalsIgnoreCase("ZBGW0-A000003")) {
                        if (targetGatewayDeviceId != null) {
                            if (targetGatewayDeviceId.equalsIgnoreCase(device.getDeviceId())) {
                                gateways.add(device);
                            }
                        } else
                            gateways.add(device);
                    }
                }
            }
        }
        mView.showRelaceGateWays(gateways);
    }
}
