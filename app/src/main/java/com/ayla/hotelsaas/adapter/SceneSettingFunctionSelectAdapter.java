package com.ayla.hotelsaas.adapter;

import com.ayla.hotelsaas.R;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

/**
 * 场景功能选择
 */
public class SceneSettingFunctionSelectAdapter extends BaseQuickAdapter<String, BaseViewHolder> {
    public SceneSettingFunctionSelectAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(BaseViewHolder helper, String item) {
        helper.setText(R.id.tv_function_name, item);
    }
}
