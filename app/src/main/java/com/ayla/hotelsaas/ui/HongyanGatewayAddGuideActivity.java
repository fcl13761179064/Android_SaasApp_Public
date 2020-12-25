package com.ayla.hotelsaas.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.adapter.HongyanGetwayAdapter;
import com.ayla.hotelsaas.base.BaseMvpActivity;
import com.ayla.hotelsaas.mvp.present.GatewayAddPresenter;
import com.ayla.hotelsaas.utils.RecycleViewDivider;
import com.ayla.hotelsaas.widget.AppBar;
import com.chad.library.adapter.base.BaseQuickAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import carlwu.top.lib_device_add.GatewayHelper;

/**
 * 鸿雁网关绑定，选择要绑定的网关页面
 * 进入时必须带上:
 * int cuId
 * long scopeId
 * String deviceName
 * String deviceCategory。
 */
public class HongyanGatewayAddGuideActivity extends BaseMvpActivity {

    private static final int REQUEST_CODE_ADD_DEVICE = 1001;
    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;
    @BindView(R.id.appBar)
    AppBar appBar;
    private HongyanGetwayAdapter mAdapter;

    @Override
    protected GatewayAddPresenter initPresenter() {
        return null;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.hongyan_found_getway_list;
    }

    @Override
    protected void initView() {
        appBar.setCenterText("添加设备");

        recyclerview.setLayoutManager(new LinearLayoutManager(this));
        recyclerview.setHasFixedSize(true);
        recyclerview.addItemDecoration(new RecycleViewDivider(this, LinearLayoutManager.VERTICAL, 3, R.color.all_bg_color));
        mAdapter = new HongyanGetwayAdapter();
        recyclerview.setAdapter(mAdapter);
        mAdapter.bindToRecyclerView(recyclerview);
        mAdapter.openLoadAnimation(BaseQuickAdapter.ALPHAIN);
        mAdapter.setEmptyView(R.layout.empty_hongyan_device);

    }

    @Override
    protected void initListener() {
        String deviceCategory = getIntent().getStringExtra("deviceCategory");
        discoverHelper = new GatewayHelper.DiscoverHelper(getApplication(), new GatewayHelper.DiscoverCallback() {
            @Override
            public void onDeviceFound(int type, List<Map<String, String>> data) {
                List<Map<String, String>> _data = new ArrayList<>();
                for (Map<String, String> datum : data) {
                    String productKey = datum.get("productKey");
                    if (TextUtils.equals(deviceCategory, productKey)) {
                        _data.add(datum);
                    }
                }
                mAdapter.addData(_data);
            }
        });
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Map<String, String> item = mAdapter.getItem(position);
                String productKey = item.get("productKey");
                String deviceName = item.get("deviceName");
                handleJump(productKey, deviceName);
            }
        });
    }

    public void handleJump(String productKey, String deviceName) {
        discoverHelper.stopDiscoverGateway();
        Intent intent = new Intent(this, DeviceAddActivity.class);
        intent.putExtra("networkType",1);
        intent.putExtra("HYproductKey", productKey);
        intent.putExtra("HYdeviceName", deviceName);
        intent.putExtras(getIntent());
        startActivityForResult(intent, REQUEST_CODE_ADD_DEVICE);
    }

    private GatewayHelper.DiscoverHelper discoverHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        discoverHelper.startDiscoverGateway();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        discoverHelper.stopDiscoverGateway();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_ADD_DEVICE && resultCode == RESULT_OK) {
            setResult(RESULT_OK);
            finish();
        }

    }
}
