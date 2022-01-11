package com.bsit_three_c.dentalrecordapp.ui.patients;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.bsit_three_c.dentalrecordapp.R;
import com.bsit_three_c.dentalrecordapp.databinding.ActivityFormBinding;
import com.bsit_three_c.dentalrecordapp.ui.patients.patient_form.PatientFormFragment;
import com.bsit_three_c.dentalrecordapp.ui.patients.procedure_form.ui.procedureform.ProcedureFormFragment;
import com.bsit_three_c.dentalrecordapp.ui.patients.registration_form.RegisterFormFragment;

public class PatientFormBaseActivity extends AppCompatActivity {
    public static final String PATIENT_FORM_KEY = "ARG_PATIENT_FORM_ACTIVITY_KEY";

    public static final int FORM_PATIENT = 0x001ED819;
    public static final int FORM_PROCEDURE = 0x001ED81A;
    public static final int FORM_REGISTRATION = 0x001ED81B;

    public static final String PATIENT_KEY = "ARG_PFA_PATIENT_KEY";
    public static final String APPOINTMENT_KEY = "ARG_PFA_APPOINTMENT_KEY";

    private ActivityFormBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityFormBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.formToolbar);

        int form = getIntent().getIntExtra(PATIENT_FORM_KEY, -1);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getString(R.string.toolbar_title_form, getTitle(form)));
        }

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.formContainer,  getFragment(form))
                    .commitNow();
        }
    }

    private String getTitle(final int form) {
        String title;

        switch (form) {
            case FORM_PATIENT:
                title = "New Patient";
                break;
            case FORM_PROCEDURE:
                title = "New Procedure";
                break;
            case FORM_REGISTRATION:
                title = "Patient Form";
                break;
            default:
                title = "Form";
                break;
        }

        return title;
    }

    private Fragment getFragment(final int form) {
        Fragment fragment = null;

        switch (form) {
            case FORM_PATIENT:
                fragment = PatientFormFragment.newInstance(
                        getIntent().getParcelableExtra(PATIENT_KEY),
                        getIntent().getParcelableExtra(APPOINTMENT_KEY)
                );
                break;
            case FORM_PROCEDURE:
                fragment = ProcedureFormFragment.newInstance(
                        getIntent().getParcelableExtra(PATIENT_KEY),
                        getIntent().getParcelableExtra(APPOINTMENT_KEY)
                );
                break;
            case FORM_REGISTRATION:
                fragment = RegisterFormFragment.newInstance();
                break;
            default:
                finish();
                break;
        }

        return fragment;
    }
}