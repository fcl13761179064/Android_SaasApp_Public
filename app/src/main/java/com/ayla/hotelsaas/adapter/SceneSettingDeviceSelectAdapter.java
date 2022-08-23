package com.ayla.hotelsaas.adapter;

import android.text.TextUtils;
import android.view.View;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.bean.DeviceListBean;
import com.ayla.hotelsaas.utils.ImageLoader;
import com.ayla.hotelsaas.utils.TempUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

/**
 * 场景设备选择
 */
public class SceneSettingDeviceSelectAdapter extends BaseQuickAdapter<DeviceListBean.DevicesBean, BaseViewHolder> {

    public SceneSettingDeviceSelectAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(BaseViewHolder helper, DeviceListBean.DevicesBean item) {
        if (helper.getAdapterPosition()==0){
           helper.getView(R.id.tv_desc).setVisibility(View.VISIBLE);
        }else {
            helper.getView(R.id.tv_desc).setVisibility(View.GONE);
        }
        if ("ONLINE".equalsIgnoreCase(item.getDeviceStatus())){
            helper.getView(R.id.offline_state).setVisibility(View.GONE);
        }else {
            helper.getView(R.id.offline_state).setVisibility(View.VISIBLE);
        }
        ImageLoader.loadImg(helper.getView(R.id.device_left_iv), item.getIconUrl(), R.drawable.ic_empty_device, R.drawable.ic_empty_device);
        helper.setText(R.id.tv_device_name, item.getNickname());
        if (!TextUtils.isEmpty(item.getRegionName())){
            helper.setText(R.id.tv_device_online_status,  item.getRegionName());
        }else {
            helper.setText(R.id.tv_device_online_status, "全部");
        }
    }
}
