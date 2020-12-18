package com.ayla.hotelsaas;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.ayla.hotelsaas.ui.CustomToast;
import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.IntentUtils;

public class UpgradeDownloadService extends JobIntentService {

    private static final String TAG = "UpgradeDownloadService";

    private final int notifyId = 123423;

    public static boolean isWorking;

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        String url = intent.getStringExtra("url");
        String title = intent.getStringExtra("title");
        String saveDir = "upgrade";


        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(getApplicationContext());

        String notificationChannelId = "应用升级进度通知";
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel("应用升级进度通知", "应用升级进度通知", NotificationManager.IMPORTANCE_LOW);

            NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            nm.createNotificationChannel(notificationChannel);

            notificationChannelId = notificationChannel.getId();
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), notificationChannelId);
        builder.setSmallIcon(R.mipmap.ic_launcher).setContentTitle(title).setOngoing(true);

        new DownloadUtil().download(getApplicationContext(), url, saveDir, new DownloadUtil.OnDownloadListener() {

            int currentProgress;

            @Override
            public void onDownloadSuccess(String path) {
                isWorking = false;
                Log.d(TAG, "onDownloadSuccess: " + path);
                Intent installAppIntent = IntentUtils.getInstallAppIntent(path);
                Notification build = builder.setContentText("下载完成,点击安装")
                        .setContentIntent(PendingIntent.getActivity(getApplicationContext(), 0, installAppIntent, PendingIntent.FLAG_CANCEL_CURRENT))
                        .setAutoCancel(true)
                        .build();
                notificationManagerCompat.notify(notifyId, build);
                AppUtils.installApp(path);
            }

            @Override
            public void onDownloading(int progress) {
                if (currentProgress == progress) {
                    return;
                }
                isWorking = true;
                Log.d(TAG, "onDownloading: " + progress);
                Notification notification = builder.setProgress(100, progress, false).setContentText("已完成:" + progress + "%").build();
                notificationManagerCompat.notify(notifyId, notification);
                currentProgress = progress;
            }

            @Override
            public void onDownloadFailed() {
                isWorking = false;
                Log.d(TAG, "onDownloadFailed: ");
                notificationManagerCompat.cancel(notifyId);
                CustomToast.makeText(getApplicationContext(), "更新失败", R.drawable.ic_toast_warming);
            }
        });
    }
}
