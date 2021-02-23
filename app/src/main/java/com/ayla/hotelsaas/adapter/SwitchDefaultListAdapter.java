package com.ayla.hotelsaas.adapter;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.bean.DeviceTemplateBean;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

public class SwitchDefaultListAdapter extends BaseQuickAdapter<SwitchDefaultListAdapter.Bean, BaseViewHolder> {


    public SwitchDefaultListAdapter() {
        super(R.layout.item_switch_default_list, null);
    }

    @Override
    protected void convert(BaseViewHolder helper, SwitchDefaultListAdapter.Bean item) {
        helper.setText(R.id.tv_function_name, item.propertyName);
    }

    public static class Bean extends DeviceTemplateBean.AttributesBean {
        private String propertyCode;
        private String propertyName;
        private String propertyValue;

        public void setPropertyCode(String propertyCode) {
            this.propertyCode = propertyCode;
        }

        public void setPropertyName(String propertyName) {
            this.propertyName = propertyName;
        }

        public void setPropertyValue(String propertyValue) {
            this.propertyValue = propertyValue;
        }

        public String getPropertyCode() {
            return propertyCode;
        }

        public String getPropertyName() {
            return propertyName;
        }

        public String getPropertyValue() {
            return propertyValue;
        }
    }
}
