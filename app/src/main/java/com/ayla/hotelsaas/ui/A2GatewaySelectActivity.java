package com.ayla.hotelsaas.ui;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.adapter.ZigBeeAddSelectGatewayAdapter;
import com.ayla.hotelsaas.base.BaseMvpActivity;
import com.ayla.hotelsaas.bean.DeviceCategoryBean;
import com.ayla.hotelsaas.bean.DeviceListBean;
import com.ayla.hotelsaas.mvp.present.ZigBeeAddSelectGatewayPresenter;
import com.ayla.hotelsaas.mvp.view.ZigBeeAddSelectGatewayView;
import com.ayla.hotelsaas.utils.TempUtils;
import com.ayla.hotelsaas.widget.AppBar;
import com.blankj.utilcode.util.SizeUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;

import java.io.Serializable;
import java.util.List;

import butterknife.BindView;

/**
 * 添加节点，选择所属网关的页面
 * 可选参数：
 * sourceId <0时 ，不区分网关所属云。
 * 返回 网关的deviceId
 */
public class A2GatewaySelectActivity extends BaseMvpActivity<ZigBeeAddSelectGatewayView, ZigBeeAddSelectGatewayPresenter> implements ZigBeeAddSelectGatewayView {
    @BindView(R.id.rv)
    public RecyclerView mRecyclerView;
    @BindView(R.id.appBar)
    AppBar appBar;
    Bundle addInfo = null;
    private ZigBeeAddSelectGatewayAdapter mAdapter;
    private DeviceCategoryBean.SubBean.NodeBean nodeBean;

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
        appBar.setCenterText("选择网关");
        mAdapter = new ZigBeeAddSelectGatewayAdapter(R.layout.item_zigbee_add_select_gateway);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                int size = SizeUtils.dp2px(10);
                int position = parent.getChildAdapterPosition(view);

                outRect.set(0, (position == 0) ? size : 0, 0, size);
            }
        });
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                DeviceListBean.DevicesBean device = (DeviceListBean.DevicesBean) adapter.getItem(position);
                if (TempUtils.isDeviceOnline(device)) {
                    addInfo.putInt("cuId", device.getCuId());
                    if (device.getCuId() == 0) {
                        addInfo.putString("deviceCategory", nodeBean.getOemModel().get(0));
                        addInfo.putInt("networkType", 3);
                    } else {
                        addInfo.putString("deviceCategory",nodeBean.getOemModel().get(1));
                        addInfo.putInt("networkType", 4);
                    }
                    Intent intent = new Intent().putExtra("deviceId", device.getDeviceId())
                            .putExtra("addInfo", addInfo);
                    setResult(RESULT_OK, intent);
                    finish();
                } else {
                    CustomToast.makeText(A2GatewaySelectActivity.this, "当前网关离线", R.drawable.ic_toast_warming);
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
        addInfo = getIntent().getBundleExtra("addInfo");
        nodeBean = (DeviceCategoryBean.SubBean.NodeBean) getIntent().getSerializableExtra("nodeBean");
        mPresenter.loadA2Gateway(1);
    }

    @Override
    public void showGateways(List<DeviceListBean.DevicesBean> devices) {
        mAdapter.setNewData(devices);
    }
}
