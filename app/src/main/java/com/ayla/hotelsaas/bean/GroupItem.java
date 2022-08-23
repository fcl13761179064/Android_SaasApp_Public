package com.ayla.hotelsaas.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class GroupItem extends BaseDevice implements Serializable {
    private String groupId;
    private String groupName;
    private Integer groupType;
    private Integer siteType;
    private String gatewayDeviceId;
    private String regionId;
    private String roomName;
    /**
     * 0-offline,1-online,2-部分在线
     */
    private String connectionStatus;
    private List<String> subGroupIdList;
    private List<DeviceItem> groupDeviceList;
    private String productLabels;

    private List<String> actionAbilities = new ArrayList<>();

    public void setActionAbilities(List<String> actionAbilities) {
        this.actionAbilities = actionAbilities;
    }

    public List<String> getActionAbilities() {
        return actionAbilities;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Integer getGroupType() {
        return groupType;
    }

    public void setGroupType(Integer groupType) {
        this.groupType = groupType;
    }

    public Integer getSiteType() {
        return siteType;
    }

    public void setSiteType(Integer siteType) {
        this.siteType = siteType;
    }

    public String getGatewayDeviceId() {
        return gatewayDeviceId;
    }

    public void setGatewayDeviceId(String gatewayDeviceId) {
        this.gatewayDeviceId = gatewayDeviceId;
    }

    public String getRegionId() {
        return regionId;
    }

    public void setRegionId(String regionId) {
        this.regionId = regionId;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getConnectionStatus() {
        return connectionStatus;
    }

    public void setConnectionStatus(String connectionStatus) {
        this.connectionStatus = connectionStatus;
    }


    public List<String> getSubGroupIdList() {
        return subGroupIdList;
    }

    public void setSubGroupIdList(List<String> subGroupIdList) {
        this.subGroupIdList = subGroupIdList;
    }

    public List<DeviceItem> getGroupDeviceList() {
        return groupDeviceList;
    }

    public void setGroupDeviceList(List<DeviceItem> groupDeviceList) {
        this.groupDeviceList = groupDeviceList;
    }

    public String getProductLabels() {
        return productLabels;
    }

    public void setProductLabels(String productLabels) {
        this.productLabels = productLabels;
    }

    public enum ConnectStatus {
        ONLINE, OFFLINE, PARTLY_ONLINE
    }
}
