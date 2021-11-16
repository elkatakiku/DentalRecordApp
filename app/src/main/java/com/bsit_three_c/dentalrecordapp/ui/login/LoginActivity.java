package com.bsit_three_c.dentalrecordapp.ui.login;

import android.app.Activity;

import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bsit_three_c.dentalrecordapp.R;
import com.bsit_three_c.dentalrecordapp.SampleActivity;
import com.bsit_three_c.dentalrecordapp.databinding.ActivityLoginBinding;

import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";

    private LoginViewModel loginViewModel;
    private ActivityLoginBinding binding;
    private Intent intent;

    private final String SP_KEY = "Login";
    private final String SP_USERNAME = "Username";
    private final String SP_PASSWORD = "Password";

    // Get device id
//    private String deviceId = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        loginViewModel = new ViewModelProvider(this, new LoginViewModelFactory()).get(LoginViewModel.class);

        final EditText usernameEditText = binding.username;
        final EditText passwordEditText = binding.password;
        final Button loginButton = binding.btnLogin;
        final ProgressBar loadingProgressBar = binding.loading;
        intent = new Intent(LoginActivity.this, SampleActivity.class);

        loginViewModel.getLoginFormState().observe(this, loginFormState -> {
            if (loginFormState == null) {
                return;
            }
            loginButton.setEnabled(loginFormState.isDataValid());
            if (loginFormState.getUsernameError() != null) {
                usernameEditText.setError(getString(loginFormState.getUsernameError()));
            }
            if (loginFormState.getPasswordError() != null) {
                passwordEditText.setError(getString(loginFormState.getPasswordError()));
            }
        });

        loginViewModel.getLoginResult().observe(this, loginResult -> {
            if (loginResult == null) {
                Log.e(TAG, "onCreate: login result is null");
                return;
            }

            loadingProgressBar.setVisibility(View.GONE);
            if (loginResult.getError() != null) {
                Log.e(TAG, "onCreate: login result is error");
                showLoginFailed(loginResult.getError());
            }
            if (loginResult.getSuccess() != null) {
                Log.i(TAG, "onCreate: login result is success");
                Log.i(TAG, "onCreate: Saving user info");
                saveUserInfo(usernameEditText.getText().toString(), passwordEditText.getText().toString());
                Log.d(TAG, "onCreate: Starting sample activity");
                updateUiWithUser(loginResult.getSuccess());
                startActivity(intent);
                finish();
            }
            setResult(Activity.RESULT_OK);

            //Complete and destroy login activity once successful
//            startActivity(intent);
//            finish();
        });

        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                loginViewModel.loginDataChanged(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString());
            }
        };
        usernameEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                loginViewModel.login(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString());
            }
            return false;
        });

        loginButton.setOnClickListener(v -> {
            loadingProgressBar.setVisibility(View.VISIBLE);
            loginViewModel.login(usernameEditText.getText().toString(), passwordEditText.getText().toString());
        });


    }

    @Override
    protected void onStart() {
        super.onStart();

        // Check if user already logged in
//        if (loginViewModel.isUserLoggedIn()) {
//            Log.i(TAG, "onCreate: user is logged in. Redirecting to home.");
//            startActivity(intent);
//            finish();
//        }

        HashMap<String, String> savedUser = getuserInfo();
        if (savedUser != null) {
            Log.d(TAG, "onStart: Logging user info");
            loginViewModel.login(savedUser.get(SP_USERNAME), savedUser.get(SP_PASSWORD));
        }

    }

    private void saveUserInfo(String username, String password) {
        Log.d(TAG, "saveUserInfo: Saving user info");
        SharedPreferences userInfo = getSharedPreferences(SP_KEY, MODE_PRIVATE);
        SharedPreferences.Editor editor = userInfo.edit();
        editor.putString(SP_USERNAME, username);
        editor.putString(SP_PASSWORD, password);
        editor.apply();
        Log.d(TAG, "saveUserInfo: Done saving user info");
    }

    private HashMap<String, String> getuserInfo () {
        Log.d(TAG, "getuserInfo: Getting user info");
        SharedPreferences userInfo = getSharedPreferences(SP_KEY, MODE_PRIVATE);
        String username = userInfo.getString(SP_USERNAME, null);
        String password = userInfo.getString(SP_PASSWORD, null);

        if (username == null || password == null) {
            Log.d(TAG, "getuserInfo: No User Info Saved");
            return null;
        }

        HashMap<String, String> savedInfo = new HashMap<>();
        savedInfo.put(SP_USERNAME, username);
        savedInfo.put(SP_PASSWORD, password);

        Log.d(TAG, "getuserInfo: Returning user info");
        return savedInfo;
    }

    private void updateUiWithUser(LoggedInUserView model) {
        // TODO : initiate successful logged in experience
        String welcome = getString(R.string.welcome) + model.getDisplayName();
        Toast.makeText(getApplicationContext(), welcome, Toast.LENGTH_LONG).show();
    }

    private void showLoginFailed(@StringRes Integer errorString) {
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
    }
}