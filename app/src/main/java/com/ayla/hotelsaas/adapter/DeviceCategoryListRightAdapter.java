package com.ayla.hotelsaas.adapter;

import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.bean.DeviceCategoryBean;
import com.ayla.hotelsaas.utils.ImageLoader;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

/**
 * 设备添加产品列表页面，右侧recyclerview 。
 * <p>
 * DeviceCategoryBean.SubBean.NodeBean[]  因为存在节点设备既可以接入艾拉网关，又可以接入鸿雁网关，
 * 品类列表把这个设备当成两个item返回，APP需要把他们合并成一个现实。
 */
public class DeviceCategoryListRightAdapter extends BaseQuickAdapter<List<DeviceCategoryBean.SubBean.NodeBean>, BaseViewHolder> {
    public DeviceCategoryListRightAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(BaseViewHolder helper, List<DeviceCategoryBean.SubBean.NodeBean> item) {
        helper.setText(R.id.text, item.get(0).getProductName());
        ImageView imageView = helper.getView(R.id.image);
        ImageLoader.loadImg(imageView, item.get(0).getActualIcon(), R.drawable.ic_empty_device, R.drawable.ic_empty_device);
    }
}
