package com.bsit_three_c.dentalrecordapp.ui.patients.view_patient;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.bsit_three_c.dentalrecordapp.R;
import com.bsit_three_c.dentalrecordapp.data.model.Patient;
import com.bsit_three_c.dentalrecordapp.data.repository.BaseRepository;
import com.bsit_three_c.dentalrecordapp.databinding.AppBarPatientBinding;
import com.bsit_three_c.dentalrecordapp.ui.base.BaseFormActivity;
import com.bsit_three_c.dentalrecordapp.ui.patients.view_patient.ui.patientinfo.PatientInfoFragment;
import com.bsit_three_c.dentalrecordapp.util.LocalStorage;

public class PatientActivity extends AppCompatActivity {
    private static final String PATIENT_KEY = "ARG_PA_PATIENT_KEY";

    private AppBarPatientBinding binding;

    private Patient patient;
    private final FragmentManager fragmentManager = getSupportFragmentManager();

    public static void startPatientActivity(Context context, Patient patient) {
        context.startActivity(new Intent(context, PatientActivity.class)
                .putExtra(PATIENT_KEY, patient));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = AppBarPatientBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }

        binding.appbar.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {
            float range = (float) -appBarLayout.getTotalScrollRange();
            binding.appBarImage.setImageAlpha((int) (255 * (1.0f - (float) verticalOffset / range)));
        });

        this.patient = getIntent().getParcelableExtra(PATIENT_KEY);

        binding.floatingActionButton.setOnClickListener(view -> {
            Intent toEditPatient = new Intent(PatientActivity.this, BaseFormActivity.class)
                    .putExtra(BaseFormActivity.FORM_KEY, BaseFormActivity.FORM_PATIENT)
                    .putExtra(BaseFormActivity.PATIENT_KEY, patient);
            toEditPatientResult.launch(toEditPatient);
        });

        if (patient == null) {
            finish();
        }

        if (savedInstanceState == null) {
            if (patient != null) {
                fragmentManager
                        .beginTransaction()
                        .replace(R.id.nav_host_fragment_content_patient,
                                PatientInfoFragment.newInstance(patient))
                        .addToBackStack(null)
                        .commit();
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (fragmentManager.getBackStackEntryCount() == 1) {
            finish();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        initializeTitle();
    }


    private void initializeTitle() {
        binding.collapsingToolbar.setTitle(patient.getFullName());
    }

    private final ActivityResultLauncher<Intent> toEditPatientResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                Patient returnedPatient = result.getData().getParcelableExtra(LocalStorage.UPDATED_PATIENT_KEY);
                if (returnedPatient != null) {
                    PatientActivity.this.patient = returnedPatient;
                    getIntent().putExtra(BaseRepository.PATIENT_UID, returnedPatient.getUid());
                }
            }
        }
    });

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}