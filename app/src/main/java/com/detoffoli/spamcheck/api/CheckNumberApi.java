package com.detoffoli.spamcheck.api;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;

import com.detoffoli.spamcheck.PrefUtil;
import com.detoffoli.spamcheck.R;
import com.detoffoli.spamcheck.Utility;

import org.apache.commons.io.IOUtil;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class CheckNumberApi {

    private static final String TAG = CheckNumberApi.class.getSimpleName();

    private static final String PATH_V1_GET_PERCENTAGE = "/v1/percentage/:number";
    private static final String PATH_V1_CHECK_TOKEN = "v1/check-token/";

    @WorkerThread
    public static String getPercentageNumber(@NonNull Context context,
                                             @NonNull String strNumber) {
        String strUrl = (getApiURL(context) + PATH_V1_GET_PERCENTAGE).replace(":number", strNumber);
        String apiKey = PrefUtil.getString(context, R.string.pref_api_key);
        String percentage = "???";
        try {
            URL url = new URL(strUrl);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestProperty("Authorization", formatBearer(apiKey));
            int responseCode = httpURLConnection.getResponseCode();
            if (responseCode == 200)
                percentage = IOUtil.toString(httpURLConnection.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return percentage;
    }

    public static boolean checkServerStatus(@NonNull Context context) {
        boolean result = false;
        try {
            URL url = new URL(getApiURL(context));
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            result = httpURLConnection.getResponseCode() == 200;
        } catch (IOException e) {
            Log.e(TAG, "checkServerStatus: ", e);
            Utility.toastThreadFree(context, e.toString());
        }
        return result;
    }

    public static boolean checkApiKey(@NonNull Context context,
                                      @NonNull String apiKey) {
        boolean result = false;
        try {
            URL url = new URL(getApiURL(context) + PATH_V1_CHECK_TOKEN);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestProperty("Authorization", formatBearer(apiKey));
            result = httpURLConnection.getResponseCode() == 200;
        } catch (IOException e) {
            Log.e(TAG, "checkServerStatus: ", e);
            Utility.toastThreadFree(context, e.toString());
        }
        return result;
    }

    private static String formatBearer(@NonNull String token) {
        return "Bearer " + token;
    }

    private static String getApiURL(@NonNull Context context) {
        return context.getString(R.string.secret_api_url);
    }
}
