package com.ayla.hotelsaas.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.ayla.hotelsaas.base.BaseMvpActivity;
import com.ayla.hotelsaas.bean.DeviceListBean;
import com.ayla.hotelsaas.databinding.ActivityDeviceReplaceBinding;
import com.ayla.hotelsaas.mvp.present.PointAndRegionPresenter;

/**
 * 设备替换页面
 * 进入需要带上 deviceId  scopeId
 */
public class DeviceReplaceActivity extends BaseMvpActivity {
    ActivityDeviceReplaceBinding mBinding;

    @Override
    protected PointAndRegionPresenter initPresenter() {
        return new PointAndRegionPresenter();
    }

    @Override
    protected int getLayoutId() {
        return 0;
    }

    @Override
    protected View getLayoutView() {
        mBinding = ActivityDeviceReplaceBinding.inflate(getLayoutInflater());
        return mBinding.getRoot();
    }

    DeviceListBean.DevicesBean devicesBean;

    String deviceId;
    long scopeId;

    @Override
    protected void initView() {
        deviceId = getIntent().getStringExtra("deviceId");
        scopeId = getIntent().getLongExtra("scopeId", 0);
    }

    @Override
    protected void initListener() {
        mBinding.button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DeviceReplaceActivity.this, DeviceAddCategoryActivity.class);
                intent.putExtra("scopeId", scopeId);
                Bundle bundle = new Bundle();
                bundle.putString("replaceDeviceId", deviceId);
                bundle.putString("targetGatewayDeviceId", "");// TODO: 1/26/21
                intent.putExtra("replaceInfo", bundle);
                startActivity(intent);
            }
        });
    }
}
