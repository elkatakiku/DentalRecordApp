package com.bsit_three_c.dentalrecordapp.ui.add_patient;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.bsit_three_c.dentalrecordapp.R;
import com.bsit_three_c.dentalrecordapp.databinding.FragmentBasicInfoBinding;

public class BsicInfoFragment extends Fragment {

    private FragmentBasicInfoBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentBasicInfoBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.buttonFirst.setOnClickListener(view1 ->
                NavHostFragment.findNavController(BsicInfoFragment.this)
                .navigate(R.id.action_FirstFragment_to_SecondFragment));

        String[] status = {"Married", "Single", "Divorced"};
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<>(getActivity(), R.layout.patient_item, status);
//        adapter.setDropDownViewResource(R.layout.fragment_first);

        Spinner civilStatus = binding.spnrCivilStatus;
        civilStatus.setAdapter(adapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}