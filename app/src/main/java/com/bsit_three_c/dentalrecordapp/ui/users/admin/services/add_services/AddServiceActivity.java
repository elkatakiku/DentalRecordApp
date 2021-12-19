package com.bsit_three_c.dentalrecordapp.ui.users.admin.services.add_services;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.bsit_three_c.dentalrecordapp.R;
import com.bsit_three_c.dentalrecordapp.data.model.DentalService;
import com.bsit_three_c.dentalrecordapp.databinding.ActivityAddServiceBinding;
import com.bsit_three_c.dentalrecordapp.util.LocalStorage;

public class AddServiceActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityAddServiceBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityAddServiceBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        DentalService service = getIntent().getParcelableExtra(LocalStorage.PARCEL_KEY);
        if (service != null) {
            binding.toolbar.setTitle("Edit Service");
        }
        else {
            binding.toolbar.setTitle("New Service");
        }

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_add_service);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_add_service);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }
}