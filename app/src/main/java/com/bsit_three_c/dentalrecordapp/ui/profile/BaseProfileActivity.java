package com.bsit_three_c.dentalrecordapp.ui.profile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.bsit_three_c.dentalrecordapp.R;
import com.bsit_three_c.dentalrecordapp.databinding.ActivityFormBinding;
import com.bsit_three_c.dentalrecordapp.ui.profile.ui.profile_patient.ProfileFragment;

public class BaseProfileActivity extends AppCompatActivity {
    private static final String TAG = BaseProfileActivity.class.getSimpleName();

    public static final String PROFILE_KEY = "ARG_BP_PROFILE_KEY";

    public static final String USER_ID = "ARG_BP_USER_ID_KEY";

    public static final int PROFILE_PATIENT = 0x001ED87D;
    public static final int PROFILE_EMPLOYEE = 0x001ED87E;
    public static final int PROFILE_ADMIN = 0x001ED87F;

    private ActivityFormBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityFormBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.formToolbar);
        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setTitle("Profile");
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainer,
                            ProfileFragment.newInstance(
                                    getIntent().getStringExtra(USER_ID),
                                    getIntent().getIntExtra(PROFILE_KEY, -1)
//                                    getIntent().getStringExtra(ACCOUNT_ID)
                            ))
                    .commitNow();
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        Log.d(TAG, "onOptionsItemSelected: item menu selected: " + item.getItemId());
        Log.d(TAG, "onOptionsItemSelected: home id: " + android.R.id.home);

        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static Intent getProfileIntent(Context context, int profile, String userId) {
        return new Intent(context, BaseProfileActivity.class)
                .putExtra(PROFILE_KEY, profile)
                .putExtra(USER_ID, userId);
//                .putExtra(ACCOUNT_ID, accountId);
    }
}
