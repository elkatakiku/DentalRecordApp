package com.bsit_three_c.dentalrecordapp.data;

import android.provider.Settings;
import android.util.Log;

import com.bsit_three_c.dentalrecordapp.data.model.LoggedInUser;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */
public class LoginRepository {
    private static final String TAG = "LoginRepository";

    private static volatile LoginRepository instance;
    private final LoginDataSource dataSource;

    // If user credentials will be cached in local storage, it is recommended it be encrypted
    // @see https://developer.android.com/training/articles/keystore
    private LoggedInUser user = null;

    // private constructor : singleton access
    private LoginRepository(LoginDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public static LoginRepository getInstance(LoginDataSource dataSource) {
        if (instance == null) {
            instance = new LoginRepository(dataSource);
        }
        return instance;
    }

    public FirebaseAuth getDataSource() {
        return dataSource.getmFirebaseAuth();
    }

    public boolean isLoggedIn() {
//        if (user != null) Log.d(TAG, "isLoggedIn: user is " + user.toString());
        return user != null && dataSource.isLoggedIn();
    }

    public void logout() {
        user = null;
        dataSource.logout();
    }

    private void setLoggedInUser(LoggedInUser user) {
        this.user = user;
        // If user credentials will be cached in local storage, it is recommended it be encrypted
        // @see https://developer.android.com/training/articles/keystore
    }

    public Task<AuthResult> login(String username, String password) {
        return dataSource.login(username, password);
    }

    private LoggedInUser createLoggedInUser(String uid, String displayName) {
        return new LoggedInUser(uid, displayName);
    }

    public LoggedInUser loginSuccess(AuthResult authResult) {
        FirebaseUser loggedUser = authResult.getUser();

        assert loggedUser != null;
        String uid = loggedUser.getUid();
        String name = loggedUser.getDisplayName();
        String email = loggedUser.getEmail();

        // Change display name when login database is set
        LoggedInUser newUser = createLoggedInUser(uid, email);
        setLoggedInUser(newUser);
        dataSource.addLoggedInUser(newUser);

        return newUser;
    }


}