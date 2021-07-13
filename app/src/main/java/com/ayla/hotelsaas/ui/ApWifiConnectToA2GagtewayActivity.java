package com.ayla.hotelsaas.ui;

import android.app.Activity;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.application.Constance;
import com.ayla.hotelsaas.base.BaseMvpActivity;
import com.ayla.hotelsaas.base.BasePresenter;
import com.ayla.hotelsaas.mvp.present.APWifiToGateWayPresenter;
import com.ayla.hotelsaas.mvp.view.APwifiToGateWayView;
import com.ayla.hotelsaas.utils.SharePreferenceUtils;
import com.ayla.hotelsaas.utils.WifiUtil;
import com.ayla.hotelsaas.widget.FastClickUtils;
import com.ayla.hotelsaas.widget.FilterWifiDialog;
import com.ayla.hotelsaas.widget.WifiUtils;
import com.ayla.ng.lib.bootstrap.AylaSetupDevice;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener;


import java.lang.ref.PhantomReference;
import java.util.List;

import butterknife.BindView;

public class ApWifiConnectToA2GagtewayActivity extends BaseMvpActivity {

    @BindView(R.id.sd_btn_action)
    Button sd_btn_action;
    @BindView(R.id.tv_connect_ap)
    TextView tv_connect_ap;
    @BindView(R.id.ssid_show)
    TextView ssid_show;
    private int PHONE_SETTING_SSID = 0X11;
    private String deviceSsid;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_wifi_connectionto_gateway;
    }


    @Override
    protected BasePresenter initPresenter() {
        return null;
    }

    @Override
    protected View getLayoutView() {
        return null;
    }

    @Override
    protected void initView() {
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
                startActivityForResult(intent, PHONE_SETTING_SSID);
            }
        });
        sd_btn_action.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    Intent intent = new Intent(ApWifiConnectToA2GagtewayActivity.this, ApDeviceAddActivity.class);
                    intent.putExtra("deviceSsid", deviceSsid);
                    intent.putExtras(getIntent());
                    startActivity(intent);
            }
        });
    }


    @Override
    protected void appBarLeftIvClicked() {
        super.appBarLeftIvClicked();
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PHONE_SETTING_SSID) {
            deviceSsid = WifiUtil.getConnectWifiSsid();
            if (deviceSsid != null && deviceSsid.matches(Constance.DEFAULT_SSID_REGEX)) {
                ssid_show.setText(deviceSsid);
                sd_btn_action.setEnabled(true);
            } else {
                sd_btn_action.setEnabled(false);
            }
        }
    }
}
