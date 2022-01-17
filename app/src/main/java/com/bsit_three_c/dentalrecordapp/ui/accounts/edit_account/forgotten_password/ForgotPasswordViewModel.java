package com.bsit_three_c.dentalrecordapp.ui.accounts.edit_account.forgotten_password;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.bsit_three_c.dentalrecordapp.data.model.Account;
import com.bsit_three_c.dentalrecordapp.data.repository.AccountRepository;
import com.bsit_three_c.dentalrecordapp.util.Checker;

public class ForgotPasswordViewModel extends ViewModel {

    private final AccountRepository accountRepository;

    private final MutableLiveData<Account> mAccount;
    private final MutableLiveData<Integer> mError;

    private final AccountRepository.AccountListener accountListener;

    public ForgotPasswordViewModel() {
        this.accountRepository = AccountRepository.getInstance();
        this.mAccount = new MutableLiveData<>();
        this.mError = new MutableLiveData<>();
        this.accountListener = new AccountRepository.AccountListener(mAccount);
    }

//    public void findAccount(String email) {
//        Log.d("FIND", "findAccount: starts finding email");
//        accountRepository
//                .getAccountByEmail(email)
//                .addListenerForSingleValueEvent(accountListener);
//    }

    public LiveData<Account> getmAccount() {
        return mAccount;
    }

    public void sendPasswordReset(String email) {
        Log.d("PASSRESEt", "sendPasswordReset: sending password reset");
        accountRepository
                .resetPassword(email)
                .addOnCompleteListener(task -> {
                    Log.d("PASSRESEt", "sendPasswordReset: password reset attempt done");
                    if (task.isSuccessful()) {
                        Log.d("PASSRESEt", "sendPasswordReset: sent password reset");
                        mError.setValue(Checker.VALID);
                    }
                });
    }

    public LiveData<Integer> getmError() {
        return mError;
    }
}
