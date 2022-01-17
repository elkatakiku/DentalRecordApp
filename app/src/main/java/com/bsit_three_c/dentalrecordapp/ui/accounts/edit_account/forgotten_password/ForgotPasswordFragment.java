package com.bsit_three_c.dentalrecordapp.ui.accounts.edit_account.forgotten_password;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bsit_three_c.dentalrecordapp.R;
import com.bsit_three_c.dentalrecordapp.databinding.FragmentForgotPasswordBinding;
import com.bsit_three_c.dentalrecordapp.util.Checker;
import com.google.android.material.snackbar.Snackbar;

public class ForgotPasswordFragment extends Fragment {
    private static final String TAG = ForgotPasswordFragment.class.getSimpleName();

    private FragmentForgotPasswordBinding binding;
    private ForgotPasswordViewModel mViewModel;

    public static ForgotPasswordFragment newInstance() {
        return new ForgotPasswordFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentForgotPasswordBinding.inflate(inflater, container, false);
        mViewModel = new ViewModelProvider(this).get(ForgotPasswordViewModel.class);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.etFPEmailAddress.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!Checker.isEmailValid(s.toString())) {
                    binding.etFPEmailAddress.setError(getString(R.string.invalid_email));
                }
            }
        });

        binding.btnResetPassword.setOnClickListener(v -> {
            Log.d(TAG, "onViewCreated: initializing email to be sent");
            String email = binding.etFPEmailAddress.getText().toString().trim();

            if (email.isEmpty()) {
                Log.d(TAG, "onViewCreated: email is empty");
                binding.etFPEmailAddress.setError(getString(R.string.invalid_empty_input));
                return;
            }

            Log.d(TAG, "onViewCreated: finding email");
            mViewModel.sendPasswordReset(email);

            Snackbar.make(v, "Send reset password link to email.", Snackbar.LENGTH_SHORT)
                    .show();
        });

//        mViewModel.getmAccount().observe(getViewLifecycleOwner(), account -> {
//            if (account != null) {
//                Log.d(TAG, "onChanged: account found: " + account);
//                Log.d(TAG, "onViewCreated: sending password reset");
//                mViewModel.sendPasswordReset(account.getEmail());
//            }
//            else {
//                Log.d(TAG, "onChanged: has no account belong to email: " + binding.etFPEmailAddress.getText().toString().trim());
//            }
//        });

        binding.btnForgotPassCancel.setOnClickListener(v -> requireActivity().finish());
    }
}