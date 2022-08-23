package com.ayla.hotelsaas.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.constant.ConstantValue;
import com.ayla.hotelsaas.base.BaseMvpActivity;
import com.ayla.hotelsaas.bean.VersionUpgradeBean;
import com.ayla.hotelsaas.mvp.present.SplashPresenter;
import com.ayla.hotelsaas.mvp.view.SplashView;
import com.ayla.hotelsaas.utils.SharePreferenceUtils;
import com.ayla.hotelsaas.utils.UpgradeUnifiedCode;

public class SPlashActivity extends BaseMvpActivity<SplashView, SplashPresenter> implements SplashView {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter.fetchVersionUpdate();
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
    protected SplashPresenter initPresenter() {
        return new SplashPresenter();
    }

    @Override
    public void onVersionResult(boolean requireSuccess, VersionUpgradeBean baseResult) {
        if (baseResult != null) {//请求到了版本信息
            if (baseResult.getIsForce() != 0) {//强制更新
                UpgradeUnifiedCode.handleUpgrade(this, baseResult);
                return;
            }
        }

        final String sava_token = SharePreferenceUtils.getString(SPlashActivity.this, ConstantValue.SP_Login_Token, null);
        Intent intent;
        if (TextUtils.isEmpty(sava_token)) {
            intent = new Intent(SPlashActivity.this, LoginActivity.class);
        } else if (!requireSuccess) {//没有请求到版本信息
            intent = new Intent(SPlashActivity.this, LoginActivity.class);
        } else {
            intent = new Intent(SPlashActivity.this, ProjectListActivity.class);
        }
        intent.putExtra("upgrade", baseResult);
        startActivity(intent);
        finish();
    }
}
