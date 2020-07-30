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
//        return devicesBean.getConnectTypeId() == 1;
        return devicesBean.getDeviceId().startsWith("AC");
    }

    /**
     * 判断是被是否在线
     *
     * @param devicesBean
     * @return
     */
    public static boolean isDeviceOnline(DeviceListBean.DevicesBean devicesBean) {
        if (devicesBean == null) {
            return false;
        }
        return "ONLINE".equals(devicesBean.getDeviceStatus());
    }
}
