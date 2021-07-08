package com.ayla.hotelsaas.ui;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.base.BaseMvpActivity;
import com.ayla.hotelsaas.mvp.present.APWifiToGateWayPresenter;
import com.ayla.hotelsaas.mvp.view.APwifiToGateWayView;
import com.ayla.hotelsaas.widget.FastClickUtils;
import com.ayla.ng.lib.bootstrap.AylaSetupDevice;


import butterknife.BindView;

public class ApWifiConnectToA2GagtewayActivity extends BaseMvpActivity<APwifiToGateWayView, APWifiToGateWayPresenter> implements APwifiToGateWayView {

    @BindView(R.id.sd_btn_action)
    Button sd_btn_action;
    @BindView(R.id.tv_connect_ap)
    TextView tv_connect_ap;
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
        }
    }

    @Override
    protected void initListener() {
        tv_connect_ap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (FastClickUtils.isDoubleClick()) {
                    return;
                }
                Intent intent = new Intent();
                intent.setAction("android.net.wifi.PICK_WIFI_NETWORK");
                startActivityForResult(intent   ,1000);

            }
        });
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mPresenter.connectToApDevice(ApWifiConnectToA2GagtewayActivity.this, dsn, ssid, pwd, "Ayla-40aa56fe09d0");
        sd_btn_action.setEnabled(false);
    }

    @Override
    public void onFailed(Throwable throwable) {
        sd_btn_action.setEnabled(false);
        CustomToast.makeText(this, throwable.getMessage().toString(), R.drawable.ic_toast_warming);
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
