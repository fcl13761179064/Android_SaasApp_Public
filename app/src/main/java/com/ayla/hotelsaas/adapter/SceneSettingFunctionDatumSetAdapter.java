package com.ayla.hotelsaas.adapter;

import com.ayla.hotelsaas.R;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

/**
 * 场景动作选择，单选
 */
public class SceneSettingFunctionDatumSetAdapter extends BaseQuickAdapter<CheckableSupport<String>, BaseViewHolder> {
    public SceneSettingFunctionDatumSetAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(BaseViewHolder helper, CheckableSupport item) {
        helper.setChecked(R.id.cb_function_checked, item.isChecked());
    }

}
