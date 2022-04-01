package com.detoffoli.spamcheck;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

public class PrefUtil {

    public static void setString(@NonNull Context context, @NonNull String aString, @StringRes int prefId) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor edit = preferences.edit();
        edit.putString(context.getString(prefId), aString);
        edit.apply();
    }

    @NonNull
    public static String getString(@NonNull Context context, @StringRes int prefId) {
        return getString(context, prefId, "");
    }

    @NonNull
    public static String getString(@NonNull Context context, @StringRes int prefId, String defaultValue) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(context.getString(prefId), defaultValue);
    }
}
