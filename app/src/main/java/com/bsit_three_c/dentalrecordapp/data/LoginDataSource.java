package com.bsit_three_c.dentalrecordapp.data;

import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.bsit_three_c.dentalrecordapp.data.model.LoggedInUser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.internal.api.FirebaseNoSignedInUserException;

import java.io.IOException;
import java.util.Objects;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class LoginDataSource {

    private static final String TAG = "LoginDataSource";
    
    private final FirebaseAuth mFirebaseAuth;
    private final FirebaseDatabase database;
    private final DatabaseReference databaseReference;

    private final static String FIREBASE_URL = "https://dental-record-app-default-rtdb.asia-southeast1.firebasedatabase.app";

    public LoginDataSource() {
        this.mFirebaseAuth = FirebaseAuth.getInstance();
        this.database = FirebaseDatabase.getInstance(FIREBASE_URL);
        this.databaseReference = database.getReference("users");

        Log.d(TAG, "LoginDataSource: firebaseAuth initialized");
        Log.d(TAG, "LoginDataSource: firebaseAuth: " + mFirebaseAuth.toString());

        // Register account
//        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
//            @Override
//            public void onSuccess(AuthResult authResult) {
//
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//
//            }
//        });
    }

    public FirebaseAuth getmFirebaseAuth() {
        return mFirebaseAuth;
    }

    public Task<AuthResult> login(String username, String password) {
        return mFirebaseAuth.signInWithEmailAndPassword(username, password);
    }

    public void logout() {
        // TODO: revoke authentication
        // Logout the current user
        mFirebaseAuth.signOut();
        Log.d(TAG, "logout: User is still logged in: " + (mFirebaseAuth.getCurrentUser() == null));
    }

    public boolean isLoggedIn() {
        return mFirebaseAuth.getCurrentUser() != null;
    }

    public boolean isLoggedin() {
        return true;
    }
    
    public void addLoggedInUser(LoggedInUser loggedInUser) {
        Log.d(TAG, "addLoggedInUser: adding userId to database");
        databaseReference.child(loggedInUser.getUserId()).setValue(loggedInUser.getDisplayName());
        Log.d(TAG, "addLoggedInUser: done adding user to database");
    }
}