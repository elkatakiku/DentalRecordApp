package com.bsit_three_c.dentalrecordapp.ui.splash;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.bsit_three_c.dentalrecordapp.R;
import com.bsit_three_c.dentalrecordapp.databinding.ActivitySplashBinding;
import com.bsit_three_c.dentalrecordapp.ui.main.MainActivity;
import com.bsit_three_c.dentalrecordapp.ui.splash.ui.main.SectionsPagerAdapter;
import com.bsit_three_c.dentalrecordapp.util.LocalStorage;
import com.google.android.material.snackbar.Snackbar;

public class SplashActivity extends AppCompatActivity {

    private ActivitySplashBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (LocalStorage.isNewlyInstalled(this)) {
            LocalStorage.setInstallState(this, false);
        } else {
            goToHome();
        }

        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = binding.viewPager;
        viewPager.setAdapter(sectionsPagerAdapter);

        binding.btnSplashPrevious.setVisibility(View.INVISIBLE);
        setRadioButton(0);

        final View.OnClickListener onClickListener = v -> {
            switch (v.getId()) {
                case R.id.radioButton1:
                    viewPager.setCurrentItem(0);
                    break;
                case R.id.radioButton2:
                    viewPager.setCurrentItem(1);
                    break;
                case R.id.radioButton3:
                    viewPager.setCurrentItem(2);
                    break;
                case R.id.btnSplashSkip:
                    goToHome();
                    break;
                case R.id.btnSpashNext:
                    viewPager.setCurrentItem(viewPager.getCurrentItem()+1);
                    break;
                case R.id.btn_splash_previous:
                    viewPager.setCurrentItem(viewPager.getCurrentItem()-1);
                    break;
            }
        };

        binding.radioButton1.setOnClickListener(onClickListener);
        binding.radioButton2.setOnClickListener(onClickListener);
        binding.radioButton3.setOnClickListener(onClickListener);
        binding.btnSplashSkip.setOnClickListener(onClickListener);
        binding.btnSpashNext.setOnClickListener(onClickListener);
        binding.btnSplashPrevious.setOnClickListener(onClickListener);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                Log.d("PAGE", "onPageSelected: selected page: " + position);
                setRadioButton(position);
                setButtonNext(position, onClickListener);
                switch (position) {
                    case 0:
                        binding.btnSplashPrevious.setVisibility(View.INVISIBLE);
                        binding.btnSpashNext.setVisibility(View.VISIBLE);
                        break;
                    case 1:
                        binding.btnSplashPrevious.setVisibility(View.VISIBLE);
                        binding.btnSpashNext.setVisibility(View.VISIBLE);
                        break;
                    case 2:
                        binding.btnSplashPrevious.setVisibility(View.VISIBLE);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void goToHome() {
        Snackbar.make(binding.getRoot(), "Sending user to home.", Snackbar.LENGTH_SHORT)
                .show();
        startActivity(new Intent(SplashActivity.this, MainActivity.class));
        finish();
    }

    private void setButtonNext(final int position, final View.OnClickListener onClickListener) {
        if (position == 2) {
            binding.btnSpashNext.setText("Continue");
            binding.btnSpashNext.setOnClickListener(v -> {
                goToHome();
            });
        } else {
            binding.btnSpashNext.setText("Next");
            binding.btnSpashNext.setOnClickListener(onClickListener);
        }
    }

    private void setRadioButton(final int position) {
        binding.radioButton1.setBackgroundResource(R.drawable.ic_baseline_radio_button_unchecked_24);
        binding.radioButton1.setBackgroundTintList(ColorStateList.valueOf(0xFF295859));
        binding.radioButton2.setBackgroundResource(R.drawable.ic_baseline_radio_button_unchecked_24);
        binding.radioButton2.setBackgroundTintList(ColorStateList.valueOf(0xFF295859));
        binding.radioButton3.setBackgroundResource(R.drawable.ic_baseline_radio_button_unchecked_24);
        binding.radioButton3.setBackgroundTintList(ColorStateList.valueOf(0xFF295859));

        switch (position) {
            case 0:
                binding.radioButton1.setBackgroundResource(R.drawable.ic_baseline_radio_button_checked_24);
                binding.radioButton1.setBackgroundTintList(ColorStateList.valueOf(0xFF00B6BC));
                break;
            case 1:
                binding.radioButton2.setBackgroundResource(R.drawable.ic_baseline_radio_button_checked_24);
                binding.radioButton2.setBackgroundTintList(ColorStateList.valueOf(0xFF00B6BC));
                break;
            case 2:
                binding.radioButton3.setBackgroundResource(R.drawable.ic_baseline_radio_button_checked_24);
                binding.radioButton3.setBackgroundTintList(ColorStateList.valueOf(0xFF00B6BC));
                break;
        }
    }
}