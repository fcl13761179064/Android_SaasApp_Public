package com.ayla.hotelsaas.adapter;

import android.os.Parcel;
import android.os.Parcelable;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.bean.DeviceListBean;
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
        helper.setText(R.id.tv_function_name, item.getData().getValueName());
    }


    public static class DatumBean implements Parcelable {

        private String functionName;
        private String valueName;

        private int deviceType;
        private String deviceId;
        private String leftValue;
        private String rightValue;
        private String operator;
        private int rightValueType;

        private String iconUrl;

        public String getIconUrl() {
            return iconUrl;
        }

        public void setIconUrl(String iconUrl) {
            this.iconUrl = iconUrl;
        }

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

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.functionName);
            dest.writeString(this.valueName);
            dest.writeInt(this.deviceType);
            dest.writeString(this.deviceId);
            dest.writeString(this.leftValue);
            dest.writeString(this.rightValue);
            dest.writeString(this.operator);
            dest.writeInt(this.rightValueType);
            dest.writeString(this.iconUrl);
        }

        public DatumBean() {
        }

        protected DatumBean(Parcel in) {
            this.functionName = in.readString();
            this.valueName = in.readString();
            this.deviceType = in.readInt();
            this.deviceId = in.readString();
            this.leftValue = in.readString();
            this.rightValue = in.readString();
            this.operator = in.readString();
            this.rightValueType = in.readInt();
            this.iconUrl = in.readString();
        }

        public static final Parcelable.Creator<DatumBean> CREATOR = new Parcelable.Creator<DatumBean>() {
            @Override
            public DatumBean createFromParcel(Parcel source) {
                return new DatumBean(source);
            }

            @Override
            public DatumBean[] newArray(int size) {
                return new DatumBean[size];
            }
        };
    }

}
