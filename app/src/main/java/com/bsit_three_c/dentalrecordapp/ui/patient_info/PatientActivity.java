package com.bsit_three_c.dentalrecordapp.ui.patient_info;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.bsit_three_c.dentalrecordapp.R;
import com.bsit_three_c.dentalrecordapp.data.model.Patient;
import com.bsit_three_c.dentalrecordapp.databinding.ActivityPatientBinding;
import com.google.android.material.snackbar.Snackbar;

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

        binding.appBarPatient.floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Show dental chart here", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        Patient patient = getIntent().getParcelableExtra(getString(R.string.PATIENT));
        String fullname = patient.getLastname() + ", " + patient.getFirstname() + " " + patient.getMiddleInitial() + ".";
        binding.appBarPatient.collapsingToolbar.setTitle(fullname);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_patient);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }
}