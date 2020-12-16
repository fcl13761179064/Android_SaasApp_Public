package com.ayla.hotelsaas;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.ayla.hotelsaas.ui.CustomToast;
import com.blankj.utilcode.util.AppUtils;

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
            @Override
            public void onDownloadSuccess(String path) {
                isWorking = false;
                Log.d(TAG, "onDownloadSuccess: " + path);
                notificationManagerCompat.cancel(notifyId);

                AppUtils.installApp(path);
            }

            @Override
            public void onDownloading(int progress) {
                isWorking = true;
                Log.d(TAG, "onDownloading: " + progress);
                Notification notification = builder.setProgress(100, progress, false).build();
                notificationManagerCompat.notify(notifyId, notification);
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
