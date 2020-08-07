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

        private String functionName;
        private String valueName;

        private int deviceType;
        private String deviceId;
        private String leftValue;
        private String rightValue;
        private String operator;
        private int rightValueType;

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

        public int getDeviceType() {
            return deviceType;
        }

        public void setDeviceType(int deviceType) {
            this.deviceType = deviceType;
        }

        public String getDeviceId() {
            return deviceId;
        }

        public void setDeviceId(String deviceId) {
            this.deviceId = deviceId;
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
