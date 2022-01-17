package com.bsit_three_c.dentalrecordapp.ui.accounts.edit_account;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bsit_three_c.dentalrecordapp.databinding.FragmentEditAccountBinding;
import com.bsit_three_c.dentalrecordapp.ui.base.BaseFormActivity;
import com.bsit_three_c.dentalrecordapp.util.UIUtil;

public class EditAccountFragment extends Fragment {

    public static final String ACCOUNT_KEY = "ARG_EA_ACCOUNT_KEY";
    public static final String DELETE_KEY = "ARG_EA_DELETE_KEY";

    private EditAccountViewModel mViewModel;
    private FragmentEditAccountBinding binding;

    public static EditAccountFragment newInstance(String accountUid) {
        Bundle arguments = new Bundle();
        arguments.putString(ACCOUNT_KEY, accountUid);
        EditAccountFragment fragment = new EditAccountFragment();
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentEditAccountBinding.inflate(inflater, container, false);
        mViewModel = new ViewModelProvider(this).get(EditAccountViewModel.class);

        if (getArguments() != null) {
            mViewModel.setmAccountUid(getArguments().getString(ACCOUNT_KEY));
        } else {
            requireActivity().finish();
        }

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mViewModel.getmAccountUid().observe(getViewLifecycleOwner(), accoountUid -> {
            if (accoountUid != null) {
                mViewModel.getAccount(accoountUid);
            }
        });

        mViewModel.getmAccount().observe(getViewLifecycleOwner(), account -> {
            if (account != null) {
                UIUtil.setText(account.getEmail(), binding.tvEditAccountEmail);
            }
        });

        final ActivityResultLauncher<Intent> editResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    mViewModel.getAccount(result.getData().getStringExtra(ACCOUNT_KEY));
                }
            });

        binding.btnEAUpdateEmail.setOnClickListener(v -> {
            editResult.launch(BaseFormActivity.getUpdateEmailIntent(
                    requireContext(),
                    mViewModel.getmAccountUid().getValue())
            );
        });

        binding.btnEAChangePassword.setOnClickListener(v ->
                editResult.launch(BaseFormActivity.getUpdatePasswordIntent(
                        requireContext(),
                        mViewModel.getmAccountUid().getValue()))
        );

        final ActivityResultLauncher<Intent> deleteResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        if (result.getData().getBooleanExtra(DELETE_KEY, false)) {
                            requireActivity().setResult(RESULT_OK, result.getData());
                            requireActivity().finish();
                        }
                    }
                });

        binding.btnEADeleteAccount.setOnClickListener(v ->
                deleteResult.launch(BaseFormActivity.getDeleteAccountForm(requireContext(),
                        mViewModel.getmAccountUid().getValue())));
    }
}