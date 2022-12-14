package com.ayla.hotelsaas.adapter;


import android.widget.CompoundButton;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.bean.scene_bean.BaseSceneBean;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;


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
        if (ruleEngineBeans.getSiteType() == 1) {
            helper.setText( R.id.tv_local_remove,"本地");
        } else {
            helper.setText( R.id.tv_local_remove,"云端");
        }
        if (helper.getAdapterPosition() % 2 == 0) {
            helper.setImageResource(R.id.iv_bg, R.drawable.bg_scene_autorun_a);
        } else {
            helper.setImageResource(R.id.iv_bg, R.drawable.bg_scene_autorun_b);
        }

        if (ruleEngineBeans.getStatus() == 0 || ruleEngineBeans.getStatus() == 1) {
            helper.setVisible(R.id.sc_enable, true);
            helper.setGone(R.id.tv_sub_1, false);
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
            helper.setGone(R.id.tv_sub_1, true);
            helper.setVisible(R.id.iv_ill_state, true);
        }
    }

    public interface OnEnableChangedListener {
        void onCheckedChanged(CompoundButton buttonView, boolean isChecked, int position);
    }
}
