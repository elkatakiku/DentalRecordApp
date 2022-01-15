package com.bsit_three_c.dentalrecordapp.ui.login_signup;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.bsit_three_c.dentalrecordapp.data.model.LoggedInUser;
import com.bsit_three_c.dentalrecordapp.databinding.ActivityLoginOrRegisterBinding;
import com.bsit_three_c.dentalrecordapp.ui.base.BaseFormActivity;
import com.bsit_three_c.dentalrecordapp.ui.login.LoginActivity;
import com.bsit_three_c.dentalrecordapp.util.LocalStorage;

public class LoginOrRegisterActivity extends AppCompatActivity {

    private ActivityLoginOrRegisterBinding binding;

    private final ActivityResultLauncher<Intent> loginActivity = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                LoggedInUser loggedInUser = result.getData().getParcelableExtra(LocalStorage.LOGGED_IN_USER_KEY);
                Log.d("LOGIN REGISTER UI", "onActivityResult: sent result: " + loggedInUser);
                if (loggedInUser != null) {
                    setResult(result.getResultCode(), result.getData());
                } else {
                    setResult(Activity.RESULT_CANCELED);
                }
                finish();
            }
        }
    });

    private final ActivityResultLauncher<Intent> registerActivity = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                Log.d("Register", "onActivityResult: returned result: " + result.getData().getParcelableExtra(LocalStorage.UPDATED_PATIENT_KEY));
                setResult(result.getResultCode(), result.getData());
                finish();
            }
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLoginOrRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnToSignUp.setOnClickListener(v ->
                registerActivity.launch(new Intent(LoginOrRegisterActivity.this, BaseFormActivity.class)
                        .putExtra(BaseFormActivity.FORM_KEY, BaseFormActivity.FORM_REGISTRATION)));

        binding.btnToLogin.setOnClickListener(v ->
                loginActivity.launch(new Intent(LoginOrRegisterActivity.this, LoginActivity.class)));
    }
}