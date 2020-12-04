package com.ayla.hotelsaas.utils;

import android.app.Activity;
import android.app.DownloadManager;
import android.net.Uri;
import android.os.Environment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.ayla.hotelsaas.application.MyApplication;
import com.ayla.hotelsaas.bean.VersionUpgradeBean;
import com.ayla.hotelsaas.widget.CustomAlarmDialog;
import com.blankj.utilcode.util.AppUtils;

import static android.content.Context.DOWNLOAD_SERVICE;

public class UpgradeUnifiedCode {
    public static void handleUpgrade(@NonNull AppCompatActivity activity, @NonNull VersionUpgradeBean upgradeBean) {
        CustomAlarmDialog.newInstance(new CustomAlarmDialog.Callback() {
            @Override
            public void onDone(CustomAlarmDialog dialog) {
                dialog.dismiss();
                DownloadManager downloadManager = (DownloadManager) activity.getSystemService(DOWNLOAD_SERVICE);
                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(upgradeBean.getUrl()));
                request.setDestinationInExternalFilesDir(MyApplication.getContext(), Environment.DIRECTORY_DOWNLOADS, "/upgrade/" + upgradeBean.getVersion() + "/app.apk");
                request.setTitle(upgradeBean.getName())
                        .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                        .setAllowedOverMetered(false).setAllowedOverRoaming(false);
                long enqueue = downloadManager.enqueue(request);
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
