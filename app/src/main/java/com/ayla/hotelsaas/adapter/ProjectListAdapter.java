package com.ayla.hotelsaas.adapter;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.bean.ConstructionBillListBean;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class ProjectListAdapter extends BaseQuickAdapter<ConstructionBillListBean.ResultListBean, BaseViewHolder> {
    SimpleDateFormat sf_source = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
    SimpleDateFormat sf_target = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());

    public ProjectListAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(BaseViewHolder helper, ConstructionBillListBean.ResultListBean item) {
        helper.setText(R.id.tv_title, item.getTitle());
        try {
            helper.setText(R.id.tv_date, String.format("施工日期：%s", sf_target.format(sf_source.parse(item.getStartDate()))));
        } catch (ParseException e) {
            helper.setText(R.id.tv_date, "施工日期：");
            e.printStackTrace();
        }

        String status = null;
        switch (item.getConstructionStatus()) {
            case 1:
                status = "待施工";
                break;
            case 2:
                status = "施工中";
                break;
            case 3:
                status = "已完成";
                break;
        }
        helper.setText(R.id.tv_status, status);

        String trade = null;
        switch (item.getTrade()) {
            case 1:
                trade = "酒店";
                break;
            case 2:
                trade = "家装";
                break;
            case 3:
                trade = "地产";
                break;
            case 4:
                trade = "公寓";
                break;
        }
        helper.setText(R.id.tv_sub_1, trade);

        String type = null;
        switch (item.getTrade()) {
            case 0:
                type = "正式";
                break;
            case 1:
                type = "展箱";
                break;
            case 2:
                type = "展厅";
                break;
        }
        helper.setText(R.id.tv_sub_2, type);
    }

}
