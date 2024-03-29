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
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.bsit_three_c.dentalrecordapp.R;
import com.bsit_three_c.dentalrecordapp.data.model.Account;
import com.bsit_three_c.dentalrecordapp.data.model.LoggedInUser;
import com.bsit_three_c.dentalrecordapp.data.model.Patient;
import com.bsit_three_c.dentalrecordapp.databinding.ActivityMainBinding;
import com.bsit_three_c.dentalrecordapp.ui.base.BaseFormActivity;
import com.bsit_three_c.dentalrecordapp.ui.dialog.SuccessDialogFragment;
import com.bsit_three_c.dentalrecordapp.ui.login.LoginActivity;
import com.bsit_three_c.dentalrecordapp.ui.login_signup.LoginOrRegisterActivity;
import com.bsit_three_c.dentalrecordapp.ui.profile.BaseProfileActivity;
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

        if (result.getResultCode() == RESULT_OK && result.getData() != null) {
            LoggedInUser resultUser = result.getData().getParcelableExtra(LocalStorage.LOGGED_IN_USER_KEY);
            Patient patient = result.getData().getParcelableExtra(LocalStorage.UPDATED_PATIENT_KEY);

            if (resultUser != null) {
                switch (resultUser.getType()) {
                    case Account.TYPE_ADMIN: case Account.TYPE_EMPLOYEE:
                        sendUserToAdminHome();
                        break;
                    case Account.TYPE_PATIENT:
                        mainViewModel.setmLoggedInUser(resultUser);
                        break;
                }
            }
            else if (patient != null) {
                DialogFragment dialogFragment = new SuccessDialogFragment();
                Bundle argument = new Bundle();
                argument.putInt(SuccessDialogFragment.ICON_KEY, R.drawable.ic_baseline_check_24);
                argument.putString(SuccessDialogFragment.TITLE_KEY, "Success");
                argument.putString(SuccessDialogFragment.MESSAGE_KEY, "Account successfully created.");
                dialogFragment.setArguments(argument);
                dialogFragment.show(getSupportFragmentManager(), null);
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

        mainViewModel.getmLoggedInUser().observe(this, loggedInUser -> {
            if (loggedInUser != null) {
                Log.d(TAG, "onCreate: logged in person: " + loggedInUser);
                if (loggedInUser.getType() == Account.TYPE_PATIENT) {
                    mainViewModel.loadPatient(loggedInUser.getAccount().getUserUid());
                } else {
                    sendUserToAdminHome();
                }
            }
        });

        mainViewModel.getmPatient().observe(this, patient -> {
            if (patient != null) {
                setUserUi(navigationView, patient);
            }
        });

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_user_home, R.id.nav_user_service,
//                R.id.nav_user_dental_history, R.id.nav_user_appointments,
                R.id.nav_about)
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
    protected void onResume() {
        super.onResume();
        mainViewModel.setmLoggedInUser(LocalStorage.getLoggedInUser(this));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        menu.findItem(R.id.menu_search).setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }

    private void setUserUi(NavigationView navigationView, Patient patient) {
        if (patient != null) {
            Log.d(TAG, "onCreate: has logged in user");
            setVisibility(menu, true,
//                    R.id.nav_user_dental_history, R.id.nav_user_appointments,
                    R.id.nav_logout);
            setVisibility(menu, false, R.id.nav_login, R.id.nav_register);
            menu.findItem(R.id.nav_user_home).setChecked(true);
        } else {
            Log.d(TAG, "onCreate: has no logged in user");
            setVisibility(menu, true, R.id.nav_login, R.id.nav_register);
            setVisibility(menu, false,
//                    R.id.nav_user_dental_history, R.id.nav_user_appointments,
                    R.id.nav_logout);
        }

        updateHeader(navigationView, patient);
    }

    private void setVisibility(Menu menu, boolean visible, int... menuIds) {
        for (int i : menuIds) {
            menu.findItem(i).setVisible(visible);
        }
    }

    private void updateHeader(NavigationView navigationView, Patient patient) {
        View view = navigationView.getHeaderView(0);
        TextView displayName = view.findViewById(R.id.navTxtViewUsername);
        TextView email = view.findViewById(R.id.navTxtViewEmail);

        if (patient != null) {
            view.findViewById(R.id.tvHeaderSystemName).setVisibility(View.GONE);
            view.findViewById(R.id.headerUserDisplay).setVisibility(View.VISIBLE);

            UIUtil.setText(patient.getFullName(), displayName);
            UIUtil.setText(patient.getEmail(), email);
        }
        else {
            view.findViewById(R.id.tvHeaderSystemName).setVisibility(View.VISIBLE);
            view.findViewById(R.id.headerUserDisplay).setVisibility(View.GONE);
        }
    }

    private void onNavMenuItemSelected(Menu menu) {

        menu.findItem(R.id.nav_register).setOnMenuItemClickListener(item -> {
            startActivity(new Intent(MainActivity.this, BaseFormActivity.class)
                    .putExtra(BaseFormActivity.FORM_KEY, BaseFormActivity.FORM_REGISTRATION));
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

        final int id = item.getItemId();

        switch (id) {
            case R.id.menu_profile:
                if (mainViewModel.getmLoggedInUser().getValue() != null
                        && mainViewModel.getmLoggedInUser().getValue().getType() == Account.TYPE_PATIENT) {
                    startActivity(new Intent(MainActivity.this, BaseProfileActivity.class)
                            .putExtra(BaseProfileActivity.USER_ID, mainViewModel.getmLoggedInUser().getValue().getPerson().getUid())
                            .putExtra(BaseProfileActivity.PROFILE_KEY, BaseProfileActivity.PROFILE_PATIENT));
                } else {
                    toLoginResult.launch(new Intent(MainActivity.this, LoginOrRegisterActivity.class));
                }
                break;
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
        if (binding.drawerLayout.isOpen()) {
            binding.drawerLayout.close();
        }
        else {
            super.onBackPressed();
        }
    }

    private void sendUserToAdminHome() {
        Snackbar.make(binding.getRoot(), "Sending user to admin ui.", Snackbar.LENGTH_SHORT)
                .show();
        startActivity(new Intent(MainActivity.this, MainAdminActivity.class));
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        binding = null;
        if (mainViewModel.getmPatient().getValue() != null) {
            mainViewModel.removeListeners();
        }
    }
}