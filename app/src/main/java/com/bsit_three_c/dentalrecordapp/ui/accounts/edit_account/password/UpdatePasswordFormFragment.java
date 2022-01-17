package com.bsit_three_c.dentalrecordapp.ui.accounts.edit_account.password;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bsit_three_c.dentalrecordapp.R;
import com.bsit_three_c.dentalrecordapp.data.model.Account;
import com.bsit_three_c.dentalrecordapp.databinding.FragmentFormUpdatePasswordBinding;
import com.bsit_three_c.dentalrecordapp.ui.accounts.edit_account.EditAccountFragment;
import com.bsit_three_c.dentalrecordapp.ui.dialog.SuccessDialogFragment;
import com.bsit_three_c.dentalrecordapp.util.Checker;
import com.bsit_three_c.dentalrecordapp.util.CustomObserver;
import com.bsit_three_c.dentalrecordapp.util.CustomTextWatcher;
import com.google.android.material.snackbar.Snackbar;

public class UpdatePasswordFormFragment extends Fragment {
    private static final String TAG = UpdatePasswordFormFragment.class.getSimpleName();

    private static final String ACCOUNT_UID = "ARG_UPF_ACCOUNT_UID_KEY";

    private FragmentFormUpdatePasswordBinding binding;
    private UpdatePasswordViewModel mViewModel;

    public static UpdatePasswordFormFragment newInstance(String accountUid) {
        Bundle arguments = new Bundle();
        arguments.putString(ACCOUNT_UID, accountUid);
        UpdatePasswordFormFragment updatePasswordFormFragment = new UpdatePasswordFormFragment();
        updatePasswordFormFragment.setArguments(arguments);
        return updatePasswordFormFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentFormUpdatePasswordBinding.inflate(inflater, container, false);
        mViewModel = new ViewModelProvider(this).get(UpdatePasswordViewModel.class);

        if (getArguments() != null) {
            mViewModel.setmAccountUid(getArguments().getString(ACCOUNT_UID));
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
            if (account == null) {
                requireActivity().finish();
            }
        });

        binding.btnUpdatePassword.setOnClickListener(v -> {
            Account account = mViewModel.getmAccount().getValue();
            String currentPassword = binding.etUpdateCurrentPassword.getText().toString().trim();
            String newPassword = binding.etUPNewPassword.getText().toString().trim();
            String retypeNewPassword = binding.etUPRetypePassword.getText().toString().trim();

            boolean isValid = true;

            if (currentPassword.isEmpty()) {
                Log.d(TAG, "onViewCreated: current pass is empty");
                binding.etUpdateCurrentPassword.setError(getString(R.string.invalid_empty_input));
                isValid = false;
            }

            if (newPassword.isEmpty()) {
                Log.d(TAG, "onViewCreated: new pass is empty");
                binding.etUPNewPassword.setError(getString(R.string.invalid_empty_input));
                isValid = false;
            }

            if (retypeNewPassword.isEmpty()) {
                Log.d(TAG, "onViewCreated: retyped pass is empty");
                binding.etUPRetypePassword.setError(getString(R.string.invalid_empty_input));
                isValid = false;
            }

            if (account != null) {
                Log.d(TAG, "onViewCreated: account is null");
                if (!account.getPassword().equals(currentPassword)) {
                    Log.d(TAG, "onViewCreated: account pass and current pass not match");
                    binding.etUpdateCurrentPassword.setError(getString(R.string.password_not_match));
                    isValid = false;
                }

                if (!newPassword.equals(retypeNewPassword)) {
                    Log.d(TAG, "onViewCreated: new pass does not match");
                    binding.etUPRetypePassword.setError(getString(R.string.password_not_match));
                    isValid = false;
                }
            } else {
                isValid = false;
            }

            if (isValid) {
                if (account.getPassword().equals(newPassword)) {
                    Snackbar.make(binding.btnUpdatePassword, "Please enter a new password" +
                            " to change the password.", Snackbar.LENGTH_SHORT)
                            .show();
                    return;
                }
                setFieldsEnabled(false);
                Log.d(TAG, "onViewCreated: inputs are valid updating password");
                mViewModel.updatePassword(newPassword);
            }
        });

        mViewModel.getmError().observe(getViewLifecycleOwner(), integer -> {
            Log.d(TAG, "onViewCreated: getting error status");
            if (integer != null) {
                Log.d(TAG, "onViewCreated: has integer");
                if (integer == Checker.VALID) {
                    Log.d(TAG, "onViewCreated: is valid");
                    setFieldsEnabled(true);
                    SuccessDialogFragment.newInstance(
                            R.drawable.ic_baseline_check_24,
                            "Success",
                            "Successfully changed password."
                    )
                            .setOnStuffClickListener(() -> {
                                requireActivity()
                                        .setResult(Activity.RESULT_OK, new Intent()
                                                .putExtra(
                                                        EditAccountFragment.ACCOUNT_KEY,
                                                        mViewModel.getmAccountUid().getValue()
                                                ));
                                requireActivity().finish();
                            })
                            .show(getChildFragmentManager(), null);
                }
                else {
                    Log.d(TAG, "onViewCreated: has error");
                    Snackbar
                            .make(binding.btnUpdatePassword, integer, Snackbar.LENGTH_SHORT)
                            .show();
                }
            }
        });

        binding.btnUpdatePasswordCancel.setOnClickListener(v -> requireActivity().finish());

        setListeners();
        setObservers();
    }

    private void setListeners() {
        binding.etUpdateCurrentPassword.addTextChangedListener(new CustomTextWatcher(mViewModel, binding.etUpdateCurrentPassword.getHint().toString().trim()));
        binding.etUPNewPassword.addTextChangedListener(new CustomTextWatcher(mViewModel, binding.etUPNewPassword.getHint().toString().trim()));
        binding.etUPRetypePassword.addTextChangedListener(new CustomTextWatcher(mViewModel, binding.etUPRetypePassword.getHint().toString().trim()));
    }

    private void setObservers() {
        Resources resources = getResources();
        mViewModel.getmCurrentPassword().observe(getViewLifecycleOwner(), new CustomObserver(binding.etUpdateCurrentPassword, resources));
        mViewModel.getmNewPassword().observe(getViewLifecycleOwner(), new CustomObserver(binding.etUPNewPassword, resources));
        mViewModel.getmRetypePassword().observe(getViewLifecycleOwner(), new CustomObserver(binding.etUPRetypePassword, resources));
    }

    private void setFieldsEnabled(boolean enabled) {
        binding.etUpdateCurrentPassword.setEnabled(enabled);
        binding.etUPNewPassword.setEnabled(enabled);
        binding.etUPRetypePassword.setEnabled(enabled);
        binding.btnUpdatePassword.setEnabled(enabled);
        binding.pbUpdatePasswordLoading.setVisibility(enabled ? View.GONE : View.VISIBLE);
    }
}
