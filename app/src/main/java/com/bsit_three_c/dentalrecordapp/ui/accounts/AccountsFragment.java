package com.bsit_three_c.dentalrecordapp.ui.accounts;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.bsit_three_c.dentalrecordapp.databinding.FragmentAccountsFragBinding;
import com.bsit_three_c.dentalrecordapp.ui.accounts.ui.main.SectionsPagerAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

public class AccountsFragment extends Fragment {

    private FragmentAccountsFragBinding binding;
    private AccountsViewModel mViewModel;

    public static AccountsFragment newInstance() {
        return new AccountsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = FragmentAccountsFragBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(requireActivity(), getChildFragmentManager());
        ViewPager viewPager = binding.accountViewPager;
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = binding.accountTabs;
        tabs.setupWithViewPager(viewPager);
        FloatingActionButton fab = binding.accountFab;

        fab.setOnClickListener(view1 -> Snackbar.make(view1, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show());
    }
}