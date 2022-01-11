package com.bsit_three_c.dentalrecordapp.ui.appointments.appointment_form;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.ui.AppBarConfiguration;

import com.bsit_three_c.dentalrecordapp.R;
import com.bsit_three_c.dentalrecordapp.databinding.ActivityFormBinding;
import com.bsit_three_c.dentalrecordapp.ui.appointments.appointment_form.ui.appoinmentform.AppointmentFormFragment;

public class AppointmentFormActivity extends AppCompatActivity {

    public static final String APPOINTMENT_KEY = "APPOINTMENT_KEY";

    private AppBarConfiguration appBarConfiguration;
    private ActivityFormBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityFormBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.formToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getString(R.string.toolbar_title_form, "Appointment"));
        }

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.formContainer,
                            AppointmentFormFragment.newInstance(getIntent().getParcelableExtra(getString(R.string.APPOINTMENT_KEY))))
                    .commitNow();
        }
    }
}