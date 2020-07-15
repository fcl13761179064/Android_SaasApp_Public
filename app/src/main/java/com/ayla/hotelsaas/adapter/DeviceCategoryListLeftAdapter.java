package com.ayla.hotelsaas.adapter;

import android.graphics.Typeface;

import androidx.core.content.ContextCompat;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.bean.DeviceCategoryBean;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

/**
 * 设备添加产品列表页面，左侧recyclerview 。
 */
public class DeviceCategoryListLeftAdapter extends BaseQuickAdapter<DeviceCategoryBean, BaseViewHolder> {
    private int selectedPosition;

    public DeviceCategoryListLeftAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(BaseViewHolder helper, DeviceCategoryBean item) {
        boolean selected = helper.getAdapterPosition() == selectedPosition;
        helper.setText(R.id.text, item.name);
        helper.setTypeface(R.id.text, Typeface.defaultFromStyle(selected ? Typeface.BOLD : Typeface.NORMAL));
        helper.setTextColor(R.id.text, selected ? ContextCompat.getColor(helper.itemView.getContext(), R.color.color_333333) : ContextCompat.getColor(helper.itemView.getContext(), R.color.color_666666));
        helper.setBackgroundRes(R.id.text, selected ? R.color.color_ffffff : android.R.color.transparent);
    }

    public void setSelectedPosition(int selectedPosition) {
        notifyItemChanged(this.selectedPosition);
        this.selectedPosition = selectedPosition;
        notifyItemChanged(this.selectedPosition);
    }

    public int getSelectedPosition() {
        return selectedPosition;
    }
}
