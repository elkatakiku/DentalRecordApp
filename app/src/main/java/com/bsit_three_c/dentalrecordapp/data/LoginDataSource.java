package com.bsit_three_c.dentalrecordapp.data;

import android.util.Log;

import androidx.annotation.NonNull;

import com.bsit_three_c.dentalrecordapp.data.model.LoggedInUser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.internal.api.FirebaseNoSignedInUserException;

import java.io.IOException;
import java.util.Objects;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class LoginDataSource {

    private static final String TAG = "LoginDataSource";
    
    private final FirebaseAuth firebaseAuth;

    public LoginDataSource() {
        this.firebaseAuth = FirebaseAuth.getInstance();

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

    public Result<LoggedInUser> login(String username, String password) {

        try {
            // TODO: handle loggedInUser authentication

            // Log in user
//             firebaseAuth.signInWithEmailAndPassword(security.getEmail(), security.getPassword())
            Log.d(TAG, "login: onComplete listener initialized");
            OnCompleteListener<AuthResult> onCompleteListener = task -> {
                Log.d(TAG, "login: onComplete listener called");
                if (!task.isSuccessful()) {
                    Log.d(TAG, "login: task is not successful");
                    Objects.requireNonNull(task.getException()).printStackTrace();
                }
            };

            Log.d(TAG, "login: logging in");
            firebaseAuth.signInWithEmailAndPassword(username, password).addOnCompleteListener(onCompleteListener);

            // Checks if user successfully logged in
            if (firebaseAuth.getCurrentUser() != null) {

                // Get user info
                String uid = firebaseAuth.getCurrentUser().getUid();
                String displayName = "Eli Lamzon";
                String email = firebaseAuth.getCurrentUser().getEmail();

                // Set user info into LoggedInUser
                Log.d(TAG, "login: task is successful");
//              Generate Random ID: java.util.UUID.randomUUID().toString()
                LoggedInUser fakeUser = new LoggedInUser(uid, displayName);

                // Return result success
                return new Result.Success<>(fakeUser);

            }

            // Throw exception if logged in failed
            else throw new FirebaseNoSignedInUserException("User not logged in.");

        } catch (Exception e) {
            Log.d(TAG, "login: try block error");
            e.printStackTrace();
            return new Result.Error(new IOException("Error logging in", e));
        }
    }

    public void logout() {
        // TODO: revoke authentication
        // Logout the current user
        firebaseAuth.signOut();
    }
}