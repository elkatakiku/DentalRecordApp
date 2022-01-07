package com.bsit_three_c.dentalrecordapp.data.repository;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.bsit_three_c.dentalrecordapp.R;
import com.bsit_three_c.dentalrecordapp.data.model.Account;
import com.bsit_three_c.dentalrecordapp.data.model.LoggedInUser;
import com.bsit_three_c.dentalrecordapp.util.Checker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AccountRepository {
    private static final String TAG = AccountRepository.class.getSimpleName();

    private final FirebaseDatabase database;
    private final FirebaseAuth mFirebaseAuth;
    private final DatabaseReference databaseReference;

    private static volatile AccountRepository instance;

    private final MutableLiveData<Account> mAccount = new MutableLiveData<>();

    public AccountRepository() {
        this.database = FirebaseDatabase.getInstance(FirebaseHelper.FIREBASE_URL);
        this.databaseReference = database.getReference(FirebaseHelper.ACCOUNTS_REFERENCE);
        this.mFirebaseAuth = FirebaseAuth.getInstance();
    }

    public static AccountRepository getInstance() {
        if (instance == null) {
            instance = new AccountRepository();
        }
        return instance;
    }

    public void addAccount(Account account) {
        databaseReference.child(account.getUid()).setValue(account);
    }

    public Task<AuthResult> createNewAccount(Account newAccount) {

        if (!(Checker.isDataAvailable(newAccount.getEmail()) && Checker.isDataAvailable(newAccount.getPassword()))) {
            return null;
        }

        return mFirebaseAuth.createUserWithEmailAndPassword(newAccount.getEmail(), newAccount.getPassword());
    }

    public int getCreateAccountError(Task<AuthResult> task) {
        int error;
        if (task.getException() instanceof FirebaseAuthWeakPasswordException) {
            error = R.string.weak_password;
        } else if (task.getException() instanceof FirebaseAuthUserCollisionException) {
            error = R.string.email_taken;
        } else {
            error = R.string.an_error_occurred;
        }

        return error;
    }

    public void reLoginUser(LoggedInUser loggedInAccount) {
        logout();
        mFirebaseAuth
                .signInWithEmailAndPassword(loggedInAccount.getEmail(), loggedInAccount.getPassword())
                .addOnSuccessListener(authResult -> Log.d(TAG, "onSuccess: newCurrentUID: " + mFirebaseAuth.getCurrentUser().getUid()))
                .addOnFailureListener(e -> Log.d(TAG, "onFailure: newCurrentUID: " + mFirebaseAuth.getCurrentUser().getUid()));
    }

    public void removeAccount(LoggedInUser loggedInUser, String accountUid) {

        databaseReference.child(accountUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Account account = snapshot.getValue(Account.class);
                if (account != null) {
                    removeAccount(loggedInUser, account);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void removeAccount(LoggedInUser loggedInUser, Account account) {
        final String email = account.getEmail();
        final String password = account.getPassword();

        mFirebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful() && task.getResult().getUser() != null) {
                    task.getResult().getUser().delete();
                }
            }
        });

        mFirebaseAuth.signInWithEmailAndPassword(loggedInUser.getEmail(), loggedInUser.getPassword());
        databaseReference.child(account.getUid()).removeValue();
    }

    public void logout() {
        // TODO: revoke authentication
        // Logout the current user
        mFirebaseAuth.signOut();
        Log.d(TAG, "logout: Person is still logged in: " + (mFirebaseAuth.getCurrentUser() == null));
    }

    private final ValueEventListener accountListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            Log.d(TAG, "onDataChange: changed in account occured");
            Account account = snapshot.getValue(Account.class);
            if (account != null) {
                mAccount.setValue(account);
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    };

    public void loadAccount(String accountUid) {
        databaseReference.child(accountUid).addValueEventListener(accountListener);
    }

    public DatabaseReference getAccountPath(String accountUid) {
        return databaseReference.child(accountUid);
    }

    public LiveData<Account> getmAccount() {
        return mAccount;
    }

}
