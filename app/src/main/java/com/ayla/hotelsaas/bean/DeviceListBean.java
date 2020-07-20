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
     * programName : saas酒店
     */


    private String currentPage;
    private String pageSize;
    private List<DeviceCategory> deviceListInfo;

    public String getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(String currentPage) {
        this.currentPage = currentPage;
    }

    public String getPageSize() {
        return pageSize;
    }

    public void setPageSize(String pageSize) {
        this.pageSize = pageSize;
    }

    public List<DeviceCategory> getDeviceCategories() {
        return deviceListInfo;
    }

    public void setDeviceCategories(List<DeviceCategory> deviceCategories) {
        deviceListInfo = deviceCategories;
    }

    public class DeviceCategory implements Serializable {
        private String deviceIconUrl;
        private String deviceName;
        private String deviceStatus;

        public String getDeviceIconUrl() {
            return deviceIconUrl;
        }

        public void setDeviceIconUrl(String deviceIconUrl) {
            this.deviceIconUrl = deviceIconUrl;
        }

        public String getDeviceName() {
            return deviceName;
        }

        public void setDeviceName(String deviceName) {
            this.deviceName = deviceName;
        }

        public String getDeviceStatus() {
            return deviceStatus;
        }

        public void setDeviceStatus(String deviceStatus) {
            this.deviceStatus = deviceStatus;
        }
    }
}
