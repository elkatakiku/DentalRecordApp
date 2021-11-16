package com.bsit_three_c.dentalrecordapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.bsit_three_c.dentalrecordapp.databinding.ActivitySampleBinding;
import com.bsit_three_c.dentalrecordapp.ui.login.LoginActivity;
import com.bsit_three_c.dentalrecordapp.ui.login.LoginViewModelFactory;

public class SampleActivity extends AppCompatActivity {

    private static final String TAG = "SampleActivity";

    private SampleViewModel viewModel;
    private ActivitySampleBinding binding;

    private final String SP_KEY = "Login";
    private final String SP_USERNAME = "Username";
    private final String SP_PASSWORD = "Password";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySampleBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this, new LoginViewModelFactory()).get(SampleViewModel.class);

        binding.btnLogout.setOnClickListener(v -> {
            Log.d(TAG, "onCreate: Clearing saved user info");
            clearSavedUser();

            viewModel.logout();
            startActivity(new Intent(SampleActivity.this, LoginActivity.class));
            finish();
        });

        binding.btnNext.setOnClickListener(v -> {
            startActivity(new Intent(SampleActivity.this, MainActivity.class));
        });
    }

    private void clearSavedUser() {
        Log.i(TAG, "clearSavedUser: Start clearing user info");
        SharedPreferences userInfo = getSharedPreferences(SP_KEY, MODE_PRIVATE);
        SharedPreferences.Editor editor = userInfo.edit();
        editor.putString(SP_USERNAME, null);
        editor.putString(SP_PASSWORD, null);
        editor.apply();
        Log.i(TAG, "clearSavedUser: Done clearing user info");
    }
}