package com.ayla.hotelsaas.ui;

import android.content.Intent;
import android.widget.EditText;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.base.BaseMvpActivity;
import com.ayla.hotelsaas.mvp.present.GatewayAddGuidePresenter;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 网关添加DSN输入页面
 * 进入时必须带上cuId 、scopeId 、deviceName。
 */
public class AylaWiFiAddInputActivity extends BaseMvpActivity {
    @BindView(R.id.et_wifi)
    public EditText mWiFiNameEditText;
    @BindView(R.id.et_pwd)
    public EditText mWiFiPasswordEditText;

    @Override
    protected GatewayAddGuidePresenter initPresenter() {
        return null;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_ayla_wifi_add_input;
    }

    @Override
    protected void initView() {
    }

    @Override
    protected void initListener() {

    }

    @OnClick(R.id.bt_start_add)
    public void handleJump() {
        String name = mWiFiNameEditText.getText().toString();
        String pwd = mWiFiPasswordEditText.getText().toString();
        if (name.length() == 0) {
            CustomToast.makeText(this, "WiFi名输入不能为空", R.drawable.ic_toast_warming).show();
        } else {
            Intent intent = new Intent(this, AylaWifiAddActivity.class);
            intent.putExtra("wifiName",name);
            intent.putExtra("wifiPassword",pwd);
            intent.putExtras(getIntent());
            startActivityForResult(intent, 0);
        }
    }
}
