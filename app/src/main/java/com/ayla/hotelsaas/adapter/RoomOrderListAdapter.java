package com.ayla.hotelsaas.adapter;


import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.bean.WorkOrderBean;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;


/**
 * @描述 待办事项条目adapter
 * @作者 丁军伟
 * @时间 2017/8/7
 */
public class RoomOrderListAdapter extends BaseQuickAdapter<WorkOrderBean.WorkOrder, BaseViewHolder> {
    public RoomOrderListAdapter() {
        super(R.layout.adapter_room_order);
    }

    @Override
    protected void convert(BaseViewHolder helper, WorkOrderBean.WorkOrder workOrder) {
        helper.setText(R.id.item_work_status, workOrder.getResourceNum());

    }

}
