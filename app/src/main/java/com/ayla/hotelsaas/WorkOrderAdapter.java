package com.ayla.hotelsaas;

import android.text.TextUtils;

import com.ayla.hotelsaas.bean.WorkOrderBean;
import com.ayla.hotelsaas.utils.DateUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;


/**
 * @描述 待办事项条目adapter
 * @作者 丁军伟
 * @时间 2017/8/7
 */
public class WorkOrderAdapter extends BaseQuickAdapter<WorkOrderBean, BaseViewHolder> {
    public WorkOrderAdapter() {
        super(R.layout.adapter_work_order);
    }

    @Override
    protected void convert(BaseViewHolder helper, WorkOrderBean item) {
        helper.setText(R.id.item_tv_name, item.getProgramName())
                .setText(R.id.item_work_date, item.getApplyTime())
                .setText(R.id.item_work_status, item.getStatus());

    }

}
