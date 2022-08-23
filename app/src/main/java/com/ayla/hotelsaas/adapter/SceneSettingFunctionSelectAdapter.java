package com.ayla.hotelsaas.adapter;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.bean.DeviceTemplateBean;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

/**
 * 场景功能选择
 */
public class SceneSettingFunctionSelectAdapter extends BaseQuickAdapter<DeviceTemplateBean.AttributesBean, BaseViewHolder> {
    public SceneSettingFunctionSelectAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(BaseViewHolder helper, DeviceTemplateBean.AttributesBean item) {
        helper.setText(R.id.tv_function_name, item.getDisplayName());
        if (item.getDeviceAttr()!=null){
            helper.setText(R.id.tv_value, item.getDeviceAttr());
        }else {
            helper.setText(R.id.tv_value, "");
        }
    }
}
