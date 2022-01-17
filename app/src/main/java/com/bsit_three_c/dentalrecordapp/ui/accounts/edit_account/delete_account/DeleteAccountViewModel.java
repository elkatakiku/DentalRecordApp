package com.bsit_three_c.dentalrecordapp.ui.accounts.edit_account.delete_account;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.bsit_three_c.dentalrecordapp.R;
import com.bsit_three_c.dentalrecordapp.data.model.Account;
import com.bsit_three_c.dentalrecordapp.data.repository.AccountRepository;
import com.bsit_three_c.dentalrecordapp.util.Checker;
import com.google.android.gms.tasks.Task;

public class DeleteAccountViewModel extends ViewModel {
    // TODO: Implement the ViewModel

    private final AccountRepository accountRepository;

    private final MutableLiveData<Account> mAccount;
    private final MutableLiveData<String> mAccountUid;
    private final MutableLiveData<Integer> mError;

    private final AccountRepository.AccountListener accountListener;

    public DeleteAccountViewModel() {
        this.accountRepository = AccountRepository.getInstance();
        this.mAccount = new MutableLiveData<>();
        this.mAccountUid = new MutableLiveData<>();
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

    public void deleteAccount() {
        Account account = mAccount.getValue();

        if (account != null) {
            Task<Void> reAuthTask = accountRepository.reAuthenticate(account);
            if (reAuthTask != null) {

                reAuthTask.addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        deleteUser(account);
                    }
                });
            }
        } else {
            mError.setValue(R.string.an_error_occurred);
        }
    }

    private void deleteUser(Account account) {
        Task<Void> updateAccountTask = accountRepository.deleteUser();

        if (updateAccountTask != null) {
            updateAccountTask
                    .addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            updateAccount(account);
                        } else {
                            mError.setValue(R.string.unsuccessful_deleting_user);
                        }
                    });
        }
    }

    private void updateAccount(Account account) {
        accountRepository
                .remove(account.getUid())
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