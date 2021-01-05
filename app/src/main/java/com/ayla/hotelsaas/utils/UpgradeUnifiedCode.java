package com.ayla.hotelsaas.utils;

import android.content.Intent;
import android.text.TextUtils;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.UpgradeDownloadService;
import com.ayla.hotelsaas.application.Constance;
import com.ayla.hotelsaas.bean.VersionUpgradeBean;
import com.ayla.hotelsaas.ui.CustomToast;
import com.ayla.hotelsaas.widget.CustomAlarmDialog;
import com.ayla.hotelsaas.widget.LoadingDialog;
import com.blankj.utilcode.util.AppUtils;

public class UpgradeUnifiedCode {

    private UpgradeUnifiedCode() {
    }

    public static void handleUpgrade(@NonNull AppCompatActivity activity, @NonNull VersionUpgradeBean
            upgradeBean) {
        CustomAlarmDialog customAlarmDialog = CustomAlarmDialog.newInstance()
                .setDoneCallback(new CustomAlarmDialog.Callback() {
                    String _path = null;
                    boolean hasAskInstall = false;

                    @Override
                    public void onDone(CustomAlarmDialog dialog) {

                        dialog.dismissAllowingStateLoss();

                        Constance.saveVersionUpgradeInfo(null);

                        LoadingDialog loadingDialog = LoadingDialog.newInstance("更新中...");
                        loadingDialog.setCancelable(false);
                        loadingDialog.show(activity.getSupportFragmentManager(), "downloading");

                        UpgradeDownloadService.UpgradeDownloadBroadcast broadcast = new UpgradeDownloadService.UpgradeDownloadBroadcast() {
                            @Override
                            public void onDownloadSuccess(String path) {
                                activity.unregisterReceiver(this);
                                loadingDialog.dismissAllowingStateLoss();
                                _path = path;
                                if (activity.getLifecycle().getCurrentState() == Lifecycle.State.RESUMED) {
                                    hasAskInstall = true;
                                    AppUtils.installApp(path);
                                }
                            }

                            @Override
                            public void onDownloading(int progress) {
                                TextView textView = loadingDialog.getDialog().getWindow().findViewById(R.id.tv);
                                if (textView != null) {
                                    textView.setText(String.format("更新中%s%%...", progress));
                                }
                            }

                            @Override
                            public void onDownloadFailed() {
                                activity.unregisterReceiver(this);
                                loadingDialog.dismissAllowingStateLoss();
                                CustomToast.makeText(activity, "更新失败", R.drawable.ic_toast_warming);
                                if (upgradeBean.getIsForce() != 0) {
                                    AppUtils.exitApp();
                                }
                            }
                        };

                        broadcast.register(activity);

                        Intent intent = new Intent(activity, UpgradeDownloadService.class)
                                .putExtra("url", upgradeBean.getUrl())
                                .putExtra("title", upgradeBean.getVersion());
                        activity.startService(intent);
                        activity.getLifecycle().addObserver(new LifecycleEventObserver() {
                            @Override
                            public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
                                if (event == Lifecycle.Event.ON_RESUME) {
                                    if (hasAskInstall && upgradeBean.getIsForce() != 0) {
                                        AppUtils.exitApp();
                                    } else if (!TextUtils.isEmpty(_path) && !hasAskInstall) {
                                        hasAskInstall = true;
                                        AppUtils.installApp(_path);
                                    }
                                }
                            }
                        });
                    }

                    @Override
                    public void onCancel(CustomAlarmDialog dialog) {
                        dialog.dismissAllowingStateLoss();
                        if (upgradeBean.getIsForce() != 0) {
                            AppUtils.exitApp();
                        }
                    }
                })
                .setTitle("发现新版本")
                .setContent(upgradeBean.getVersionInfo())
                .setEnsureText("升级");
        customAlarmDialog.setCancelable(false);
        customAlarmDialog.show(activity.getSupportFragmentManager(), "upgrade");
    }

}
