package com.bsit_three_c.dentalrecordapp;

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

import com.bsit_three_c.dentalrecordapp.data.model.LoggedInUser;
import com.bsit_three_c.dentalrecordapp.data.repository.FirebaseHelper;
import com.bsit_three_c.dentalrecordapp.databinding.ActivityMainBinding;
import com.bsit_three_c.dentalrecordapp.ui.login_signup.LoginOrRegisterActivity;
import com.bsit_three_c.dentalrecordapp.ui.users.admin.MainAdminActivity;
import com.bsit_three_c.dentalrecordapp.ui.login.LoginViewModelFactory;
import com.bsit_three_c.dentalrecordapp.ui.search.SearchActivity;
import com.bsit_three_c.dentalrecordapp.ui.settings.SettingsActivity;
import com.bsit_three_c.dentalrecordapp.util.LocalStorage;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainAdminActivity";
    private static final int NAV_LOGOUT = R.id.nav_logout;
    private static final int SETTINGS = R.id.nav_settings;
    private AppBarConfiguration mAppBarConfiguration;
    private MainViewModel mainViewModel;
    private ActivityMainBinding binding;
    private LoggedInUser loggedInUser;
    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "onCreate: start of oncreate");

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mainViewModel = new ViewModelProvider(this, new LoginViewModelFactory()).get(MainViewModel.class);

        setSupportActionBar(binding.appBarMainUser.toolbar);

//        LoggedInUser hasLoggedInUser = LocalStorage.getLoggedInUser(this);
//        if (hasLoggedInUser != null) {
//            Log.d(TAG, "onCreate: user already logged in");
//            mainViewModel.setLoggedInUser(hasLoggedInUser);
//        }

//        mainViewModel.getmLoggedInUser().observe(this, new Observer<LoggedInUser>() {
//            @Override
//            public void onChanged(LoggedInUser loggedInUser) {
//                if (loggedInUser != null && !mainViewModel.isLogged()) {
//                    Log.d(TAG, "onChanged: logged: " + mainViewModel.isLogged());
//                    startActivity(new Intent(MainActivity.this, MainActivity.class));
//                    finish();
//                }
//            }
//        });
//
//        loggedInUser = (LoggedInUser) getIntent().getSerializableExtra(LocalStorage.LOGGED_IN_USER_KEY);
//        if (loggedInUser == null) {
//            loggedInUser = new LoggedInUser("N/A", "Guest", "guest@email.com", "User");
//            Log.d(TAG, "onStart: getIntent is null");
//        } else Log.d(TAG, "onStart: getIntent isn't null");

//        updateHeader(binding.navMainView);


        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navUserMainView;
//        navigationView.getMenu().clear();

//        binding.appBarMain;
//        View v = binding.appBarMain.contentMain.getRoot();

//        if (loggedInUser != null) {
//
//            Log.d(TAG, "onCreate: type: " + loggedInUser.getType());
//
//            if ("Client".equals(loggedInUser.getType())) {
//                Log.d(TAG, "onCreate: this is client");
//            } else if ("Admin".equals(loggedInUser.getType())) {
//                Log.d(TAG, "onCreate: this is admin");
//            }
//        }
//
//        if (loggedInUser != null && Checker.isDataAvailable(loggedInUser.getType())) {
//
//            if ("User".equals(loggedInUser.getType())) {
//                Log.d(TAG, "onCreate: user is null");
//                navigationView.inflateMenu(R.menu.home_drawer);
                mAppBarConfiguration = new AppBarConfiguration.Builder(
                        R.id.nav_user_home, R.id.nav_user_service)
                        .setOpenableLayout(drawer)
                        .build();

                navigationView.getMenu().findItem(R.id.nav_logout).setVisible(false);

                navController = Navigation.findNavController(this, R.id.nav_host_fragment_container_user);

