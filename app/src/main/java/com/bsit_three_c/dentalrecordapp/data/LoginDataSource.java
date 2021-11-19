package com.bsit_three_c.dentalrecordapp.data;

import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class LoginDataSource {

    private static final String TAG = "LoginDataSource";
    
    private final FirebaseAuth mFirebaseAuth;
//    private final FirebaseDatabase database;
//    private final DatabaseReference databaseReference;

//    private final static String FIREBASE_URL = "https://dental-record-app-default-rtdb.asia-southeast1.firebasedatabase.app";

    public LoginDataSource() {
        this.mFirebaseAuth = FirebaseAuth.getInstance();
//        this.database = FirebaseDatabase.getInstance(FIREBASE_URL);
//        this.databaseReference = database.getReference("users");

//        Log.d(TAG, "LoginDataSource: firebaseAuth initialized");
//        Log.d(TAG, "LoginDataSource: firebaseAuth: " + mFirebaseAuth.toString());

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

    // Remove when account registration created
//    public void addLoggedInUser(LoggedInUser loggedInUser) {
//        Log.d(TAG, "addLoggedInUser: adding userId to database");
//        databaseReference.child(loggedInUser.getUserId()).setValue(loggedInUser);
//        Log.d(TAG, "addLoggedInUser: done adding user to database");
//    }

//    public void createDummy(LoggedInUser loggedInUser) {
//        databaseReference.child(loggedInUser.getUserId()).setValue(loggedInUser);
//    }

//    public void createNewAccount() {
//        final String currentUserUID = getmFirebaseAuth().getCurrentUser().getUid();
//        final FirebaseUser currentUser = getmFirebaseAuth().getCurrentUser();
//        Log.d(TAG, "createNewAccount: current UID: " + currentUserUID);
//        mFirebaseAuth.createUserWithEmailAndPassword("anotherEmail@email.com", "123456").addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//            @Override
//            public void onComplete(@NonNull Task<AuthResult> task) {
//
//                FirebaseUser user = task.getResult().getUser();
//                LoggedInUser otherUser = new LoggedInUser(user.getUid(), "Ohter user");
//                createDummy(otherUser);
//                Log.d(TAG, "onComplete: newCurrentUID: " + getmFirebaseAuth().getCurrentUser().getUid());
//                logout();
//                getmFirebaseAuth().signInWithEmailAndPassword("lamzonelizer1@gmail.com", "admin123").addOnSuccessListener(new OnSuccessListener<AuthResult>() {
//                    @Override
//                    public void onSuccess(AuthResult authResult) {
//                        Log.d(TAG, "onSuccess: newCurrentUID: " + getmFirebaseAuth().getCurrentUser().getUid());
//                    }
//                }).addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Log.d(TAG, "onFailure: newCurrentUID: " + getmFirebaseAuth().getCurrentUser().getUid());
//                    }
//                });
//            }
//        });
//    }
}