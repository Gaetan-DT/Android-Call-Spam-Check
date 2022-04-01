package com.detoffoli.spamcheck.worker;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.detoffoli.spamcheck.api.CheckNumberApi;

public class TestApiKeyWorker extends Worker {

    public final static String DATA_API_KEY = "DATA_API_KEY";

    public static OneTimeWorkRequest startWorker(String apiKey) {
        Data workData = new Data.Builder()
                .putString(DATA_API_KEY, apiKey)
                .build();
        return new OneTimeWorkRequest
                .Builder(TestApiKeyWorker.class)
                .setInputData(workData)
                .build();
    }

    public TestApiKeyWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        String apiKey = getInputData().getString(DATA_API_KEY);
        if (apiKey == null)
            return Result.failure();
        return CheckNumberApi.checkApiKey(getApplicationContext(), apiKey) ?
                Result.success(this.dataApiKey(apiKey)) :
                Result.failure();
    }

    public Data dataApiKey(String apiKey) {
        return new Data.Builder().putString(DATA_API_KEY, apiKey).build();
    }
}
