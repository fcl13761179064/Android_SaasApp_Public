package com.ayla.hotelsaas.ui;

import java.io.Serializable;

interface ISceneSettingFunctionDatumSet {
    CallBackBean getDatum();

    class CallBackBean implements Serializable {
        private String functionName;
        private String valueName;

        private int deviceType;
        private String deviceId;
        private String leftValue;
        private String rightValue;
        private String operator;
        private int rightValueType;

        private String iconUrl;

        public void setFunctionName(String functionName) {
            this.functionName = functionName;
        }

        public void setValueName(String valueName) {
            this.valueName = valueName;
        }

        public void setDeviceType(int deviceType) {
            this.deviceType = deviceType;
        }

        public void setDeviceId(String deviceId) {
            this.deviceId = deviceId;
        }

        public void setLeftValue(String leftValue) {
            this.leftValue = leftValue;
        }

        public void setRightValue(String rightValue) {
            this.rightValue = rightValue;
        }

        public void setOperator(String operator) {
            this.operator = operator;
        }

        public void setRightValueType(int rightValueType) {
            this.rightValueType = rightValueType;
        }

        public void setIconUrl(String iconUrl) {
            this.iconUrl = iconUrl;
        }

        public String getFunctionName() {
            return functionName;
        }

        public String getValueName() {
            return valueName;
        }

        public int getDeviceType() {
            return deviceType;
        }

        public String getDeviceId() {
            return deviceId;
        }

        public String getLeftValue() {
            return leftValue;
        }

        public String getRightValue() {
            return rightValue;
        }

        public String getOperator() {
            return operator;
        }

        public int getRightValueType() {
            return rightValueType;
        }

        public String getIconUrl() {
            return iconUrl;
        }
    }
}
