package com.bsit_three_c.dentalrecordapp.ui.accounts.edit_account.password;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.bsit_three_c.dentalrecordapp.R;
import com.bsit_three_c.dentalrecordapp.data.model.Account;
import com.bsit_three_c.dentalrecordapp.data.model.FormState;
import com.bsit_three_c.dentalrecordapp.data.repository.AccountRepository;
import com.bsit_three_c.dentalrecordapp.interfaces.TextChange;
import com.bsit_three_c.dentalrecordapp.util.Checker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class UpdatePasswordViewModel extends ViewModel implements TextChange {
    private final AccountRepository accountRepository;

    private final MutableLiveData<String> mAccountUid;
    private final MutableLiveData<Account> mAccount;
    private final MutableLiveData<Integer> mError;

    private final AccountRepository.AccountListener accountListener;

    public UpdatePasswordViewModel() {
        this.accountRepository = AccountRepository.getInstance();
        this.mAccountUid = new MutableLiveData<>();
        this.mAccount = new MutableLiveData<>();
        this.mError = new MutableLiveData<>();
        this.accountListener = new AccountRepository.AccountListener(mAccount);
    }

    public void setmAccountUid(String accountUid) {
        mAccountUid.setValue(accountUid);
    }

    public LiveData<String> getmAccountUid() {
        return mAccountUid;
    }

    public void getAccount(String accountUid) {
        accountRepository
                .getPath(accountUid)
                .addListenerForSingleValueEvent(accountListener);
    }

    public LiveData<Account> getmAccount() {
        return mAccount;
    }

    @Override
    public void beforeDataChange(String input, int after, String s) {

    }

    private final MutableLiveData<FormState> mCurrentPassword = new MutableLiveData<>();
    private final MutableLiveData<FormState> mNewPassword = new MutableLiveData<>();
    private final MutableLiveData<FormState> mRetypePassword = new MutableLiveData<>();

    private static final String CURRENT_PASSWORD = "Current password";
    private static final String NEW_PASSWORD = "New password";
    private static final String RETYPE_NEW_PASSWORD = "Retype new password";

    @Override
    public void dataChanged(String label, String input) {
        if (Checker.isDataAvailable(input)) {
            if (!Checker.isPasswordValid(input)) {
                setState(label, R.string.invalid_password);
            } else {
                setState(label, Checker.VALID);
            }
        }
    }

    private void setState(final String label, final Integer msg) {
        FormState field;

        if (msg == null) field = null;
        else if (msg == -1) field = new FormState(true);
        else field = new FormState(msg);

        switch (label) {
            case CURRENT_PASSWORD:
                mCurrentPassword.setValue(field);
                break;
            case NEW_PASSWORD:
                mNewPassword.setValue(field);
                break;
            case RETYPE_NEW_PASSWORD:
                mRetypePassword.setValue(field);
                break;
        }
    }

    public LiveData<FormState> getmCurrentPassword() {
        return mCurrentPassword;
    }

    public LiveData<FormState> getmNewPassword() {
        return mNewPassword;
    }

    public LiveData<FormState> getmRetypePassword() {
        return mRetypePassword;
    }

    private static final String TAG = UpdatePasswordViewModel.class.getSimpleName();

    public void updatePassword(String newPassword) {
        Log.d(TAG, "updatePassword: initializing update pass");
        Account account = mAccount.getValue();
        if (account != null) {
            Log.d(TAG, "updatePassword: has account");

            Task<Void> reAuthTask = accountRepository.reAuthenticate(account);
            if (reAuthTask != null) {

                reAuthTask.addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        updatePassword(account, newPassword);
                    }
                });
            }
        } else {
            Log.d(TAG, "updatePassword: has no account");
            mError.setValue(R.string.invalid_empty_input);
        }
    }

    private void updatePassword(Account account, String newPassword) {
        Task<Void> updatePassTask = accountRepository.updatePassword(newPassword);

        if (updatePassTask != null) {
            updatePassTask
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                updateAccount(account, newPassword);
                            } else {
                                mError.setValue(R.string.unseuccessful_update_password);
                            }
                        }
                    });
        }
    }

    private void updateAccount(Account account, String newPassword) {
        account.setPassword(newPassword);
        accountRepository
                .uploadAccount(account)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        mError.setValue(Checker.VALID);
                    }
                });

    }

    public LiveData<Integer> getmError() {
        return mError;
    }
}
