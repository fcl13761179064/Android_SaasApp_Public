package com.ayla.hotelsaas.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.application.Constance;
import com.ayla.hotelsaas.base.BaseMvpActivity;
import com.ayla.hotelsaas.base.BasePresenter;
import com.ayla.hotelsaas.utils.SharePreferenceUtils;

public class SPlashActivity extends BaseMvpActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new Handler(getMainLooper())
                .postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        final String sava_token = SharePreferenceUtils.getString(SPlashActivity.this, Constance.SP_Login_Token, null);
                        if (TextUtils.isEmpty(sava_token)) {
                            Intent intent = new Intent(SPlashActivity.this, LoginActivity.class);
                            startActivity(intent);
                        } else {
                            Intent intent = new Intent(SPlashActivity.this, ProjectListActivity.class);
                            startActivity(intent);
                        }
                        finish();
                    }
                }, 1000L);
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
    protected BasePresenter initPresenter() {
        return null;
    }
}
