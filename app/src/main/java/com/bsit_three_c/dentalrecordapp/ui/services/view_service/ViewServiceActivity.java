package com.bsit_three_c.dentalrecordapp.ui.services.view_service;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.bsit_three_c.dentalrecordapp.R;
import com.bsit_three_c.dentalrecordapp.data.model.DentalService;
import com.bsit_three_c.dentalrecordapp.databinding.ActivityViewServiceBinding;
import com.bsit_three_c.dentalrecordapp.ui.services.services_form.ServiceFormActivity;
import com.bsit_three_c.dentalrecordapp.util.LocalStorage;
import com.bsit_three_c.dentalrecordapp.util.UIUtil;

public class ViewServiceActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityViewServiceBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityViewServiceBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.serviceToolbar);

        binding.serviceAppbar.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {
            float range = (float) -appBarLayout.getTotalScrollRange();
            binding.serviceAppBarImage.setImageAlpha((int) (255 * (1.0f - (float) verticalOffset / range)));
        });

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_view_service);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        DentalService service = getIntent().getParcelableExtra(LocalStorage.PARCEL_KEY);

        binding.serviceCollapsingToolbar.setTitle(service.getTitle());
        UIUtil.loadDisplayImage(this, binding.serviceAppBarImage, service.getDisplayImage(), R.drawable.ic_baseline_image_24);

        boolean isAdmin = getIntent().getBooleanExtra(LocalStorage.IS_ADMIN, true);

        if (!isAdmin) {
            binding.serviceFloatingActionButton.setVisibility(View.GONE);
        }

        binding.serviceFloatingActionButton.setOnClickListener(view -> {
            //  Start activity for result
            startActivity(
                    new Intent(ViewServiceActivity.this, ServiceFormActivity.class)
                    .putExtra(LocalStorage.PARCEL_KEY, service));
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_view_service);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }
}