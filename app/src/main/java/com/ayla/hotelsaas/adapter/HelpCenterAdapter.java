package com.ayla.hotelsaas.adapter;


import android.widget.CompoundButton;

import com.ayla.hotelsaas.R;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;


/**
 * @描述 帮助中心 的 adapter
 * @作者 fanchunlei
 * @时间 2021/8/3
 */
public class HelpCenterAdapter extends BaseQuickAdapter<String, BaseViewHolder> {
    private OnEnableChangedListener onEnableChangedListener;

    public HelpCenterAdapter() {
        super(R.layout.adapter_help_center);
    }


    @Override
    protected void convert(BaseViewHolder helper, String tv_help_desc) {
        helper.setText(R.id.tv_help_desc, tv_help_desc);
    }

    public interface OnEnableChangedListener {
        void onCheckedChanged(CompoundButton buttonView, boolean isChecked, int position);
    }
}
