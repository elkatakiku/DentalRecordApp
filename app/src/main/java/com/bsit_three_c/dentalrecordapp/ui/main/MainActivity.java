package com.bsit_three_c.dentalrecordapp.ui.main;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.bsit_three_c.dentalrecordapp.R;
import com.bsit_three_c.dentalrecordapp.data.model.Account;
import com.bsit_three_c.dentalrecordapp.data.model.LoggedInUser;
import com.bsit_three_c.dentalrecordapp.databinding.ActivityMainBinding;
import com.bsit_three_c.dentalrecordapp.ui.login.LoginActivity;
import com.bsit_three_c.dentalrecordapp.ui.login_signup.LoginOrRegisterActivity;
import com.bsit_three_c.dentalrecordapp.ui.patients.registration_form.RegisterActivity;
import com.bsit_three_c.dentalrecordapp.ui.search.SearchActivity;
import com.bsit_three_c.dentalrecordapp.ui.settings.SettingsActivity;
import com.bsit_three_c.dentalrecordapp.util.LocalStorage;
import com.bsit_three_c.dentalrecordapp.util.UIUtil;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final int NAV_LOGOUT = R.id.nav_logout;
    private static final int SETTINGS = R.id.nav_settings;
    private AppBarConfiguration mAppBarConfiguration;
    private MainViewModel mainViewModel;
    private ActivityMainBinding binding;
    private Menu menu;

    private final ActivityResultLauncher<Intent> toLoginResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {

        Log.d(TAG, "onActivityResult: success code: " + RESULT_OK);
        Log.d(TAG, "onActivityResult: result code: " + result.getResultCode());
        Log.d(TAG, "onActivityResult: data: " + result.getData());

        if (result.getResultCode() == RESULT_OK && result.getData() != null) {
            LoggedInUser resultUser = result.getData().getParcelableExtra(LocalStorage.LOGGED_IN_USER_KEY);
            Log.d(TAG, "got result: " + resultUser);

            if (resultUser != null) {
                switch (resultUser.getType()) {
                    case Account.TYPE_ADMIN:
                        sendUserToAdminHome();
                        break;
                    case Account.TYPE_EMPLOYEE:
                        sendUserToAdminHome();
                        break;
                    case Account.TYPE_PATIENT:
                        mainViewModel.setmLoggedInUser(resultUser);
                        break;
                }
            }
            else {
                Snackbar
                        .make(binding.getRoot(), "Failed to logged in.", Snackbar.LENGTH_SHORT)
                        .show();
            }
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navUserMainView;
        setSupportActionBar(binding.appBarMainUser.toolbar);

        mainViewModel.setmLoggedInUser(LocalStorage.getLoggedInUser(this));

        mainViewModel.getmLoggedInUser().observe(this, loggedInUser -> {
            setUserUi(navigationView, loggedInUser);
        });

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_user_home, R.id.nav_user_service, R.id.nav_user_dental_history, R.id.nav_user_appointments)
                .setOpenableLayout(drawer)
                .build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_container_user);

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        menu = navigationView.getMenu();
        onNavMenuItemSelected(menu);

        Log.d(TAG, "onCreate: ending of on create");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onStart() {
        super.onStart();

        Log.d(TAG, "onStart: starting main activity");

        LoggedInUser loggedInUser = LocalStorage.getLoggedInUser(this);
        Log.d(TAG, "onStart: loggedInUser: " + loggedInUser);

        if (loggedInUser != null) {
            switch (loggedInUser.getType()) {
                case Account.TYPE_ADMIN:
                    sendUserToAdminHome();
                    break;
                case Account.TYPE_EMPLOYEE:
                    sendUserToAdminHome();
                    break;
                case Account.TYPE_PATIENT:
                    mainViewModel.setmLoggedInUser(loggedInUser);
                    break;
            }
        }

        Log.d(TAG, "onStart: on start called");
    }

    private void setUserUi(NavigationView navigationView, LoggedInUser loggedInUser) {
        if (loggedInUser != null) {
            Log.d(TAG, "onCreate: has logged in user");
            setVisibility(menu, true, R.id.nav_user_dental_history, R.id.nav_user_appointments, R.id.nav_logout);
            setVisibility(menu, false, R.id.nav_login, R.id.nav_register);
            menu.findItem(R.id.nav_user_home).setChecked(true);
        } else {
            Log.d(TAG, "onCreate: has no logged in user");
            setVisibility(menu, true, R.id.nav_login, R.id.nav_register);
            setVisibility(menu, false, R.id.nav_user_dental_history, R.id.nav_user_appointments, R.id.nav_logout);
        }

        updateHeader(navigationView, loggedInUser);
    }

    private void setVisibility(Menu menu, boolean visible, int... menuIds) {
        for (int i : menuIds) {
            menu.findItem(i).setVisible(visible);
        }
    }

    private void updateHeader(NavigationView navigationView, LoggedInUser loggedInUser) {
        View view = navigationView.getHeaderView(0);
        TextView displayName = view.findViewById(R.id.navTxtViewUsername);
        TextView email = view.findViewById(R.id.navTxtViewEmail);

        if (loggedInUser != null) {
            view.findViewById(R.id.tvHeaderSystemName).setVisibility(View.GONE);
            view.findViewById(R.id.headerUserDisplay).setVisibility(View.VISIBLE);

            UIUtil.setText(loggedInUser.getDisplayName(), displayName);
            UIUtil.setText(loggedInUser.getEmail(), email);
        }
        else {
            view.findViewById(R.id.tvHeaderSystemName).setVisibility(View.VISIBLE);
            view.findViewById(R.id.headerUserDisplay).setVisibility(View.GONE);
        }
    }

    private void onNavMenuItemSelected(Menu menu) {

        menu.findItem(SETTINGS).setOnMenuItemClickListener(item -> {
            startActivity(new Intent(MainActivity.this, SettingsActivity.class));
            return true;
        });

        menu.findItem(R.id.nav_register).setOnMenuItemClickListener(item -> {
            startActivity(new Intent(MainActivity.this, RegisterActivity.class));
            return true;
        });

        menu.findItem(R.id.nav_login).setOnMenuItemClickListener(item -> {
            toLoginResult.launch(new Intent(MainActivity.this, LoginActivity.class));
            return true;
        });

        menu.findItem(NAV_LOGOUT).setOnMenuItemClickListener(item -> {
            mainViewModel.logout(this);
            return true;
        });


    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        Log.d(TAG, "onOptionsItemSelected: this is called");
        Log.d(TAG, "onOptionsItemSelected: menu item: " + item.getItemId());
        Log.d(TAG, "onOptionsItemSelected: menu id: " + R.id.home);
        Log.d(TAG, "onOptionsItemSelected: profile id: " + R.id.menu_profile);

        Intent intent = null;
        final int id = item.getItemId();

        switch (id) {
            case R.id.menu_profile:
                startActivity(new Intent(MainActivity.this, LoginOrRegisterActivity.class));
                break;
        }

        if (id == R.id.menu_search) {
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
    public boolean onSupportNavigateUp() {

        Log.d(TAG, "onSupportNavigateUp: this is called");

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_container_user);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        if (binding.drawerLayout.isOpen()) binding.drawerLayout.close();
        else super.onBackPressed();
    }

    private void sendUserToAdminHome() {
        Intent adminIntent = new Intent(MainActivity.this, MainAdminActivity.class);
        startActivity(adminIntent);
        finish();
    }
}