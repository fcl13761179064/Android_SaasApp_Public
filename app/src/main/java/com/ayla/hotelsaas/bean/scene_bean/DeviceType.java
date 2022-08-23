package com.ayla.hotelsaas.bean.scene_bean;

public interface DeviceType {
    int AYLA_DEVICE_ID = 0,//     * 艾拉设备id
            ALI_DEVICE_ID = 1,//     * 阿里设备id
            AYLA_DEVICE_CATEGORY = 4,//     * 艾拉 设备位置+品类
            ALI_DEVICE_CATEGORY = 5,//     * 阿里 设备位置+品类
            XIAODU_SERVER = 6,//     * 调用服务
            CALL_SCENE = 7,//     * 复用场景
            INFRARED_VIRTUAL_SUB_DEVICE = 8,//     * 红外设备虚拟子设备
            SWITCH_PURPOSE_SUB_DEVICE = 9,//     * 用途源设备的虚拟子设备
            DELAY_ACTION = 10,
            GROUP_ACTION=11;
}
