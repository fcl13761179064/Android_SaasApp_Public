package com.ayla.hotelsaas.ui;

import android.content.Intent;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.aliyun.alink.business.devicecenter.api.add.DeviceInfo;
import com.aliyun.alink.business.devicecenter.api.discovery.DiscoveryType;
import com.aliyun.alink.business.devicecenter.api.discovery.IDeviceDiscoveryListener;
import com.aliyun.alink.business.devicecenter.api.discovery.LocalDeviceMgr;
import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.adapter.HongyanGetwayAdapter;
import com.ayla.hotelsaas.base.BaseMvpActivity;
import com.ayla.hotelsaas.feiyansdk.DeviceAddHandler;
import com.ayla.hotelsaas.feiyansdk.FoundDeviceListItem;
import com.ayla.hotelsaas.feiyansdk.OnDeviceAddListener;
import com.ayla.hotelsaas.feiyansdk.SupportDeviceListItem;
import com.ayla.hotelsaas.mvp.present.GatewayAddGuidePresenter;
import com.ayla.hotelsaas.utils.RecycleViewDivider;
import com.ayla.hotelsaas.widget.AppBar;
import com.chad.library.adapter.base.BaseQuickAdapter;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import butterknife.BindView;

/**
 * 鸿雁网关添加引导页面
 * 进入时必须带上cuId 、scopeId 、deviceName、deviceCategory。
 * fanchunlei
 */
public class HongyanGatewayAddGuideActivity extends BaseMvpActivity implements OnDeviceAddListener {

    private static final int REQUEST_CODE_ADD_DEVICE = 1001;
    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;
    @BindView(R.id.appBar)
    AppBar appBar;
    private HongyanGetwayAdapter mAdapter;
    private DeviceAddHandler deviceAddHandler;
    private List<FoundDeviceListItem> mFoundDeviceListItems;

    @Override
    public void refreshUI() {
        deviceAddHandler = new DeviceAddHandler((OnDeviceAddListener) HongyanGatewayAddGuideActivity.this);
        appBar.setCenterText("添加设备");
        super.refreshUI();
    }

    @Override
    protected GatewayAddGuidePresenter initPresenter() {
        return null;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.hongyan_found_getway_list;
    }

    @Override
    protected void initView() {
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
        deviceAddHandler.reset();
        //发现局域网内设备以及WiFi热点设备
        EnumSet<DiscoveryType> discoveryTypeEnumSet = EnumSet.allOf(DiscoveryType.class);
        LocalDeviceMgr.getInstance().startDiscovery(HongyanGatewayAddGuideActivity.this, discoveryTypeEnumSet, null, new IDeviceDiscoveryListener() {
            @Override
            public void onDeviceFound(DiscoveryType discoveryType, List<DeviceInfo> list) {
                Log.d("DeviceAddBusiness", "--发现设备--" + JSON.toJSONString(list));
                mFoundDeviceListItems = new ArrayList<>();
                for (DeviceInfo deviceInfo : list) {
                    final FoundDeviceListItem deviceListItem = new FoundDeviceListItem();
                    if (discoveryType == DiscoveryType.LOCAL_ONLINE_DEVICE) {
                        deviceListItem.deviceStatus = FoundDeviceListItem.NEED_BIND;
                    } else if (discoveryType == DiscoveryType.CLOUD_ENROLLEE_DEVICE) {
                        deviceListItem.deviceStatus = FoundDeviceListItem.NEED_CONNECT;
                    }
                    deviceListItem.discoveryType = discoveryType;
                    deviceListItem.deviceInfo = deviceInfo;
                    deviceListItem.deviceName = deviceInfo.deviceName;
                    deviceListItem.productKey = deviceInfo.productKey;
                    deviceListItem.token = deviceInfo.token;
                    mFoundDeviceListItems.add(deviceListItem);
                }
                mAdapter.addData(mFoundDeviceListItems);
            }

        });

        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                handleJump(position);
            }
        });
    }

    public void handleJump(int position) {
        Intent intent = new Intent(this, HongyanGatewayAddActivity.class);
        String productKey = mAdapter.getData().get(position).getProductKey();
        String deviceName = mAdapter.getData().get(position).getDeviceName();
        intent.putExtra("HongyanproductKey", productKey);
        intent.putExtra("HongyandeviceName", deviceName);
        intent.putExtras(getIntent());
        startActivityForResult(intent, REQUEST_CODE_ADD_DEVICE);
        LocalDeviceMgr.getInstance().stopDiscovery();
        Log.d("stopDiscovery","已经停止发现网关");
    }

    @Override
    public void showToast(String message) {

    }

    @Override
    public void onSupportDeviceSuccess(List<SupportDeviceListItem> mSupportDeviceListItems) {

    }

    @Override
    public void onFilterComplete(List<FoundDeviceListItem> foundDeviceListItems) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalDeviceMgr.getInstance().stopDiscovery();
        Log.d("stopDiscovery","已经停止发现网关");
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
