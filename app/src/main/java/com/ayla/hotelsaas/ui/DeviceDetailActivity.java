package com.ayla.hotelsaas.ui;

import android.widget.TextView;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.base.BaseMvpActivity;
import com.ayla.hotelsaas.base.BasePresenter;
import com.ayla.hotelsaas.bean.DeviceListBean;
import com.ayla.hotelsaas.widget.AppBar;

import butterknife.BindView;

public class DeviceDetailActivity extends BaseMvpActivity {

    @BindView(R.id.appBar)
    AppBar appBar;
    @BindView(R.id.tv_device_id)
    TextView tv_device_id;
    @BindView(R.id.tv_device_type)
    TextView tv_device_type;

    private DeviceListBean.DevicesBean mDevicesBean;

    @Override
    public void refreshUI() {
        mDevicesBean = (DeviceListBean.DevicesBean) getIntent().getSerializableExtra("devicesBean");
        appBar.setCenterText("设备详情");

        super.refreshUI();
    }


    @Override
    protected int getLayoutId() {
        return R.layout.device_detail_activity;
    }

    @Override
    protected void initView() {
        if (mDevicesBean != null)
            tv_device_id.setText(mDevicesBean.getDeviceId());
        tv_device_type.setText(mDevicesBean.getDeviceName());

    }

    @Override
    protected void initListener() {

    }

    @Override
    protected BasePresenter initPresenter() {
        return null;
    }
}
