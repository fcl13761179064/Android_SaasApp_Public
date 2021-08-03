package com.ayla.hotelsaas.adapter;


import android.widget.CompoundButton;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.localBean.BaseSceneBean;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;


/**
 * @描述 自动化 的 adapter
 * @作者 fanchunlei
 * @时间 2017/8/7
 */
public class HelpCenterAdapter extends BaseQuickAdapter<String, BaseViewHolder> {
    private OnEnableChangedListener onEnableChangedListener;

    public HelpCenterAdapter() {
        super(R.layout.adapter_scene_autorun);
    }


    @Override
    protected void convert(BaseViewHolder helper, String ruleEngineBeans) {
        helper.setText(R.id.tv_device_name, ruleEngineBeans);
    }

    public interface OnEnableChangedListener {
        void onCheckedChanged(CompoundButton buttonView, boolean isChecked, int position);
    }
}
