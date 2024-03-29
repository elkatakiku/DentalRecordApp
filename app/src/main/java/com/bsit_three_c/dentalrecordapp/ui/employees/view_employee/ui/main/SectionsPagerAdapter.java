package com.bsit_three_c.dentalrecordapp.ui.employees.view_employee.ui.main;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.bsit_three_c.dentalrecordapp.R;
import com.bsit_three_c.dentalrecordapp.ui.employees.view_employee.EmployeeActivitiesFragment;
import com.bsit_three_c.dentalrecordapp.ui.employees.view_employee.EmployeeInfoFragment;

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

    private final String employeeUid;

    @StringRes
    private static final int[] TAB_TITLES = new int[]{R.string.employee_tab, R.string.account_activity_tab};
    private final Context mContext;

    public SectionsPagerAdapter(Context context, FragmentManager fm, String employeeUid) {
        super(fm);
        mContext = context;

        this.employeeUid = employeeUid;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
//        return PlaceholderFragment.newInstance(position + 1);
        Fragment fragment = null;

        switch (position) {
            case 0:
                fragment = EmployeeInfoFragment.newInstance(employeeUid);
                break;
            case 1:
                fragment = EmployeeActivitiesFragment.newInstance(employeeUid);
                break;
        }

        return fragment;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mContext.getResources().getString(TAB_TITLES[position]);
    }

    @Override
    public int getCount() {
        // Show 2 total pages.
        return 2;
    }
}