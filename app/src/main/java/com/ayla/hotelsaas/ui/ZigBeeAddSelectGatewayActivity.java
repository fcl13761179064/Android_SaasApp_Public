package com.ayla.hotelsaas.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.adapter.ZigBeeAddSelectGatewayAdapter;
import com.ayla.hotelsaas.base.BaseMvpActivity;
import com.ayla.hotelsaas.bean.DeviceListBean;
import com.ayla.hotelsaas.mvp.present.ZigBeeAddSelectGatewayPresenter;
import com.ayla.hotelsaas.mvp.view.ZigBeeAddSelectGatewayView;
import com.ayla.hotelsaas.utils.TempUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.util.List;

import butterknife.BindView;
import me.jessyan.autosize.utils.AutoSizeUtils;

/**
 * 添加节点，选择所属网关的页面
 * 进入时必须带入scopeId、deviceName、deviceCategory、categoryId
 */
public class ZigBeeAddSelectGatewayActivity extends BaseMvpActivity<ZigBeeAddSelectGatewayView, ZigBeeAddSelectGatewayPresenter> implements ZigBeeAddSelectGatewayView {
    @BindView(R.id.rv)
    public RecyclerView mRecyclerView;

    private ZigBeeAddSelectGatewayAdapter mAdapter;

    @Override
    protected ZigBeeAddSelectGatewayPresenter initPresenter() {
        return new ZigBeeAddSelectGatewayPresenter();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_zigbee_add_select_gateway;
    }

    @Override
    protected void initView() {
        mAdapter = new ZigBeeAddSelectGatewayAdapter(R.layout.item_zigbee_add_select_gateway);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(this)
                .showLastDivider()
                .color(android.R.color.transparent).size(AutoSizeUtils.dp2px(this, 10)).build());
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                DeviceListBean.DevicesBean device = (DeviceListBean.DevicesBean) adapter.getItem(position);
                if (TempUtils.isDeviceOnline(device)) {
//                    Intent mainActivity = new Intent(ZigBeeAddSelectGatewayActivity.this, ZigBeeAddGuideActivity.class);
//                    mainActivity.putExtra("deviceId", device.getDeviceId());
//                    mainActivity.putExtra("cuId", device.getCuId());
//                    mainActivity.putExtras(getIntent());
//                    startActivityForResult(mainActivity, 0);
                }
            }
        });
    }

    @Override
    protected void initListener() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter.loadGateway();
    }

    @Override
    public void showGateways(List<DeviceListBean.DevicesBean> devices) {
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
