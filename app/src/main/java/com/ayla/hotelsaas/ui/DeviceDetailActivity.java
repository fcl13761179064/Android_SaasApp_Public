package com.ayla.hotelsaas.ui;

import android.os.Bundle;
import android.widget.TextView;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.base.BaseMvpActivity;
import com.ayla.hotelsaas.bean.DeviceListBean;
import com.ayla.hotelsaas.mvp.present.DeviceDetailPresenter;
import com.ayla.hotelsaas.mvp.view.DeviceDetailView;
import com.ayla.hotelsaas.widget.AppBar;

import butterknife.BindView;

public class DeviceDetailActivity extends BaseMvpActivity<DeviceDetailView, DeviceDetailPresenter> implements DeviceDetailView {

    @BindView(R.id.appBar)
    AppBar appBar;
    @BindView(R.id.tv_device_id)
    TextView tv_device_id;
    @BindView(R.id.tv_device_type)
    TextView tv_device_type;
    @BindView(R.id.tv_device_firmware_version)
    TextView tv_device_firmware_version;

    private DeviceListBean.DevicesBean mDevicesBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter.loadFirmwareVersion(mDevicesBean.getDeviceId());
    }

    @Override
    protected int getLayoutId() {
        return R.layout.device_detail_activity;
    }

    @Override
    protected void initView() {
        mDevicesBean = (DeviceListBean.DevicesBean) getIntent().getSerializableExtra("devicesBean");
        appBar.setCenterText("设备详情");

        tv_device_id.setText(mDevicesBean.getDeviceId());
        tv_device_type.setText(mDevicesBean.getDeviceName());
    }

    @Override
    protected void initListener() {

    }

    @Override
    protected DeviceDetailPresenter initPresenter() {
        return new DeviceDetailPresenter();
    }

    @Override
    public void showFirmwareVersion(String firmwareVersion) {
        tv_device_firmware_version.setText(firmwareVersion);
    }
}
