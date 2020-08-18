/*
 * create by cairurui on 18-11-1 下午3:29.
 * Copyright (c) 2018 SunseaIoT. All rights reserved.
 */

package com.ayla.hotelsaas.ui;

import android.Manifest;
import android.content.Intent;
import android.os.Vibrator;
import android.widget.ImageView;
import android.widget.TextView;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.base.BaseMvpActivity;
import com.ayla.hotelsaas.base.BasePresenter;
import com.ayla.hotelsaas.widget.CustomAlarmDialog;
import com.blankj.utilcode.util.PermissionUtils;
import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;

import butterknife.BindView;
import butterknife.OnClick;
import cn.bingoogolapple.qrcode.core.QRCodeView;
import cn.bingoogolapple.qrcode.zbar.ZBarView;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 *
 */
public class ScanActivity extends BaseMvpActivity implements QRCodeView.Delegate {
    public static final int RESULT_FOR_INPUT = -2;
    @BindView(R.id.zxingview)
    ZBarView mZXingView;
    @BindView(R.id.iv_flash_light)
    ImageView mFlightLightStateImageView;
    @BindView(R.id.tv_flash_light)
    TextView mFlashLightTextView;

    private boolean firstLoad = true;
    private boolean flashLightState = false;

    @Override
    protected void onStart() {
        super.onStart();
        adjustFlashLight(false);
        startScan();
    }

    protected void startScan() {
        Disposable subscribe = new RxPermissions(this)
                .requestEachCombined(Manifest.permission.CAMERA)
                .subscribe(new Consumer<Permission>() {
                    @Override
                    public void accept(Permission permission) throws Exception {
                        if (permission.granted) {
                            mZXingView.post(new Runnable() {
                                @Override
                                public void run() {
                                    mZXingView.startSpotAndShowRect(); // 显示扫描框，并开始识别
                                }
                            });
                        } else if (permission.shouldShowRequestPermissionRationale) {
                            CustomToast.makeText(getBaseContext(), "请允许使用相机权限", R.drawable.ic_toast_warming).show();
                        } else if (firstLoad) {
                            firstLoad = false;
                            CustomAlarmDialog
                                    .newInstance(new CustomAlarmDialog.Callback() {
                                        @Override
                                        public void onDone(CustomAlarmDialog dialog) {
                                            dialog.dismissAllowingStateLoss();
                                            finish();
                                            PermissionUtils.launchAppDetailsSettings();
                                        }

                                        @Override
                                        public void onCancel(CustomAlarmDialog dialog) {
                                            dialog.dismissAllowingStateLoss();
                                        }
                                    })
                                    .setContent("请允许使用相机权限").show(getSupportFragmentManager(), null);
                        }
                    }
                });
    }

    @Override
    protected void onStop() {
        super.onStop();
        adjustFlashLight(false);
        mZXingView.stopCamera(); // 关闭摄像头预览，并且隐藏扫描框
    }

    @Override
    protected BasePresenter initPresenter() {
        return null;
    }

    @Override
    protected void onDestroy() {
        mZXingView.onDestroy(); // 销毁二维码扫描控件
        super.onDestroy();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_scan_share;
    }

    @Override
    protected void initView() {
        mZXingView.setDelegate(this);
    }

    @Override
    protected void initListener() {

    }

    @Override
    public void onScanQRCodeSuccess(String result) {
        vibrate();
        setResult(RESULT_OK, new Intent().putExtra("result", result));
        finish();
    }

    private void vibrate() {
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        vibrator.vibrate(200);
    }

    @Override
    public void onCameraAmbientBrightnessChanged(boolean isDark) {

    }

    @Override
    public void onScanQRCodeOpenCameraError() {
        CustomToast.makeText(getBaseContext(), "打开相机出错", R.drawable.ic_toast_warming).show();
        finish();
    }

    @OnClick(R.id.flash_light)
    public void onFlashLight() {
        adjustFlashLight(!flashLightState);
    }

    @OnClick(R.id.ll_write)
    public void jumpWritePage() {
        setResult(RESULT_FOR_INPUT);
        finish();
    }

    private void adjustFlashLight(boolean open) {
        if (open) {
            mZXingView.openFlashlight();
            mFlightLightStateImageView.setImageResource(R.drawable.ic_scan_flash_off);
            mFlashLightTextView.setText("关闭灯光");
        } else {
            mZXingView.closeFlashlight();
            mFlightLightStateImageView.setImageResource(R.drawable.ic_scan_flash_on);
            mFlashLightTextView.setText("打开灯光");
        }
        flashLightState = open;
    }
}
