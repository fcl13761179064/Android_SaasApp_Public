package com.ayla.hotelsaas.ui.activities.local_scene;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.adapter.local_scene.LocalSceneSelectGatewayAdapter;
import com.ayla.hotelsaas.base.BaseMvpActivity;
import com.ayla.hotelsaas.bean.DeviceListBean;
import com.ayla.hotelsaas.mvp.present.ZigBeeAddSelectGatewayPresenter;
import com.ayla.hotelsaas.mvp.view.ZigBeeAddSelectGatewayView;
import com.ayla.hotelsaas.utils.CustomToast;
import com.ayla.hotelsaas.utils.TempUtils;
import com.ayla.hotelsaas.widget.AppBar;
import com.blankj.utilcode.util.SizeUtils;

import java.util.List;

import butterknife.BindView;

/**
 * 添加节点，选择所属网关的页面
 * 可选参数：
 * sourceId <0时 ，不区分网关所属云。
 * 返回 网关的deviceId
 */
public
class LocalSceneGatewaySelectActivity extends BaseMvpActivity<ZigBeeAddSelectGatewayView, ZigBeeAddSelectGatewayPresenter> implements ZigBeeAddSelectGatewayView {
    @BindView(R.id.rv)
    public RecyclerView mRecyclerView;
    @BindView(R.id.gateway_appbar)
    AppBar appBar;

    private LocalSceneSelectGatewayAdapter mAdapter;

    @Override
    protected ZigBeeAddSelectGatewayPresenter initPresenter() {
        return new ZigBeeAddSelectGatewayPresenter();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_local_scene_select_gateway;
    }

    @Override
    protected void initView() {
        appBar.setCenterText("选择网关");
        mAdapter = new LocalSceneSelectGatewayAdapter(R.layout.item_gate_way_list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                int size = SizeUtils.dp2px(12);
                int position = parent.getChildAdapterPosition(view);

                outRect.set(0, (position == 0) ? size : 0, 0, size);
            }
        });
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener((adapter, view, position) -> {
            DeviceListBean.DevicesBean device = (DeviceListBean.DevicesBean) adapter.getItem(position);
            if (TempUtils.isDeviceOnline(device)) {
                Intent intent = new Intent(getIntent()).putExtra("deviceId", device.getDeviceId());
                setResult(RESULT_OK, intent);
                finish();
            } else {
                CustomToast.makeText(LocalSceneGatewaySelectActivity.this, "当前网关离线", R.drawable.ic_toast_warning);
            }
        });
    }

    @Override
    protected void initListener() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int sourceId = getIntent().getIntExtra("sourceId", -1);
        mPresenter.loadGateway(sourceId);
    }

    @Override
    public void showGateways(List<DeviceListBean.DevicesBean> devices) {
        mAdapter.setNewData(devices);
    }

    @Override
    public void showRelaceGateWays(List<DeviceListBean.DevicesBean> gateways) {

    }
}
