package com.ayla.hotelsaas.adapter;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.bean.DeviceTemplateBean;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

/**
 * 场景功能动作选择，单选
 */
public class SceneSettingFunctionEventSetAdapter extends BaseQuickAdapter<CheckableSupport<DeviceTemplateBean.EventbutesBean>, BaseViewHolder> {
    public SceneSettingFunctionEventSetAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(BaseViewHolder helper, CheckableSupport<DeviceTemplateBean.EventbutesBean> item) {
        helper.setChecked(R.id.cb_function_checked, item.isChecked());
        if (item!=null && item.getData()!=null && item.getData().getDisplayName() !=null){
            helper.setText(R.id.tv_function_name, item.getData().getDisplayName());
        }else {
            helper.setText(R.id.tv_function_name, "未知");
        }

    }
}
