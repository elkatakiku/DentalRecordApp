package com.bsit_three_c.dentalrecordapp.util;

import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import androidx.lifecycle.MutableLiveData;

import com.bsit_three_c.dentalrecordapp.R;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;

public class Internet extends AsyncTask<Void, Void, Boolean> {
    private static final String TAG = Internet.class.getSimpleName();

    private static final MutableLiveData<Boolean> isOnline = new MutableLiveData<>();

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        isOnline.setValue(aBoolean);
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        return isOnline();
    }

    public static void showSnackBarInternetError(View view) {
        Snackbar.make(view, R.string.error_network, Snackbar.LENGTH_LONG)
                .setBackgroundTint(Color.WHITE)
                .setTextColor(Color.BLACK)
                .show();
    }

    public static boolean isOnline() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int exitValue = ipProcess.waitFor();
            Log.d(TAG, "isOnline: exitValue:" + exitValue);
            return (exitValue == 0);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        return false;
    }

    public static MutableLiveData<Boolean> getIsOnline() {
        return isOnline;
    }
}
