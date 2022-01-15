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
import android.widget.Toast;

import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

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

        // Show snack bar if offline
        Internet.getIsOnline().observe(this, aBoolean -> {
            if (!aBoolean) Internet.showSnackBarInternetError(binding.getRoot());
            isOnline = aBoolean;
        });

        loginViewModel = new ViewModelProvider(this, new LoginViewModelFactory()).get(LoginViewModel.class);

        final EditText etEmail = binding.email;
        final EditText etPassword = binding.password;
        final Button btnLogin = binding.btnLogin;
        intent = new Intent();

        loginViewModel.getLoginFormState().observe(this, loginFormState -> {
            if (loginFormState == null) {
                return;
            }

            if (loginFormState.getUsernameError() != null) {
                etEmail.setError(getString(loginFormState.getUsernameError()));
            }

            if (loginFormState.getPasswordError() != null) {
                etPassword.setError(getString(loginFormState.getPasswordError()));
            }

            if (loginFormState.isDataValid()) {
                etEmail.setError(null);
                etPassword.setError(null);
            }
        });

        loginViewModel.getLoginResult().observe(this, loginResult -> {
            isOnline = Internet.isOnline();

            setEnabled(true);

            if (LocalStorage.getLoggedInUser(this) != null) {
                returnResult();
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
                returnResult();
            }

            setResult(Activity.RESULT_CANCELED);
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
                loginViewModel.loginDataChanged(etEmail.getText().toString(),
                        etPassword.getText().toString());
            }
        };

        etEmail.addTextChangedListener(afterTextChangedListener);
        etPassword.addTextChangedListener(afterTextChangedListener);
        etPassword.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (!isValid()) {
                    return false;
                }
                setEnabled(false);
                loginViewModel.login(etEmail.getText().toString(), etPassword.getText().toString());
            }
            return false;
        });

        btnLogin.setOnClickListener(v -> {
            if (!isValid()) {
                return;
            }

            setEnabled(false);
            loginViewModel.login(etEmail.getText().toString(), etPassword.getText().toString());
        });
    }


    private boolean isValid() {
        boolean isValid = true;

        if (binding.email.getText().toString().trim().isEmpty()) {
            binding.email.setError(getString(R.string.invalid_empty_input));
            isValid = false;
        }

        if (binding.password.getText().toString().trim().isEmpty()) {
            binding.password.setError(getString(R.string.invalid_empty_input));
            isValid = false;
        }

        return isValid;
    }

    private void setEnabled(boolean enabled) {
        Log.d(TAG, "setEnabled: setting fields");
        binding.email.setEnabled(enabled);
        binding.password.setEnabled(enabled);
        binding.pbLoadingLogin.setVisibility(!enabled ? View.VISIBLE : View.GONE);
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Check internet connection in background
        Internet.getInstance().execute();

        // Checks if user has logged in before
        LoggedInUser loggedInUser = LocalStorage.getLoggedInUser(this);
        Log.d(TAG, "onStart: logged in user: " + loggedInUser);
        if (loggedInUser != null) {
            Log.d(TAG, "onStart: user already logged in");
            loginViewModel.setLoggedInUser(loggedInUser);
        }
    }


    private void returnResult() {

        //  TODO: checks if account is admin, staff or client

        // Checks if logged in user is already saved
        if (LocalStorage.getLoggedInUser(this) == null) {
            Log.d(TAG, "returnResult: saving logged in user");
            LocalStorage.saveLoggedInUser(this, loginViewModel.getLoggedInUser());
        }

        // Passing loggedInUser object
        intent.putExtra(LocalStorage.LOGGED_IN_USER_KEY, loginViewModel.getLoggedInUser());

        // Redirect user to main activity
        setResult(RESULT_OK, intent);
        finish();
    }

    private void updateUiWithUser(LoggedInUserView model) {
        // TODO : initiate successful logged in experience
        String welcome = getString(R.string.welcome) + ' ' + model.getDisplayName();
        Toast.makeText(getApplicationContext(), welcome, Toast.LENGTH_LONG).show();
    }

    private void showLoginFailed(@StringRes Integer errorString) {
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
    }
}