//                v.findViewById(R.id.nav_host_fragment_content_main).setVisibility(View.GONE);
//                v.findViewById(R.id.nav_host_fragment_container_user).setVisibility(View.VISIBLE);
//            }
//
//            else if ("Admin".equals(loggedInUser.getType())) {
//                startActivity(new Intent(MainActivity.this, MainAdminActivity.class));
//                finish();
//
//                navigationView.inflateMenu(R.menu.activity_main_drawer);
//                mAppBarConfiguration = new AppBarConfiguration.Builder(
//                        R.id.nav_menu, R.id.nav_dashboard, R.id.nav_patients, R.id.nav_service, R.id.nav_appointments)
//                        .setOpenableLayout(drawer)
//                        .build();
//
//                navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
//
//                v.findViewById(R.id.nav_host_fragment_content_main).setVisibility(View.VISIBLE);
//                v.findViewById(R.id.nav_host_fragment_container_user).setVisibility(View.GONE);
//            }
//        }


        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.

        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        onNavMenuItemSelected(navigationView.getMenu());

        Log.d(TAG, "onCreate: ending of oncreate");

    }

    private void updateHeader(NavigationView navigationView) {
        View view = navigationView.getHeaderView(0);
        TextView displayName = view.findViewById(R.id.navTxtViewUsername);
        TextView email = view.findViewById(R.id.navTxtViewEmail);

        displayName.setText(loggedInUser.getDisplayName());
        email.setText(loggedInUser.getEmail());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        Log.d(TAG, "onOptionsItemSelected: this is called");
        Log.d(TAG, "onOptionsItemSelected: menu item: " + item.getItemId());
        Log.d(TAG, "onOptionsItemSelected: menu id: " + R.id.home);
        Log.d(TAG, "onOptionsItemSelected: profile id: " + R.id.menu_profile);

        Intent intent = null;
        int id = item.getItemId();

        if (id == R.id.app_bar_search) {
            intent = new Intent(this, SearchActivity.class);
        } else if (id == R.id.home) {
            Snackbar.make(binding.getRoot(), "Menu clicked", Snackbar.LENGTH_SHORT).show();
            Log.d(TAG, "onOptionsItemSelected: hamburger menu clicked");
        } else if (id == R.id.menu_profile) {
            //  Start activity on result
            startActivity(new Intent(MainActivity.this, LoginOrRegisterActivity.class));
        }

//        else if (id == R.id.nav_settings) {
//            intent = new Intent(this, SettingsActivity.class);
//        }
//
        if (intent != null) {
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
//        MenuItem searchMenuItem = menu.findItem(R.id.app_bar_search);
//
//        final SearchView searchView = (SearchView) searchMenuItem.getActionView();
//        searchView.getQueryHint("Seach patient");
//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                return false;
//            }
//        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        LoggedInUser loggedInUser = LocalStorage.getLoggedInUser(this);

        if (loggedInUser != null) {
            if (FirebaseHelper.TYPE_ADMIN.equals(loggedInUser.getType())) {
                sendUserToAdminHome();
                return;
            }
        }

        Log.d(TAG, "onStart: onstart called");
    }

    private void sendUserToAdminHome() {
        Intent adminIntent = new Intent(MainActivity.this, MainAdminActivity.class);
        startActivity(adminIntent);
        finish();
    }


    @Override
    public boolean onSupportNavigateUp() {

        Log.d(TAG, "onSupportNavigateUp: this is called");

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_container_user);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    private void onNavMenuItemSelected(Menu menu) {
        menu.findItem(NAV_LOGOUT).setOnMenuItemClickListener(item -> {
            logout();
            return true;
        });

        menu.findItem(SETTINGS).setOnMenuItemClickListener(item -> {
            startActivity(new Intent(MainActivity.this, SettingsActivity.class));
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
        mainViewModel.logout();

        Log.d(TAG, "onNavigationItemSelected: going back to login ui");
        startActivity(new Intent(MainActivity.this, MainActivity.class));

        Log.d(TAG, "onNavigationItemSelected: killing activity");
        finish();
    }
}