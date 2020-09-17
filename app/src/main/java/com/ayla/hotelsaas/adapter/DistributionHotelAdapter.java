package com.ayla.hotelsaas.adapter;

import androidx.annotation.Nullable;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.bean.HotelListBean;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

public class DistributionHotelAdapter extends BaseQuickAdapter<HotelListBean.RecordsBean, BaseViewHolder> {

    public DistributionHotelAdapter(@Nullable List<HotelListBean.RecordsBean> data) {
        super(data);
        mLayoutResId = R.layout.item_distribution_hotel;
    }

    @Override
    protected void convert(BaseViewHolder helper, HotelListBean.RecordsBean item) {
        helper.setText(R.id.tv_room_name, item.getHotelName());
    }
}
