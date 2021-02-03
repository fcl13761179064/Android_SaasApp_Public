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
 * 场景条件项目
 */
public class SceneSettingConditionItemAdapter extends BaseMultiItemQuickAdapter<SceneSettingConditionItemAdapter.ConditionItem, BaseViewHolder> {

    /**
     * Same as QuickAdapter#QuickAdapter(Context,int) but with
     * some initialization data.
     *
     * @param data A new list is created out of this one to avoid mutable list
     */
    public SceneSettingConditionItemAdapter(List<ConditionItem> data) {
        super(data);
        addItemType(ConditionItem.item_device_normal, R.layout.item_scene_setting_action_device_normal);
        addItemType(ConditionItem.item_device_removed, R.layout.item_scene_setting_action_device_removed);
        addItemType(ConditionItem.item_device_wait_add, R.layout.item_scene_setting_action_device_wait_add);
        addItemType(ConditionItem.item_one_key, R.layout.item_scene_setting_condition_one_key);
    }

    @Override
    protected void convert(BaseViewHolder helper, ConditionItem item) {
        BaseSceneBean.Condition condition = item.condition;
        if (item.getItemType() == ConditionItem.item_device_normal) {
            String option = ((BaseSceneBean.DeviceCondition) condition).getOperator();
            if (TextUtils.equals("==", option)) {
                option = ":";
            }
            DeviceListBean.DevicesBean devicesBean = MyApplication.getInstance().getDevicesBean(((BaseSceneBean.DeviceCondition) condition).getSourceDeviceId());
            if (devicesBean != null) {
                ImageLoader.loadImg(helper.getView(R.id.left_iv), devicesBean.getIconUrl(), R.drawable.ic_empty_device, R.drawable.ic_empty_device);
                helper.setText(R.id.tv_function_name, String.format("%s%s%s", condition.getFunctionName(), option, condition.getValueName()));
                helper.setText(R.id.tv_name, TextUtils.isEmpty(devicesBean.getNickname()) ? devicesBean.getDeviceId() : devicesBean.getNickname());
            }
        } else if (item.getItemType() == ConditionItem.item_device_wait_add) {
            DeviceListBean.DevicesBean devicesBean = MyApplication.getInstance().getDevicesBean(((BaseSceneBean.DeviceCondition) condition).getSourceDeviceId());
            if (devicesBean != null) {
                ImageLoader.loadImg(helper.getView(R.id.left_iv), devicesBean.getIconUrl(), R.drawable.ic_empty_device, R.drawable.ic_empty_device);
                helper.setText(R.id.tv_function_name, devicesBean.getNickname());
            }
        } else if (item.getItemType() == ConditionItem.item_device_removed) {
            helper.setImageResource(R.id.left_iv, R.drawable.ic_scene_removed_device_item);
            helper.setText(R.id.tv_function_name, "无效设备");
            helper.setText(R.id.tv_name, "XXX-000-000");
        }
        helper.addOnClickListener(R.id.iv_delete);
    }

    public static class ConditionItem implements MultiItemEntity {
        public static final int item_device_normal = 0;
        public static final int item_device_removed = 1;
        public static final int item_device_wait_add = 2;
        public static final int item_one_key = 3;

        public BaseSceneBean.Condition condition;

        public ConditionItem(BaseSceneBean.Condition condition) {
            this.condition = condition;
        }

        @Override
        public int getItemType() {
            if (condition instanceof BaseSceneBean.DeviceCondition) {
                DeviceListBean.DevicesBean devicesBean = MyApplication.getInstance().getDevicesBean(((BaseSceneBean.DeviceCondition) condition).getSourceDeviceId());
                if (devicesBean != null) {
                    if (devicesBean.getBindType() == 0) {
                        return item_device_normal;
                    } else {
                        return item_device_wait_add;
                    }
                } else {
                    return item_device_removed;
                }
            } else {
                return item_one_key;
            }
        }
    }
}
