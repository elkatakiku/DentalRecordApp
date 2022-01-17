package com.bsit_three_c.dentalrecordapp.ui.accounts.edit_account.delete_account;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bsit_three_c.dentalrecordapp.R;
import com.bsit_three_c.dentalrecordapp.databinding.FragmentDeleteAccountBinding;
import com.bsit_three_c.dentalrecordapp.ui.accounts.edit_account.EditAccountFragment;
import com.bsit_three_c.dentalrecordapp.ui.dialog.SuccessDialogFragment;
import com.bsit_three_c.dentalrecordapp.util.Checker;
import com.google.android.material.snackbar.Snackbar;

public class DeleteAccountFragment extends Fragment {
    private static final String ACCOUNT_ID = "ARG_DA_ACCOUNT_ID_KEY";

    private DeleteAccountViewModel mViewModel;
    private FragmentDeleteAccountBinding binding;

    public static DeleteAccountFragment newInstance(String accountUid) {
        Bundle arguments = new Bundle();
        arguments.putString(ACCOUNT_ID, accountUid);
        DeleteAccountFragment deleteAccountFragment = new DeleteAccountFragment();
        deleteAccountFragment.setArguments(arguments);
        return deleteAccountFragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mViewModel = new ViewModelProvider(this).get(DeleteAccountViewModel.class);
        binding = FragmentDeleteAccountBinding.inflate(inflater, container, false);

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
            if (account == null) {
                requireActivity().finish();
            }
        });

        binding.btnDeleteAccount.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            builder.setTitle("Delete Account Confirmation")
                    .setMessage("Deleting account is permanent. Are you sure you want to continue?")
                    .setPositiveButton("Ok", (dialog, which) -> {
                        setFieldsEnabled(false);
                        mViewModel.deleteAccount();
                        dialog.dismiss();
                    })
                    .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                    .create()
                    .show();
        });

        mViewModel.getmError().observe(getViewLifecycleOwner(), integer -> {
            if (integer != null) {
                if (integer == Checker.VALID) {
                    setFieldsEnabled(true);
                    SuccessDialogFragment.newInstance(
                            R.drawable.ic_baseline_check_24,
                            "Success",
                            "Successfully deleted account. Redirecting to you home page."
                    )
                            .setOnStuffClickListener(() -> {
                                requireActivity().setResult(Activity.RESULT_OK, new Intent()
                                        .putExtra(EditAccountFragment.DELETE_KEY, true));
                                requireActivity().finish();
                            })
                            .show(getChildFragmentManager(), null);
                }
                else {
                    Snackbar
                            .make(binding.btnDeleteAccount, integer, Snackbar.LENGTH_SHORT)
                            .show();
                }
            }
        });

    }

    private void setFieldsEnabled(boolean enabled) {
        binding.btnDeleteAccount.setEnabled(enabled);
        binding.pbDeleteLoading.setVisibility(enabled ? View.GONE : View.VISIBLE);
    }
}