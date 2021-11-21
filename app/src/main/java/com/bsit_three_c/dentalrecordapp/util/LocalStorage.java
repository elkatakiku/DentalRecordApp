package com.bsit_three_c.dentalrecordapp.util;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.bsit_three_c.dentalrecordapp.data.model.LoggedInUser;
import com.google.gson.Gson;

public class LocalStorage {
    public static final String LOGGED_IN_USER_KEY = "LoggedInUser";
    private static final String TAG = "LocalStorage";
    private static final String SP_KEY = "LoggedInUser Object";

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
}
