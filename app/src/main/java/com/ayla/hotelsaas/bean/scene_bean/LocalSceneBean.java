package com.ayla.hotelsaas.bean.scene_bean;

import java.io.Serializable;

public class LocalSceneBean extends BaseSceneBean implements Serializable {
    private String targetGateway;//云端联动时，网关的dsn

    private int targetGatewayType;//云端联动时，网关的云平台类型 //0:艾拉 1：阿里

    public LocalSceneBean() {
        super(SITE_TYPE.LOCAL);
    }

    public String getTargetGateway() {
        return targetGateway;
    }

    public void setTargetGateway(String targetGateway) {
        this.targetGateway = targetGateway;
    }

    public int getTargetGatewayType() {
        return targetGatewayType;
    }

    public void setTargetGatewayType(int targetGatewayType) {
        this.targetGatewayType = targetGatewayType;
    }
}
