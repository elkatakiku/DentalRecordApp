package com.bsit_three_c.dentalrecordapp.ui.accounts.edit_account;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.bsit_three_c.dentalrecordapp.data.model.Account;
import com.bsit_three_c.dentalrecordapp.data.repository.AccountRepository;

public class EditAccountViewModel extends ViewModel {
    // TODO: Implement the ViewModel
    private final AccountRepository accountRepository;
    private final MutableLiveData<String> mAccountUid;
    private final MutableLiveData<Account> mAccount;
    private final AccountRepository.AccountListener accountListener;

    public EditAccountViewModel() {
        this.accountRepository = AccountRepository.getInstance();
        this.mAccountUid = new MutableLiveData<>();
        this.mAccount = new MutableLiveData<>();
        this.accountListener = new AccountRepository.AccountListener(mAccount);
    }

    public void getAccount(String accountUid) {
        accountRepository
                .getPath(accountUid)
                .addListenerForSingleValueEvent(accountListener);
    }

    public void setmAccountUid(String accountUid) {
        mAccountUid.setValue(accountUid);
    }

    public LiveData<String> getmAccountUid() {
        return mAccountUid;
    }

    public LiveData<Account> getmAccount() {
        return mAccount;
    }
}