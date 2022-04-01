package com.detoffoli.spamcheck.worker;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.detoffoli.spamcheck.api.CheckNumberApi;

public class TestApiWorker extends Worker {

    public TestApiWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        return CheckNumberApi.checkServerStatus(getApplicationContext()) ? Result.success() : Result.failure();
    }
}
