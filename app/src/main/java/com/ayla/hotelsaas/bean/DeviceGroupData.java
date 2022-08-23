package com.ayla.hotelsaas.bean;

public class DeviceGroupData {
    /**
     * 标志设备类型 0:表示设备，1:表示组
     */
    public Integer deviceGroupFlag;
    /**
     * deviceGroupFlag = 0 ,则表示设备信息json
     * deviceGroupFlag = 1 ,则表示组信息json
     */
    public String deviceGroupJson;

}
