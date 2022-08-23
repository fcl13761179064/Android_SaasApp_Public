package com.ayla.hotelsaas.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class GroupDetail implements Serializable {
    private String groupId;
    private String groupName;
    private String scopeId;
    private List<GroupNewDeviceItem> groupDeviceList;
    private int groupType;
    private String rootGatewayDeviceId;
    private String productLabels;
    private long regionId;
    private String connectionStatus;
    private int siteType;
    private String createTime;
    private String modifyTime;

    public GroupDetail() {
    }

    public String getProductLabels() {
        return productLabels;
    }

    public void setGroupType(int groupType) {
        this.groupType = groupType;
    }

    public void setRootGatewayDeviceId(String rootGatewayDeviceId) {
        this.rootGatewayDeviceId = rootGatewayDeviceId;
    }

    public void setProductLabels(String productLabels) {
        this.productLabels = productLabels;
    }

    public void setConnectionStatus(String connectionStatus) {
        this.connectionStatus = connectionStatus;
    }

    public void setSiteType(int siteType) {
        this.siteType = siteType;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public void setModifyTime(String modifyTime) {
        this.modifyTime = modifyTime;
    }

    public long getRegionId() {
        return regionId;
    }

    public void setRegionId(long regionId) {
        this.regionId = regionId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getScopeId() {
        return scopeId;
    }

    public void setScopeId(String scopeId) {
        this.scopeId = scopeId;
    }

    public List<GroupNewDeviceItem> getGroupDeviceList() {
        return groupDeviceList;
    }

    public void setGroupDeviceList(List<GroupNewDeviceItem> groupDeviceList) {
        this.groupDeviceList = groupDeviceList;
    }

    public GroupDetail getNewGroupDetail() {
        GroupDetail detail = new GroupDetail();
        detail.setGroupId(groupId);
        detail.setGroupName(groupName);
        detail.setScopeId(scopeId);
        detail.setGroupDeviceList(groupDeviceList);
        detail.setRegionId(regionId);
        detail.setConnectionStatus(connectionStatus);
        detail.setCreateTime(createTime);
        detail.setGroupType(groupType);
        detail.setModifyTime(modifyTime);
        detail.setProductLabels(productLabels);
        detail.setRootGatewayDeviceId(rootGatewayDeviceId);
        detail.setSiteType(siteType);
        return detail;
    }
}
