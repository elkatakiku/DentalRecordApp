package com.bsit_three_c.dentalrecordapp.data.repository;

import com.bsit_three_c.dentalrecordapp.data.model.LoggedInUser;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */
public class LoginRepository {
    private static final String TAG = "LoginRepository";

    private final FirebaseAuth mFirebaseAuth;

    private static volatile LoginRepository instance;

    // If user credentials will be cached in local storage, it is recommended it be encrypted
    // @see https://developer.android.com/training/articles/keystore
    private LoggedInUser user = null;

    // private constructor : singleton access
    private LoginRepository() {
        this.mFirebaseAuth = FirebaseAuth.getInstance();
    }

    public static LoginRepository getInstance() {
        if (instance == null) {
            instance = new LoginRepository();
        }
        return instance;
    }

    public boolean isLoggedIn() {
        return user != null && mFirebaseAuth.getCurrentUser() != null;
    }

    public void logout() {
        user = null;
        mFirebaseAuth.signOut();
    }

    private void setLoggedInUser(LoggedInUser user) {
        this.user = user;
        // If user credentials will be cached in local storage, it is recommended it be encrypted
        // @see https://developer.android.com/training/articles/keystore
    }

    public Task<AuthResult> login(String username, String password) {
        return mFirebaseAuth.signInWithEmailAndPassword(username, password);
    }
}