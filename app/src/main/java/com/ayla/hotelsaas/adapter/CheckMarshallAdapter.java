package com.ayla.hotelsaas.adapter;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.bean.GroupDeviceItem;
import com.ayla.hotelsaas.utils.ImageLoader;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

/**
 * 查看子设备、
 * chunlei.fan
 */
public class CheckMarshallAdapter extends BaseQuickAdapter<GroupDeviceItem, BaseViewHolder> {
    public CheckMarshallAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(BaseViewHolder helper, GroupDeviceItem item) {
        helper.setVisible(R.id. iv_delete_device, false);
        helper.setText(R.id.tv_device_name, item.getNickname());
        helper.setText(R.id.tv_device_regeinName, item.getRegionName());
        ImageLoader.loadImg(helper.getView(R.id.device_left_iv), item.getActualIcon(), R.drawable.ic_empty_device, R.drawable.ic_empty_device);
    }
}
