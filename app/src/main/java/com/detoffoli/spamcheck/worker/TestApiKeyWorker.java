package com.detoffoli.spamcheck.worker;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.detoffoli.spamcheck.api.CheckNumberApi;

public class TestApiKeyWorker extends Worker {

    public final static String DATA_API_KEY = "DATA_API_KEY";

    public TestApiKeyWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        String apiKey = getInputData().getString(DATA_API_KEY);
        if (apiKey == null)
            return Result.failure();
        return CheckNumberApi.checkApiKey(getApplicationContext(), apiKey) ? Result.success() : Result.failure();
    }
}
