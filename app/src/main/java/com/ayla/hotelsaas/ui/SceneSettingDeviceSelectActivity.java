package com.ayla.hotelsaas.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.adapter.SceneSettingDeviceSelectAdapter;
import com.ayla.hotelsaas.adapter.ZigBeeAddSelectGatewayAdapter;
import com.ayla.hotelsaas.base.BaseMvpActivity;
import com.ayla.hotelsaas.bean.Device;
import com.ayla.hotelsaas.mvp.present.SceneSettingDeviceSelectPresenter;
import com.ayla.hotelsaas.mvp.view.SceneSettingDeviceSelectView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.util.List;

import butterknife.BindView;
import me.jessyan.autosize.utils.AutoSizeUtils;

/**
 * 场景创建，选择设备的页面
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
        mAdapter = new SceneSettingDeviceSelectAdapter(R.layout.item_zigbee_add_select_gateway);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(this)
                .showLastDivider()
                .color(android.R.color.transparent).size(AutoSizeUtils.dp2px(this, 10)).build());
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    protected void initListener() {
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Intent mainActivity = new Intent(SceneSettingDeviceSelectActivity.this, SceneSettingFunctionSelectActivity.class);
                startActivityForResult(mainActivity, 0);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter.loadDevice();
    }

    @Override
    public void showDevices(List<Device> devices) {
        mAdapter.setNewData(devices);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && resultCode == RESULT_OK) {
            finish();
        }
    }
}
