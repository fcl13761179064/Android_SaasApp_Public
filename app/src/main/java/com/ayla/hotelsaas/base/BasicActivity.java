package com.ayla.hotelsaas.base;


import android.annotation.TargetApi;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.widget.LoadingDialog;
import com.blankj.utilcode.util.BarUtils;
import com.blankj.utilcode.util.ScreenUtils;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * author fancunlei
 * BaseActivity
 * 基础Activity
 */
abstract class BasicActivity extends AppCompatActivity {

    private Unbinder unbinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        unbinder = ButterKnife.bind(this);
        initAppbar();
        initView();
        initListener();
        if (!ScreenUtils.isFullScreen(this)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                BarUtils.setStatusBarColor(this, Color.TRANSPARENT);
                BarUtils.setStatusBarLightMode(this, true);
            } else {
                BarUtils.setStatusBarColor(this, ContextCompat.getColor(this, R.color.color_statusbar_compare_color));
            }
        }
    }

    protected abstract int getLayoutId();

    protected abstract void initView();

    protected abstract void initListener();

    private void initAppbar() {
        final View appbarRoot = findViewById(R.id.appbar_root_rl_ff91090);
        if (appbarRoot != null) {
            View leftIV = appbarRoot.findViewById(R.id.iv_left);
            if (leftIV != null && !leftIV.hasOnClickListeners()) {
                leftIV.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        appBarLeftIvClicked();
                    }
                });
            }
            View leftTV = appbarRoot.findViewById(R.id.tv_left);
            if (leftTV != null && !leftTV.hasOnClickListeners()) {
                leftTV.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        appBarLeftTvClicked();
                    }
                });
            }
            View rightIV = appbarRoot.findViewById(R.id.iv_right);
            if (rightIV != null && !rightIV.hasOnClickListeners()) {
                rightIV.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        appBarRightIvClicked();
                    }
                });
            }
            View rightTv = appbarRoot.findViewById(R.id.tv_right);
            if (rightTv != null && !rightTv.hasOnClickListeners()) {
                rightTv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        appBarRightTvClicked();
                    }
                });
            }
        }
    }

    private LoadingDialog progressDialog;

    public void showProgress() {
        showProgress("加载中...");
    }

    @TargetApi(17)
    public boolean isFinished() {
        if (Build.VERSION.SDK_INT >= 16) {
            return isDestroyed() || isFinishing();
        } else {
            return isFinishing();
        }
    }


    public void showProgress(String msg) {
        if (isFinished() || isDestroyed()) {
            return;
        }
        if (null != progressDialog) {
            return;
        }
        progressDialog = LoadingDialog.newInstance(msg);
        progressDialog.show(getSupportFragmentManager(), "loading");
    }

    public void hideProgress() {
        if (null != progressDialog) {
            progressDialog.dismissAllowingStateLoss();
        }
        progressDialog = null;
    }

    /**
     * appbar左侧图标点击事件
     */
    protected void appBarLeftIvClicked() {
        onBackPressed();
    }

    protected void appBarLeftTvClicked() {
        onBackPressed();
    }

    protected void appBarRightIvClicked() {

    }

    protected void appBarRightTvClicked() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}

