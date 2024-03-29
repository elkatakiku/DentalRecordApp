package com.bsit_three_c.dentalrecordapp.ui.main;

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
import com.bsit_three_c.dentalrecordapp.data.model.Employee;
import com.bsit_three_c.dentalrecordapp.data.model.LoggedInUser;
import com.bsit_three_c.dentalrecordapp.data.model.Person;
import com.bsit_three_c.dentalrecordapp.databinding.ActivityMainAdminBinding;
import com.bsit_three_c.dentalrecordapp.ui.accounts.edit_account.EditAccountFragment;
import com.bsit_three_c.dentalrecordapp.ui.dental_chart.DentalChartActivity;
import com.bsit_three_c.dentalrecordapp.ui.login.LoginViewModelFactory;
import com.bsit_three_c.dentalrecordapp.ui.profile.BaseProfileActivity;
import com.bsit_three_c.dentalrecordapp.util.LocalStorage;
import com.bsit_three_c.dentalrecordapp.util.UIUtil;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

public class MainAdminActivity extends AppCompatActivity {

    private static final String TAG = MainAdminActivity.class.getSimpleName();

    private static final int NAV_LOGOUT = R.id.nav_logout;
    private AppBarConfiguration mAppBarConfiguration;
    private MainAdminViewModel viewModel;
    private ActivityMainAdminBinding binding;

    final ActivityResultLauncher<Intent> profileResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    if (result.getData().getBooleanExtra(EditAccountFragment.DELETE_KEY, false)) {
                        logout();
                    }
                }
            });


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
                R.id.nav_service, R.id.nav_appointments, R.id.nav_dental_chart,
//                R.id.nav_accounts,
                R.id.nav_about)
                .setOpenableLayout(drawer)
                .build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main_admin);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);


        final Menu menu = navigationView.getMenu();
        onNavMenuItemSelected(menu);

        viewModel.getmLoggedInUser().observe(this, loggedInUser -> {
            if (loggedInUser != null) {
                if (loggedInUser.getType() == Account.TYPE_PATIENT) {
                    logout();
                } else if (loggedInUser.getType() == Account.TYPE_EMPLOYEE){
                    viewModel.getEmployee(loggedInUser.getAccount().getUserUid());
                } else if (loggedInUser.getType() == Account.TYPE_ADMIN) {
                    viewModel.getAdmin();
                }
            }
            else {
                logout();
            }
        });

        viewModel.getmEmployee().observe(this, employee -> {
            if (employee != null) {
                setUserUi(navigationView, employee, menu);
            }
        });

        viewModel.getmAdmin().observe(this, person -> {
            if (person != null) {
                setUserUi(navigationView, person, menu);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        menu.findItem(R.id.menu_search).setVisible(false);

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
                if (viewModel.getmLoggedInUser().getValue() != null) {
                    LoggedInUser loggedInUser = viewModel.getmLoggedInUser().getValue();
                    profileResult.launch(BaseProfileActivity.getProfileIntent(
                            this,
                            loggedInUser.getType() == Account.TYPE_ADMIN ? BaseProfileActivity.PROFILE_ADMIN : BaseProfileActivity.PROFILE_EMPLOYEE,
                            viewModel.getmLoggedInUser().getValue().getPerson().getUid()
                    ));
                }
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

        if (loggedInUser == null || (loggedInUser.getType() != Account.TYPE_ADMIN && loggedInUser.getType() != Account.TYPE_EMPLOYEE)) {
            logout();
            return;
        } else {
            Log.d(TAG, "onStart: account type: " + loggedInUser.getType());
            viewModel.setmLoggedInUser(loggedInUser);
        }

        Log.d(TAG, "onStart: on start called");
    }

    @Override
    public boolean onSupportNavigateUp() {

        Log.d(TAG, "onSupportNavigateUp: this is called");

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main_admin);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    private void setUserUi(NavigationView navigationView, Person person, Menu menu) {
        if (person instanceof Employee) {
            Log.d(TAG, "onCreate: has logged in user");
            setVisibility(menu, false, R.id.nav_dashboard, R.id.nav_employees
//                    , R.id.nav_accounts
            );
        } else {
            Log.d(TAG, "onCreate: has no logged in user");
            setVisibility(menu, true, R.id.nav_dashboard, R.id.nav_employees
//                    , R.id.nav_accounts
            );
        }

        menu.findItem(R.id.nav_menu).setChecked(true);
        updateHeader(navigationView, person);
    }

    private void setVisibility(Menu menu, boolean visible, int... menuIds) {
        for (int i : menuIds) {
            menu.findItem(i).setVisible(visible);
        }
    }

    private void onNavMenuItemSelected(Menu menu) {
        final LoggedInUser loggedInUser = LocalStorage.getLoggedInUser(this);
        if (loggedInUser != null &&
                loggedInUser.getType() == Account.TYPE_EMPLOYEE) {
            menu.findItem(R.id.nav_dental_chart).setOnMenuItemClickListener(item -> {
                startActivity(new Intent(MainAdminActivity.this, DentalChartActivity.class));
                return true;
            });
        }

        menu.findItem(NAV_LOGOUT).setOnMenuItemClickListener(item -> {
            logout();
            return true;
        });
    }

    @Override
    public void onBackPressed() {
        if (binding.drawerLayout.isOpen()) binding.drawerLayout.close();
        else super.onBackPressed();
    }

    private void updateHeader(NavigationView navigationView, Person person) {
        View view = navigationView.getHeaderView(0);
        TextView displayName = view.findViewById(R.id.navTxtViewUsername);
        TextView email = view.findViewById(R.id.navTxtViewEmail);

        view.findViewById(R.id.tvHeaderSystemName).setVisibility(View.GONE);
        view.findViewById(R.id.headerUserDisplay).setVisibility(View.VISIBLE);

        UIUtil.setText(person.getFullName(), displayName);
        UIUtil.setText(person.getEmail(), email);
    }

    private void logout() {
        Snackbar.make(binding.getRoot(), "Logging out user.", Snackbar.LENGTH_SHORT)
                .show();
        Log.d(TAG, "onNavigationItemSelected: Clearing saved user info");
        LocalStorage.clearSavedUser(this);

        Log.d(TAG, "onNavigationItemSelected: logout in view model called");
        viewModel.logout();

        Log.d(TAG, "onNavigationItemSelected: going back to login ui");
        startActivity(new Intent(MainAdminActivity.this, MainActivity.class));

        Log.d(TAG, "onNavigationItemSelected: killing activity");
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
        viewModel.removeListeners();
    }
}