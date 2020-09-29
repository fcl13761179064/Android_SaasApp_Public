package com.ayla.hotelsaas.base;


import android.annotation.TargetApi;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.utils.ClickUtils;
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
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);// 锁定竖屏
        setContentView(getLayoutId());
        unbinder = ButterKnife.bind(this);
        initSaveInstace(savedInstanceState);
        refreshUI();
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

    /**
     * 需要从savedInstanceState拿数据的调用改方法
     */
    public void initSaveInstace(Bundle savedInstanceState) {
    }

    // 初始化UI，setContentView等
    protected abstract int getLayoutId();

    protected abstract void initView();

    protected abstract void initListener();


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

    public void refreshUI() {
        final View appbarRoot = findViewById(R.id.appbar_root_rl_ff91090);
        if (appbarRoot != null) {
            View leftIV = appbarRoot.findViewById(R.id.iv_left);
            if (leftIV != null && !leftIV.hasOnClickListeners()) {
                ClickUtils.applySingleDebouncing(leftIV, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        appBarLeftIvClicked();
                    }
                });
            }
            View leftTV = appbarRoot.findViewById(R.id.tv_left);
            if (leftTV != null && !leftTV.hasOnClickListeners()) {
                ClickUtils.applySingleDebouncing(leftTV, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        appBarLeftTvClicked();
                    }
                });
            }
            View rightIV = appbarRoot.findViewById(R.id.iv_right);
            if (rightIV != null && !rightIV.hasOnClickListeners()) {
                ClickUtils.applySingleDebouncing(rightIV, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        appBarRightIvClicked();
                    }
                });
            }
            View rightTv = appbarRoot.findViewById(R.id.tv_right);
            if (rightTv != null && !rightTv.hasOnClickListeners()) {
                ClickUtils.applySingleDebouncing(rightTv, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        appBarRightTvClicked();
                    }
                });
            }
        }
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

