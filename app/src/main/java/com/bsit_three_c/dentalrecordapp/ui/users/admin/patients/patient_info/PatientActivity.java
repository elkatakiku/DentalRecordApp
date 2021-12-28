package com.bsit_three_c.dentalrecordapp.ui.users.admin.patients.patient_info;

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
import com.bsit_three_c.dentalrecordapp.ui.users.admin.patients.patient_form.AddPatientActivity;
import com.bsit_three_c.dentalrecordapp.util.Checker;
import com.bsit_three_c.dentalrecordapp.util.LocalStorage;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class PatientActivity extends AppCompatActivity {
    private static final String TAG = PatientActivity.class.getSimpleName();

    private AppBarConfiguration appBarConfiguration;
    private ActivityPatientBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityPatientBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarPatient.toolbar);
//        AppBarLayout actionBar = binding.appBarPatient.appbar;
//        if (actionBar != null) {
//            actionBar.setDisplayHomeAsUpEnabled(true);
//            actionBar.setDisplayHomeAsUpEnabled(true);
//        }

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_patient);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        Patient patient = getIntent().getParcelableExtra(getString(R.string.PATIENT));

        binding.appBarPatient.floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent toEditPatient = new Intent(PatientActivity.this, AddPatientActivity.class);
                toEditPatient.putExtra(getString(R.string.PATIENT), patient);
                toEditPatientResult.launch(toEditPatient);
            }
        });

        String fullname = patient.getLastname() + ", " + patient.getFirstname();
        if (Checker.isDataAvailable(patient.getMiddleInitial())) fullname  += " " + patient.getMiddleInitial() + ".";
        binding.appBarPatient.collapsingToolbar.setTitle(fullname);
    }

    private final ActivityResultLauncher<Intent> toEditPatientResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                Log.d(TAG, "onActivityResult: result data: " + result.getData());
//                getIntent().putExtra(getString(R.string.PATIENT), result.getData());

                for (String key : result.getData().getExtras().keySet()) {
                    Log.d(TAG, "onActivityResult: key: " + key);
                }

                Patient returnedPatient = result.getData().getParcelableExtra(LocalStorage.UPDATED_PATIENT_KEY);
                if (returnedPatient != null) {
                    Log.d(TAG, "onActivityResult: got patient: " + returnedPatient);
//                    getIntent().putExtra(FirebaseHelper.PATIENT_UID, returnedPatient.getUid());
//                    Log.d(TAG, "onCreateView: patient uid in intent: " + getIntent().getStringExtra(FirebaseHelper.PATIENT_UID));
                    Log.d(TAG, "onActivityResult: intent: " + result.getData());

                    getIntent().putExtra(FirebaseHelper.PATIENT_UID, returnedPatient.getUid());
                    Log.d(TAG, "onActivityResult: patient uid in orig intent: " + getIntent().getStringExtra(FirebaseHelper.PATIENT_UID));


                    for (String key : getIntent().getExtras().keySet()) {
                        Log.d(TAG, "onActivityResult: orig intent key: " + key);
                    }

                }
//                getIntent().putExtra(FirebaseHelper.PATIENT_UID, result.getData().getParcelableExtra(getString(R.string.PATIENT, null)));

            }
        }
    });

    public FloatingActionButton getFloatingButton() {
        return binding.appBarPatient.floatingActionButton;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_patient);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }
}