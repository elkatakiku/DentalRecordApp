package com.bsit_three_c.dentalrecordapp.ui.patients.patient_form;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.bsit_three_c.dentalrecordapp.R;
import com.bsit_three_c.dentalrecordapp.data.model.Appointment;
import com.bsit_three_c.dentalrecordapp.data.model.Patient;
import com.bsit_three_c.dentalrecordapp.databinding.ActivityFormBinding;
import com.bsit_three_c.dentalrecordapp.util.LocalStorage;

public class PatientFormActivity extends AppCompatActivity {
    private static final String TAG = PatientFormActivity.class.getSimpleName();

    private AppBarConfiguration appBarConfiguration;
    private ActivityFormBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityFormBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.formToolbar);
        ActionBar actionBar = getSupportActionBar();

        Patient patient = getIntent().getParcelableExtra(getString(R.string.PATIENT));

        Log.d(TAG, "onCreate: action bar: " + (actionBar != null));
        if (actionBar != null) {
            actionBar.setTitle(getString(R.string.toolbar_title_form,
                            patient == null ? "New Patient" : "Edit Patient"));
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }

        Appointment appointment = getIntent().getParcelableExtra(LocalStorage.APPOINTMENT_KEY);

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.formContainer,
                            PatientFormFragment.newInstance(patient, appointment))
                    .commitNow();
        }

//        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_add_patient);
//        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
//        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_add_patient);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }
}