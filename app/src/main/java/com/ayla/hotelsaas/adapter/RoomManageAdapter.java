package com.ayla.hotelsaas.adapter;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.bean.RoomManageBean;
import com.ayla.hotelsaas.bean.RoomOrderBean;
import com.ayla.hotelsaas.bean.WorkOrderBean;
import com.ayla.hotelsaas.utils.TimeUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

/**
 * @描述 待办事项条目adapter
 * @作者 fanchunlei
 * @时间 2020/8/24
 */
public class RoomManageAdapter extends BaseQuickAdapter<RoomManageBean.RecordsBean, BaseViewHolder> {
    public RoomManageAdapter() {
        super(R.layout.adapter_room_manage);
    }

    @Override
    protected void convert(BaseViewHolder helper, RoomManageBean.RecordsBean roomManage) {
        helper.setText(R.id.tv_room_show_name, roomManage.getContentName());
    }
}
