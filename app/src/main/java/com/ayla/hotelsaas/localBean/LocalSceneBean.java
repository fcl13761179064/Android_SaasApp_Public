package com.ayla.hotelsaas.localBean;

public class LocalSceneBean extends BaseSceneBean {
    private String targetGateway;//云端联动时，网关的dsn

    private DeviceType targetGatewayType;//云端联动时，网关的云平台类型 //0:艾拉 1：阿里

    public LocalSceneBean() {
        super(SITE_TYPE.LOCAL);
    }

    public String getTargetGateway() {
        return targetGateway;
    }

    public void setTargetGateway(String targetGateway) {
        this.targetGateway = targetGateway;
    }

    public DeviceType getTargetGatewayType() {
        return targetGatewayType;
    }

    public void setTargetGatewayType(DeviceType targetGatewayType) {
        this.targetGatewayType = targetGatewayType;
    }
}
