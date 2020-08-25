package com.ayla.hotelsaas.adapter;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.bean.DeviceTemplateBean;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

public class FunctionRenameListAdapter extends BaseQuickAdapter<FunctionRenameListAdapter.Bean, BaseViewHolder> {
    public FunctionRenameListAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(BaseViewHolder helper, FunctionRenameListAdapter.Bean item) {
        helper.setText(R.id.tv_function_name, item.getDisplayName());
        helper.setText(R.id.tv_nickname, item.getPropertyValue());
    }

    public static class Bean{
        private String code;
        private String displayName;
        private int id;
        private String propertyValue;

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

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getPropertyValue() {
            return propertyValue;
        }

        public void setPropertyValue(String propertyValue) {
            this.propertyValue = propertyValue;
        }
    }

}
