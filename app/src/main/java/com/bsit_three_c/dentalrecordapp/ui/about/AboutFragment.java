package com.bsit_three_c.dentalrecordapp.ui.about;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.bsit_three_c.dentalrecordapp.data.model.Clinic;
import com.bsit_three_c.dentalrecordapp.databinding.FragmentAboutBinding;
import com.bsit_three_c.dentalrecordapp.util.UIUtil;

public class AboutFragment extends Fragment {

    private AboutViewModel mViewModel;
    private FragmentAboutBinding binding;

    public static AboutFragment newInstance() {
        return new AboutFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mViewModel = new ViewModelProvider(this).get(AboutViewModel.class);
        binding = FragmentAboutBinding.inflate(inflater, container, false);

        mViewModel.getClinic();

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mViewModel.getmClinic().observe(getViewLifecycleOwner(), new Observer<Clinic>() {
            @Override
            public void onChanged(Clinic clinic) {
                if (clinic != null) {
                    UIUtil.setText(clinic.getContactNumber(), binding.tvAboutContact);
                    UIUtil.setText(clinic.getLocation(), binding.tvAboutLocation);
                }
            }
        });
    }
}