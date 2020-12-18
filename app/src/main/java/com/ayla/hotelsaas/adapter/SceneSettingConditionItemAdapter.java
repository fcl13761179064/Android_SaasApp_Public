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
        addItemType(0, R.layout.item_scene_setting_action_device);
        addItemType(1, R.layout.item_scene_setting_condition_one_key);
    }

    @Override
    protected void convert(BaseViewHolder helper, ConditionItem item) {
        BaseSceneBean.Condition condition = item.condition;
        if (condition instanceof BaseSceneBean.DeviceCondition) {
            String option = ((BaseSceneBean.DeviceCondition) condition).getOperator();
            if (TextUtils.equals("==", option)) {
                option = ":";
            }

            DeviceListBean.DevicesBean devicesBean = null;
            for (DeviceListBean.DevicesBean bean : MyApplication.getInstance().getDevicesBean()) {
                if (TextUtils.equals(bean.getDeviceId(), ((BaseSceneBean.DeviceCondition) condition).getSourceDeviceId())) {
                    devicesBean = bean;
                }
            }
            if (devicesBean != null) {
                ImageLoader.loadImg(helper.getView(R.id.left_iv), devicesBean.getIconUrl(), R.drawable.ic_empty_device, R.drawable.ic_empty_device);
                helper.setText(R.id.tv_function_name, String.format("%s%s%s", condition.getFunctionName(), option, condition.getValueName()));
                helper.setText(R.id.tv_name, TextUtils.isEmpty(devicesBean.getNickname()) ? devicesBean.getDeviceId() : devicesBean.getNickname());
                helper.setAlpha(R.id.left_iv, 1);
                helper.setAlpha(R.id.tv_name, 1);
                helper.setAlpha(R.id.tv_function_name, 1);

                helper.setGone(R.id.tv_should_delete_device, false);
            } else {
                helper.setImageResource(R.id.left_iv, R.drawable.ic_scene_removed_device_item);
                helper.setText(R.id.tv_function_name, "无效设备");
                helper.setText(R.id.tv_name, "XXX-000-000");
                helper.setAlpha(R.id.tv_name, 0.7f);
                helper.setAlpha(R.id.tv_function_name, 0.7f);

                helper.setGone(R.id.tv_should_delete_device, true);
            }

        }
        helper.addOnClickListener(R.id.iv_delete);
    }

    public static class ConditionItem implements MultiItemEntity {
        public BaseSceneBean.Condition condition;

        public ConditionItem(BaseSceneBean.Condition condition) {
            this.condition = condition;
        }

        @Override
        public int getItemType() {
            return (condition instanceof BaseSceneBean.DeviceCondition) ? 0 : 1;
        }
    }

}
