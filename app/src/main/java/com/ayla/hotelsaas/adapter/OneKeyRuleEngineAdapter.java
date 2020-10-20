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
        super(R.layout.adapter_scene_first);
    }

    @Override
    protected void convert(BaseViewHolder helper, BaseSceneBean ruleEngineBeans) {
        helper.setText(R.id.tv_device_name, ruleEngineBeans.getRuleName());
        helper.addOnClickListener(R.id.tv_edit);
    }
}
