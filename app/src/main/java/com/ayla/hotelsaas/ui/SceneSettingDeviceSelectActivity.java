package com.ayla.hotelsaas.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.adapter.SceneSettingDeviceSelectAdapter;
import com.ayla.hotelsaas.base.BaseMvpActivity;
import com.ayla.hotelsaas.bean.DeviceListBean;
import com.ayla.hotelsaas.mvp.present.SceneSettingDeviceSelectPresenter;
import com.ayla.hotelsaas.mvp.view.SceneSettingDeviceSelectView;
import com.chad.library.adapter.base.BaseQuickAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * 场景创建，选择设备的页面
 * 进入时必须带入参数： int type  ，0：condition  1：action
 * 可选参数：datums {@link ArrayList<com.ayla.hotelsaas.adapter.SceneSettingFunctionDatumSetAdapter.DatumBean>} 已经选择了的栏目。
 *          ruleSetMode 条件组合方式。 ALL(2,"多条条件全部命中")   ANY(3,"多条条件任一命中")
 */
public class SceneSettingDeviceSelectActivity extends BaseMvpActivity<SceneSettingDeviceSelectView, SceneSettingDeviceSelectPresenter> implements SceneSettingDeviceSelectView {
    @BindView(R.id.rv)
    public RecyclerView mRecyclerView;

    private SceneSettingDeviceSelectAdapter mAdapter;

    @Override
    protected SceneSettingDeviceSelectPresenter initPresenter() {
        return new SceneSettingDeviceSelectPresenter();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_zigbee_add_select_gateway;
    }

    @Override
    protected void initView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new SceneSettingDeviceSelectAdapter(R.layout.item_scene_action_device_select);
        mAdapter.bindToRecyclerView(mRecyclerView);
        mAdapter.setEmptyView(R.layout.empty_device_order);
    }

    @Override
    protected void initListener() {
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                DeviceListBean.DevicesBean deviceBean = (DeviceListBean.DevicesBean) adapter.getItem(position);
                Intent mainActivity = new Intent(SceneSettingDeviceSelectActivity.this, SceneSettingFunctionSelectActivity.class);
                mainActivity.putExtra("deviceBean", deviceBean);
                mainActivity.putExtra("oemModel", oemModels.get(position));
                mainActivity.putStringArrayListExtra("properties", new ArrayList<>(properties.get(position)));
                mainActivity.putExtras(getIntent());
                startActivityForResult(mainActivity, 0);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int type = getIntent().getIntExtra("type", 0);
        mPresenter.loadDevice(type == 0);
    }

    private List<List<String>> properties;//支持的条件或者动作的描述信息
    private List<String> oemModels;

    @Override
    public void showDevices(List<DeviceListBean.DevicesBean> devices, List<List<String>> properties, List<String> oemModels) {
        mAdapter.setNewData(devices);
        this.properties = properties;
        this.oemModels = oemModels;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && resultCode == RESULT_OK) {
            setResult(RESULT_OK, data);
            finish();
        }
    }
}
