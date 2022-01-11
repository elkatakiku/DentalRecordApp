package com.bsit_three_c.dentalrecordapp.ui.patients.procedure_form;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.bsit_three_c.dentalrecordapp.R;
import com.bsit_three_c.dentalrecordapp.data.model.Appointment;
import com.bsit_three_c.dentalrecordapp.data.model.Patient;
import com.bsit_three_c.dentalrecordapp.databinding.ActivityFormBinding;
import com.bsit_three_c.dentalrecordapp.ui.patients.procedure_form.ui.procedureform.ProcedureFormFragment;
import com.bsit_three_c.dentalrecordapp.util.LocalStorage;

public class ProcedureFormActivity extends AppCompatActivity {

    private ActivityFormBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityFormBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.formToolbar);

        binding.formToolbar.setTitle(getString(R.string.toolbar_title_form, "Procedure"));

        Patient patient = getIntent().getParcelableExtra(LocalStorage.UPDATED_PATIENT_KEY);
        Appointment appointment = getIntent().getParcelableExtra(LocalStorage.APPOINTMENT_KEY);
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.formContainer, ProcedureFormFragment.newInstance(patient, appointment))
                    .commitNow();
        }
    }
}