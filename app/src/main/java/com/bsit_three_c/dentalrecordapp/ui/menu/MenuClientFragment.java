package com.bsit_three_c.dentalrecordapp.ui.menu;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bsit_three_c.dentalrecordapp.R;
import com.bsit_three_c.dentalrecordapp.data.adapter.ServiceDisplaysAdapter;
import com.bsit_three_c.dentalrecordapp.data.adapter.ServicesViewHolder;
import com.bsit_three_c.dentalrecordapp.data.model.LoggedInUser;
import com.bsit_three_c.dentalrecordapp.databinding.FragmentClientMenuBinding;
import com.bsit_three_c.dentalrecordapp.ui.login.LoginActivity;
import com.bsit_three_c.dentalrecordapp.ui.register.RegisterActivity;
import com.bsit_three_c.dentalrecordapp.ui.services.view_service.ViewServiceActivity;
import com.bsit_three_c.dentalrecordapp.util.LocalStorage;
import com.bsit_three_c.dentalrecordapp.util.UIUtil;

public class MenuClientFragment extends Fragment {
    private static final String TAG = MenuClientFragment.class.getSimpleName();

    private MenuClientViewModel mViewModel;
    private FragmentClientMenuBinding binding;

    public static MenuClientFragment newInstance() {
        return new MenuClientFragment();
    }

    private final ActivityResultLauncher<Intent> loginActivity = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                Intent getIntent = result.getData();
                LoggedInUser loggedInUser = getIntent.getParcelableExtra(LocalStorage.LOGGED_IN_USER_KEY);

                Log.d(TAG, "onActivityResult: logged in user: " + loggedInUser);
                Log.d(TAG, "onActivityResult: is logged in user null: " + (loggedInUser == null));

                if (loggedInUser != null) {
                    Log.d(TAG, "onActivityResult: user logged in: " + loggedInUser.getDisplayName());
                    requireActivity().startActivity(UIUtil.redirectUser(requireContext(), loggedInUser.getType()));
                    requireActivity().finish();
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
        mViewModel = new ViewModelProvider(this).get(MenuClientViewModel.class);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ServiceDisplaysAdapter adapter = new ServiceDisplaysAdapter(requireActivity(), true);

        binding.rvMenuServices.setHasFixedSize(true);
        LinearLayoutManager manager = new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false);

        binding.rvMenuServices.setLayoutManager(manager);

        adapter.setmItemOnClickListener(itemOnClickListener);
        binding.rvMenuServices.setAdapter(adapter);

        mViewModel.initializeRepository(adapter);

        mViewModel.getmDentalServices().observe(getViewLifecycleOwner(), dentalServices -> {
            if (dentalServices != null) {
                adapter.addItems(dentalServices);
                adapter.notifyDataSetChanged();
            }
        });

        binding.btnUserRegister.setOnClickListener(v ->
                registerActivty.launch(new Intent(requireActivity(), RegisterActivity.class)));

        binding.cvViewServices.setOnClickListener(v ->
                NavHostFragment.findNavController(MenuClientFragment.this)
                .navigate(R.id.nav_user_service));

        binding.btnUserLogin.setOnClickListener(v ->
                loginActivity.launch(new Intent(requireActivity(), LoginActivity.class)));
    }

    @Override
    public void onResume() {
        super.onResume();

        mViewModel.loadServices();
    }

    private final ServicesViewHolder.ItemOnClickListener itemOnClickListener = service -> {
        requireActivity().startActivity(
                new Intent(requireContext(), ViewServiceActivity.class)
                        .putExtra(LocalStorage.PARCEL_KEY, service)
                        .putExtra(LocalStorage.IS_ADMIN, false));
    };

}