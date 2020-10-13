package com.ayla.hotelsaas.mvp.present;

import com.ayla.hotelsaas.application.MyApplication;
import com.ayla.hotelsaas.base.BasePresenter;
import com.ayla.hotelsaas.bean.DeviceListBean;
import com.ayla.hotelsaas.mvp.view.ZigBeeAddSelectGatewayView;
import com.ayla.hotelsaas.utils.TempUtils;

import java.util.ArrayList;
import java.util.List;

public class ZigBeeAddSelectGatewayPresenter extends BasePresenter<ZigBeeAddSelectGatewayView> {

    public void loadGateway(int cuId) {
        List<DeviceListBean.DevicesBean> gateways = new ArrayList<>();
        List<DeviceListBean.DevicesBean> devicesBean = MyApplication.getInstance().getDevicesBean();
        if (devicesBean != null) {
            for (DeviceListBean.DevicesBean device : devicesBean) {
                if (TempUtils.isDeviceGateway(device) && device.getCuId() == cuId) {
                    gateways.add(device);
                }
            }
        }
        mView.showGateways(gateways);
    }
}
