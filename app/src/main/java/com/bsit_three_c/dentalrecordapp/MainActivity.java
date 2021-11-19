package com.bsit_three_c.dentalrecordapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import android.widget.TextView;

import com.bsit_three_c.dentalrecordapp.data.model.LoggedInUser;
import com.bsit_three_c.dentalrecordapp.data.model.Patient;
import com.bsit_three_c.dentalrecordapp.ui.login.LoginActivity;
import com.bsit_three_c.dentalrecordapp.ui.login.LoginViewModelFactory;
import com.bsit_three_c.dentalrecordapp.util.LocalStorage;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.bsit_three_c.dentalrecordapp.databinding.ActivityMainBinding;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private AppBarConfiguration mAppBarConfiguration;
    private MainViewModel mainViewModel;
    private ActivityMainBinding binding;
    private LoggedInUser loggedInUser;

    private static final int NAV_LOGOUT = R.id.nav_logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mainViewModel = new ViewModelProvider(this, new LoginViewModelFactory()).get(MainViewModel.class);

        setSupportActionBar(binding.appBarMain.toolbar);
        binding.appBarMain.fabAddPatients.setOnClickListener(view -> {
            Snackbar.make(view, "Show add patient/user ui.", Snackbar.LENGTH_LONG)
                    .setAction("Action", null)
                    .show();
            savePatients();
        });
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        navigationView.getMenu().findItem(NAV_LOGOUT).setOnMenuItemClickListener(item -> {
            logout();
            return true;
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        loggedInUser = (LoggedInUser) getIntent().getSerializableExtra(LocalStorage.LOGGED_IN_USER_KEY);
        if (loggedInUser == null) {
            Log.d(TAG, "onStart: getIntent is null");
        } else Log.d(TAG, "onStart: getIntent isn't null");

        updateHeader(binding.navView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        MenuItem searchMenuItem = menu.findItem(R.id.app_bar_search);

        final SearchView searchView = (SearchView) searchMenuItem.getActionView();
//        searchView.getQueryHint("Seach patient");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    private void updateHeader(NavigationView navigationView) {
        View view = navigationView.getHeaderView(0);
        TextView displayName = (TextView) view.findViewById(R.id.navTxtViewUsername);
        TextView email = (TextView) view.findViewById(R.id.navTxtViewEmail);

        displayName.setText(loggedInUser.getDisplayName());
        email.setText(loggedInUser.getEmail());
    }

    private void logout() {
        Log.d(TAG, "onNavigationItemSelected: Clearing saved user info");
        LocalStorage.clearSavedUser(this);

        Log.d(TAG, "onNavigationItemSelected: logout in view model called");
        mainViewModel.logout();

        Log.d(TAG, "onNavigationItemSelected: going back to login ui");
        startActivity(new Intent(MainActivity.this, LoginActivity.class));

        Log.d(TAG, "onNavigationItemSelected: killing activity");
        finish();
    }

    // Test code
    private void savePatients() {
        final ArrayList<String> firstname = new ArrayList<>(5);
        firstname.add("Eli");
        firstname.add("Jeng");
        firstname.add("Eper");
        firstname.add("Bana");
        firstname.add("Gale");

        final ArrayList<String> lastname = new ArrayList<>(5);
        lastname.add("Lamzon");
        lastname.add("Albaciete");
        lastname.add("Adaza");
        lastname.add("Magno");
        lastname.add("Fernandez");

        final ArrayList<String> email = new ArrayList<>(5);
        email.add("email1@email.com");
        email.add("email2@email.com");
        email.add("email3@email.com");
        email.add("email4@email.com");
        email.add("email5@email.com");

        final ArrayList<String> number = new ArrayList<>(5);
        number.add("09123548697");
        number.add("09123548766");
        number.add("09482346159");
        number.add("09187456321");
        number.add("09483625189");

        for (int i = 0; i < 5; i++) {
            Log.d(TAG, "savePatients: Loooping arraylist " + i);
            Patient patient = new Patient(firstname.get(i), lastname.get(i),email.get(i), number.get(i));
            mainViewModel.addPatient(patient);
        }
    }
}