package com.ayla.hotelsaas.adapter;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.utils.ImageLoader;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

/**
 *
 */
public class SceneSettingActionItemAdapter extends BaseQuickAdapter<SceneSettingFunctionDatumSetAdapter.DatumBean, BaseViewHolder> {

    public SceneSettingActionItemAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(BaseViewHolder helper, SceneSettingFunctionDatumSetAdapter.DatumBean item) {
        String dsn = item.getTargetDeviceId();
        ImageLoader.loadImg(helper.getView(R.id.left_iv), "", R.drawable.ic_empty_device, R.drawable.ic_empty_device);
        helper.setText(R.id.tv_function_name, String.format("%s:%s", item.getFunctionName(), item.getValueName()));
        helper.setText(R.id.tv_name, dsn);
        helper.addOnClickListener(R.id.tv_delete);
    }
}
