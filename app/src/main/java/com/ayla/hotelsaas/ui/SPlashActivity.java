package com.ayla.hotelsaas.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;

import com.ayla.hotelsaas.BuildConfig;
import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.application.Constance;
import com.ayla.hotelsaas.base.BaseMvpActivity;
import com.ayla.hotelsaas.base.BasePresenter;
import com.ayla.hotelsaas.bean.BaseResult;
import com.ayla.hotelsaas.bean.VersionUpgradeBean;
import com.ayla.hotelsaas.mvp.model.RequestModel;
import com.ayla.hotelsaas.mvp.present.SplashPresenter;
import com.ayla.hotelsaas.mvp.view.SplashView;
import com.ayla.hotelsaas.utils.SharePreferenceUtils;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

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
    public void onVersionResult(VersionUpgradeBean baseResult) {
        final String sava_token = SharePreferenceUtils.getString(SPlashActivity.this, Constance.SP_Login_Token, null);
        Intent intent;
        if (TextUtils.isEmpty(sava_token)) {
            intent = new Intent(SPlashActivity.this, LoginActivity.class);
        } else {
            intent = new Intent(SPlashActivity.this, ProjectListActivity.class);
        }
        if (baseResult != null) {//如果有更新信息，就携带更新信息到下一个页面。
            intent.putExtra("upgrade", baseResult);
        }
        startActivity(intent);
        finish();
    }
}
