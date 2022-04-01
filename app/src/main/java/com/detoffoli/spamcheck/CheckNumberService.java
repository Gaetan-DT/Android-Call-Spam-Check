package com.detoffoli.spamcheck;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.telephony.TelephonyManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.detoffoli.spamcheck.receiver.CallReviver;

public class CheckNumberService extends Service {

    private static final String TAG = "CheckNumberService";
    private static final int CST_FOREGROUND_ID = 39;

    private CallReviver mCallReviver;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(@NonNull Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate() called");
        Utility.createNotificationChannel(this);
        startForeground(CST_FOREGROUND_ID, this.getNotification());
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(TelephonyManager.ACTION_PHONE_STATE_CHANGED);
        this.mCallReviver = new CallReviver();
        registerReceiver(this.mCallReviver, intentFilter);
    }

    @Override
    public void onDestroy() {
        stopForeground(true);
        Log.d(TAG, "onDestroy() called");
        try {
            unregisterReceiver(this.mCallReviver);
        } catch (Exception e) {
            Log.e(TAG, "onDestroy: ", e);
        }
        super.onDestroy();
    }

    public Notification getNotification() {
        return new NotificationCompat.Builder(this, Utility.CST_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(Utility.SERVICE_NOTIFICATION_TITLE)
                .setContentText("Service started !")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setOnlyAlertOnce(true).build();
    }
}
