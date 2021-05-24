package com.ayla.hotelsaas.ui;

import android.view.View;
import android.widget.Button;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.base.BaseMvpActivity;
import com.ayla.hotelsaas.mvp.present.APWifiToGateWayPresenter;
import com.ayla.hotelsaas.mvp.view.APwifiToGateWayView;
import com.ayla.ng.lib.bootstrap.AylaSetupDevice;
import com.blankj.utilcode.util.ToastUtils;

import butterknife.BindView;

public class ApWifiConnectToA2GagtewayActivity extends BaseMvpActivity<APwifiToGateWayView, APWifiToGateWayPresenter> implements APwifiToGateWayView {

     @BindView(R.id.sd_btn_action)
    Button sd_btn_action;
    @Override
    protected int getLayoutId() {
        return  R.layout.activity_wifi_connectionto_gateway;
    }

    @Override
    protected APWifiToGateWayPresenter initPresenter() {
        return new APWifiToGateWayPresenter();
    }

    @Override
    protected View getLayoutView() {
        return null;
    }

    @Override
    protected void initView() {
        String ssid = getIntent().getStringExtra("ssid");
        String pwd = getIntent().getStringExtra("pwd");
        String dsn = getIntent().getStringExtra("deviceId");
        mPresenter.connectToApDevice(ApWifiConnectToA2GagtewayActivity.this,dsn,ssid,pwd);

    }

    @Override
    protected void initListener() {

    }

    @Override
    public void onFailed(Throwable throwable)
    {
        sd_btn_action.setEnabled(false);
        ToastUtils.showShort("连接网关 Wi-Fi 失败，请重试");
    }

    @Override
    public void onSuccess(AylaSetupDevice aylaSetupDevice) {
        sd_btn_action.setEnabled(true);
        ToastUtils.showShort("323232323223");
    }
}
