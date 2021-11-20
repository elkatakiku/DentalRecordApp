package com.bsit_three_c.dentalrecordapp.util;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Log;
import android.view.View;

import com.bsit_three_c.dentalrecordapp.R;
import com.bsit_three_c.dentalrecordapp.data.model.LoggedInUser;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import java.io.IOException;

public class Util {
    private static final String TAG = "Util";
    private static final String SP_KEY = "LoggedInUser Object";
    public static final String LOGGED_IN_USER_KEY = "LoggedInUser";

    public static void saveLoggedInUser(Context context, LoggedInUser loggedInUser) {
        String jsonLoggedInUser = new Gson().toJson(loggedInUser);

        Log.d(TAG, "saveLoggedInUser: Saving user info");

        SharedPreferences spUser = context.getSharedPreferences(SP_KEY, MODE_PRIVATE);
        SharedPreferences.Editor editor = spUser.edit();
        editor.putString(LOGGED_IN_USER_KEY, jsonLoggedInUser);
        editor.apply();

        Log.d(TAG, "saveLoggedInUser: Done saving user info");
    }

    public static LoggedInUser getLoggedInUser(Context context) {

        Log.d(TAG, "getuserInfo: Getting user info");

        SharedPreferences spUser = context.getSharedPreferences(SP_KEY, MODE_PRIVATE);
        String loggedInUser = spUser.getString(LOGGED_IN_USER_KEY, null);

        if (loggedInUser == null || loggedInUser.isEmpty()) {
            Log.d(TAG, "getuserInfo: No Person Saved");
            return null;
        }

        Log.d(TAG, "getuserInfo: Returning user info");
        return new Gson().fromJson(loggedInUser, LoggedInUser.class);
    }

    public static void clearSavedUser(Context context) {
        Log.i(TAG, "clearSavedUser: Start clearing user info");
        saveLoggedInUser(context, null);
        Log.i(TAG, "clearSavedUser: Done clearing user info");
    }

    public static boolean isOnline() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int     exitValue = ipProcess.waitFor();
            Log.d(TAG, "isOnline: exitValue:" + exitValue);
            return (exitValue == 0);
        }
        catch (IOException | InterruptedException e)          { e.printStackTrace(); }

        return false;
    }

    public static void showSnackBarInternetError(View view) {
        Snackbar.make(view, R.string.error_network, Snackbar.LENGTH_LONG)
                .setBackgroundTint(Color.WHITE)
                .setTextColor(Color.BLACK)
                .show();
    }
}
