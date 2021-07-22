package com.ayla.hotelsaas.adapter;

import androidx.annotation.Nullable;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.bean.TreeListBean;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

public class HelpCenterDetailAdapter extends BaseQuickAdapter<String, BaseViewHolder> {

    public HelpCenterDetailAdapter(@Nullable List<String> data) {
        super(data);
        mLayoutResId = R.layout.item_distribution_hotel;
    }

    @Override
    protected void convert(BaseViewHolder helper, String item) {
        helper.setText(R.id.tv_room_name, item);
    }
}
