package com.ayla.hotelsaas.bean;

public class PurposeCategoryBean {

    /**
     * id : 1
     * deviceModelId : 3
     * purposeName : 吸顶灯
     * iconUrl : http://cdn-smht.ayla.com.cn/webh5/assets/public/use/ceiling_lamp.png
     * purposeSourceType : 0
     */

    private int id;
    private int deviceModelId;
    private String purposeName;
    private String iconUrl;
    private int purposeSourceType;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDeviceModelId() {
        return deviceModelId;
    }

    public void setDeviceModelId(int deviceModelId) {
        this.deviceModelId = deviceModelId;
    }

    public String getPurposeName() {
        return purposeName;
    }

    public void setPurposeName(String purposeName) {
        this.purposeName = purposeName;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public int getPurposeSourceType() {
        return purposeSourceType;
    }

    public void setPurposeSourceType(int purposeSourceType) {
        this.purposeSourceType = purposeSourceType;
    }

    @Override
    public String toString() {
        return purposeName;
    }
}
