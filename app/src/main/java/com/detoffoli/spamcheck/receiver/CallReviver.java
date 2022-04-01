package com.detoffoli.spamcheck.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import com.detoffoli.spamcheck.worker.CheckNumberWorker;

public class CallReviver extends BroadcastReceiver {

    private static final String TAG = "CallReviver";

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            Log.d(TAG, "onReceive() called with: context = [" + context + "], intent = [" + intent + "]");
            String phoneState = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
            if (phoneState == null)
                return;
            Bundle extras = intent.getExtras();
            if (phoneState.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                if (extras == null)
                    return;
                String incomingNumber = extras.getString(TelephonyManager.EXTRA_INCOMING_NUMBER);
                if (incomingNumber == null)
                    return;
                this.startCheckNumberWorker(context, incomingNumber);
            }
        } catch (Exception e) {
            Log.e(TAG, "onReceive: ", e);
            Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    public void startCheckNumberWorker(Context context, String incomingNumber) {
        WorkRequest onTimeRequest = CheckNumberWorker.getOnTimeRequest(incomingNumber, true);
        WorkManager.getInstance(context).enqueue(onTimeRequest);
    }
}
