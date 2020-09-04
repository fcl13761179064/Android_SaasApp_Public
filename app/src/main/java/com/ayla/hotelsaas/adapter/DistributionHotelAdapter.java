package com.ayla.hotelsaas.adapter;

import androidx.annotation.Nullable;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.bean.RoomManageBean;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

public class DistributionHotelAdapter extends BaseQuickAdapter<CheckableSupport<RoomManageBean.RecordsBean>, BaseViewHolder> {
    public DistributionHotelAdapter(@Nullable List data) {
        super(data);
        mLayoutResId = R.layout.item_distribution_room;
    }

    @Override
    protected void convert(BaseViewHolder helper, CheckableSupport<RoomManageBean.RecordsBean> item) {
        helper.setText(R.id.tv_room_name, item.getData().getContentName());
        helper.setChecked(R.id.cb, item.isChecked());
    }
}
