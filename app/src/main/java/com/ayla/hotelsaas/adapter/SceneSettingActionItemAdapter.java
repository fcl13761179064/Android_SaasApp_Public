package com.ayla.hotelsaas.adapter;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.bean.Device;
import com.ayla.hotelsaas.utils.ImageLoader;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

/**
 *
 */
public class SceneSettingActionItemAdapter extends BaseQuickAdapter<Device, BaseViewHolder> {

    public SceneSettingActionItemAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(BaseViewHolder helper, Device item) {
        ImageLoader.loadImg(helper.getView(R.id.left_iv), item.getIcon(), R.drawable.ic_empty_device, R.drawable.ic_empty_device);
        helper.setText(R.id.tv_name, item.getName());
        helper.setBackgroundRes(R.id.v_online_status_dot, "online".equals(item.getOnlineStatus()) ? R.drawable.ic_device_online : R.drawable.ic_device_offline);
        helper.setText(R.id.tv_online_status, "online".equals(item.getOnlineStatus()) ? "在线" : "离线");
    }
}
