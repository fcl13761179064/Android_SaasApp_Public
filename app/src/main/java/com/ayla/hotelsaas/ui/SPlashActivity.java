package com.ayla.hotelsaas.ui;

import android.content.Intent;
import android.os.Handler;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.base.BasicActivity;
import com.ayla.hotelsaas.utils.AppManager;
import com.ayla.hotelsaas.utils.StatusBarUtil;

public class SPlashActivity extends BasicActivity {
    @Override
    protected int getLayoutId() {
        return R.layout.activity_splash;
    }

    @Override
    protected void setStatusBar() {
        StatusBarUtil.setTransparent(this);
    }


    @Override
    protected void initView() {

    }

    @Override
    protected void initListener() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SPlashActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        }, 2000L);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        AppManager.getAppManager().AppExit(this);
    }
}
