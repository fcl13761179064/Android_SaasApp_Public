package com.ayla.hotelsaas.ui;

import android.content.Intent;
import android.os.Handler;
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
    private String ssid;
    private String pwd;
    private String dsn;
    private String randomString;
    public static Boolean is_relatation_success = false;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_wifi_connectionto_gateway;
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
        ssid = getIntent().getStringExtra("ssid");
        pwd = getIntent().getStringExtra("pwd");
        dsn = getIntent().getStringExtra("deviceId");

        if (is_relatation_success) {
            sd_btn_action.setEnabled(true);
        } else {
            mPresenter.connectToApDevice(ApWifiConnectToA2GagtewayActivity.this, dsn, ssid, pwd);
            sd_btn_action.setEnabled(false);
        }
    }

    @Override
    protected void initListener() {
        sd_btn_action.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ApWifiConnectToA2GagtewayActivity.this, ApDeviceAddActivity.class);
                intent.putExtra("randomNum", randomString);
                intent.putExtras(getIntent());
                startActivity(intent);
            }
        });
    }

    @Override
    public void onFailed(Throwable throwable) {
        sd_btn_action.setEnabled(false);
        ToastUtils.showShort("连接网关 Wi-Fi 失败，请重试");
    }

    @Override
    public void onSuccess(AylaSetupDevice aylaSetupDevice, String randomString) {
        is_relatation_success = true;
        sd_btn_action.setEnabled(true);
        this.randomString = randomString;
        Intent intent = new Intent(ApWifiConnectToA2GagtewayActivity.this, ApDeviceAddActivity.class);
        intent.putExtra("randomNum", randomString);
        intent.putExtras(getIntent());
        startActivity(intent);
    }

    @Override
    protected void appBarLeftIvClicked() {
        super.appBarLeftIvClicked();
        finish();
    }
}
