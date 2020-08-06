package com.ayla.hotelsaas.adapter;


import android.widget.CompoundButton;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.bean.RuleEngineBean;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;


/**
 * @描述 自动化 的 adapter
 * @作者 fanchunlei
 * @时间 2017/8/7
 */
public class AutoRunRuleEngineAdapter extends BaseQuickAdapter<RuleEngineBean, BaseViewHolder> {
    private OnEnableChangedListener onEnableChangedListener;

    public AutoRunRuleEngineAdapter() {
        super(R.layout.adapter_scene_autorun);
    }

    public void setEnableChangedListener(OnEnableChangedListener onEnableChangedListener) {
        this.onEnableChangedListener = onEnableChangedListener;
    }

    @Override
    protected void convert(BaseViewHolder helper, RuleEngineBean ruleEngineBeans) {
        helper.setText(R.id.tv_device_name, ruleEngineBeans.getRuleName());
        helper.addOnClickListener(R.id.tv_edit);
        helper.setChecked(R.id.sc_enable, ruleEngineBeans.getStatus() == 1);
        helper.setOnCheckedChangeListener(R.id.sc_enable, new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (null != onEnableChangedListener) {
                    if(buttonView.isPressed()){
                        onEnableChangedListener.onCheckedChanged(buttonView, isChecked, helper.getAdapterPosition() - getHeaderLayoutCount());
                    }
                }
            }
        });
    }

    public interface OnEnableChangedListener {
        void onCheckedChanged(CompoundButton buttonView, boolean isChecked, int position);
    }
}
