package com.bsit_three_c.dentalrecordapp.ui.patients.view_patient;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.bsit_three_c.dentalrecordapp.R;
import com.bsit_three_c.dentalrecordapp.data.model.Patient;
import com.bsit_three_c.dentalrecordapp.data.repository.FirebaseHelper;
import com.bsit_three_c.dentalrecordapp.databinding.ActivityPatientBinding;
import com.bsit_three_c.dentalrecordapp.ui.patients.patient_form.AddPatientActivity;
import com.bsit_three_c.dentalrecordapp.util.LocalStorage;

public class PatientActivity extends AppCompatActivity {
    private static final String TAG = PatientActivity.class.getSimpleName();

    private AppBarConfiguration appBarConfiguration;
    private ActivityPatientBinding binding;

    private Patient patient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityPatientBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarPatient.toolbar);

        binding.appBarPatient.appbar.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {
            float range = (float) -appBarLayout.getTotalScrollRange();
            binding.appBarPatient.appBarImage.setImageAlpha((int) (255 * (1.0f - (float) verticalOffset / range)));
        });

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_patient);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        this.patient = getIntent().getParcelableExtra(getString(R.string.PATIENT));

        binding.appBarPatient.floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent toEditPatient = new Intent(PatientActivity.this, AddPatientActivity.class);
                toEditPatient.putExtra(getString(R.string.PATIENT), patient);
                toEditPatientResult.launch(toEditPatient);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.d(TAG, "onResume: called");
        Log.d(TAG, "onResume: patient: " + patient);
        initializeTitle();
    }


    private void initializeTitle() {
        binding.appBarPatient.collapsingToolbar.setTitle(patient.getFullName());
    }

    private final ActivityResultLauncher<Intent> toEditPatientResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                Patient returnedPatient = result.getData().getParcelableExtra(LocalStorage.UPDATED_PATIENT_KEY);
                if (returnedPatient != null) {
                    PatientActivity.this.patient = returnedPatient;
                    getIntent().putExtra(FirebaseHelper.PATIENT_UID, returnedPatient.getUid());
                }
            }
        }
    });

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_patient);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }
}