package com.ayla.hotelsaas.ui;

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
        private String targetValue;
        private DeviceTemplateBean.AttributesBean.SetupBean setupBean;

        public SetupCallBackBean(String operator, String targetValue, DeviceTemplateBean.AttributesBean.SetupBean setupBean) {
            super(operator);
            this.targetValue = targetValue;
            this.setupBean = setupBean;
        }

        public String getTargetValue() {
            return targetValue;
        }

        public DeviceTemplateBean.AttributesBean.SetupBean getSetupBean() {
            return setupBean;
        }
    }
}
