package com.bsit_three_c.dentalrecordapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.bsit_three_c.dentalrecordapp.databinding.ActivitySampleBinding;
import com.bsit_three_c.dentalrecordapp.ui.login.LoginActivity;
import com.bsit_three_c.dentalrecordapp.ui.login.LoginViewModelFactory;

public class SampleActivity extends AppCompatActivity {

    private SampleViewModel viewModel;
    private ActivitySampleBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySampleBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this, new LoginViewModelFactory()).get(SampleViewModel.class);

        binding.btnLogout.setOnClickListener(v -> {
            viewModel.logout();
            startActivity(new Intent(SampleActivity.this, LoginActivity.class));
            finish();
        });
    }
}