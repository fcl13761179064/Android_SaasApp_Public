package com.ayla.hotelsaas.adapter;


import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.localBean.BaseSceneBean;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;


/**
 * @描述 一键联动 的 adapter
 * @作者 fanchunlei
 * @时间 2017/8/7
 */
public class OneKeyRuleEngineAdapter extends BaseQuickAdapter<BaseSceneBean, BaseViewHolder> {
    public OneKeyRuleEngineAdapter() {
        super(R.layout.adapter_scene_onekey);
    }

    @Override
    protected void convert(BaseViewHolder helper, BaseSceneBean ruleEngineBeans) {
        helper.setText(R.id.tv_device_name, ruleEngineBeans.getRuleName());
        helper.addOnClickListener(R.id.iv_more);

        if (ruleEngineBeans.getStatus() == 1) {
            helper.setVisible(R.id.ll_enable_state, true);
            helper.setVisible(R.id.tv_sub_1, false);
        } else {
            helper.setVisible(R.id.ll_enable_state, false);
            helper.setVisible(R.id.tv_sub_1, true);
        }
    }
}
