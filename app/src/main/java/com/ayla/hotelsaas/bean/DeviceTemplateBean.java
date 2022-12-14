package com.ayla.hotelsaas.bean;

import com.ayla.hotelsaas.ui.activities.ISceneSettingFunctionDatumSet;

import java.io.Serializable;
import java.util.List;

public class DeviceTemplateBean {

    /**
     * version : 15902102452
     * deviceCategory : 9874EW56CF34
     * deviceType : 1
     * deviceNodeType : 1
     * attributes : [{"code":"roomText","displayName":"卧室文本","description":"卧室文本描述","readWriteMode":1,"custom":1,"direction":1,"dataType":1},{"code":"roomSwitch","displayName":"卧室开关状态","description":"卧室开关状态类型bool","readWriteMode":1,"custom":1,"direction":1,"dataType":6,"value":[{"dataType":6,"displayName":"关闭","value":0},{"dataType":6,"displayName":"开启","value":1}]},{"code":"roomEnum","displayName":"卧室枚举","description":"卧室枚举描述","readWriteMode":1,"custom":1,"direction":1,"dataType":7,"value":[{"dataType":7,"displayName":"一楼","value":1},{"dataType":7,"displayName":"二楼","value":2},{"dataType":7,"displayName":"三楼","value":3}]}]
     */

    private String version;
    private String deviceCategory;
    private String pid;
    private String deviceId;
    private long deviceType;//    AYLA(0,"艾拉设备"),    ALI(1,"阿里设备");
    private long deviceNodeType;//    NODE(0,"节点设备"),    GATEWAY(1,"网关设备");
    private List<AttributesBean> attributes;
    private List<EventbutesBean> events;
    private List<AttributesBean> extendAttributes;

    public List<EventbutesBean> getEvents() {
        return events;
    }

    public void setEvents(List<EventbutesBean> events) {
        this.events = events;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getDeviceCategory() {
        return deviceCategory;
    }

    public void setDeviceCategory(String deviceCategory) {
        this.deviceCategory = deviceCategory;
    }

    public long getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(long deviceType) {
        this.deviceType = deviceType;
    }

    public long getDeviceNodeType() {
        return deviceNodeType;
    }

    public void setDeviceNodeType(long deviceNodeType) {
        this.deviceNodeType = deviceNodeType;
    }

    public List<AttributesBean> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<AttributesBean> attributes) {
        this.attributes = attributes;
    }

    public List<AttributesBean> getExtendAttributes() {
        return extendAttributes;
    }

    public void setExtendAttributes(List<AttributesBean> extendAttributes) {
        this.extendAttributes = extendAttributes;
    }

    public static class AttributesBean implements Serializable {
        /**
         * code : roomText
         * displayName : 卧室文本
         * description : 卧室文本描述
         * readWriteMode : 1
         * custom : 1
         * direction : 1
         * dataType : 1
         * value : [{"dataType":6,"displayName":"关闭","value":0},{"dataType":6,"displayName":"开启","value":1}]
         */

        private String code;
        private String displayName;
        private String description;
        private int readWriteMode;//    READ_ONLY(1,"只读"),    READ_WRITE(2,"读写");
        private int custom;//    DEVICE_FIXED(1,"设备固有"),    OEM_CUSTOM(2,"OEM自定义"),    USER_CUSTOM(3,"用户自定义");
        private int direction;//    OUTPUT_ONLY(1,"只上报"),    OUTPUT_INPUT(2,"可设置可上报");
        private int dataType;// TEXT(1, "字符串")，INT(2, "整型")，FLOAT(3, "单精度浮点")，DOUBLE(4, "双精度浮点")，DATE_TIME(5, "时间")，BOOL(6, "布尔")，ENUM(7, "枚举")，ARRAY(8, "数组")，ASSEMBLY(9, "组合")
        private List<ValueBean> value;//property属性
        private SetupBean setup;//步进类型的数据
        private List<BitValueBean> bitValue;//bit位
        private String deviceAttr;
        public ISceneSettingFunctionDatumSet.CallBackBean callBackBean;

        public ISceneSettingFunctionDatumSet.CallBackBean getCallBackBean() {
            return callBackBean;
        }

        public void setCallBackBean(ISceneSettingFunctionDatumSet.CallBackBean callBackBean) {
            this.callBackBean = callBackBean;
        }

        public String getDeviceAttr() {
            return deviceAttr;
        }

