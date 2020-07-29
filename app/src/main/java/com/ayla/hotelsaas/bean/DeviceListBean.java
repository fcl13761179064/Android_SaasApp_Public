package com.ayla.hotelsaas.bean;

import java.io.Serializable;
import java.util.List;

/**
 * @描述 工作订单的bean
 * @作者 fanchunlei
 * @时间 2020/7/14
 */
public class DeviceListBean implements Serializable {


    /**
     * currentPage : 1
     * totalPages : 1
     * pageSize : 10
     * totalCount : 2
     * devices : [{"cuId":null,"deviceId":"SC000W000194710","deviceName":"热水壶插座","nickname":null,"deviceStatus":"ONLINE"},{"cuId":null,"deviceId":"GADw3NnUI4Xa54nsr5tYz20000","deviceName":"chuangmi-chengdu-001","nickname":null,"deviceStatus":"ONLINE"}]
     */

    private int currentPage;
    private int totalPages;
    private int pageSize;
    private int totalCount;
    private List<DevicesBean> devices;

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public List<DevicesBean> getDevices() {
        return devices;
    }

    public void setDevices(List<DevicesBean> devices) {
        this.devices = devices;
    }

    public static class DevicesBean implements Serializable {
        /**
         * cuId : null
         * deviceId : SC000W000194710
         * deviceName : 热水壶插座
         * nickname : null
         * deviceStatus : ONLINE
         */

        private long cuId;
        private String deviceId;
        private String deviceName;
        private Object nickname;
        private String deviceStatus;
        private int connectTypeId;//1-网关设备、2-节点设备

        public int getConnectTypeId() {
            return connectTypeId;
        }

        public void setConnectTypeId(int connectTypeId) {
            this.connectTypeId = connectTypeId;
        }

        public long getCuId() {
            return cuId;
        }

        public void setCuId(long cuId) {
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

        public Object getNickname() {
            return nickname;
        }

        public void setNickname(Object nickname) {
            this.nickname = nickname;
        }

        public String getDeviceStatus() {
            return deviceStatus;
        }

        public void setDeviceStatus(String deviceStatus) {
            this.deviceStatus = deviceStatus;
        }
    }
}
