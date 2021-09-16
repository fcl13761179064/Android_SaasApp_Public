/*
 * create by cairurui on 18-11-1 下午3:29.
 * Copyright (c) 2018 SunseaIoT. All rights reserved.
 */

package com.ayla.hotelsaas.ui;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Vibrator;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.base.BaseMvpActivity;
import com.ayla.hotelsaas.bean.MoveWallBean;
import com.ayla.hotelsaas.bean.ZxingMoveWallBean;
import com.ayla.hotelsaas.mvp.present.MoveWallPresenter;
import com.ayla.hotelsaas.mvp.view.MoveWallView;
import com.ayla.hotelsaas.utils.DateUtils;
import com.ayla.hotelsaas.widget.CustomAlarmDialog;
import com.blankj.utilcode.util.GsonUtils;
import com.blankj.utilcode.util.PermissionUtils;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;

import butterknife.BindView;
import butterknife.OnClick;
import cn.bingoogolapple.qrcode.core.QRCodeView;
import cn.bingoogolapple.qrcode.zbar.ZBarView;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

import static com.ayla.hotelsaas.ui.MainActivity.RESULT_CODE_RENAMED;

/**
 *
 */
public class MoveExhibitionWallScanActivity extends BaseMvpActivity<MoveWallView, MoveWallPresenter> implements QRCodeView.Delegate, MoveWallView {
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
                                    try {
                                        mZXingView.startSpotAndShowRect(); // 显示扫描框，并开始识别
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        } else if (permission.shouldShowRequestPermissionRationale) {
                            CustomAlarmDialog
                                    .newInstance(new CustomAlarmDialog.Callback() {
                                        @Override
                                        public void onDone(CustomAlarmDialog dialog) {
                                            dialog.dismissAllowingStateLoss();
                                            Intent localIntent = new Intent();
                                            localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                                            localIntent.setData(Uri.fromParts("package", getPackageName(), null));
                                            startActivity(localIntent);
                                        }

                                        @Override
                                        public void onCancel(CustomAlarmDialog dialog) {
                                            dialog.dismissAllowingStateLoss();
                                            finish();
                                        }
                                    })
                                    .setTitle("获取相机权限")
                                    .setEnsureText("前往开启")
                                    .setContent("需要使用相机权限，用以扫描二维码点击“前往开启”打开相机权限")
                                    .show(getSupportFragmentManager(), "");
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
    protected MoveWallPresenter initPresenter() {
        return new MoveWallPresenter();
    }

    @Override
    protected void onDestroy() {
        mZXingView.onDestroy(); // 销毁二维码扫描控件
        super.onDestroy();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_move_wallo_scan_share;
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
        try {
            Type type = new TypeToken<ZxingMoveWallBean>() {
            }.getType();
            ZxingMoveWallBean obj = GsonUtils.fromJson(result, type);
            if (obj != null) {
                if (!TextUtils.isEmpty(obj.getType())&&!TextUtils.isEmpty(obj.getParam())) {
                    ZxingMoveWallBean zxingMoveWallBean = setDecrypt(obj.getParam());
                    mPresenter.getNetworkConfigGuide(s, obj);
                } else {
                    CustomAlarmDialog.newInstance().setTitle("信息错误")
                            .setContent(String.format("二维码信息错误，请检查信息正确后再扫描二维码"))
                            .setStyle(CustomAlarmDialog.Style.STYLE_SINGLE_BUTTON)
                            .setEnsureText("重试")
                            .setDoneCallback(new CustomAlarmDialog.Callback() {
                                @Override
                                public void onDone(CustomAlarmDialog dialog) {
                                    dialog.dismissAllowingStateLoss();
                                    mZXingView.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            mZXingView.startSpotAndShowRect(); // 显示扫描框，并开始识别
                                        }
                                    });
                                }

                                @Override
                                public void onCancel(CustomAlarmDialog dialog) {

                                }
                            })
                            .show(getSupportFragmentManager(), "dialog");
                    return;
                }

            } else {
                CustomAlarmDialog.newInstance().setTitle("信息错误")
                        .setContent(String.format("二维码信息错误，请检查信息正确后再扫描二维码"))
                        .setStyle(CustomAlarmDialog.Style.STYLE_SINGLE_BUTTON)
                        .setEnsureText("重试")
                        .setDoneCallback(new CustomAlarmDialog.Callback() {
                            @Override
                            public void onDone(CustomAlarmDialog dialog) {
                                dialog.dismissAllowingStateLoss();
                                mZXingView.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        mZXingView.startSpotAndShowRect(); // 显示扫描框，并开始识别
                                    }
                                });
                            }