        public void setDeviceAttr(String deviceAttr) {
            this.deviceAttr = deviceAttr;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getDisplayName() {
            return displayName;
        }

        public void setDisplayName(String displayName) {
            this.displayName = displayName;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public int getReadWriteMode() {
            return readWriteMode;
        }

        public void setReadWriteMode(int readWriteMode) {
            this.readWriteMode = readWriteMode;
        }

        public int getCustom() {
            return custom;
        }

        public void setCustom(int custom) {
            this.custom = custom;
        }

        public int getDirection() {
            return direction;
        }

        public void setDirection(int direction) {
            this.direction = direction;
        }

        public int getDataType() {
            return dataType;
        }

        public void setDataType(int dataType) {
            this.dataType = dataType;
        }

        public List<ValueBean> getValue() {
            return value;
        }

        public void setValue(List<ValueBean> value) {
            this.value = value;
        }

        public SetupBean getSetup() {
            return setup;
        }

        public void setSetup(SetupBean setup) {
            this.setup = setup;
        }

        public List<BitValueBean> getBitValue() {
            return bitValue;
        }

        public void setBitValue(List<BitValueBean> bitValue) {
            this.bitValue = bitValue;
        }

        public static class ValueBean implements Serializable {
            /**
             * dataType : 6
             * displayName : 关闭
             * value : 0
             */

            private int dataType;// TEXT(1, "字符串")，INT(2, "整型")，FLOAT(3, "单精度浮点")，DOUBLE(4, "双精度浮点")，DATE_TIME(5, "时间")，BOOL(6, "布尔")，ENUM(7, "枚举")，ARRAY(8, "数组")，ASSEMBLY(9, "组合")
            private String displayName;
            private String value;
            private SetupBean setupBean;
            private boolean check;
            private String abilitySubCode;
            private String version;

            public String getVersion() {
                return version;
            }

            public void setVersion(String version) {
                this.version = version;
            }

            public String getAbilitySubCode() {
                return abilitySubCode;
            }

            public void setAbilitySubCode(String abilitySubCode) {
                this.abilitySubCode = abilitySubCode;
            }

            public boolean isCheck() {
                return check;
            }

            public void setCheck(boolean check) {
                this.check = check;
            }

            public SetupBean getSetupBean() {
                return setupBean;
            }

            public void setSetupBean(SetupBean setupBean) {
                this.setupBean = setupBean;
            }

            public int getDataType() {
                return dataType;
            }

            public void setDataType(int dataType) {
                this.dataType = dataType;
            }

            public String getDisplayName() {
                return displayName;
            }

            public void setDisplayName(String displayName) {
                this.displayName = displayName;
            }

            public String getValue() {
                return value;
            }

            public void setValue(String value) {
                this.value = value;
            }
        }

        public static class SetupBean implements Serializable {
            private Double max;
            private Double min;
            private Double step;
            private String unit;

            public Double getMax() {
                return max;
            }

            public void setMax(Double max) {
                this.max = max;
            }

            public Double getMin() {
                return min;
            }

            public void setMin(Double min) {
                this.min = min;
            }

            public Double getStep() {
                return step;
            }

            public void setStep(Double step) {
                this.step = step;
            }

            public String getUnit() {
                return unit;
            }

            public void setUnit(String unit) {
                this.unit = unit;
            }
        }

        public static class BitValueBean implements Serializable {

            /**
             * dataType : 2
             * displayName : 状态正常
             * value : 0
             * bit : 0
             * compareValue : 1
             */

            private int dataType;
            private String displayName;
            private int value;
            private int bit;
            private int compareValue;

            public int getDataType() {
                return dataType;
            }

            public void setDataType(int dataType) {
                this.dataType = dataType;
            }

            public String getDisplayName() {
                return displayName;
            }

            public void setDisplayName(String displayName) {
                this.displayName = displayName;
            }

            public int getValue() {
                return value;
            }

            public void setValue(int value) {
                this.value = value;
            }

            public int getBit() {
                return bit;
            }

            public void setBit(int bit) {
                this.bit = bit;
            }

            public int getCompareValue() {
                return compareValue;
            }

            public void setCompareValue(int compareValue) {
                this.compareValue = compareValue;
            }
        }
    }

    public static class EventbutesBean extends AttributesBean implements Serializable {

        private List<AttributesBean> outParams;

        public List<AttributesBean> getOutParams() {
            return outParams;
        }

        public void setOutParams(List<AttributesBean> outParams) {
            this.outParams = outParams;
        }
    }
}
