package com.ayla.hotelsaas.bean;

import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;

public class DeviceNodeBean implements Serializable {

    private Integer id;
    private CloudModelDTO cloudModel;
    private String pid;
    private String productModel;
    private String productName;
    private Integer source;
    private Integer productType;
    private Integer isNeedGateway;
    private String connectType;
    private String wifiNetworkMode;
    private String actualIcon;
    private String virtualIcon;
    private Object isPurposeDevice;


        public static class CloudModelDTO implements Serializable {
            private String $0;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public CloudModelDTO getCloudModel() {
        return cloudModel;
    }

    public void setCloudModel(CloudModelDTO cloudModel) {
        this.cloudModel = cloudModel;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getProductModel() {
        return productModel;
    }

    public void setProductModel(String productModel) {
        this.productModel = productModel;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Integer getSource() {
        return source;
    }

    public void setSource(Integer source) {
        this.source = source;
    }

    public Integer getProductType() {
        return productType;
    }

    public void setProductType(Integer productType) {
        this.productType = productType;
    }

    public Integer getIsNeedGateway() {
        return isNeedGateway;
    }

    public void setIsNeedGateway(Integer isNeedGateway) {
        this.isNeedGateway = isNeedGateway;
    }

    public String getConnectType() {
        return connectType;
    }

    public void setConnectType(String connectType) {
        this.connectType = connectType;
    }

    public String getWifiNetworkMode() {
        return wifiNetworkMode;
    }

    public void setWifiNetworkMode(String wifiNetworkMode) {
        this.wifiNetworkMode = wifiNetworkMode;
    }

    public String getActualIcon() {
        return actualIcon;
    }

    public void setActualIcon(String actualIcon) {
        this.actualIcon = actualIcon;
    }

    public String getVirtualIcon() {
        return virtualIcon;
    }

    public void setVirtualIcon(String virtualIcon) {
        this.virtualIcon = virtualIcon;
    }

    public Object getIsPurposeDevice() {
        return isPurposeDevice;
    }

    public void setIsPurposeDevice(Object isPurposeDevice) {
        this.isPurposeDevice = isPurposeDevice;
    }
}
