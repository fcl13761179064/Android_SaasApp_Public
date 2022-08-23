package com.ayla.hotelsaas.bean;

import java.io.Serializable;

public class GroupNewDeviceItem implements Serializable {
    private String deviceId;
//    @ApiModelProperty(value = "所属云，0-艾拉，1-阿里")
    private Integer cuId;
//    @ApiModelProperty(value = "设备品类")
    private String deviceCategory;
//    @ApiModelProperty(value = "设备名")
    private String nickname;
//    @ApiModelProperty(value = "在线状态， OFFLINE,ONLINE, UNKNOWN")
    private String connectionStatus;
//    @ApiModelProperty(value = "设备使用类型：0: 默认，1： 用途，2： 存在用途")
    private int deviceUseType;
//    @ApiModelProperty(value = "PID")
    private String pid;
//    @ApiModelProperty(value = "绑定类型：0： 默认，1： 待绑定")
    private Integer bindType;
//    @ApiModelProperty(value = "subDeviceKey")
    private String subDeviceKey;
//    @ApiModelProperty(value = "区域id")
    private long regionId;
//    @ApiModelProperty(value = "区域名称")
    private String regionName;
//    @ApiModelProperty(value = "实际icon")
    private String actualIcon;
//    @ApiModelProperty(value = "hasH5")
    private Integer hasH5;
//    @ApiModelProperty(value = "H5域名")
    private String domain;
//    @ApiModelProperty(value = "h5Url")
    private String h5Url;
//    @ApiModelProperty(value = "是否用途设备0、普通1、用途设备")
    private int isPurposeDevice;

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public Integer getCuId() {
        return cuId;
    }

    public void setCuId(Integer cuId) {
        this.cuId = cuId;
    }

    public String getDeviceCategory() {
        return deviceCategory;
    }

    public void setDeviceCategory(String deviceCategory) {
        this.deviceCategory = deviceCategory;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getConnectionStatus() {
        return connectionStatus;
    }

    public void setConnectionStatus(String connectionStatus) {
        this.connectionStatus = connectionStatus;
    }

    public int getDeviceUseType() {
        return deviceUseType;
    }

    public void setDeviceUseType(Integer deviceUseType) {
        this.deviceUseType = deviceUseType;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public Integer getBindType() {
        return bindType;
    }

    public void setBindType(Integer bindType) {
        this.bindType = bindType;
    }

    public String getSubDeviceKey() {
        return subDeviceKey;
    }

    public void setSubDeviceKey(String subDeviceKey) {
        this.subDeviceKey = subDeviceKey;
    }

    public long getRegionId() {
        return regionId;
    }

    public void setRegionId(long regionId) {
        this.regionId = regionId;
    }

    public String getRegionName() {
        return regionName;
    }

    public void setRegionName(String regionName) {
        this.regionName = regionName;
    }

    public String getActualIcon() {
        return actualIcon;
    }

    public void setActualIcon(String actualIcon) {
        this.actualIcon = actualIcon;
    }

    public Integer getHasH5() {
        return hasH5;
    }

    public void setHasH5(Integer hasH5) {
        this.hasH5 = hasH5;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getH5Url() {
        return h5Url;
    }

    public void setH5Url(String h5Url) {
        this.h5Url = h5Url;
    }

    public int getIsPurposeDevice() {
        return isPurposeDevice;
    }

    public void setIsPurposeDevice(int isPurposeDevice) {
        this.isPurposeDevice = isPurposeDevice;
    }
}
