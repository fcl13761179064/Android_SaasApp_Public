package com.ayla.hotelsaas.adapter;

import com.ayla.hotelsaas.R;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

/**
 * 场景功能动作选择，单选
 */
public class SceneSettingFunctionDatumSetAdapter extends BaseQuickAdapter<CheckableSupport<String>, BaseViewHolder> {
    public SceneSettingFunctionDatumSetAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(BaseViewHolder helper, CheckableSupport<String> item) {
        helper.getView(R.id.cb_function_checked).setSelected( item.isChecked());
        helper.setText(R.id.tv_function_name, item.getData());
    }
}
