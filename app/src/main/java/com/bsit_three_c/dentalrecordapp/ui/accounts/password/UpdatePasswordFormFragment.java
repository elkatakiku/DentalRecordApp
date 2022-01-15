package com.bsit_three_c.dentalrecordapp.ui.accounts.password;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bsit_three_c.dentalrecordapp.databinding.FragmentFormUpdatePasswordBinding;
import com.bsit_three_c.dentalrecordapp.ui.accounts.email.UpdateEmailFormFragment;

public class UpdatePasswordFormFragment extends Fragment {

    private FragmentFormUpdatePasswordBinding binding;

    public static UpdateEmailFormFragment newInstance() {
        return new UpdateEmailFormFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentFormUpdatePasswordBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


    }
}
