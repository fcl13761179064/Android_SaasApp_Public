package com.ayla.hotelsaas.adapter;

import com.ayla.hotelsaas.R;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.Map;

/**
 * @描述 adapter
 * @作者 fanchunlei
 * @时间 2020/8/10
 */
public class HongyanGetwayAdapter extends BaseQuickAdapter<Map<String, String>, BaseViewHolder> {
    public HongyanGetwayAdapter() {
        super(R.layout.hongyan_getway_list);
    }

    @Override
    protected void convert(BaseViewHolder helper, Map<String, String> data) {
        helper.setText(R.id.item_tv_name, data.get("deviceName"));

    }
}
