package com.ayla.hotelsaas.adapter;

import android.text.TextUtils;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.application.MyApplication;
import com.ayla.hotelsaas.bean.DeviceListBean;
import com.ayla.hotelsaas.localBean.BaseSceneBean;
import com.ayla.hotelsaas.utils.ImageLoader;
import com.ayla.hotelsaas.utils.TempUtils;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.entity.MultiItemEntity;

import java.util.List;

/**
 * 场景动作项目
 */
public class SceneSettingActionItemAdapter extends BaseMultiItemQuickAdapter<SceneSettingActionItemAdapter.ActionItem, BaseViewHolder> {


    /**
     * Same as QuickAdapter#QuickAdapter(Context,int) but with
     * some initialization data.
     *
     * @param data A new list is created out of this one to avoid mutable list
     */
    public SceneSettingActionItemAdapter(List<ActionItem> data) {
        super(data);
        addItemType(ActionItem.item_device_normal, R.layout.item_scene_setting_action_device_normal);
        addItemType(ActionItem.item_device_removed, R.layout.item_scene_setting_action_device_removed);
        addItemType(ActionItem.item_device_wait_add, R.layout.item_scene_setting_action_device_wait_add);
        addItemType(ActionItem.item_delay, R.layout.item_scene_setting_action_delay);
        addItemType(ActionItem.item_welcome, R.layout.item_scene_setting_action_welcome);
    }

    @Override
    protected void convert(BaseViewHolder helper, ActionItem item) {
        BaseSceneBean.Action action = item.action;
        if (item.getItemType() == ActionItem.item_device_normal) {
            DeviceListBean.DevicesBean devicesBean = MyApplication.getInstance().getDevicesBean(action.getTargetDeviceId());
            if (devicesBean != null) {
                ImageLoader.loadImg(helper.getView(R.id.left_iv), devicesBean.getIconUrl(), R.drawable.ic_empty_device, R.drawable.ic_empty_device);
                helper.setText(R.id.tv_function_name, String.format("%s:%s", action.getFunctionName(), action.getValueName()));
                helper.setText(R.id.tv_name, TextUtils.isEmpty(devicesBean.getNickname()) ? devicesBean.getDeviceId() : devicesBean.getNickname());
            }
        } else if (item.getItemType() == ActionItem.item_device_wait_add) {
            DeviceListBean.DevicesBean devicesBean = MyApplication.getInstance().getDevicesBean(action.getTargetDeviceId());
            if (devicesBean != null) {
                ImageLoader.loadImg(helper.getView(R.id.left_iv), devicesBean.getIconUrl(), R.drawable.ic_empty_device, R.drawable.ic_empty_device);
                helper.setText(R.id.tv_function_name, devicesBean.getNickname());
            }
        } else if (item.getItemType() == ActionItem.item_device_removed) {
            helper.setImageResource(R.id.left_iv, R.drawable.ic_scene_removed_device_item);
            helper.setText(R.id.tv_function_name, "无效设备");
            helper.setText(R.id.tv_name, "XXX-000-000");
        } else if (item.getItemType() == ActionItem.item_delay) {
            String rightValue = action.getRightValue();
            int seconds = 0;
            try {
                seconds = Integer.parseInt(rightValue);
            } catch (Exception e) {
                e.printStackTrace();
            }
            int minute = seconds / 60;
            int second = seconds % 60;
            String _minute = minute < 10 ? "0" + minute : String.valueOf(minute);
            String _second = second < 10 ? "0" + second : String.valueOf(second);

            helper.setText(R.id.tv_name, String.format("%s分%s秒", _minute, _second));
        }
        helper.addOnClickListener(R.id.iv_delete);
    }

    public static class ActionItem implements MultiItemEntity {
        private static final int item_device_normal = 0;
        private static final int item_device_removed = 1;
        private static final int item_device_wait_add = 2;
        private static final int item_delay = 3;
        private static final int item_welcome = 4;

        public BaseSceneBean.Action action;

        public ActionItem(BaseSceneBean.Action condition) {
            this.action = condition;
        }

        @Override
        public int getItemType() {
            if (action instanceof BaseSceneBean.DeviceAction) {
                DeviceListBean.DevicesBean devicesBean = MyApplication.getInstance().getDevicesBean(action.getTargetDeviceId());
                if (devicesBean != null) {
                    if (devicesBean.getBindType() == 0) {
                        return item_device_normal;
                    } else {
                        return item_device_wait_add;
                    }
                } else {
                    return item_device_removed;
                }
            }
            if (action instanceof BaseSceneBean.DelayAction) {
                return item_delay;
            }
            if (action instanceof BaseSceneBean.WelcomeAction) {
                return item_welcome;
            }
            return -1;
        }
    }
}
