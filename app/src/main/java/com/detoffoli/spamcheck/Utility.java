package com.detoffoli.spamcheck;

import android.app.ActivityManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class Utility {

    public static final String CST_CHANNEL_ID = "NOTIFICATION_CHANNEL_CHECK_NUMBER";
    public static final String SERVICE_NOTIFICATION_TITLE = "Service";
    public static final int CST_NOTIFICATION_ID_SERVICE_START = 328;
    public static final int CST_NOTIFICATION_ID_CALL_INCOMING = 328;

    public static boolean isServiceRunning(@NonNull Context context, @NonNull Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName()))
                return true;
        }
        return false;
    }

    public static void toastThreadFree(@NonNull Context context, @NonNull String message) {
        if (Looper.getMainLooper() == Looper.myLooper()) {
            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
        } else {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(() -> Toast.makeText(context, message, Toast.LENGTH_LONG).show());
        }
    }

    public static void openNotificationSettings(@NonNull Context context) {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setData(Uri.parse("package:" + context.getPackageName()));
        context.startActivity(intent);
    }

    public static void removeNotificationChannel(@NonNull Context context) {
        NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
        notificationManager.deleteNotificationChannel(CST_CHANNEL_ID);
    }

    public static void createNotificationChannel(@NonNull Context context) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        String channelName = context.getString(R.string.notification_channel_name);
        String channelDescription = context.getString(R.string.notification_channel_description);
        int importance = NotificationManager.IMPORTANCE_HIGH;
        NotificationChannel channel = new NotificationChannel(CST_CHANNEL_ID, channelName, importance);
        channel.setDescription(channelDescription);
        // Register the channel with the system; you can't change the importance
        // or other notification behaviors after this
        NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }

    public static void showTempNotification(@NonNull Context context,
                                            int notificationId,
                                            @NonNull String title,
                                            @NonNull String message) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CST_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setTimeoutAfter(3000)
                .setOnlyAlertOnce(true);
        NotificationManagerCompat.from(context).notify(notificationId, builder.build());
    }

    public static void showMessageNotification(@NonNull Context context,
                                               int notificationId,
                                               @NonNull String title,
                                               @NonNull String message) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CST_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setOnlyAlertOnce(true);
        NotificationManagerCompat.from(context).notify(notificationId, builder.build());
    }

    public static void removeNotification(@NonNull Context context, int notificationId) {
        NotificationManagerCompat.from(context).cancel(notificationId);
    }
}
