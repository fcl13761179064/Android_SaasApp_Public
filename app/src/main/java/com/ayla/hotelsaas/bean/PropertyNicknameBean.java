package com.ayla.hotelsaas.bean;

import java.io.Serializable;

public class PropertyNicknameBean implements Serializable {


    /**
     * id : 1
     * cuId : 1
     * deviceId : J9WX4aPBnZlxtipuQqwC000000
     * propertyName : 1
     * propertyType : Words
     * propertyValue : 场景2
     * propertyDescription :
     */

    private int id;
    private int cuId;
    private String deviceId;
    private String propertyName;
    private String propertyType;
    private String propertyValue;
    private String propertyDescription;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public String getPropertyType() {
        return propertyType;
    }

    public void setPropertyType(String propertyType) {
        this.propertyType = propertyType;
    }

    public String getPropertyValue() {
        return propertyValue;
    }

    public void setPropertyValue(String propertyValue) {
        this.propertyValue = propertyValue;
    }

    public String getPropertyDescription() {
        return propertyDescription;
    }

    public void setPropertyDescription(String propertyDescription) {
        this.propertyDescription = propertyDescription;
    }
}
