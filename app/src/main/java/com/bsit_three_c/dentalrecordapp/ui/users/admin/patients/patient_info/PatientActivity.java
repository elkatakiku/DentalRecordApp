package com.bsit_three_c.dentalrecordapp.ui.users.admin.patients.patient_info;

import android.content.Intent;
import android.os.Bundle;
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
import com.bsit_three_c.dentalrecordapp.databinding.ActivityPatientBinding;
import com.bsit_three_c.dentalrecordapp.ui.users.admin.patients.patient_form.AddPatientActivity;
import com.bsit_three_c.dentalrecordapp.util.Checker;

public class PatientActivity extends AppCompatActivity {

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
//                Snackbar.make(view, "Show dental chart here", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
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
                getIntent().putExtra(getString(R.string.PATIENT), result.getData());

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