package com.bsit_three_c.dentalrecordapp.ui.appointments.appointment_form;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.ui.AppBarConfiguration;

import com.bsit_three_c.dentalrecordapp.R;
import com.bsit_three_c.dentalrecordapp.databinding.ActivityFormAppointmentBinding;
import com.bsit_three_c.dentalrecordapp.ui.appointments.appointment_form.ui.appoinmentform.AppointmentFormFragment;

public class AppointmentActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityFormAppointmentBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityFormAppointmentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appointmentToolbar);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.formAppointmentContainer, AppointmentFormFragment.newInstance())
                    .commitNow();
        }
    }
}