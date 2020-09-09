package com.ayla.hotelsaas.adapter;

import androidx.annotation.Nullable;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.bean.HotelListBean;
import com.ayla.hotelsaas.bean.TreeListBean;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

public class DistributionStructAdapter extends BaseQuickAdapter<TreeListBean, BaseViewHolder> {

    public DistributionStructAdapter(@Nullable List<TreeListBean> data) {
        super(data);
        mLayoutResId = R.layout.item_distribution_hotel;
    }

    @Override
    protected void convert(BaseViewHolder helper, TreeListBean item) {
        helper.setText(R.id.tv_room_name, item.getContentName());
    }
}
