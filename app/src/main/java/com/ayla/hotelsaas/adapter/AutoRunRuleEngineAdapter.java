package com.ayla.hotelsaas.adapter;


import android.widget.CompoundButton;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.localBean.BaseSceneBean;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.entity.MultiItemEntity;


/**
 * @描述 自动化 的 adapter
 * @作者 fanchunlei
 * @时间 2017/8/7
 */
public class AutoRunRuleEngineAdapter extends BaseQuickAdapter<BaseSceneBean, BaseViewHolder> {
    private OnEnableChangedListener onEnableChangedListener;

    public AutoRunRuleEngineAdapter() {
        super(R.layout.adapter_scene_autorun);
    }

    public void setEnableChangedListener(OnEnableChangedListener onEnableChangedListener) {
        this.onEnableChangedListener = onEnableChangedListener;
    }

    @Override
    protected void convert(BaseViewHolder helper, BaseSceneBean ruleEngineBeans) {
        helper.setText(R.id.tv_device_name, ruleEngineBeans.getRuleName());

        if (ruleEngineBeans.getStatus() == 0 || ruleEngineBeans.getStatus() == 1) {
            helper.setVisible(R.id.sc_enable, true);
            helper.setVisible(R.id.tv_sub_1, false);
            helper.setVisible(R.id.iv_ill_state, false);
            helper.setChecked(R.id.sc_enable, ruleEngineBeans.getStatus() == 1);
            helper.setOnCheckedChangeListener(R.id.sc_enable, new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (null != onEnableChangedListener) {
                        if (buttonView.isPressed()) {
                            onEnableChangedListener.onCheckedChanged(buttonView, isChecked, helper.getAdapterPosition() - getHeaderLayoutCount());
                        }
                    }
                }
            });
        } else {
            helper.setVisible(R.id.sc_enable, false);
            helper.setVisible(R.id.tv_sub_1, true);
            helper.setVisible(R.id.iv_ill_state, true);
        }
    }

    public interface OnEnableChangedListener {
        void onCheckedChanged(CompoundButton buttonView, boolean isChecked, int position);
    }
}
