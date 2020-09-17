package com.ayla.hotelsaas.adapter;

import android.text.TextUtils;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.application.MyApplication;
import com.ayla.hotelsaas.bean.DeviceListBean;
import com.ayla.hotelsaas.localBean.BaseSceneBean;
import com.ayla.hotelsaas.utils.ImageLoader;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

/**
 * 场景动作项目
 */
public class SceneSettingActionItemAdapter extends BaseQuickAdapter<BaseSceneBean.Action, BaseViewHolder> {

    public SceneSettingActionItemAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(BaseViewHolder helper, BaseSceneBean.Action action) {
        helper.setText(R.id.tv_function_name, String.format("%s:%s", action.getFunctionName(), action.getValueName()));

        DeviceListBean.DevicesBean devicesBean = null;
        for (DeviceListBean.DevicesBean bean : MyApplication.getInstance().getDevicesBean()) {
            if (TextUtils.equals(bean.getDeviceId(), action.getTargetDeviceId())) {
                devicesBean = bean;
            }
        }
        if (devicesBean != null) {
            ImageLoader.loadImg(helper.getView(R.id.left_iv), devicesBean.getIconUrl(), R.drawable.ic_empty_device, R.drawable.ic_empty_device);
            helper.setText(R.id.tv_name, TextUtils.isEmpty(devicesBean.getNickname()) ? devicesBean.getDeviceId() : devicesBean.getNickname());
        } else {
            helper.setImageResource(R.id.left_iv, R.drawable.ic_empty_device);
            helper.setText(R.id.tv_name, action.getTargetDeviceId());
        }
        helper.addOnClickListener(R.id.tv_delete);
    }
}
