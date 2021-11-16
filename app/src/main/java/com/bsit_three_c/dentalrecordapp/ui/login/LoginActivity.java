package com.bsit_three_c.dentalrecordapp.ui.login;

import android.app.Activity;

import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.ViewModelProvider;

import android.content.DialogInterface;
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
import com.bsit_three_c.dentalrecordapp.data.model.LoggedInUser;
import com.bsit_three_c.dentalrecordapp.databinding.ActivityLoginBinding;
import com.bsit_three_c.dentalrecordapp.util.LocalStorage;

import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";

    private LoginViewModel loginViewModel;
    private ActivityLoginBinding binding;
    private Intent intent;

    private final String SP_KEY = "Login";
    private final String SP_USERNAME = "Username";
    private final String SP_PASSWORD = "Password";

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
                if (LocalStorage.getLoggedInUser(this) == null)
                    LocalStorage.saveLoggedInUser(this, loginViewModel.getLoggedInUser());
//                if (getuserInfo() == null) saveUserInfo(usernameEditText.getText().toString(), passwordEditText.getText().toString());
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
                loadingProgressBar.setVisibility(View.VISIBLE);
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

        // Checks if connected to internet
        if (!loginViewModel.isOnline()) {
            Log.d(TAG, "onStart: showing alert dialog");
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Networ Error");
            builder.setMessage("Not connected to the internet. Exiting application");
            builder.setNeutralButton("Ok", (dialog, which) -> {
                // Exits application
                Log.d(TAG, "onStart: Exiting application");
                Toast.makeText(getApplicationContext(), "Exiting application", Toast.LENGTH_LONG).show();
                dialog.dismiss();
                finish();
            });
            builder.create().show();
        }

        // Checks if user has logged in before
        LoggedInUser loggedInUser = LocalStorage.getLoggedInUser(this);
        if (loggedInUser != null) {
            Log.d(TAG, "onStart: user already logged in");
            loginViewModel.setLoggedInUser(loggedInUser);
        }

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