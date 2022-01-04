package com.bsit_three_c.dentalrecordapp.data.login;

import android.util.Log;

import com.bsit_three_c.dentalrecordapp.data.model.Account;
import com.bsit_three_c.dentalrecordapp.data.model.LoggedInUser;
import com.bsit_three_c.dentalrecordapp.data.model.Person;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Date;

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


    public LoggedInUser loginSuccess(AuthResult authResult) {
        FirebaseUser loggedUser = authResult.getUser();

        assert loggedUser != null;
        String uid = loggedUser.getUid();
        String name = loggedUser.getDisplayName();
        String email = loggedUser.getEmail();
        String type = "Admin";

        // Change display name when login database is set
        LoggedInUser newUser = new LoggedInUser(
                new Person(
                        "uid",
                        "firstname",
                        "lastname",
                        "middleInitial",
                        "suffix",
                        "dateOfBirth",
                        new ArrayList<>(),
                        "address",
                        1,
                        0,
                        new Date(),
                        email),
                new Account(
                        uid,
                        email,
                        "123456",
                        Account.TYPE_ADMIN,
                        "uid"),
                Account.TYPE_ADMIN
        );

        setLoggedInUser(newUser);
        Log.d(TAG, "loginSuccess: new user: " + newUser);

        return newUser;
    }


}