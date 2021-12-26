package com.bsit_three_c.dentalrecordapp.ui.users.admin;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.bsit_three_c.dentalrecordapp.MainActivity;
import com.bsit_three_c.dentalrecordapp.R;
import com.bsit_three_c.dentalrecordapp.data.model.LoggedInUser;
import com.bsit_three_c.dentalrecordapp.data.repository.FirebaseHelper;
import com.bsit_three_c.dentalrecordapp.databinding.ActivityMainAdminBinding;
import com.bsit_three_c.dentalrecordapp.ui.login.LoginViewModelFactory;
import com.bsit_three_c.dentalrecordapp.ui.search.SearchActivity;
import com.bsit_three_c.dentalrecordapp.ui.settings.SettingsActivity;
import com.bsit_three_c.dentalrecordapp.util.LocalStorage;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

public class MainAdminActivity extends AppCompatActivity {

    private static final String TAG = MainAdminActivity.class.getSimpleName();

    private static final int NAV_LOGOUT = R.id.nav_logout;
    private static final int SETTINGS = R.id.nav_settings;
    private AppBarConfiguration mAppBarConfiguration;
    private MainAdminViewModel viewModel;
    private ActivityMainAdminBinding binding;
    private LoggedInUser loggedInUser;
    private NavController navController;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "onCreate: start of oncreate");

        binding = ActivityMainAdminBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this, new LoginViewModelFactory()).get(MainAdminViewModel.class);

        setSupportActionBar(binding.appBarMainAdmin.toolbarAdmin);

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navAdminMainView;

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_menu, R.id.nav_dashboard, R.id.nav_patients, R.id.nav_employees, R.id.nav_service, R.id.nav_appointments)
                .setOpenableLayout(drawer)
                .build();

        navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main_admin);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        onNavMenuItemSelected(navigationView.getMenu());
    }

    private void updateHeader(NavigationView navigationView) {
        View view = navigationView.getHeaderView(0);
        TextView displayName = view.findViewById(R.id.navTxtViewUsername);
        TextView email = view.findViewById(R.id.navTxtViewEmail);

        displayName.setText(loggedInUser.getDisplayName());
        email.setText(loggedInUser.getEmail());
    }

    private void sendUserToGuestHome() {
        Intent homeIntent = new Intent(MainAdminActivity.this, MainActivity.class);
        startActivity(homeIntent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        Log.d(TAG, "onOptionsItemSelected: this is called ");

        Intent intent = null;
        int id = item.getItemId();

        if (id == R.id.app_bar_search) {
            intent = new Intent(this, SearchActivity.class);
        } else if (id == R.id.home) {
            Snackbar.make(binding.getRoot(), "Menu clicked", Snackbar.LENGTH_SHORT).show();
            Log.d(TAG, "onOptionsItemSelected: hamburger menu clicked");
        }

        if (intent != null) {
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();

        LoggedInUser loggedInUser = LocalStorage.getLoggedInUser(this);

        if (loggedInUser == null || !FirebaseHelper.TYPE_ADMIN.equals(loggedInUser.getType())) {
            logout();
//            sendUserToGuestHome();
            return;
        }

        //  Set loggedInUser lccally if it will be only used once
        this.loggedInUser = loggedInUser;
        updateHeader(binding.navAdminMainView);

        Log.d(TAG, "onStart: onstart called");
    }

    @Override
    public boolean onSupportNavigateUp() {

        Log.d(TAG, "onSupportNavigateUp: this is called");

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main_admin);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    private void onNavMenuItemSelected(Menu menu) {
        menu.findItem(NAV_LOGOUT).setOnMenuItemClickListener(item -> {
            logout();
            return true;
        });

        menu.findItem(SETTINGS).setOnMenuItemClickListener(item -> {
            startActivity(new Intent(MainAdminActivity.this, SettingsActivity.class));
            return true;
        });
    }

    @Override
    public void onBackPressed() {
        if (binding.drawerLayout.isOpen()) binding.drawerLayout.close();
        else super.onBackPressed();
    }

    private void logout() {
        Log.d(TAG, "onNavigationItemSelected: Clearing saved user info");
        LocalStorage.clearSavedUser(this);

        Log.d(TAG, "onNavigationItemSelected: logout in view model called");
        viewModel.logout();

        Log.d(TAG, "onNavigationItemSelected: going back to login ui");
        startActivity(new Intent(MainAdminActivity.this, MainActivity.class));

        Log.d(TAG, "onNavigationItemSelected: killing activity");
        finish();
    }
}