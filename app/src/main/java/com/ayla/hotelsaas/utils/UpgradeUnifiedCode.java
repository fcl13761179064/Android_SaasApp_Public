package com.ayla.hotelsaas.utils;

import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.ayla.hotelsaas.UpgradeDownloadService;
import com.ayla.hotelsaas.bean.VersionUpgradeBean;
import com.ayla.hotelsaas.widget.CustomAlarmDialog;
import com.blankj.utilcode.util.AppUtils;

public class UpgradeUnifiedCode {

    private UpgradeUnifiedCode() {
    }

    public static void handleUpgrade(@NonNull AppCompatActivity activity, @NonNull VersionUpgradeBean
            upgradeBean) {
        CustomAlarmDialog.newInstance()
                .setDoneCallback(new CustomAlarmDialog.Callback() {
                    @Override
                    public void onDone(CustomAlarmDialog dialog) {
                        dialog.dismiss();
                        Intent intent = new Intent().putExtra("url", upgradeBean.getUrl())
                                .putExtra("title", upgradeBean.getVersion());
                        UpgradeDownloadService.enqueueWork(activity, UpgradeDownloadService.class, 123, intent);
                    }

                    @Override
                    public void onCancel(CustomAlarmDialog dialog) {
                        dialog.dismiss();
                        if (upgradeBean.getIsForce() != 0) {
                            AppUtils.exitApp();
                        }
                    }
                }).setTitle("发现新版本").setContent(upgradeBean.getVersionInfo())
                .setEnsureText("升级")
                .show(activity.getSupportFragmentManager(), "upgrade");
    }

}
