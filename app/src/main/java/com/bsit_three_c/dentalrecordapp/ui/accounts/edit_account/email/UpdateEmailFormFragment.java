package com.bsit_three_c.dentalrecordapp.ui.accounts.edit_account.email;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bsit_three_c.dentalrecordapp.R;
import com.bsit_three_c.dentalrecordapp.data.model.Account;
import com.bsit_three_c.dentalrecordapp.databinding.FragmentFormUpdateEmailBinding;
import com.bsit_three_c.dentalrecordapp.ui.accounts.edit_account.EditAccountFragment;
import com.bsit_three_c.dentalrecordapp.ui.dialog.SuccessDialogFragment;
import com.bsit_three_c.dentalrecordapp.util.Checker;
import com.google.android.material.snackbar.Snackbar;

public class UpdateEmailFormFragment extends Fragment {
    private static final String TAG = UpdateEmailFormFragment.class.getSimpleName();

    private static final String ACCOUNT_ID = "ARG_UPF_ACCOUNT_ID_KEY";

    private FragmentFormUpdateEmailBinding binding;
    private UpdateEmailViewModel mViewModel;

    public static UpdateEmailFormFragment newInstance(String accountId) {
        Bundle arguments = new Bundle();
        arguments.putString(ACCOUNT_ID, accountId);
        UpdateEmailFormFragment emailFormFragment = new UpdateEmailFormFragment();
        emailFormFragment.setArguments(arguments);
        return emailFormFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentFormUpdateEmailBinding.inflate(inflater, container, false);
        mViewModel = new ViewModelProvider(this).get(UpdateEmailViewModel.class);

        if (getArguments() != null) {
            mViewModel.setmAccountUid(getArguments().getString(ACCOUNT_ID));
        } else {
            requireActivity().finish();
        }

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mViewModel.getmAccountUid().observe(getViewLifecycleOwner(), s -> {
            if (s != null) {
                mViewModel.getAccount(s);
            } else {
                requireActivity().finish();
            }
        });

        mViewModel.getmAccount().observe(getViewLifecycleOwner(), account -> {
            if (account != null) {
                binding.tvEditEmailEmail.setText(account.getEmail());
            }
        });

        binding.etUpdatedEmailAddress.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!Checker.isEmailValid(s.toString())) {
                    binding.etUpdatedEmailAddress.setError(getString(R.string.invalid_email));
                }
            }
        });

        binding.btnEAUpdateEmail.setOnClickListener(v -> {
            String password = binding.etUpdateEmailPass.getText().toString().trim();
            Account account = mViewModel.getmAccount().getValue();
            String newEmail = binding.etUpdatedEmailAddress.getText().toString().trim();

            boolean isValid = true;

            if (newEmail.isEmpty()) {
                binding.etUpdatedEmailAddress.setError(getString(R.string.invalid_empty_input));
                isValid = false;
            }

            if (password.isEmpty()) {
                binding.etUpdateEmailPass.setError(getString(R.string.invalid_empty_input));
                isValid = false;
            }

            if (account != null) {
                if (!account.getPassword().equals(password)) {
                    binding.etUpdateEmailPass.setError(getString(R.string.password_not_match));
                    isValid = false;
                }
            } else {
                isValid = false;
            }

            if (isValid) {
                if (account.getEmail().equals(newEmail)) {
                    Snackbar.make(binding.btnEAUpdateEmail, "You entered the same email as you currently have.", Snackbar.LENGTH_SHORT)
                            .show();
                    return;
                }
                setFieldsEnabled(false);
                mViewModel.updateEmail(newEmail);
            }
        });

        mViewModel.getmError().observe(getViewLifecycleOwner(), integer -> {
            if (integer != null) {
                if (integer == Checker.VALID) {
                    setFieldsEnabled(true);
                    SuccessDialogFragment.newInstance(
                            R.drawable.ic_baseline_check_24,
                            "Success",
                            "Successfully updated email address. Verify your new email " +
                                    "address to access the application fully."
                    )
                            .setOnStuffClickListener(() -> {
                                requireActivity().setResult(Activity.RESULT_OK, new Intent()
                                        .putExtra(EditAccountFragment.ACCOUNT_KEY, mViewModel.getmAccountUid().getValue()));
                                requireActivity().finish();
                            })
                            .show(getChildFragmentManager(), null);
                }
                else {
                    Snackbar
                            .make(binding.btnEAUpdateEmail, integer, Snackbar.LENGTH_SHORT)
                            .show();
                }
            }
        });

        binding.btnUpdateEmailCancel.setOnClickListener(v -> requireActivity().finish());
    }

    private void setFieldsEnabled(boolean enabled) {
        binding.etUpdatedEmailAddress.setEnabled(enabled);
        binding.etUpdateEmailPass.setEnabled(enabled);
        binding.btnEAUpdateEmail.setEnabled(enabled);
        binding.updateEmailLoading.setVisibility(enabled ? View.GONE : View.VISIBLE);
    }
}
