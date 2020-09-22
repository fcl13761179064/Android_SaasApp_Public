package com.ayla.hotelsaas.bean;

import java.util.List;

/**
 * 设备添加列表页面
 */
public class DeviceCategoryBean {
    /**
     * id : 1
     * name : 电工
     * sub : [{"name":"一路开关","connectMode":1,"icon":"http://172.31.16.100/product/typeIcon/cz.png"},{"name":"二路开关","connectMode":1,"icon":"http://172.31.16.100/product/typeIcon/cz.png"},{"name":"三路开关","connectMode":1,"icon":"http://172.31.16.100/product/typeIcon/cz.png"}]
     */

    private int id;
    private String name;
    private List<SubBean> sub;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<SubBean> getSub() {
        return sub;
    }

    public void setSub(List<SubBean> sub) {
        this.sub = sub;
    }

    public static class SubBean {
        /**
         * name : 一路开关
         * connectMode : 1
         * icon : http://172.31.16.100/product/typeIcon/cz.png
         */
        private long id;
        private String name;
        private String deviceName;
        private int cuId;
        private int deviceConnectType;//1、网关设备 2、节点设备 3、wifi设备
        private int networkType;//1、鸿雁-插网线网关配网2、顺舟-插网线网关配网3、艾拉zigbee配网 4、鸿雁节点 5、艾拉wifi设备
        private String icon;
        private String oemModel;

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public String getOemModel() {
            return oemModel;
        }

        public void setOemModel(String oemModel) {
            this.oemModel = oemModel;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDeviceName() {
            return deviceName;
        }

        public void setDeviceName(String deviceName) {
            this.deviceName = deviceName;
        }

        public int getCuId() {
            return cuId;
        }

        public void setCuId(int cuId) {
            this.cuId = cuId;
        }

        public int getNetworkType() {
            return networkType;
        }

        public void setNetworkType(int networkType) {
            this.networkType = networkType;
        }

        public String getIcon() {
            return icon;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }

        public int getDeviceConnectType() {
            return deviceConnectType;
        }

        public void setDeviceConnectType(int deviceConnectType) {
            this.deviceConnectType = deviceConnectType;
        }
    }
}
