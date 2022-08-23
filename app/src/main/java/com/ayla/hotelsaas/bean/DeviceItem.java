package com.ayla.hotelsaas.bean;

import com.ayla.hotelsaas.constant.ConstantValue;

import java.io.Serializable;

public class DeviceItem extends BaseDevice implements Serializable {
    private int cuId;//设备所属云 0：艾拉 1：阿里
    private String deviceId;
    private String deviceName;
    private String nickname;
    private String deviceCategory;
    private String deviceStatus;
    private String iconUrl;
    private boolean hasH5;
    private int deviceUseType;//0:常规设备      1:用途设备      2:红外源设备   3:用途源设备。      用途设备在联动里面，直接可以作为联动动作，支持的动作以物模型为准。
    private int isPurposeDevice;//0:普通   1：可以创建用途设备的设备
    private String purposeName;//用途名称  et:吸顶灯 、吊灯
    private String pointName;
    private long regionId;
    private String domain;//跳转到单控页面h5时用，这个是域名
    private String h5Url;//跳转到单控页面h5时用，这个是url
    private String regionName;
    private String productLabel;
    private String purposeId;

    public String getPurposeId() {
        return purposeId;
    }

    public void setPurposeId(String purposeId) {
        this.purposeId = purposeId;
    }

    public String getProductLabel() {
        return productLabel;
    }

    public void setProductLabel(String productLabel) {
        this.productLabel = productLabel;
    }

    public void setHasH5(boolean hasH5) {
        this.hasH5 = hasH5;
    }

    public void setDeviceUseType(int deviceUseType) {
        this.deviceUseType = deviceUseType;
    }

    public void setIsPurposeDevice(int isPurposeDevice) {
        this.isPurposeDevice = isPurposeDevice;
    }

    public void setPurposeName(String purposeName) {
        this.purposeName = purposeName;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public void setProductType(int productType) {
        this.productType = productType;
    }

    public void setIsNeedGateway(int isNeedGateway) {
        this.isNeedGateway = isNeedGateway;
    }

    public String getH5Url() {
        return h5Url;
    }

    public void setH5Url(String h5Url) {
        this.h5Url = h5Url;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public void setRegionId(long regionId) {
        this.regionId = regionId;
    }

    public void setRegionName(String regionName) {
        this.regionName = regionName;
    }


    private String pid;
    private int productType;//产品类型 1、网关设备 2、设备
    private int isNeedGateway;


    public int getProductType() {
        return productType;
    }

    public String getPurposeName() {
        return purposeName;
    }

    public int getIsPurposeDevice() {
        return isPurposeDevice;
    }

    public String getDeviceCategory() {
        return deviceCategory;
    }

    public void setDeviceCategory(String deviceCategory) {
        this.deviceCategory = deviceCategory;
    }

    public int getCuId() {
        return cuId;
    }

    public void setCuId(int cuId) {
        this.cuId = cuId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getDeviceStatus() {
        return deviceStatus;
    }

    public void setDeviceStatus(String deviceStatus) {
        this.deviceStatus = deviceStatus;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public boolean isHasH5() {
        return hasH5;
    }

    public int getDeviceUseType() {
        return deviceUseType;
    }

    public String getPointName() {
        return pointName;
    }

    public long getRegionId() {
        return regionId;
    }

    public String getRegionName() {
        return regionName;
    }

    public void setPointName(String pointName) {
        this.pointName = pointName;
    }



    public String getPid() {
        return pid;
    }

    public int getIsNeedGateway() {
        return isNeedGateway;
    }

    /**
     * 判断是否是艾拉智能网关并且是否是bindtype==0
     */
    public boolean isAylaSmartGateway() {
        return ConstantValue.A2_GATEWAY_PID.equals(pid);
    }


}
