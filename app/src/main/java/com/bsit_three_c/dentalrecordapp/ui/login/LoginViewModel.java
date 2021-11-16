package com.bsit_three_c.dentalrecordapp.ui.login;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import android.util.Log;
import android.util.Patterns;

import com.bsit_three_c.dentalrecordapp.data.LoginRepository;
import com.bsit_three_c.dentalrecordapp.R;
import com.google.firebase.auth.FirebaseAuth;

public class LoginViewModel extends ViewModel {
    private static final String TAG = "LoginViewModel";

    private MutableLiveData<LoginFormState> loginFormState = new MutableLiveData<>();
    private MutableLiveData<LoginResult> loginResult = new MutableLiveData<>();
    private MutableLiveData<Boolean> isLoggedIn = new MutableLiveData<>();
    private LoginRepository loginRepository;

    LoginViewModel(LoginRepository loginRepository) {
        this.loginRepository = loginRepository;
    }

    LiveData<LoginFormState> getLoginFormState() {
        return loginFormState;
    }

    LiveData<LoginResult> getLoginResult() {
        return loginResult;
    }

    LiveData<Boolean> getIsLoggedIn() {
        return isLoggedIn;
    }

    public void login(String username, String password) {
        // can be launched in a separate asynchronous job
        // Firebase login listener
        loginRepository
                .login(username, password)
                .addOnSuccessListener(authResult -> {
                    Log.d(TAG, "loginUser: logged in success");

                    // Initialize LoggedInUserView in repository
                    // LoggedInUserView isn't public
                    LoggedInUserView loggedInUser = createLoggedInUserView(loginRepository.loginSuccess(authResult).getDisplayName());
                    loginResult.setValue(new LoginResult(loggedInUser));
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "loginUser: logged in error");
                    loginResult.setValue(new LoginResult(R.string.login_failed));
                });
    }

    // Get user details to be displayed in the UI
    private LoggedInUserView createLoggedInUserView(String displayName) {
        return new LoggedInUserView(displayName);
    }

    public void loginDataChanged(String username, String password) {
        if (!isUserNameValid(username)) {
            loginFormState.setValue(new LoginFormState(R.string.invalid_username, null));
        } else if (!isPasswordValid(password)) {
            loginFormState.setValue(new LoginFormState(null, R.string.invalid_password));
        } else {
            loginFormState.setValue(new LoginFormState(true));
        }
    }

    // A placeholder username validation check
    private boolean isUserNameValid(String username) {
        if (username == null) {
            return false;
        }
        if (username.contains("@")) {
            return Patterns.EMAIL_ADDRESS.matcher(username).matches();
        } else {
            return !username.trim().isEmpty();
        }
    }

    // A placeholder password validation check
    private boolean isPasswordValid(String password) {
        return password != null && password.trim().length() > 5;
    }

    public boolean isUserLoggedIn() {
        return loginRepository.isLoggedIn();
    }

    public void setIsLoggedIn(boolean bool) {
        isLoggedIn.setValue(bool);
    }

    public FirebaseAuth authStateChanged() {
        return loginRepository.getDataSource();
    }

}