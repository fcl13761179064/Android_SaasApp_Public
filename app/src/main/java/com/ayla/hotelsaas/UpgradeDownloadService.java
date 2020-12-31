package com.ayla.hotelsaas;

import android.app.Activity;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.blankj.utilcode.util.IntentUtils;

public class UpgradeDownloadService extends IntentService {

    private static final String TAG = "UpgradeDownloadService";

    private final int notifyId = 123423;

    public static boolean isWorking;

    public UpgradeDownloadService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Log.d(TAG, "onHandleWork: ");

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
//                Intent installAppIntent = IntentUtils.getInstallAppIntent(path);
//                Notification build = builder.setContentText("下载完成,点击安装")
//                        .setContentIntent(PendingIntent.getActivity(getApplicationContext(), 0, installAppIntent, PendingIntent.FLAG_CANCEL_CURRENT))
//                        .setAutoCancel(true)
//                        .setOngoing(false)
//                        .build();
//                notificationManagerCompat.notify(notifyId, build);
                notificationManagerCompat.cancel(notifyId);

                Intent msg = new Intent();
                msg.setAction(UpgradeDownloadBroadcast.sAction_onDownloadSuccess);
                msg.putExtra("path", path);
                sendBroadcast(msg);
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

                Intent msg = new Intent();
                msg.setAction(UpgradeDownloadBroadcast.sAction_onDownloading);
                msg.putExtra("progress", progress);
                sendBroadcast(msg);
            }

            @Override
            public void onDownloadFailed() {
                isWorking = false;
                Log.d(TAG, "onDownloadFailed: ");
                notificationManagerCompat.cancel(notifyId);

                Intent msg = new Intent();
                msg.setAction(UpgradeDownloadBroadcast.sAction_onDownloadFailed);
                sendBroadcast(msg);
            }
        });
    }

    public static abstract class UpgradeDownloadBroadcast extends BroadcastReceiver {
        public static final String sAction_onDownloadFailed = "onDownloadFailed";
        public static final String sAction_onDownloading = "onDownloading";
        public static final String sAction_onDownloadSuccess = "onDownloadSuccess";

        @Override
        public final void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case sAction_onDownloadFailed:
                    onDownloadFailed();
                    break;
                case sAction_onDownloading:
                    onDownloading(intent.getIntExtra("progress", 0));
                    break;
                case sAction_onDownloadSuccess:
                    onDownloadSuccess(intent.getStringExtra("path"));
                    break;
            }
        }

        public abstract void onDownloadSuccess(String path);

        public abstract void onDownloading(int progress);

        public abstract void onDownloadFailed();

        public void register(Activity activity) {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(sAction_onDownloadSuccess);
            intentFilter.addAction(sAction_onDownloading);
            intentFilter.addAction(sAction_onDownloadFailed);
            activity.registerReceiver(this, intentFilter);
        }
    }
}
