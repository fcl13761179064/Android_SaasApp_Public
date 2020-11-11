package com.ayla.hotelsaas.adapter;

import android.text.TextUtils;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.application.MyApplication;
import com.ayla.hotelsaas.bean.DeviceListBean;
import com.ayla.hotelsaas.localBean.BaseSceneBean;
import com.ayla.hotelsaas.utils.ImageLoader;
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
        addItemType(0, R.layout.item_scene_setting_action_device);
        addItemType(1, R.layout.item_scene_setting_action_delay);
    }

    @Override
    protected void convert(BaseViewHolder helper, ActionItem item) {
        BaseSceneBean.Action action = item.action;
        if (action instanceof BaseSceneBean.DeviceAction) {
            helper.setText(R.id.tv_function_name, String.format("%s:%s", action.getFunctionName(), action.getValueName()));

            DeviceListBean.DevicesBean devicesBean = null;
            for (DeviceListBean.DevicesBean bean : MyApplication.getInstance().getDevicesBean()) {
                if (TextUtils.equals(bean.getDeviceId(), action.getTargetDeviceId())) {
                    devicesBean = bean;
                }
            }
            if (devicesBean != null) {
                ImageLoader.loadImg(helper.getView(R.id.left_iv), devicesBean.getIconUrl(), R.drawable.ic_empty_device, R.drawable.ic_empty_device);
                helper.setText(R.id.tv_name, TextUtils.isEmpty(devicesBean.getNickname()) ? devicesBean.getDeviceId() : devicesBean.getNickname());
            } else {
                helper.setImageResource(R.id.left_iv, R.drawable.ic_empty_device);
                helper.setText(R.id.tv_name, action.getTargetDeviceId());
            }
        } else if (action instanceof BaseSceneBean.DelayAction) {
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
        helper.addOnClickListener(R.id.tv_delete);
    }

    public static class ActionItem implements MultiItemEntity {
        public BaseSceneBean.Action action;

        public ActionItem(BaseSceneBean.Action condition) {
            this.action = condition;
        }

        @Override
        public int getItemType() {
            return (action instanceof BaseSceneBean.DeviceAction) ? 0 : 1;
        }
    }
}
