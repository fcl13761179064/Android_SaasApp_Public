package com.ayla.hotelsaas.ui;

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

    @Override
    protected void initView() {
        String deviceId = getIntent().getStringExtra("deviceId");

    }

    @Override
    protected void initListener() {

    }

}
