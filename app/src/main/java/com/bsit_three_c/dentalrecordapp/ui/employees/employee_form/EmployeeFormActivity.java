package com.bsit_three_c.dentalrecordapp.ui.employees.employee_form;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.bsit_three_c.dentalrecordapp.R;
import com.bsit_three_c.dentalrecordapp.data.model.Employee;
import com.bsit_three_c.dentalrecordapp.databinding.ActivityAddEmployeeBinding;

public class EmployeeFormActivity extends AppCompatActivity {

    public static final String EMPLOYEE_KEY = "ARG_EF_EMPLOYEE_KEY";

    private AppBarConfiguration appBarConfiguration;
    private ActivityAddEmployeeBinding binding;

    public static void showEmployeeForm(Context context, Employee employee) {
        context.startActivity(new Intent(context, EmployeeFormActivity.class)
                .putExtra(EMPLOYEE_KEY, employee));
    }

    public static void showEmployeeForm(Context context) {
        showEmployeeForm(context, null);
    }

    public static Intent getEmployeeForm(Context context, Employee employee) {
        return  new Intent(context, EmployeeFormActivity.class)
                .putExtra(EMPLOYEE_KEY, employee);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityAddEmployeeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_add_employee);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_add_employee);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }

    
}