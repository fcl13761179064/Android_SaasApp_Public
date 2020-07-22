package com.ayla.hotelsaas.adapter;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.bean.Device;
import com.ayla.hotelsaas.bean.DeviceListBean;
import com.ayla.hotelsaas.utils.ImageLoader;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

public class SceneSettingDeviceSelectAdapter extends BaseQuickAdapter<DeviceListBean.DevicesBean, BaseViewHolder> {

    public SceneSettingDeviceSelectAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(BaseViewHolder helper, DeviceListBean.DevicesBean item) {
        ImageLoader.loadImg(helper.getView(R.id.left_iv), "", R.drawable.ic_empty_device, R.drawable.ic_empty_device);
        helper.setText(R.id.tv_name, item.getDeviceName());
        helper.setBackgroundRes(R.id.v_online_status_dot, "online".equals(item.getDeviceStatus()) ? R.drawable.ic_device_online : R.drawable.ic_device_offline);
        helper.setText(R.id.tv_online_status, "online".equals(item.getDeviceStatus()) ? "在线" : "离线");
    }
}
