package com.bsit_three_c.dentalrecordapp.ui.employees.view_employee;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.bsit_three_c.dentalrecordapp.R;
import com.bsit_three_c.dentalrecordapp.data.model.Employee;
import com.bsit_three_c.dentalrecordapp.databinding.ActivityViewEmployeeBinding;
import com.bsit_three_c.dentalrecordapp.ui.employees.employee_form.EmployeeFormActivity;
import com.bsit_three_c.dentalrecordapp.ui.employees.view_employee.ui.main.SectionsPagerAdapter;
import com.bsit_three_c.dentalrecordapp.util.Checker;
import com.bsit_three_c.dentalrecordapp.util.UIUtil;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

public class ViewEmployeeActivity extends AppCompatActivity {
    private static final String TAG = ViewEmployeeActivity.class.getSimpleName();

    private ActivityViewEmployeeBinding binding;
    private Employee employee;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityViewEmployeeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        employee = getIntent().getParcelableExtra(getString(R.string.EMPLOYEE));
        Log.d(TAG, "onCreate: employee uid sent: " + employee);

        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager(), employee.getUid());
        ViewPager viewPager = binding.contentViewPager.viewPager;
        viewPager.setAdapter(sectionsPagerAdapter);

        TabLayout tabs = binding.tabs;
        tabs.setupWithViewPager(viewPager);

        FloatingActionButton fab = binding.employeeFloatingActionButton;

        fab.setOnClickListener(view -> {
            EmployeeFormActivity.showEmployeeForm(this, employee);
//            startActivity(new Intent(this, EmployeeFormActivity.class)
//                    .putExtra(BasicInfoFormFragment.EMPLOYEE_KEY, employee));
        });

        final CollapsingToolbarLayout collapsingToolbarLayout = binding.employeeCollapsingToolbar;
        AppBarLayout appBarLayout = binding.employeeAppbar;
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = true;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbarLayout.setTitle(employee.getFullName());
                    Log.d(TAG, "onOffsetChanged: firstname: " + employee.getFirstname());
                    isShow = true;
                } else if(isShow) {
                    collapsingToolbarLayout.setTitle(" ");  //careful there should a space between double quote otherwise it wont work
                    isShow = false;
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.d(TAG, "onResume: called");
        initializeToolbar();
    }

    private void initializeToolbar() {
        if (Checker.isDataAvailable(employee.getDisplayImage())) {
            employee.loadDisplayImage(this, binding.employeeAppBarImage);
        }
        else {
            binding.employeeAppBarImage.setImageResource(R.drawable.ic_baseline_person_24);
        }

        UIUtil.setText(employee.getFullName(), binding.tvEmployeeName);
        UIUtil.setText(employee.getJobTitle(getResources()), binding.tvEmployeeJobTitle);

    }
}