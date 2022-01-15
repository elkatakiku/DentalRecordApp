package com.bsit_three_c.dentalrecordapp.ui.employees.view_employee;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bsit_three_c.dentalrecordapp.R;
import com.bsit_three_c.dentalrecordapp.data.model.Account;
import com.bsit_three_c.dentalrecordapp.data.model.EmergencyContact;
import com.bsit_three_c.dentalrecordapp.data.model.Employee;
import com.bsit_three_c.dentalrecordapp.data.view_model_factory.CustomViewModelFactory;
import com.bsit_three_c.dentalrecordapp.databinding.FragmentViewEmployeeInfoBinding;
import com.bsit_three_c.dentalrecordapp.util.UIUtil;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EmployeeInfoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EmployeeInfoFragment extends Fragment {
    private static final String TAG = EmployeeInfoFragment.class.getSimpleName();

    private FragmentViewEmployeeInfoBinding binding;
    private EmployeeInfoViewModel viewModel;
//    private Employee employee;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
//    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
//    private String mParam2;

    public EmployeeInfoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
//     * @param param2 Parameter 2.
     * @return A new instance of fragment EmployeeInfoFragment.
     */
    // TODO: Rename and change types and number of parameters
//    public static EmployeeInfoFragment newInstance(String param1, String param2) {
//        EmployeeInfoFragment fragment = new EmployeeInfoFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
//        return fragment;
//    }

    public static EmployeeInfoFragment newInstance(String param1) {
        EmployeeInfoFragment fragment = new EmployeeInfoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    public static EmployeeInfoFragment newInstance() {
        return new EmployeeInfoFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = new ViewModelProvider(this, new CustomViewModelFactory()).get(EmployeeInfoViewModel.class);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            viewModel.loadEmployeeData(mParam1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
        } else {
            Log.e(TAG, "onCreate: no employee uid passed");
            requireActivity().finish();
        }

        Log.d(TAG, "onCreate: employee: " + requireActivity().getIntent().getParcelableExtra(getString(R.string.EMPLOYEE)));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentViewEmployeeInfoBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel.getmEmployee().observe(getViewLifecycleOwner(), employee -> {
            Log.d(TAG, "onViewCreated: employee data changed");
            if (employee != null) {
                Log.d(TAG, "onViewCreated: got employee: " + employee);
                initializePersonalInfoFields(employee);
                viewModel.loadAccount(employee.getAccountUid());
                viewModel.loadEmergencyContact(employee.getEmergencyContactUid());
            }
            else {
                requireActivity().finish();
            }
        });

        viewModel.getmAccount().observe(getViewLifecycleOwner(), account -> {
            if (account != null) {
                initializeAccountFields(account);
            }
        });

        viewModel.getmEmergencyContact().observe(getViewLifecycleOwner(), emergencyContact -> {
            if (emergencyContact != null) {
                Log.d(TAG, "onViewCreated: got emergency contact: " + emergencyContact);
                initializeEmergencyContactFields(emergencyContact);
            }
        });
    }

    private void initializePersonalInfoFields(Employee employee) {
        Log.d(TAG, "initializePersonalInfoFields: firstName: " + employee.getFirstname());
        UIUtil.setText(employee.getFirstname(), binding.txtViewEIFirstname);
        UIUtil.setText(employee.getLastname(), binding.txtViewEILastname);
        UIUtil.setText(employee.getMiddleInitial(), binding.txtViewEIMiddleInitial);
        UIUtil.setText(employee.getSuffix(), binding.tvEISuffix);
        UIUtil.setText(employee.getJobTitle(getResources()), binding.txtViewEIJob);
        UIUtil.setText(employee.getEmail(), binding.tvEIBasicEmail);
        UIUtil.setText(employee.getDateOfBirth(), binding.tvEIBirthdate);
        UIUtil.setText(employee.getAge(), binding.txtViewEIAge);
        UIUtil.setText(employee.getContactNumber(), binding.txtViewEITelephoneNumber);
        UIUtil.setText(employee.getBirthdate(), binding.tvEIBirthdate);
        UIUtil.setText(employee.getFullAddress(), binding.txtViewEIAddress);
        UIUtil.setText(employee.getCivilStatus(getResources()), binding.txtViewEICivilStatus);
    }

    private void initializeAccountFields(Account account) {
        UIUtil.setText(account.getEmail(), binding.tvEIEmail);
        UIUtil.setText(UIUtil.getPasswordText(account.getPassword()), binding.tvEIPassword);
    }

    private void initializeEmergencyContactFields(EmergencyContact emergencyContact) {
        UIUtil.setText(emergencyContact.getFullName(), binding.tvEmergencyName);
        UIUtil.setText(emergencyContact.getFullAddress(), binding.tvEmergencyAddress);
        UIUtil.setText(emergencyContact.getContactNumber(), binding.tvEmergencyContact);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        binding = null;
        viewModel.removeListeners();
    }
}