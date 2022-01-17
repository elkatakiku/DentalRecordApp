package com.bsit_three_c.dentalrecordapp.ui.base;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.bsit_three_c.dentalrecordapp.R;
import com.bsit_three_c.dentalrecordapp.databinding.ActivityFormBinding;
import com.bsit_three_c.dentalrecordapp.ui.appointments.AppointmentsFragment;
import com.bsit_three_c.dentalrecordapp.ui.employees.view_employee.EmployeeActivitiesFragment;
import com.bsit_three_c.dentalrecordapp.ui.patients.dental_history.DentalHistoryFragment;

public class BaseListActivity extends AppCompatActivity {

    public static final String LIST_KEY = "ARG_LB_LIST_KEY";

    public static final int DENTAL_HISTORY_LIST = 0x001ED8E1;
    public static final int APPOINTMENT_LIST = 0x001ED8E2;
    public static final int ACCOUNT_HISTORY_LIST = 0x001ED8E3;

    private ActivityFormBinding binding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityFormBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.formToolbar);
        ActionBar actionBar = getSupportActionBar();

        int list = getIntent().getIntExtra(LIST_KEY, -1);

        if (actionBar != null) {
            actionBar.setTitle(getTitle(list));
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainer,  getFragment(list))
                    .commitNow();
        }
    }

    private String getTitle(final int form) {
        String title;

        switch (form) {
            case DENTAL_HISTORY_LIST:
                title = "Dental History";
                break;
            case APPOINTMENT_LIST:
                title = "Appointments";
                break;
            case ACCOUNT_HISTORY_LIST:
                title = "Account History";
                break;
            default:
                title = "List";
                break;
        }

        return title;
    }

    private Fragment getFragment(final int form) {
        Fragment fragment = null;

        switch (form) {
            case DENTAL_HISTORY_LIST:
                fragment = DentalHistoryFragment.newInstance(
                        getIntent().getStringExtra(getString(R.string.USER_UID_KEY)));
                break;
            case APPOINTMENT_LIST:
                Log.d("ASD", "getFragment: getting appointment fragment");
                fragment = AppointmentsFragment.newInstance(
                        getIntent().getStringExtra(getString(R.string.USER_UID_KEY)));
                break;
            case ACCOUNT_HISTORY_LIST:
                fragment = EmployeeActivitiesFragment.newInstance(
                        getIntent().getStringExtra(getString(R.string.USER_UID_KEY)));
                break;
            default:
                finish();
                break;
        }

        return fragment;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
