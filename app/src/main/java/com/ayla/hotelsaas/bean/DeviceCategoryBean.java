package com.ayla.hotelsaas.bean;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

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
        private List<NodeBean> node;

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<NodeBean> getNode() {
            return node;
        }

        public void setNode(List<NodeBean> node) {
            this.node = node;
        }

        public static class NodeBean implements Serializable {

            /**
             * "id": 1,
             * "oemModel": "ZB-NODE-SW0-001",
             * "pid": "ZBSW0-A000001",
             * "productModel": "AZB-SW0-001",  //原deviceName
             * "productName": "罗马一路智能面板",//原name
             * "source": 0, //原cuId产品来源：0 艾拉云 1 阿里云
             * "productType": 2, //产品类型 1、网关设备 2、设备
             * "isNeedGateway": 1, //是否接入网关 0 否 1 是
             * "connectType": "1", //原networkType如果接入网关该字段表示 协议类型：1、ZigBee 2、蓝牙 3、modbus 4、opc ua 5、lora 6、其他；如果不接入网关 该字段连网方式：1、网线 2、wifi 3、蜂窝 4、以太网 5、其他
             * "wifiNetworkMode": "",
             * "actualIcon": "http://cdn-smht.ayla.com.cn/minip/assets/public/construction/devices/Wired_Gateway(shunzhou).png",//原icon
             * "virtualIcon": "http://cdn-smht.ayla.com.cn/webh5/assets/public/H1/c_Wired_Gateway(shunzhou).png",	//原miya_icon
             * "isPurposeDevice": 0 //是否支持可配置用途 默认0 无 1可配置
             */

            private int id;
            private Map<String, String> cloudModel;
            private String pid;
            private String productModel;
            private String productName;
            /**
             * 产品来源：0 艾拉云 1 阿里云
             */
            private int source;
            private int productType;
            private int isNeedGateway;
            private String connectType;
            //1 网线配网 2 ap配网 3 蓝牙配网
            private String wifiNetworkMode;
            private String actualIcon;
            private String virtualIcon;
            private int isPurposeDevice;

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }


            public Map<String, String> getOemModel() {
                return cloudModel;
            }

            public void setOemModel(Map<String, String> oemModel) {
                this.cloudModel = oemModel;
            }

            public String getPid() {
                return pid;
            }

            public void setPid(String pid) {
                this.pid = pid;
            }

            public String getProductModel() {
                return productModel;
            }

            public void setProductModel(String productModel) {
                this.productModel = productModel;
            }

            public String getProductName() {
                return productName;
            }

            public void setProductName(String productName) {
                this.productName = productName;
            }

            public int getSource() {
                return source;
            }

            public void setSource(int source) {
                this.source = source;
            }

            public int getProductType() {
                return productType;
            }

            public void setProductType(int productType) {
                this.productType = productType;
            }

            public int getIsNeedGateway() {
                return isNeedGateway;
            }

            public void setIsNeedGateway(int isNeedGateway) {
                this.isNeedGateway = isNeedGateway;
            }

            public String getConnectType() {
                return connectType;
            }

            public void setConnectType(String connectType) {
                this.connectType = connectType;
            }

            public String getWifiNetworkMode() {
                return wifiNetworkMode;
            }

            public void setWifiNetworkMode(String wifiNetworkMode) {
                this.wifiNetworkMode = wifiNetworkMode;
            }

            public String getActualIcon() {
                return actualIcon;
            }

            public void setActualIcon(String actualIcon) {
                this.actualIcon = actualIcon;
            }

            public String getVirtualIcon() {
                return virtualIcon;
            }

            public void setVirtualIcon(String virtualIcon) {
                this.virtualIcon = virtualIcon;
            }

            public int getIsPurposeDevice() {
                return isPurposeDevice;
            }

            public void setIsPurposeDevice(int isPurposeDevice) {
                this.isPurposeDevice = isPurposeDevice;
            }
        }
    }
}
