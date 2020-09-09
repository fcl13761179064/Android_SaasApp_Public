package com.ayla.hotelsaas.adapter;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.bean.WorkOrderBean;
import com.ayla.hotelsaas.utils.TimeUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

/**
 * @描述 待办事项条目adapter
 * @作者 丁军伟
 * @时间 2017/8/7
 */
public class WorkOrderAdapter extends BaseQuickAdapter<WorkOrderBean.ResultListBean, BaseViewHolder> {
    public WorkOrderAdapter() {
        super(R.layout.adapter_work_order);
    }

    @Override
    protected void convert(BaseViewHolder helper, WorkOrderBean.ResultListBean workOrder) {
        helper.setText(R.id.item_tv_name, workOrder.getTitle())
                .setText(R.id.item_work_srart_date, TimeUtils.getYYMMdd(workOrder.getStartDate()))
                .setText(R.id.item_work_end_date, TimeUtils.getYYMMdd(workOrder.getEndDate()));

        workOrder.setStartDate(TimeUtils.getYYMMdd(workOrder.getStartDate()));
        workOrder.setEndDate(TimeUtils.getYYMMdd(workOrder.getEndDate()));
        if (workOrder.getConstructionStatus() == 1) {
            helper.setText(R.id.item_work_status, "待施工");
        } else if (workOrder.getConstructionStatus() == 2) {
            helper.setText(R.id.item_work_status, "施工");
        }
    }
}
