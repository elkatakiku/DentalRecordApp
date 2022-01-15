package com.bsit_three_c.dentalrecordapp.ui.splash.ui.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.bsit_three_c.dentalrecordapp.databinding.FragmentSplashBinding;

/**
 * A placeholder fragment containing a simple view.
 */
public class PlaceholderFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";
    private static final String ARG_IMAGE_KEY = "ARG_SC_IMAGE_KEY";
    private static final String ARG_TEXT_KEY = "ARG_SC_TEXT_KEY";
    private static final String ARG_RADIO_KEY = "ARG_SC_RADIO_KEY";

    private PageViewModel pageViewModel;
    private FragmentSplashBinding binding;

    public static PlaceholderFragment newInstance(int index) {
        PlaceholderFragment fragment = new PlaceholderFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_SECTION_NUMBER, index);
        fragment.setArguments(bundle);
        return fragment;
    }

    public static PlaceholderFragment newInstance(int display, String txt, int radioBtn) {
        PlaceholderFragment fragment = new PlaceholderFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_IMAGE_KEY, display);
        bundle.putString(ARG_TEXT_KEY, txt);
        bundle.putInt(ARG_RADIO_KEY, radioBtn);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pageViewModel = new ViewModelProvider(this).get(PageViewModel.class);
        int index = 1;
        if (getArguments() != null) {
            index = getArguments().getInt(ARG_SECTION_NUMBER);
            pageViewModel.setmDrawable(getArguments().getInt(ARG_IMAGE_KEY));
            pageViewModel.setmText(getArguments().getString(ARG_TEXT_KEY));
            pageViewModel.setmRadio(getArguments().getInt(ARG_RADIO_KEY));
        }
//        pageViewModel.setIndex(index);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentSplashBinding.inflate(inflater, container, false);

//        final TextView textView = binding.sectionLabel;
//        pageViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                textView.setText(s);
//            }
//        });

        pageViewModel.getmDrawable().observe(getViewLifecycleOwner(), integer -> {
            if (integer != null ) {
                binding.ivSplashDisplayImage.setImageResource(integer);
            }
        });

        pageViewModel.getmText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if (s != null) {
                    binding.tvSplashMessage.setText(s);
                }
            }
        });

        pageViewModel.getmRadio().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                if (integer != null) {
                    switch (integer) {
                        case 1:
//                            binding.
                            break;
                        case 2:
                            break;
                        case 3:
                            break;
                    }
                }
            }
        });
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}