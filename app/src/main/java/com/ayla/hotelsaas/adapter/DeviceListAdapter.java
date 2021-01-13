package com.ayla.hotelsaas.adapter;


import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.bean.DeviceListBean;
import com.ayla.hotelsaas.utils.ImageLoader;
import com.ayla.hotelsaas.utils.TempUtils;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.entity.MultiItemEntity;

import java.util.List;


/**
 * @描述 roomOrderAdapter
 * @作者 fanchunlei
 * @时间 2017/8/7
 */
public class DeviceListAdapter extends BaseMultiItemQuickAdapter<DeviceListAdapter.DeviceItem, BaseViewHolder> {
    /**
     * Same as QuickAdapter#QuickAdapter(Context,int) but with
     * some initialization data.
     *
     * @param data A new list is created out of this one to avoid mutable list
     */
    public DeviceListAdapter(List<DeviceItem> data) {
        super(data);
        addItemType(DeviceItem.item_normal, R.layout.item_device_list);
        addItemType(DeviceItem.item_wait_add, R.layout.item_device_list_wait);
    }

    @Override
    protected void convert(BaseViewHolder helper, DeviceListAdapter.DeviceItem deviceItem) {
        DeviceListBean.DevicesBean devicesBean = deviceItem.devicesBean;
        if (deviceItem.getItemType() == DeviceItem.item_normal) {
            helper.setBackgroundRes(R.id.v_device_online_status_dot, TempUtils.isDeviceOnline(devicesBean) ? R.drawable.ic_device_online : R.drawable.ic_device_offline);
            helper.setText(R.id.tv_device_online_status, TempUtils.isDeviceOnline(devicesBean) ? "在线" : "离线");
            helper.setText(R.id.tv_device_name, devicesBean.getNickname());
        } else if (deviceItem.getItemType() == DeviceItem.item_wait_add) {
            helper.setText(R.id.tv_sub_2, devicesBean.getDeviceName());
            helper.setText(R.id.tv_device_name, devicesBean.getNickname());
        }
        ImageLoader.loadImg(helper.getView(R.id.device_left_iv), devicesBean.getIconUrl(), R.drawable.ic_empty_device, R.drawable.ic_empty_device);

    }

    public static class DeviceItem implements MultiItemEntity {
        static final int item_normal = 0;
        static final int item_wait_add = 1;

        private DeviceListBean.DevicesBean devicesBean;

        public DeviceItem(DeviceListBean.DevicesBean devicesBean) {
            this.devicesBean = devicesBean;
        }

        public DeviceListBean.DevicesBean getDevicesBean() {
            return devicesBean;
        }

        @Override
        public int getItemType() {
            if (devicesBean.getBindType() == 0) {
                return item_normal;
            } else {
                return item_wait_add;
            }
        }
    }
}