                            @Override
                            public void onCancel(CustomAlarmDialog dialog) {

                            }
                        })
                        .show(getSupportFragmentManager(), "dialog");
            }

        } catch (Exception e) {
            e.printStackTrace();
            CustomAlarmDialog.newInstance().setTitle("信息错误")
                    .setContent(String.format("二维码信息错误，请检查信息正确后再扫描二维码"))
                    .setStyle(CustomAlarmDialog.Style.STYLE_SINGLE_BUTTON)
                    .setEnsureText("重试")
                    .setDoneCallback(new CustomAlarmDialog.Callback() {
                        @Override
                        public void onDone(CustomAlarmDialog dialog) {
                            dialog.dismissAllowingStateLoss();
                            mZXingView.post(new Runnable() {
                                @Override
                                public void run() {
                                    mZXingView.startSpotAndShowRect(); // 显示扫描框，并开始识别
                                }
                            });
                        }

                        @Override
                        public void onCancel(CustomAlarmDialog dialog) {

                        }
                    })
                    .show(getSupportFragmentManager(), "dialog");
            return;
        }
        vibrate();
        if (TextUtils.isEmpty(result)) {
            return;
        }

    }

    /**
     * 解密
     * encodeWord：加密后的文字/比如密码
     */
    public ZxingMoveWallBean setDecrypt(String encodeWord){

        try {
            String decodeWord = new String(Base64.decode(encodeWord.getBytes(), Base64.DEFAULT));
            String decodeWordtwo = Uri.decode(decodeWord);
            Type type = new TypeToken<ZxingMoveWallBean>() {}.getType();
            ZxingMoveWallBean obj = GsonUtils.fromJson(decodeWordtwo, type);
            return obj;
        } catch (Exception e) {
            e.printStackTrace();
        }
       return null;
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
        CustomToast.makeText(getBaseContext(), "打开相机出错", R.drawable.ic_toast_warming);
        finish();
    }

    @OnClick(R.id.flash_light)
    public void onFlashLight() {
        adjustFlashLight(!flashLightState);
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

    @Override
    public void getMoveWallDataSuccess(MoveWallBean moveWallBean, ZxingMoveWallBean move_wall_data) {
        if (moveWallBean != null) {
            if (moveWallBean.getEndDate() != null) {
                try {
                    String shigong_time = DateUtils.formatTimeEight(moveWallBean.getEndDate());
                    String currentDate = DateUtils.getThisDate();
                    boolean compare = DateUtils.compare(currentDate, shigong_time);
                    if (compare) {
                        CustomAlarmDialog.newInstance().setTitle("历史项目")
                                .setContent(String.format("历史项目无法操作，请联系艾拉客服部进行开通"))
                                .setStyle(CustomAlarmDialog.Style.STYLE_SINGLE_BUTTON)
                                .setEnsureText("重试")
                                .setDoneCallback(new CustomAlarmDialog.Callback() {
                                    @Override
                                    public void onDone(CustomAlarmDialog dialog) {
                                        dialog.dismissAllowingStateLoss();
                                        mZXingView.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                mZXingView.startSpotAndShowRect(); // 显示扫描框，并开始识别
                                            }
                                        });
                                    }

                                    @Override
                                    public void onCancel(CustomAlarmDialog dialog) {

                                    }
                                })
                                .show(getSupportFragmentManager(), "dialog");
                        return;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("roomId", move_wall_data.getRoomId());
            intent.putExtra("roomName", move_wall_data.getName());
            intent.putExtra("move_wall_type", moveWallBean.getType());
            startActivityForResult(intent, RESULT_CODE_RENAMED);
            finish();
        } else {
            CustomAlarmDialog.newInstance().setTitle("信息错误")
                    .setContent(String.format("二维码信息错误，请检查信息正确后再扫描二维码"))
                    .setStyle(CustomAlarmDialog.Style.STYLE_SINGLE_BUTTON)
                    .setEnsureText("重试")
                    .setDoneCallback(new CustomAlarmDialog.Callback() {
                        @Override
                        public void onDone(CustomAlarmDialog dialog) {
                            dialog.dismissAllowingStateLoss();
                            mZXingView.post(new Runnable() {
                                @Override
                                public void run() {
                                    mZXingView.startSpotAndShowRect(); // 显示扫描框，并开始识别
                                }
                            });
                        }

                        @Override
                        public void onCancel(CustomAlarmDialog dialog) {

                        }
                    })
                    .show(getSupportFragmentManager(), "dialog");
        }
    }

    @Override
    public void getMoveWallDataFail(String o) {
        CustomAlarmDialog.newInstance().setTitle("无施工权限")
                .setContent(String.format("您没有当前房间的施工权限，请联系艾拉客服部进行开通"))
                .setStyle(CustomAlarmDialog.Style.STYLE_SINGLE_BUTTON)
                .setEnsureText("重试")
                .setDoneCallback(new CustomAlarmDialog.Callback() {
                    @Override
                    public void onDone(CustomAlarmDialog dialog) {
                        dialog.dismissAllowingStateLoss();
                        mZXingView.post(new Runnable() {
                            @Override
                            public void run() {
                                mZXingView.startSpotAndShowRect(); // 显示扫描框，并开始识别
                            }
                        });
                    }

                    @Override
                    public void onCancel(CustomAlarmDialog dialog) {

                    }
                })
                .show(getSupportFragmentManager(), "dialog");
    }
}
