package com.ayla.hotelsaas.adapter.local_scene;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.bean.DeviceListBean;
import com.ayla.hotelsaas.utils.ImageLoader;
import com.ayla.hotelsaas.utils.TempUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

public class LocalSceneSelectGatewayAdapter extends BaseQuickAdapter<DeviceListBean.DevicesBean, BaseViewHolder> {

    public LocalSceneSelectGatewayAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(BaseViewHolder helper, DeviceListBean.DevicesBean item) {
        ImageLoader.loadImg(helper.getView(R.id.device_left_iv), item.getIconUrl(), R.drawable.ic_empty_device, R.drawable.ic_empty_device);
        helper.setText(R.id.tv_device_name, item.getNickname());
        boolean deviceOnline = TempUtils.isDeviceOnline(item);
        helper.setGone(R.id.offline_state, !deviceOnline);
        helper.getView(R.id.tv_device_name).setEnabled(deviceOnline);
        helper.setText(R.id.room_name, item.getRegionName());
        helper.getView(R.id.room_name).setEnabled(deviceOnline);
    }

}
