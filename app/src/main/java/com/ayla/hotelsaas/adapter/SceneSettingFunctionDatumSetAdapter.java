package com.ayla.hotelsaas.adapter;

import com.ayla.hotelsaas.R;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.io.Serializable;

/**
 * 场景功能动作选择，单选
 */
public class SceneSettingFunctionDatumSetAdapter extends BaseQuickAdapter<CheckableSupport<SceneSettingFunctionDatumSetAdapter.DatumBean>, BaseViewHolder> {
    public SceneSettingFunctionDatumSetAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(BaseViewHolder helper, CheckableSupport<DatumBean> item) {
        helper.setChecked(R.id.cb_function_checked, item.isChecked());
        helper.setText(R.id.tv_function_name,item.getData().getValueName());
    }


    public static class DatumBean implements Serializable {

        /**
         * targetDeviceType : 2
         * targetDeviceId : GADw3NnUI4Xa54nsr5tYz20000
         * leftValue : StatusLightSwitch
         * rightValueType : 1
         * rightValue : 1
         * operator : ==
         */
        private String functionName;
        private String valueName;
        private int targetDeviceType;
        private String targetDeviceId;
        private String leftValue;
        private int rightValueType;
        private String rightValue;
        private String operator;

        public String getFunctionName() {
            return functionName;
        }

        public void setFunctionName(String functionName) {
            this.functionName = functionName;
        }

        public String getValueName() {
            return valueName;
        }

        public void setValueName(String valueName) {
            this.valueName = valueName;
        }

        public int getTargetDeviceType() {
            return targetDeviceType;
        }

        public void setTargetDeviceType(int targetDeviceType) {
            this.targetDeviceType = targetDeviceType;
        }

        public String getTargetDeviceId() {
            return targetDeviceId;
        }

        public void setTargetDeviceId(String targetDeviceId) {
            this.targetDeviceId = targetDeviceId;
        }

        public String getLeftValue() {
            return leftValue;
        }

        public void setLeftValue(String leftValue) {
            this.leftValue = leftValue;
        }

        public int getRightValueType() {
            return rightValueType;
        }

        public void setRightValueType(int rightValueType) {
            this.rightValueType = rightValueType;
        }

        public String getRightValue() {
            return rightValue;
        }

        public void setRightValue(String rightValue) {
            this.rightValue = rightValue;
        }

        public String getOperator() {
            return operator;
        }

        public void setOperator(String operator) {
            this.operator = operator;
        }
    }

}
