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
        return devicesBean.getConnectTypeId() == 1;
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

    /**
     * 判断是否为艾拉的红外遥控器 家电虚拟遥控器
     * @param deviceBean
     * @return
     */
    public static boolean isINFRARED_VIRTUAL_SUB_DEVICE(DeviceListBean.DevicesBean deviceBean) {
        if (isSWITCH_PURPOSE_SUB_DEVICE(deviceBean)) {//如果是 开关用途设备
            return false;
        }
        return deviceBean.getDeviceUseType() == 1 && deviceBean.getCuId() == 0;
    }

    /**
     * 判断是否为 开关用途设备
     * @param deviceBean
     * @return
     */
    public static boolean isSWITCH_PURPOSE_SUB_DEVICE(DeviceListBean.DevicesBean deviceBean) {
        return deviceBean.getDeviceUseType() == 1 && deviceBean.getDeviceCategory().startsWith("PD-NODE-ABP9-");
    }
}
