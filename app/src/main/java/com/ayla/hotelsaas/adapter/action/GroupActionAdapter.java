package com.ayla.hotelsaas.adapter.action;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.bean.DeviceTemplateBean;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

/**
 * 场景功能动作选择，单选
 */
public class GroupActionAdapter extends BaseQuickAdapter<DeviceTemplateBean.AttributesBean.ValueBean, BaseViewHolder> {
    public GroupActionAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(BaseViewHolder helper, DeviceTemplateBean.AttributesBean.ValueBean item) {
        if (helper.getView(R.id.cb_function_checked) != null) {
            helper.getView(R.id.cb_function_checked).setSelected(item.isCheck());
        }
        helper.setText(R.id.tv_function_name, item.getDisplayName());
    }
}
