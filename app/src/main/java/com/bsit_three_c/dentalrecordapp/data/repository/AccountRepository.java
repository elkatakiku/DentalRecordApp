package com.bsit_three_c.dentalrecordapp.data.repository;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.bsit_three_c.dentalrecordapp.R;
import com.bsit_three_c.dentalrecordapp.data.model.Account;
import com.bsit_three_c.dentalrecordapp.data.model.LoggedInUser;
import com.bsit_three_c.dentalrecordapp.data.model.Person;
import com.bsit_three_c.dentalrecordapp.util.Checker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class AccountRepository extends BaseRepository {
    private static final String TAG = AccountRepository.class.getSimpleName();

    public static final String ACCOUNT_UID_PATH = "accountUid";

    private final FirebaseAuth mFirebaseAuth;

    private static volatile AccountRepository instance;

    public AccountRepository() {
        super(ACCOUNTS_REFERENCE);
        this.mFirebaseAuth = FirebaseAuth.getInstance();
    }

    public static AccountRepository getInstance() {
        if (instance == null) {
            instance = new AccountRepository();
        }
        return instance;
    }

    public void setUserIds(Person person, Account account, String userUid) {
        account.setUid(userUid);
        account.setUserUid(person.getUid());
        person.setAccountUid(account.getUid());
    }

    public Task<Void> addAccount(Account account) {
        return databaseReference.child(account.getUid()).setValue(account);
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

        mFirebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult().getUser() != null) {
                task.getResult().getUser().delete();
                databaseReference.child(account.getUid()).removeValue();
                mFirebaseAuth.signInWithEmailAndPassword(loggedInUser.getEmail(), loggedInUser.getPassword());
            }
        });
    }

    public void logout() {
        // TODO: revoke authentication
        // Logout the current user
        mFirebaseAuth.signOut();
        Log.d(TAG, "logout: Person is still logged in: " + (mFirebaseAuth.getCurrentUser() == null));
    }

//    public DatabaseReference loadAccount(String accountUid) {
//        return databaseReference.child(accountUid);
//    }

//    public DatabaseReference getAccountPath(String accountUid) {
//        return databaseReference.child(accountUid);
//    }

    public void removeListener(String accountUid,
                               AccountListener accountListener) {
        databaseReference.child(accountUid).removeEventListener(accountListener);
    }

    public static class AccountListener implements ValueEventListener {
        private final MutableLiveData<Account> mAccount;

        public AccountListener(MutableLiveData<Account> mAccount) {
            this.mAccount = mAccount;
        }

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
    }

    public static class CreateAccountListener implements OnCompleteListener<AuthResult> {

        private final MutableLiveData<Integer> mError;
        private final MutableLiveData<Boolean> mCreateAttempt;

        private final AccountRepository accountRepository;

        private final Account account;
        private final LoggedInUser loggedInUser;

        private final String userUID;

        private final int VALID = -1;

        public CreateAccountListener(MutableLiveData<Integer> mError,
                                     MutableLiveData<Boolean> mCreateAttempt,
                                     AccountRepository accountRepository,
                                     Account account,
                                     LoggedInUser loggedInUser,
                                     String userUID) {
            this.mError = mError;
            this.mCreateAttempt = mCreateAttempt;
            this.accountRepository = accountRepository;
            this.account = account;
            this.loggedInUser = loggedInUser;
            this.userUID = userUID;
        }


        @Override
        public void onComplete(@NonNull Task<AuthResult> task) {
            Log.d(TAG, "onComplete: create account attempt");
            if (!task.isSuccessful()) {
                mError.setValue(accountRepository.getCreateAccountError(task));
                mCreateAttempt.setValue(true);
                Log.d(TAG, "onComplete: task is unsuccessful");
                return;
            }

            FirebaseUser user = task.getResult().getUser();
            if (user != null) {
                Log.d(TAG, "onComplete: creating account data");
//                account.setUserUid(userUID);
//                accountRepository.setUserIds();   Set user ids
                accountRepository.addAccount(account);

                if (loggedInUser != null) {
                    Log.d(TAG, "onComplete: no user logged in");
                    accountRepository.reLoginUser(loggedInUser);
                }

                mError.setValue(VALID);
            } else {
                mError.setValue(R.string.an_error_occurred);
            }

            Log.d(TAG, "onComplete: finishing attempt");
            mCreateAttempt.setValue(true);
        }
    }

}
