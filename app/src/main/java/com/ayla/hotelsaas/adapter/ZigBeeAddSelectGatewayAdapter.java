package com.ayla.hotelsaas.adapter;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.bean.DeviceListBean;
import com.ayla.hotelsaas.utils.ImageLoader;
import com.ayla.hotelsaas.utils.TempUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

public class ZigBeeAddSelectGatewayAdapter extends BaseQuickAdapter<DeviceListBean.DevicesBean, BaseViewHolder> {

    public ZigBeeAddSelectGatewayAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(BaseViewHolder helper, DeviceListBean.DevicesBean item) {
        ImageLoader.loadImg(helper.getView(R.id.left_iv), item.getIconUrl(), R.drawable.ic_empty_device, R.drawable.ic_empty_device);
        helper.setText(R.id.tv_name, item.getNickname());
        helper.setBackgroundRes(R.id.v_online_status_dot, TempUtils.isDeviceOnline(item) ? R.drawable.ic_device_online : R.drawable.ic_device_offline);
        helper.setText(R.id.tv_online_status, TempUtils.isDeviceOnline(item) ? "在线" : "离线");
        helper.setVisible(R.id.iv_right_arrow, TempUtils.isDeviceOnline(item));
    }
}
