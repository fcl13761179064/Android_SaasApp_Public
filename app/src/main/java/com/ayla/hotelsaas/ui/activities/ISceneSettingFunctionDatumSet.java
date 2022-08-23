package com.ayla.hotelsaas.ui.activities;

import com.ayla.hotelsaas.bean.DeviceTemplateBean;

import java.io.Serializable;

public interface ISceneSettingFunctionDatumSet {
    CallBackBean getDatum();

    abstract class CallBackBean implements Serializable {
        public CallBackBean(String operator) {
            this.operator = operator;
        }

        private String operator;

        public String getOperator() {
            return operator;
        }
    }

    class ValueCallBackBean extends CallBackBean {

        private DeviceTemplateBean.AttributesBean.ValueBean valueBean;

        public ValueCallBackBean(DeviceTemplateBean.AttributesBean.ValueBean valueBean) {
            super("==");
            this.valueBean = valueBean;
        }

        public DeviceTemplateBean.AttributesBean.ValueBean getValueBean() {
            return valueBean;
        }
    }

    class BitValueCallBackBean extends CallBackBean {

        private DeviceTemplateBean.AttributesBean.BitValueBean bitValueBean;

        public BitValueCallBackBean(DeviceTemplateBean.AttributesBean.BitValueBean bitValueBean) {
            super("==");
            this.bitValueBean = bitValueBean;
        }

        public DeviceTemplateBean.AttributesBean.BitValueBean getBitValueBean() {
            return bitValueBean;
        }
    }

    class EventCallBackBean extends CallBackBean {

        private final DeviceTemplateBean.AttributesBean eventBean;

        public EventCallBackBean(DeviceTemplateBean.AttributesBean eventBean) {
            super("==");
            this.eventBean = eventBean;
        }

        public DeviceTemplateBean.AttributesBean getEvnetBean() {
            return eventBean;
        }
    }

    class SetupCallBackBean extends CallBackBean {
        private  String unit;
        private String targetValue;
        private DeviceTemplateBean.AttributesBean.SetupBean setupBean;
        private String displayName;
        private String abilitySubCode;
        private String version;

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public SetupCallBackBean(String operator, String targetValue, DeviceTemplateBean.AttributesBean.SetupBean setupBean, String unit) {
            super(operator);
            this.targetValue = targetValue;
            this.setupBean = setupBean;
            this.unit = unit;
        }

        public String getAbilitySubCode() {
            return abilitySubCode;
        }

        public void setAbilitySubCode(String abilitySubCode) {
            this.abilitySubCode = abilitySubCode;
        }

        public void setUnit(String unit) {
            this.unit = unit;
        }

        public String getDisplayName() {
            return displayName;
        }

        public void setDisplayName(String displayName) {
            this.displayName = displayName;
        }

        public void setTargetValue(String targetValue) {
            this.targetValue = targetValue;
        }

        public String getTargetValue() {
            return targetValue;
        }

        public String getUnit() {
            return unit;
        }
        public DeviceTemplateBean.AttributesBean.SetupBean getSetupBean() {
            return setupBean;
        }
    }
}
