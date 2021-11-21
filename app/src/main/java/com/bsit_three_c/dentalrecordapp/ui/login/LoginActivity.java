package com.bsit_three_c.dentalrecordapp.ui.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.bsit_three_c.dentalrecordapp.MainActivity;
import com.bsit_three_c.dentalrecordapp.R;
import com.bsit_three_c.dentalrecordapp.data.model.LoggedInUser;
import com.bsit_three_c.dentalrecordapp.databinding.ActivityLoginBinding;
import com.bsit_three_c.dentalrecordapp.util.Internet;
import com.bsit_three_c.dentalrecordapp.util.LocalStorage;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";

    private LoginViewModel loginViewModel;
    private ActivityLoginBinding binding;
    private Intent intent;

    private boolean isOnline;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

//        isOnline = Internet.isOnline();

        // Show snack bar if offline
        Internet.getIsOnline().observe(this, aBoolean -> {
            if (!aBoolean) Internet.showSnackBarInternetError(binding.getRoot());
            isOnline = aBoolean;
        });
//        if (!Internet.isOnline()) Internet.showSnackBarInternetError(binding.getRoot());

        loginViewModel = new ViewModelProvider(this, new LoginViewModelFactory()).get(LoginViewModel.class);

        final EditText usernameEditText = binding.username;
        final EditText passwordEditText = binding.password;
        final Button loginButton = binding.btnLogin;
        final ProgressBar loadingProgressBar = binding.loading;
        intent = new Intent(LoginActivity.this, MainActivity.class);

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
            isOnline = Internet.isOnline();
//            new Internet().execute();

            loadingProgressBar.setVisibility(View.GONE);
            if (LocalStorage.getLoggedInUser(this) != null) {
                redirectToHome();
            } else if (!isOnline) {
                Internet.showSnackBarInternetError(binding.getRoot());
                return;
            } else if (loginResult == null) {
                Log.e(TAG, "onCreate: login result is null");
                return;
            } else if (loginResult.getError() != null) {
                Log.e(TAG, "onCreate: login result is error");
                showLoginFailed(loginResult.getError());
            } else if (loginResult.getSuccess() != null) {
                updateUiWithUser(loginResult.getSuccess());
                redirectToHome();
            }

            setResult(Activity.RESULT_OK);
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

        // Check internet connection in background
        new Internet().execute();

        // Checks if user has logged in before
        LoggedInUser loggedInUser = LocalStorage.getLoggedInUser(this);
        if (loggedInUser != null) {
            Log.d(TAG, "onStart: user already logged in");
            loginViewModel.setLoggedInUser(loggedInUser);
        }
    }


    private void redirectToHome() {
        Log.i(TAG, "redirectToHome: login result is success");

        Log.i(TAG, "redirectToHome: Saving user info");
        // Checks if logged in user is already saved
        if (LocalStorage.getLoggedInUser(this) == null)
            LocalStorage.saveLoggedInUser(this, loginViewModel.getLoggedInUser());
        Log.d(TAG, "redirectToHome: Starting sample activity");

        // Passing loggedInUser object
        Log.d(TAG, "redirectToHome: passing loggedInUser object");
        intent.putExtra(LocalStorage.LOGGED_IN_USER_KEY, loginViewModel.getLoggedInUser());

        // Redirect user to main activity
        startActivity(intent);
        finish();
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