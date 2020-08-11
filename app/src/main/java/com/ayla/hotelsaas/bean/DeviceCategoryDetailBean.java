package com.ayla.hotelsaas.bean;

import java.util.List;

/**
 * 品类中心，品类详细描述
 * 包含oemModel 、支持的条件properties 和动作properties
 */
public class DeviceCategoryDetailBean {

    /**
     * deviceName : xxxxx
     * cuId : 0
     * oemModel : sxxxxxx
     * conditionProperties : ["1111","2222"]
     * actionProperties : ["1111","2222"]
     */

    private String deviceName;
    private long cuId;
    private String oemModel;
    private List<String> conditionProperties;
    private List<String> actionProperties;

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public long getCuId() {
        return cuId;
    }

    public void setCuId(long cuId) {
        this.cuId = cuId;
    }

    public String getOemModel() {
        return oemModel;
    }

    public void setOemModel(String oemModel) {
        this.oemModel = oemModel;
    }

    public List<String> getConditionProperties() {
        return conditionProperties;
    }

    public void setConditionProperties(List<String> conditionProperties) {
        this.conditionProperties = conditionProperties;
    }

    public List<String> getActionProperties() {
        return actionProperties;
    }

    public void setActionProperties(List<String> actionProperties) {
        this.actionProperties = actionProperties;
    }
}
