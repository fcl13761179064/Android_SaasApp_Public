package com.ayla.hotelsaas.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.annotation.Nullable;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.base.BaseMvpActivity;
import com.ayla.hotelsaas.base.BasePresenter;

import butterknife.OnClick;

/**
 * Ayla网关添加引导页面
 * 进入必须带上{@link Bundle addInfo}
 */
public class A2GatewayAddGuideActivity extends BaseMvpActivity {
    private final int REQUEST_CODE_FOR_DSN_INPUT = 0X11;
    private final int REQUEST_CODE_FOR_DSN_SCAN = 0X12;

    @Override
    protected BasePresenter initPresenter() {
        return null;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_gateway_add_guide;
    }

    @Override
    protected void initView() {
    }

    @Override
    protected void initListener() {

    }

    @OnClick(R.id.bt_start_add)
    public void handleJump() {
        Intent mainActivity = new Intent(this, ScanActivity.class);
        startActivityForResult(mainActivity, REQUEST_CODE_FOR_DSN_SCAN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == REQUEST_CODE_FOR_DSN_SCAN || requestCode == REQUEST_CODE_FOR_DSN_INPUT) && resultCode == RESULT_OK) {//获取到了DSN
            if (data != null) {
                String deviceId = data.getStringExtra("result").trim();
                if (!TextUtils.isEmpty(deviceId)) {
                    if (deviceId.startsWith("Lark_DSN:") && deviceId.endsWith("##")) {
                        deviceId = deviceId.substring(9, deviceId.length() - 2).trim();
                    }
                    if (!TextUtils.isEmpty(deviceId)) {
                        Intent mainActivity = new Intent(this, DeviceAddActivity.class);
                        Bundle addInfo = getIntent().getBundleExtra("addInfo");
                        addInfo.putString("deviceId", deviceId);
                        mainActivity.putExtra("addInfo", addInfo);
                        startActivity(mainActivity);
                        return;
                    }
                }
            }
            CustomToast.makeText(this, "无效的设备ID号", R.drawable.ic_toast_warming);
        } else if (requestCode == REQUEST_CODE_FOR_DSN_SCAN && resultCode == ScanActivity.RESULT_FOR_INPUT) {//扫码页面回退到手动输入页面
            Intent mainActivity = new Intent(this, GatewayAddDsnInputActivity.class);
            startActivityForResult(mainActivity, REQUEST_CODE_FOR_DSN_INPUT);
        }
    }
}
