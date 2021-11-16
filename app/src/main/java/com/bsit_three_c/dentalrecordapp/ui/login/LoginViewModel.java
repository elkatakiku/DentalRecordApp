package com.bsit_three_c.dentalrecordapp.ui.login;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.util.Log;
import android.util.Patterns;

import com.bsit_three_c.dentalrecordapp.data.LoginRepository;
import com.bsit_three_c.dentalrecordapp.R;
import com.bsit_three_c.dentalrecordapp.data.model.LoggedInUser;
import com.bsit_three_c.dentalrecordapp.util.LocalStorage;
import com.google.firebase.auth.FirebaseAuth;

import java.io.IOException;

public class LoginViewModel extends ViewModel {
    private static final String TAG = "LoginViewModel";

    private MutableLiveData<LoginFormState> loginFormState = new MutableLiveData<>();
    private MutableLiveData<LoginResult> loginResult = new MutableLiveData<>();
    private MutableLiveData<Boolean> isConnectedToInternet = new MutableLiveData<>();
    private LoginRepository loginRepository;
    private LoggedInUser loggedInUser;

    LoginViewModel(LoginRepository loginRepository) {
        this.loginRepository = loginRepository;
    }

    LiveData<LoginFormState> getLoginFormState() {
        return loginFormState;
    }

    LiveData<LoginResult> getLoginResult() {
        return loginResult;
    }

    public MutableLiveData<Boolean> getIsConnectedToInternet() {
        return isConnectedToInternet;
    }

    public LoggedInUser getLoggedInUser() {
        return loggedInUser;
    }

    public void setLoggedInUser(LoggedInUser loggedInUser) {
        this.loggedInUser = loggedInUser;
        loginResult.setValue(new LoginResult(createLoggedInUserView(loggedInUser)));
    }

    public void login(String username, String password) {
        // can be launched in a separate asynchronous job
        // Launched in asych
        new LoginUser().doInBackground(username, password);
    }

    // Get user details to be displayed in the UI
    private LoggedInUserView createLoggedInUserView(LoggedInUser loggedInUser) {
        return new LoggedInUserView(loggedInUser.getDisplayName());
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

    public boolean isOnline() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int     exitValue = ipProcess.waitFor();
            Log.d(TAG, "isOnline: exitValue:" + exitValue);
            return (exitValue == 0);
        }
        catch (IOException | InterruptedException e)          { e.printStackTrace(); }

        return false;
    }

    private class LoginUser extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... strings) {
            // Firebase login listener
            loginRepository
                    .login(strings[0], strings[1])
                    .addOnSuccessListener(authResult -> {
                        Log.d(TAG, "loginUser: logged in success");

                        // Initialize LoggedInUserView in repository
                        // LoggedInUserView isn't public
                        loggedInUser = loginRepository.loginSuccess(authResult);

                        LoggedInUserView loggedInUserView = createLoggedInUserView(loggedInUser);
                        loginResult.setValue(new LoginResult(loggedInUserView));
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "loginUser: logged in error");
                        loginResult.setValue(new LoginResult(R.string.login_failed));
                    });
            return null;
        }
    }

}