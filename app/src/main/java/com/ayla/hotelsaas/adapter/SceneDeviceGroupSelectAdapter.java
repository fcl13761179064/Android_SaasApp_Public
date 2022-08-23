package com.ayla.hotelsaas.adapter;

import android.text.TextUtils;
import android.view.View;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.bean.BaseDevice;
import com.ayla.hotelsaas.bean.DeviceItem;
import com.ayla.hotelsaas.bean.GroupItem;
import com.ayla.hotelsaas.utils.ImageLoader;
import com.ayla.hotelsaas.utils.TempUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

/**
 * 场景设备选择
 */
public class SceneDeviceGroupSelectAdapter extends BaseQuickAdapter<BaseDevice, BaseViewHolder> {

    public SceneDeviceGroupSelectAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(BaseViewHolder helper, BaseDevice item) {
        if (helper.getAdapterPosition() == 0) {
            helper.getView(R.id.tv_desc).setVisibility(View.VISIBLE);
        } else {
            helper.getView(R.id.tv_desc).setVisibility(View.GONE);
        }
        if (item instanceof DeviceItem) {
            helper.setGone(R.id.tv_group, false);
            if ("ONLINE".equalsIgnoreCase(((DeviceItem) item).getDeviceStatus())) {
                helper.getView(R.id.offline_state).setVisibility(View.GONE);
            } else {
                helper.getView(R.id.offline_state).setVisibility(View.VISIBLE);
            }
            ImageLoader.loadImg(helper.getView(R.id.device_left_iv), ((DeviceItem) item).getIconUrl(), R.drawable.ic_empty_device, R.drawable.ic_empty_device);
            helper.setText(R.id.tv_device_name, ((DeviceItem) item).getNickname());
            if (!TextUtils.isEmpty(((DeviceItem) item).getRegionName())) {
                helper.setText(R.id.tv_device_online_status, ((DeviceItem) item).getRegionName());
            } else {
                helper.setText(R.id.tv_device_online_status, "全部");
            }
        }
        if (item instanceof GroupItem) {
            helper.setGone(R.id.tv_group, true);
            helper.getView(R.id.offline_state).setVisibility(View.GONE);
            helper.setImageResource(R.id.device_left_iv, R.drawable.icon_group);
            helper.setText(R.id.tv_device_name, ((GroupItem) item).getGroupName());
            String roomId = ((GroupItem) item).getRegionId();
            String roomName = TempUtils.getRoomName(roomId);
            if (!TextUtils.isEmpty(roomName)) {
                helper.setText(R.id.tv_device_online_status, roomName);
            } else {
                helper.setText(R.id.tv_device_online_status, "全部");
            }
        }

    }
}
