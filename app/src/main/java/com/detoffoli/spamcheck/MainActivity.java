package com.detoffoli.spamcheck;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.work.ListenableWorker;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import com.detoffoli.spamcheck.databinding.ActivityMainBinding;
import com.detoffoli.spamcheck.worker.CheckNumberWorker;
import com.detoffoli.spamcheck.worker.TestApiKeyWorker;
import com.detoffoli.spamcheck.worker.TestApiWorker;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity {

    //private static final String TAG = MainActivity.class.getSimpleName();

    private ActivityMainBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(this.mBinding.getRoot());
        this.mBinding.startService.setOnClickListener(v -> this.startServiceCheckNumber());
        this.mBinding.stopService.setOnClickListener(v -> this.stopServiceCheckNumber());
        this.mBinding.btnTestNotification.setOnClickListener(v -> this.testNotification());
        this.mBinding.btnGotoNotificationSettings.setOnClickListener(v -> Utility.openNotificationSettings(this));
        this.mBinding.btnSaveCheckApiKey.setOnClickListener(v -> this.checkApiKey());
        this.mBinding.btnCheckNumber.setOnClickListener(v -> this.checkPhoneNumber());
    }

    @Override
    protected void onResume() {
        super.onResume();
        Utility.createNotificationChannel(this);
        this.fillData();
        this.checkServiceStatus();
        this.checkServerStatus();
    }

    private void fillData() {
        this.mBinding.editApiKey.setText(PrefUtil.getString(this, R.string.pref_api_key));
    }

    private void checkServerStatus() {
        this.listenOnTimeWorker(TestApiWorker.class, workInfo -> {
            if (workInfo.getState() != WorkInfo.State.FAILED)
                return;
            String message = getString(R.string.Unable_to_connect_to_server___);
            Snackbar.make(this.getRoot(), message, BaseTransientBottomBar.LENGTH_INDEFINITE)
                    .setAction(R.string.Retry, v -> this.checkServerStatus())
                    .show();
        });
    }

    private void checkApiKey() {
        this.listenWorker(TestApiKeyWorker.startWorker(this.mBinding.editApiKey.getText().toString()), workInfo -> {
            String message;
            switch (workInfo.getState()) {
                case SUCCEEDED:
                    String apiKey = workInfo.getOutputData().getString(TestApiKeyWorker.DATA_API_KEY);
                    if (apiKey == null)
                        return;
                    message = getString(R.string.Api_key_saved__);
                    Snackbar.make(this.getRoot(), message, BaseTransientBottomBar.LENGTH_SHORT)
                            .show();
                    PrefUtil.setString(this, apiKey, R.string.pref_api_key);
                    break;
                case FAILED:
                    message = getString(R.string.Api_key_incorrect__);
                    Snackbar.make(this.getRoot(), message, BaseTransientBottomBar.LENGTH_LONG)
                            .setAction(R.string.Retry, v -> this.checkApiKey())
                            .show();
                    break;
            }
        });
    }

    private void checkPhoneNumber() {
        String phoneNumber = this.mBinding.editNumberCheck.getText().toString();
        if (phoneNumber.isEmpty())
            return;
        this.listenWorker(CheckNumberWorker.getOnTimeRequest(phoneNumber, false), workInfo -> {
            switch (workInfo.getState()) {
                case SUCCEEDED:
                    String percentage = workInfo.getOutputData().getString(CheckNumberWorker.DATA_RETURN_PERCENTAGE);
                    if (percentage == null)
                        return;
                    String message = String.format("%s %s%%",
                            getApplicationContext().getString(R.string.Danger_level),
                            percentage
                    );
                    Snackbar.make(this.getRoot(), message, BaseTransientBottomBar.LENGTH_LONG).show();
                    break;
                case FAILED:
                    Utility.toastThreadFree(this, getString(R.string.Unable_to_check_number));
                    break;
            }
        });
    }

    private void listenOnTimeWorker(@NonNull Class<? extends ListenableWorker> workerToStart,
                                    @NonNull Observer<WorkInfo> workInfoObserver) {
        OneTimeWorkRequest oneTimeWorkRequest = new OneTimeWorkRequest.Builder(workerToStart).build();
        this.listenWorker(oneTimeWorkRequest, workInfoObserver);
    }

    private void listenWorker(@NonNull WorkRequest workerRequest,
                              @NonNull Observer<WorkInfo> workInfoObserver) {
        WorkManager.getInstance(this).enqueue(workerRequest);
        WorkManager.getInstance(this).getWorkInfoByIdLiveData(workerRequest.getId()).observe(this, workInfoObserver);
    }

    private void startServiceCheckNumber() {
        Intent intent = new Intent(this, CheckNumberService.class);
        startForegroundService(intent);
        this.checkServiceStatus();
    }

    private void stopServiceCheckNumber() {
        Intent intent = new Intent(this, CheckNumberService.class);
        stopService(intent);
        this.checkServiceStatus();
    }

    private void checkServiceStatus() {
        boolean isServiceRunning = Utility.isServiceRunning(this, CheckNumberService.class);
        this.mBinding.startService.setEnabled(!isServiceRunning);
        this.mBinding.stopService.setEnabled(isServiceRunning);
    }

    private void testNotification() {
        Utility.showMessageNotification(
                this,
                Utility.CST_NOTIFICATION_ID_CALL_INCOMING,
                Utility.SERVICE_NOTIFICATION_TITLE,
                "TEST NOTIFICATION"
        );
    }

    private View getRoot() {
        return this.mBinding.getRoot();
    }
}