package com.bsit_three_c.dentalrecordapp.ui.users.admin.services.view_service;

import android.content.Intent;
import android.net.Uri;
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
import com.bsit_three_c.dentalrecordapp.ui.users.admin.services.services_form.ServiceFormActivity;
import com.bsit_three_c.dentalrecordapp.util.LocalStorage;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

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

        Glide
                .with(this)
                .load(Uri.parse(service.getDisplayImage()))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(binding.serviceAppBarImage);

        binding.serviceFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //  Start activity for result
                startActivity(
                        new Intent(ViewServiceActivity.this, ServiceFormActivity.class)
                        .putExtra(LocalStorage.PARCEL_KEY, service));

            }
        });
    }

    public FloatingActionButton getFloatingActionButton() {
        return binding.serviceFloatingActionButton;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_view_service);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }
}