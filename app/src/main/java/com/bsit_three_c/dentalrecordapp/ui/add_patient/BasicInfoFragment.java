package com.bsit_three_c.dentalrecordapp.ui.add_patient;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.bsit_three_c.dentalrecordapp.R;
import com.bsit_three_c.dentalrecordapp.databinding.FragmentBasicInfoBinding;

public class BasicInfoFragment extends Fragment {

    private FragmentBasicInfoBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentBasicInfoBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.buttonFirst.setOnClickListener(view1 ->
                NavHostFragment.findNavController(BasicInfoFragment.this)
                        .navigate(R.id.action_FirstFragment_to_SecondFragment));

//        String[] status = {"Married", "Single", "Divorced"};
//        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<>(getActivity(), R.layout.item_civil_status, status);

//        Spinner civilStatus = binding.spnrCivilStatus;
//        civilStatus.setAdapter(adapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}