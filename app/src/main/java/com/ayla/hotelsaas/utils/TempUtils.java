package com.ayla.hotelsaas.utils;

import com.ayla.hotelsaas.bean.DeviceListBean;

public class TempUtils {
    /**
     * 判断设备是否为网关
     *
     * @param devicesBean
     * @return
     */
    public static boolean isDeviceGateway(DeviceListBean.DevicesBean devicesBean) {
        if (devicesBean == null) {
            return false;
        }
        if (devicesBean.getDeviceId().startsWith("AC")) {
            return true;
        }
        return false;
    }

    public static boolean isDeviceOnline(DeviceListBean.DevicesBean devicesBean) {
        if (devicesBean == null) {
            return false;
        }
        return "ONLINE".equals(devicesBean.getDeviceStatus());
    }
}
