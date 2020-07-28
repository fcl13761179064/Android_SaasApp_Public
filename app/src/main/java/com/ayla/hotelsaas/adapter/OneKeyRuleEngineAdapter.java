package com.ayla.hotelsaas.adapter;


import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.bean.DeviceListBean;
import com.ayla.hotelsaas.bean.RuleEngineBean;
import com.ayla.hotelsaas.utils.ImageLoader;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;


/**
 * @描述 一键联动 的 adapter
 * @作者 fanchunlei
 * @时间 2017/8/7
 */
public class OneKeyRuleEngineAdapter extends BaseQuickAdapter<RuleEngineBean, BaseViewHolder> {
    public OneKeyRuleEngineAdapter() {
        super(R.layout.adapter_scene_first);
    }

    @Override
    protected void convert(BaseViewHolder helper, RuleEngineBean ruleEngineBeans) {
        helper.setText(R.id.tv_device_name, ruleEngineBeans.getRuleName());
        helper.addOnClickListener(R.id.rl_edit_btn);
    }
}
