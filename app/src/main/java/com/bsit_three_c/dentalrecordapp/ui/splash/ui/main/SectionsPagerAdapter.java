package com.bsit_three_c.dentalrecordapp.ui.splash.ui.main;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.bsit_three_c.dentalrecordapp.R;

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

    @StringRes
    private static final int[] TAB_TITLES = new int[]{R.string.tab_text_1, R.string.tab_text_2, R.string.tab_text_1};
    private final Context mContext;

    public SectionsPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        PlaceholderFragment fragment = PlaceholderFragment.newInstance(position+1);
        switch (position) {
            case 0:
                fragment = PlaceholderFragment.newInstance(R.drawable.logo_final, "Welcome to ELL\nDental Clinic", 1);
                break;
            case 1:
                fragment = PlaceholderFragment.newInstance(R.drawable.splash_1, "Caring for all your\nfamily's dental\needs.", 2);
                break;
            case 2:
                fragment = PlaceholderFragment.newInstance(R.drawable.splash_2, "Comfortable and high\nquality. You can rely on\nour dental treatment.", 3);
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
        return 3;
    }
}