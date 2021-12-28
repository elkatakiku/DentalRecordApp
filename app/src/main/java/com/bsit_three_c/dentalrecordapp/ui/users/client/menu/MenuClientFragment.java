package com.bsit_three_c.dentalrecordapp.ui.users.client.menu;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.bsit_three_c.dentalrecordapp.R;
import com.bsit_three_c.dentalrecordapp.data.model.LoggedInUser;
import com.bsit_three_c.dentalrecordapp.databinding.FragmentClientMenuBinding;
import com.bsit_three_c.dentalrecordapp.ui.login.LoginActivity;
import com.bsit_three_c.dentalrecordapp.ui.register.RegisterActivity;
import com.bsit_three_c.dentalrecordapp.util.LocalStorage;
import com.google.android.material.snackbar.Snackbar;

public class MenuClientFragment extends Fragment {

    private MenuClientViewModel mViewModel;
    private FragmentClientMenuBinding binding;

    public static MenuClientFragment newInstance() {
        return new MenuClientFragment();
    }

    private final ActivityResultLauncher<Intent> loginActivity = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == RESULT_OK) {
                if (result.getData() != null) {
                    Intent getIntent = result.getData();
                    LoggedInUser loggedInUser = (LoggedInUser) getIntent.getSerializableExtra(LocalStorage.LOGGED_IN_USER_KEY);
                    if (loggedInUser != null) {
                        Snackbar.make(binding.getRoot(), "User logged in" + loggedInUser.getDisplayName(), Snackbar.LENGTH_SHORT).show();
                        startActivity(getIntent);
                        requireActivity().finish();
                    }
                }
            }
        }
    });

    private final ActivityResultLauncher<Intent> registerActivty = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == RESULT_OK && result.getData() != null) {

            }
        }
    });

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = FragmentClientMenuBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.btnUserRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerActivty.launch(new Intent(requireActivity(), RegisterActivity.class));
            }
        });

        binding.cvViewServices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavHostFragment.findNavController(MenuClientFragment.this)
                        .navigate(R.id.nav_user_service);
            }
        });

        binding.btnUserLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginActivity.launch(new Intent(requireActivity(), LoginActivity.class));
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(MenuClientViewModel.class);
        // TODO: Use the ViewModel
    }

}