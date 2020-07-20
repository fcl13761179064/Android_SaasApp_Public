package com.ayla.hotelsaas.adapter;


import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.bean.DeviceListBean;
import com.ayla.hotelsaas.bean.RoomOrderBean;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;


/**
 * @描述 roomOrderAdapter
 * @作者 fanchunlei
 * @时间 2017/8/7
 */
public class DeviceListAdapter extends BaseQuickAdapter<DeviceListBean.RoomOrder, BaseViewHolder> {
    public DeviceListAdapter() {
        super(R.layout.item_device_categorylist);
    }

    @Override
    protected void convert(BaseViewHolder helper, DeviceListBean.RoomOrder roomOrder) {
        helper.setBackgroundRes(R.id.device_left_iv, R.drawable.circle);
        helper.setText(R.id.tv_device_name,"22222");
        helper.setBackgroundColor(R.id.v_device_online_status_dot, R.drawable.circle);
        helper.setText(R.id.tv_device_online_status, "离线");
    }

}
