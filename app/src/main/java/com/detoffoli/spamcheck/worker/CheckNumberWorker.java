package com.detoffoli.spamcheck.worker;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkRequest;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.detoffoli.spamcheck.R;
import com.detoffoli.spamcheck.api.CheckNumberApi;
import com.detoffoli.spamcheck.Utility;

public class CheckNumberWorker extends Worker {

    public static final String TAG = "CheckNumberWorker";

    private static final String DATA_PHONE_NUMBER = "DATE_PHONE_NUMBER";
    private static final String DATA_UPDATE_NOTIFICATION = "";
    public static final String DATA_RETURN_PERCENTAGE = "DATA_RETURN_PERCENTAGE";

    public static WorkRequest getOnTimeRequest(@NonNull String phoneNumber, boolean updateNotification) {
        Data workData = new Data.Builder()
                .putString(DATA_PHONE_NUMBER, phoneNumber)
                .putBoolean(DATA_UPDATE_NOTIFICATION, updateNotification)
                .build();
        OneTimeWorkRequest.Builder builder = new OneTimeWorkRequest.Builder(CheckNumberWorker.class);
        builder.setInputData(workData);
        builder.addTag(TAG);
        return builder.build();
    }

    public CheckNumberWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        String phoneNumber = getInputData().getString(DATA_PHONE_NUMBER);
        boolean updateNotification = getInputData().getBoolean(DATA_UPDATE_NOTIFICATION, false);
        if (phoneNumber == null)
            return Result.failure();
        // Update notification
        if (updateNotification)
            showNotificationMessage(getApplicationContext().getString(R.string.Checking_call));
        // Fetching danger percentage
        String percentage = CheckNumberApi.getPercentageNumber(getApplicationContext(), phoneNumber);
        if (updateNotification) {
            String message = String.format("%s %s%%",
                    getApplicationContext().getString(R.string.Danger_level),
                    percentage
            );
            showNotificationMessage(message);
            Utility.toastThreadFree(getApplicationContext(), message);
        }
        return Result.success(this.getReturnDataPercentage(percentage));
    }

    private Data getReturnDataPercentage(String percentage) {
        return new Data.Builder().putString(DATA_RETURN_PERCENTAGE, percentage).build();
    }

    private void showNotificationMessage(@NonNull String message) {
        Utility.showMessageNotification(
                getApplicationContext(),
                Utility.CST_NOTIFICATION_ID_CALL_INCOMING,
                getApplicationContext().getString(R.string.Call_incoming),
                message
        );
    }
}
