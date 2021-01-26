package com.ayla.hotelsaas.adapter;

import android.widget.ImageView;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.bean.DeviceCategoryBean;
import com.ayla.hotelsaas.utils.ImageLoader;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

/**
 * 设备添加产品列表页面，右侧recyclerview 。
 */
public class DeviceCategoryListRightAdapter extends BaseQuickAdapter<DeviceCategoryBean.SubBean.NodeBean, BaseViewHolder> {
    public DeviceCategoryListRightAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(BaseViewHolder helper, DeviceCategoryBean.SubBean.NodeBean item) {
        helper.setText(R.id.text, item.getProductName());
        ImageView imageView = helper.getView(R.id.image);
        ImageLoader.loadImg(imageView,item.getActualIcon(),R.drawable.ic_empty_device,R.drawable.ic_empty_device);
    }
}
