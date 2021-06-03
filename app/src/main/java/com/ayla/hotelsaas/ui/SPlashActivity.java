package com.ayla.hotelsaas.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.application.Constance;
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

        //final String sava_token = SharePreferenceUtils.getString(SPlashActivity.this, Constance.SP_Login_Token, null);
        final String sava_token = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxMjk5MjY1NDgxODg2MjE2MjEzIiwidXNlck5hbWUiOiLkuI3opoHliqjkvZXkuInog5YiLCJsb2dpblR5cGUiOiIxIiwibG9naW5Tb3VyY2UiOiIxIiwiYXlsYUFwcGxpY2F0aW9uSWQiOiIzIiwidHlwZSI6ImF1dGhfdG9rZW4iLCJpYXQiOjE2MjI2MDQwMzR9.W5jGA-wBoQieRyVMzi4Oik4wki1TXaPIsCmMKN5Pb_k";
        SharePreferenceUtils.saveString(SPlashActivity.this, Constance.SP_Login_Token, "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxMjk5MjY1NDgxODg2MjE2MjEzIiwidXNlck5hbWUiOiLkuI3opoHliqjkvZXkuInog5YiLCJsb2dpblR5cGUiOiIxIiwibG9naW5Tb3VyY2UiOiIxIiwiYXlsYUFwcGxpY2F0aW9uSWQiOiIzIiwidHlwZSI6ImF1dGhfdG9rZW4iLCJpYXQiOjE2MjI2MzUwNjB9.xYpOOBCI314FemVNoewNNflNBoRZPIyk8DGjhwGZ-Gg");
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
