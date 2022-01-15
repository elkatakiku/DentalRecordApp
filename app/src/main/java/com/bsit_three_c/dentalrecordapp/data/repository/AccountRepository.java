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

    public void setUserIds(Person person, Account account) {
        account.setUserUid(person.getUid());
        person.setAccountUid(account.getUid());

        Log.d(TAG, "setUserIds: account after setting id: " + account);
        Log.d(TAG, "setUserIds: person after setting account id: " + person);
    }

    public Task<Void> uploadAccount(Account account, String userUid) {
        account.setUid(userUid);
        return databaseReference.child(account.getUid()).setValue(account);
    }

    public Task<Void> uploadAccount(Account account) {
        return databaseReference.child(account.getUid()).setValue(account);
    }

    public Task<AuthResult> createNewAccount(String email, String password) {

        if (!(Checker.isDataAvailable(email) && Checker.isDataAvailable(password))) {
            Log.d(TAG, "createNewAccount: create account null");
            return null;
        }

        return mFirebaseAuth.createUserWithEmailAndPassword(email, password);
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
        Log.d(TAG, "reLoginUser: logged in user: " + loggedInAccount);
        logout();
        mFirebaseAuth
                .signInWithEmailAndPassword(loggedInAccount.getEmail(), loggedInAccount.getPassword())
                .addOnSuccessListener(authResult -> Log.d(TAG, "onSuccess: newCurrentUID: " + mFirebaseAuth.getCurrentUser().getUid()))
                .addOnFailureListener(Throwable::printStackTrace);
    }

    public void removeAccount(LoggedInUser loggedInUser, String accountUid) {
        getPath(accountUid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Account account = snapshot.getValue(Account.class);
                if (account != null) {
                    Log.d(TAG, "onDataChange: to be deleted account: " + account);
                    final String email = account.getEmail();
                    final String password = account.getPassword();

                    mFirebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult().getUser() != null) {
                            task
                                    .getResult()
                                    .getUser()
                                    .delete()
                                    .continueWith(task12 -> {
                                        if (task12.isSuccessful()) {
                                            remove(account.getUid()).continueWith(task1 -> {
                                                if (task1.isSuccessful()) {
                                                    mFirebaseAuth.signInWithEmailAndPassword(loggedInUser.getEmail(), loggedInUser.getPassword());
                                                }
                                                return null;
                                            });
                                        }

                                        return null;
                                    });
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public Task<AuthResult> signInWithEmail(Account account) {
        return mFirebaseAuth.signInWithEmailAndPassword(account.getEmail(), account.getPassword());
    }

    public void logout() {
        // TODO: revoke authentication
        // Logout the current user
        mFirebaseAuth.signOut();
        Log.d(TAG, "logout: Is no one logged in: " + (mFirebaseAuth.getCurrentUser() == null));
    }

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
                Log.d(TAG, "onDataChange: got account: " + account);
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
                accountRepository.uploadAccount(account, user.getUid());

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
