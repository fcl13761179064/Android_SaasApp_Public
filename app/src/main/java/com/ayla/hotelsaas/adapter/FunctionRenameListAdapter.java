package com.ayla.hotelsaas.adapter;

import android.text.TextUtils;

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
        helper.setText(R.id.tv_function_name, item.propertyName);
        if (TextUtils.isEmpty(item.getNickNameId())) {
            helper.setText(R.id.tv_nickname, item.propertyName);
        }else{
            helper.setText(R.id.tv_nickname, item.propertyNickname);
        }
    }

    public static class Bean{
        private String propertyCode;
        private String propertyName;
        private String propertyNickname;
        private String nickNameId;

        public String getPropertyCode() {
            return propertyCode;
        }

        public void setPropertyCode(String propertyCode) {
            this.propertyCode = propertyCode;
        }

        public void setPropertyName(String propertyName) {
            this.propertyName = propertyName;
        }

        public void setPropertyNickname(String propertyNickname) {
            this.propertyNickname = propertyNickname;
        }

        public void setNickNameId(String nickNameId) {
            this.nickNameId = nickNameId;
        }

        public String getPropertyName() {
            return propertyName;
        }

        public String getPropertyNickname() {
            return propertyNickname;
        }

        public String getNickNameId() {
            return nickNameId;
        }
    }
}
