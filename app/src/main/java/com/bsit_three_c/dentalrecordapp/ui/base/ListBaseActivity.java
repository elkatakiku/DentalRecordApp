package com.bsit_three_c.dentalrecordapp.ui.base;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bsit_three_c.dentalrecordapp.databinding.ActivityFormBinding;

public class ListBaseActivity extends AppCompatActivity {

    private ActivityFormBinding binding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityFormBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.formToolbar);

//        int form = getIntent().getIntExtra(PATIENT_FORM_KEY, -1);

//        binding.formToolbar.setTitle(getString(R.string.toolbar_title_form, getTitle(form)));

//        if (savedInstanceState == null) {
//            getSupportFragmentManager()
//                    .beginTransaction()
//                    .replace(R.id.formContainer,  getFragment(form))
//                    .commitNow();
//        }
    }
}
