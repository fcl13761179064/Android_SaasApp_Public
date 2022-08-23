package com.ayla.hotelsaas.adapter;


import android.view.View;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.bean.BaseDevice;
import com.ayla.hotelsaas.bean.DeviceItem;
import com.ayla.hotelsaas.bean.DeviceListBean;
import com.ayla.hotelsaas.bean.GroupItem;
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
public class DeviceListAdapter extends BaseMultiItemQuickAdapter<BaseDevice, BaseViewHolder> {
    /**
     * Same as QuickAdapter#QuickAdapter(Context,int) but with
     * some initialization data.
     *
     * @param data A new list is created out of this one to avoid mutable list
     */
    public DeviceListAdapter(List<BaseDevice> data) {
        super(data);
        addItemType(BaseDevice.item_normal, R.layout.item_device_list);
        addItemType(BaseDevice.item_wait_add, R.layout.item_device_list_wait);
        addItemType(BaseDevice.item_group, R.layout.item_group_list);

    }

    @Override
    protected void convert(BaseViewHolder helper, BaseDevice deviceItem) {
        if (deviceItem instanceof DeviceItem) {
            if (deviceItem.getItemType() == BaseDevice.item_normal) {
                helper.setBackgroundRes(R.id.v_device_online_status_dot, "ONLINE".equals(((DeviceItem) deviceItem).getDeviceStatus()) ? R.drawable.ic_device_online : R.drawable.ic_device_offline);
                helper.setText(R.id.tv_device_online_status, "ONLINE".equals(((DeviceItem) deviceItem).getDeviceStatus()) ? "在线" : "离线");
                helper.setText(R.id.tv_device_name, ((DeviceItem) deviceItem).getNickname());
            } else if (deviceItem.getItemType() == BaseDevice.item_wait_add) {
                helper.setText(R.id.tv_sub_2, ((DeviceItem) deviceItem).getDeviceName());
                helper.setText(R.id.tv_device_name, ((DeviceItem) deviceItem).getNickname());

            }
            ImageLoader.loadImg(helper.getView(R.id.device_left_iv), ((DeviceItem) deviceItem).getIconUrl(), R.drawable.ic_empty_device, R.drawable.ic_empty_device);
        } else if (deviceItem instanceof GroupItem) {
            String connectionStatus = ((GroupItem) deviceItem).getConnectionStatus();
            View view = helper.getView(R.id.tv_sub_1);
            if (view != null) {
                view.setEnabled(true);
            }
            View groupNameView = helper.getView(R.id.tv_group_name);
            if (groupNameView != null) groupNameView.setEnabled(true);

            if (connectionStatus.equals(GroupItem.ConnectStatus.ONLINE.name())) {
                helper.setBackgroundRes(R.id.v_group_online_status_dot, R.drawable.ic_device_online);
                helper.setText(R.id.tv_grouptv_group_online_status_online_status, "在线");
            } else if (connectionStatus.equals(GroupItem.ConnectStatus.PARTLY_ONLINE.name())) {
                helper.setBackgroundRes(R.id.v_group_online_status_dot, R.drawable.ic_device_offline_some);
                helper.setText(R.id.tv_grouptv_group_online_status_online_status, "部分在线");
            } else {
                //离线
                helper.setBackgroundRes(R.id.v_group_online_status_dot, R.drawable.ic_device_offline);
                helper.setText(R.id.tv_grouptv_group_online_status_online_status, "离线");
                if (view != null) {
                    view.setEnabled(false);
                }
                if (groupNameView != null) groupNameView.setEnabled(false);
            }
            helper.setText(R.id.tv_group_name, ((GroupItem) deviceItem).getGroupName());
        }

    }

//    public static class DeviceItem implements MultiItemEntity {
//        static final int item_normal = 0;
//        static final int item_wait_add = 1;
//
//        private DeviceListBean.DevicesBean devicesBean;
//
//        public DeviceItem(DeviceListBean.DevicesBean devicesBean) {
//            this.devicesBean = devicesBean;
//        }
//
//        public DeviceListBean.DevicesBean getDevicesBean() {
//            return devicesBean;
//        }
//
//        @Override
//        public int getItemType() {
//            if (devicesBean.getBindType() == 0) {
//                return item_normal;
//            } else {
//                return item_wait_add;
//            }
//        }
//    }
}
