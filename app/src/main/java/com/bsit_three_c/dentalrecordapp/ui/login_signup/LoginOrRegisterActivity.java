package com.bsit_three_c.dentalrecordapp.ui.login_signup;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.bsit_three_c.dentalrecordapp.data.model.LoggedInUser;
import com.bsit_three_c.dentalrecordapp.databinding.ActivityLoginOrRegisterBinding;
import com.bsit_three_c.dentalrecordapp.ui.login.LoginActivity;
import com.bsit_three_c.dentalrecordapp.ui.register.RegisterActivity;
import com.bsit_three_c.dentalrecordapp.util.LocalStorage;
import com.google.android.material.snackbar.Snackbar;

public class LoginOrRegisterActivity extends AppCompatActivity {

    private ActivityLoginOrRegisterBinding binding;

    private final ActivityResultLauncher<Intent> loginActivity = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == RESULT_OK) {
                if (result.getData() != null) {
                    Intent getIntent = result.getData();
                    LoggedInUser loggedInUser = (LoggedInUser) getIntent.getSerializableExtra(LocalStorage.LOGGED_IN_USER_KEY);
                    if (loggedInUser != null) {
                        Snackbar.make(binding.getRoot(), "User logged in" + loggedInUser.getDisplayName(), Snackbar.LENGTH_SHORT).show();
                        startActivity(getIntent);
                        finish();
                    }
                }
            }
        }
    });

    private final ActivityResultLauncher<Intent> registerActivty = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == RESULT_OK && result.getData() != null) {

            }
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLoginOrRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnToSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerActivty.launch(new Intent(LoginOrRegisterActivity.this, RegisterActivity.class));
            }
        });

        binding.btnToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginActivity.launch(new Intent(LoginOrRegisterActivity.this, LoginActivity.class));
            }
        });
    }
}