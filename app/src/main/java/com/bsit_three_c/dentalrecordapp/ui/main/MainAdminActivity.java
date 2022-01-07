package com.bsit_three_c.dentalrecordapp.ui.main;

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

import com.bsit_three_c.dentalrecordapp.R;
import com.bsit_three_c.dentalrecordapp.data.model.LoggedInUser;
import com.bsit_three_c.dentalrecordapp.databinding.ActivityMainAdminBinding;
import com.bsit_three_c.dentalrecordapp.ui.login.LoginViewModelFactory;
import com.bsit_three_c.dentalrecordapp.ui.settings.SettingsActivity;
import com.bsit_three_c.dentalrecordapp.util.LocalStorage;
import com.bsit_three_c.dentalrecordapp.util.UIUtil;
import com.google.android.material.navigation.NavigationView;

public class MainAdminActivity extends AppCompatActivity {

    private static final String TAG = MainAdminActivity.class.getSimpleName();

    private static final int NAV_LOGOUT = R.id.nav_logout;
    private static final int SETTINGS = R.id.nav_settings;
    private AppBarConfiguration mAppBarConfiguration;
    private MainAdminViewModel viewModel;
    private ActivityMainAdminBinding binding;
    private LoggedInUser loggedInUser;


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
                R.id.nav_menu, R.id.nav_dashboard, R.id.nav_patients, R.id.nav_employees,
                R.id.nav_service, R.id.nav_appointments, R.id.nav_dental_chart, R.id.nav_accounts)
                .setOpenableLayout(drawer)
                .build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main_admin);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        onNavMenuItemSelected(navigationView.getMenu());

        Log.d(TAG, "onCreate: stack count: " + getFragmentManager().getBackStackEntryCount());

        Log.d(TAG, "onCreate: current fragment: " + getFragmentManager().findFragmentById(R.layout.fragment_list_patients));
    }

    private void updateHeader(NavigationView navigationView) {
        View view = navigationView.getHeaderView(0);
        TextView displayName = view.findViewById(R.id.navTxtViewUsername);
        TextView email = view.findViewById(R.id.navTxtViewEmail);

        view.findViewById(R.id.tvHeaderSystemName).setVisibility(View.GONE);
        view.findViewById(R.id.headerUserDisplay).setVisibility(View.VISIBLE);

        UIUtil.setText(loggedInUser.getDisplayName(), displayName);
        UIUtil.setText(loggedInUser.getEmail(), email);
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

        menu.findItem(R.id.menu_search).setVisible(false);
//        MenuItem searchItem = menu.findItem(R.id.menu_search);
//        SearchManager searchManager = (SearchManager) this.getSystemService(Context.SEARCH_SERVICE);
//        if (searchItem != null) {
//            searchView = (SearchView) searchItem.getActionView();
//        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        Log.d(TAG, "onOptionsItemSelected: this is called ");

        Intent intent = null;
        int id = item.getItemId();

        final int SEARCH_ID = R.id.menu_search;
        final int PROFILE_ID = R.id.menu_profile;

        switch (id) {
            case SEARCH_ID:
                break;
            case PROFILE_ID:
                Log.d(TAG, "onOptionsItemSelected: main admin activity menu called");
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();

        Log.d(TAG, "onStart: getting logged in user");

        LoggedInUser loggedInUser = LocalStorage.getLoggedInUser(this);

        Log.d(TAG, "onStart: logged user: " + loggedInUser);

        if (loggedInUser != null) {
            Log.d(TAG, "onStart: account type: " + loggedInUser.getAccount().getUserType());
            this.loggedInUser = loggedInUser;
            updateHeader(binding.navAdminMainView);
        }

//        if (loggedInUser == null || loggedInUser.getType() != Account.TYPE_ADMIN) {
//            logout();
//            return;
//        }

        Log.d(TAG, "onStart: on start called");
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