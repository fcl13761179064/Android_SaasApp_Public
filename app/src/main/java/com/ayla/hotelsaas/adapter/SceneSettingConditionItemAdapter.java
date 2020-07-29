package com.ayla.hotelsaas.adapter;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.application.MyApplication;
import com.ayla.hotelsaas.bean.DeviceListBean;
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
        addItemType(0,R.layout.item_scene_setting_action_device);
        addItemType(1,R.layout.item_scene_setting_condition_one_key);
    }

    @Override
    protected void convert(BaseViewHolder helper, ConditionItem item) {
        if (item instanceof DeviceConditionItem) {
            String dsn = ((DeviceConditionItem) item).getDatumBean().getTargetDeviceId();
            ImageLoader.loadImg(helper.getView(R.id.left_iv), "", R.drawable.ic_empty_device, R.drawable.ic_empty_device);
            helper.setText(R.id.tv_function_name, String.format("%s:%s", ((DeviceConditionItem) item).getDatumBean().getFunctionName(), ((DeviceConditionItem) item).getDatumBean().getValueName()));
            List<DeviceListBean.DevicesBean> devicesBean = MyApplication.getInstance().getDevicesBean();
            helper.setText(R.id.tv_name, dsn);
            if (devicesBean != null) {
                for (DeviceListBean.DevicesBean bean : devicesBean) {
                    if (bean.getDeviceId().equals(dsn)) {
                        helper.setText(R.id.tv_name, bean.getDeviceName());
                        break;
                    }
                }
            }
        }
        helper.addOnClickListener(R.id.tv_delete);
    }

    public interface ConditionItem extends MultiItemEntity {

    }

    /**
     * 设备类型的条件项目
     */
    public static class DeviceConditionItem implements ConditionItem {
        private SceneSettingFunctionDatumSetAdapter.DatumBean datumBean;

        @Override
        public int getItemType() {
            return 0;
        }

        public DeviceConditionItem(SceneSettingFunctionDatumSetAdapter.DatumBean datumBean) {
            this.datumBean = datumBean;
        }

        public SceneSettingFunctionDatumSetAdapter.DatumBean getDatumBean() {
            return datumBean;
        }
    }

    /**
     * 一键执行类型的条件项目
     */
    public static class OneKeyConditionItem implements ConditionItem {

        @Override
        public int getItemType() {
            return 1;
        }
    }
}
