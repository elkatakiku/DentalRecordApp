package com.bsit_three_c.dentalrecordapp.util;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.activity.result.ActivityResultLauncher;

import com.bsit_three_c.dentalrecordapp.data.model.LoggedInUser;
import com.google.gson.Gson;

public class LocalStorage {
    public static final String LOGGED_IN_USER_KEY = "LoggedInUser";
    private static final String TAG = "LocalStorage";
    private static final String SP_KEY = "LoggedInUser Object";

    private static final String ADMIN = "ELL_Admin";
    private static final String EMPLOYEE = "ELL_Employee";
    private static final String CLIENT = "ELL_Patient";

    public static final String DIALOG_PATIENT = "PATIENT";
    public static final String DIALOG_EMPLOYEE = "EMPLOYEE";
    public static final String DIALOG_APPOINTMENT = "APPOINTMENT";
    public static final String DIALOG_SERVICE = "SERVICE";

    public static final String PARCEL_KEY = "parcel";


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


    public static void imageChooser(ActivityResultLauncher<Intent> selectImage) {

        // create an instance of the
        // intent of the type image
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);

        // pass the constant to compare it
        // with the returned requestCode
        selectImage.launch(Intent.createChooser(i, "Select Picture"));
    }
}
