package com.ayla.hotelsaas.adapter;

import com.ayla.hotelsaas.R;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

/**
 * 场景功能动作选择，单选
 */
public class MultiDevicePositionSettingAdapter extends BaseQuickAdapter<CheckableSupport<String>, BaseViewHolder> {
    public MultiDevicePositionSettingAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(BaseViewHolder helper, CheckableSupport<String> item) {
        helper.setChecked(R.id.cb_function_checked, item.isChecked());
        helper.setText(R.id.tv_function_name, item.getData());
    }
}
