package com.ayla.hotelsaas.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;

import com.aliyun.iot.aep.sdk.IoTSmart;
import com.ayla.hotelsaas.BuildConfig;
import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.application.Constance;
import com.ayla.hotelsaas.application.MyApplication;
import com.ayla.hotelsaas.base.BaseMvpActivity;
import com.ayla.hotelsaas.base.BasePresenter;
import com.ayla.hotelsaas.utils.SharePreferenceUtils;
import com.blankj.utilcode.util.AppUtils;

public class SPlashActivity extends BaseMvpActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        IoTSmart.init(MyApplication.getInstance(), new IoTSmart.InitConfig().setDebug(BuildConfig.DEBUG));
        new Handler(getMainLooper())
                .postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        final String sava_token = SharePreferenceUtils.getString(SPlashActivity.this, Constance.SP_Login_Token, null);
                        if (sava_token == null || TextUtils.isEmpty(sava_token)) {
                            Intent intent = new Intent(SPlashActivity.this, LoginActivity.class);
                            startActivity(intent);
                        } else {
                            Intent intent = new Intent(SPlashActivity.this, WorkRoomManageActivity.class);
                            startActivity(intent);
                        }
                        finish();
                    }
                }, 2000L);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_splash;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initListener() {

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        AppUtils.exitApp();
    }

    @Override
    protected BasePresenter initPresenter() {
        return null;
    }
}
