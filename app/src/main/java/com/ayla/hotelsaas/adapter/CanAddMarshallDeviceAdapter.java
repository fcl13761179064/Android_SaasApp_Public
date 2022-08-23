package com.ayla.hotelsaas.adapter;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.bean.BaseDevice;
import com.ayla.hotelsaas.bean.DeviceItem;
import com.ayla.hotelsaas.bean.GroupItem;
import com.ayla.hotelsaas.bean.MarshallEntryBean;
import com.ayla.hotelsaas.utils.ImageLoader;
import com.ayla.hotelsaas.utils.TempUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

/**
 * 场景功能动作选择，单选
 */
public class CanAddMarshallDeviceAdapter extends BaseQuickAdapter<BaseDevice, BaseViewHolder> {
    public CanAddMarshallDeviceAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(BaseViewHolder helper, BaseDevice item) {
        if (item instanceof DeviceItem)
            setData(helper, ((DeviceItem) item).getNickname(), ((DeviceItem) item).getRegionName(),
                    ((DeviceItem) item).getDeviceStatus(), ((DeviceItem) item).getIconUrl());
        if (item instanceof GroupItem) {
            String roomName = TempUtils.getRoomName(((GroupItem) item).getRegionId());
            setData(helper, ((GroupItem) item).getGroupName(), roomName,
                    ((GroupItem) item).getConnectionStatus(), null);
        }

        helper.addOnClickListener(R.id.iv_add_device);
    }

    private void setData(BaseViewHolder helper, String name, String regionName, String connectionStatus, String icon) {
        helper.setText(R.id.tv_device_name, name);
        helper.setText(R.id.tv_device_regeinName, regionName);
        helper.setBackgroundRes(R.id.device_left_iv, R.color.white);
        if ("OFFLINE".equalsIgnoreCase(connectionStatus)) {
            helper.setVisible(R.id.rl_line_off, true);
            helper.setBackgroundRes(R.id.device_left_iv, R.drawable.round_gray_shape);
        } else {
            helper.setVisible(R.id.rl_line_off, false);
        }
        if (icon == null) {
            helper.setImageResource(R.id.device_left_iv, R.drawable.icon_group);
        } else
            ImageLoader.loadImg(helper.getView(R.id.device_left_iv), icon, R.drawable.ic_empty_device, R.drawable.ic_empty_device);
    }


}
