package com.ayla.hotelsaas.adapter;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.bean.WorkOrderBean;
import com.ayla.hotelsaas.feiyansdk.FoundDeviceListItem;
import com.ayla.hotelsaas.utils.TimeUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

/**
 * @描述 adapter
 * @作者 fanchunlei
 * @时间 2020/8/10
 */
public class HongyanGetwayAdapter extends BaseQuickAdapter<FoundDeviceListItem, BaseViewHolder> {
    public HongyanGetwayAdapter() {
        super(R.layout.hongyan_getway_list);
    }

    @Override
    protected void convert(BaseViewHolder helper, FoundDeviceListItem data) {
        helper.setText(R.id.item_tv_name, data.getDeviceName());

    }
}
