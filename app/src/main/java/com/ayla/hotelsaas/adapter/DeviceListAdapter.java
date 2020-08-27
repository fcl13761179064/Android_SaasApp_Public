package com.ayla.hotelsaas.adapter;


import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.bean.DeviceListBean;
import com.ayla.hotelsaas.utils.ImageLoader;
import com.ayla.hotelsaas.utils.TempUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;


/**
 * @描述 roomOrderAdapter
 * @作者 fanchunlei
 * @时间 2017/8/7
 */
public class DeviceListAdapter extends BaseQuickAdapter<DeviceListBean.DevicesBean, BaseViewHolder> {
    public DeviceListAdapter() {
        super(R.layout.item_device_categorylist);
    }

    @Override
    protected void convert(BaseViewHolder helper, DeviceListBean.DevicesBean deviceCategory) {
        ImageLoader.loadImg(helper.getView(R.id.device_left_iv), deviceCategory.getIconUrl(), R.drawable.ic_empty_device, R.drawable.ic_empty_device);
        helper.setText(R.id.tv_device_name, deviceCategory.getDeviceName());
        helper.setBackgroundRes(R.id.v_device_online_status_dot, TempUtils.isDeviceOnline(deviceCategory) ? R.drawable.ic_device_online : R.drawable.ic_device_offline);
        helper.setText(R.id.tv_device_online_status, TempUtils.isDeviceOnline(deviceCategory) ? "在线" : "离线");

    }
}
