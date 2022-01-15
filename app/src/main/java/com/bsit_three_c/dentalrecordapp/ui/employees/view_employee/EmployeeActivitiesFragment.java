package com.bsit_three_c.dentalrecordapp.ui.employees.view_employee;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bsit_three_c.dentalrecordapp.databinding.FragmentListBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EmployeeActivitiesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EmployeeActivitiesFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String UID_KEY = "AR_EA_UID_KEY";
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private FragmentListBinding binding;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public EmployeeActivitiesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment EmployeeActivitiesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EmployeeActivitiesFragment newInstance(String param1, String param2) {
        EmployeeActivitiesFragment fragment = new EmployeeActivitiesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public static EmployeeActivitiesFragment newInstance(String uid) {
        Bundle arguments = new Bundle();
        arguments.putString(UID_KEY, uid);
        EmployeeActivitiesFragment employeeActivitiesFragment = new EmployeeActivitiesFragment();
        employeeActivitiesFragment.setArguments(arguments);
        return employeeActivitiesFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentListBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


    }
}