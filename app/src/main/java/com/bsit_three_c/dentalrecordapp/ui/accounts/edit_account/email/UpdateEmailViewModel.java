package com.bsit_three_c.dentalrecordapp.ui.accounts.edit_account.email;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.bsit_three_c.dentalrecordapp.R;
import com.bsit_three_c.dentalrecordapp.data.model.Account;
import com.bsit_three_c.dentalrecordapp.data.repository.AccountRepository;
import com.bsit_three_c.dentalrecordapp.util.Checker;
import com.google.android.gms.tasks.Task;

public class UpdateEmailViewModel extends ViewModel {
    private final AccountRepository accountRepository;
    private final MutableLiveData<String> mAccountUid;
    private final MutableLiveData<Account> mAccount;
    private final MutableLiveData<Integer> mError;
    private final AccountRepository.AccountListener accountListener;

    public UpdateEmailViewModel() {
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

    public void updateEmail(String newEmail) {
        Account account = mAccount.getValue();
        if (account != null) {

            Task<Void> reAuthTask = accountRepository.reAuthenticate(account);
            if (reAuthTask != null) {

                reAuthTask.addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        updateEmail(account, newEmail);
                    }
                });
            }
        } else {
            mError.setValue(R.string.invalid_empty_input);
        }
    }

    private void updateEmail(Account account, String newEmail) {
        Task<Void> updateEmailTask = accountRepository.updateEmail(newEmail);

        if (updateEmailTask != null) {
            updateEmailTask
                    .addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            updateAccount(account, newEmail);
                        } else {
                            mError.setValue(R.string.unseuccessful_update_email);
                        }
                    });
        }
    }

    private void updateAccount(Account account, String newEmail) {
        account.setEmail(newEmail);
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
