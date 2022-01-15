package com.bsit_three_c.dentalrecordapp.ui.clinic;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.bsit_three_c.dentalrecordapp.R;
import com.bsit_three_c.dentalrecordapp.data.adapter.ListWithRemoveItemAdapter;
import com.bsit_three_c.dentalrecordapp.data.model.Clinic;
import com.bsit_three_c.dentalrecordapp.data.repository.BaseRepository;
import com.bsit_three_c.dentalrecordapp.databinding.FragmentFormClinicBinding;
import com.bsit_three_c.dentalrecordapp.ui.dialog.TimePickerFragment;
import com.bsit_three_c.dentalrecordapp.util.Checker;
import com.bsit_three_c.dentalrecordapp.util.ContactNumber;
import com.bsit_three_c.dentalrecordapp.util.DateUtil;
import com.bsit_three_c.dentalrecordapp.util.UIUtil;
import com.google.android.material.snackbar.Snackbar;

public class ClinicFormFragment extends Fragment {
    private static final String TAG = ClinicFormFragment.class.getSimpleName();

    private ClinicFormViewModel mViewModel;
    private FragmentFormClinicBinding binding;
    private ListWithRemoveItemAdapter numbersAdapter;

    public static ClinicFormFragment newInstance() {
        return new ClinicFormFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentFormClinicBinding.inflate(inflater, container, false);
        mViewModel = new ViewModelProvider(this).get(ClinicFormViewModel.class);
        numbersAdapter = new ListWithRemoveItemAdapter(requireContext(), R.layout.item_list_with_remove);

        mViewModel.getClinic();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.lvClinicContactNumbers.setAdapter(numbersAdapter);
        numbersAdapter.setListView(binding.lvClinicContactNumbers);

        mViewModel.getmError().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                if (integer == Checker.VALID) {
                    requireActivity().setResult(Activity.RESULT_OK);
                    requireActivity().finish();
                } else {
                    Snackbar
                            .make(binding.btnClinicConfirm, getString(integer, "Clinic"), Snackbar.LENGTH_SHORT)
                            .show();
                }

                setFieldsEnabled(true);
                binding.pbLoadingConfirm.setVisibility(View.GONE);
            }
        });

        mViewModel.getmClinic().observe(getViewLifecycleOwner(), clinic -> {
            Log.d(TAG, "onChanged: set clinic");
            if (clinic != null) {
                initializeFields(clinic);
            }
        });

        binding.btnClinicAddNumber.setOnClickListener(v -> {
            String inputNumber = ContactNumber.getFormattedContactNumber(
                    binding.etClinicNumber,
                    binding.spnrClinicNumberMode,
                    requireContext().getResources()
            );

            if (inputNumber != null) {
                addMobileNumber(inputNumber);
            }
        });

        binding.layoutClinicStartTime.setOnClickListener(v -> showStartTimeDialog(true));
        binding.ibClinicStartTimeTime.setOnClickListener(v -> showStartTimeDialog(true));
        binding.layoutClinicEndTime.setOnClickListener(v -> showStartTimeDialog(false));
        binding.ibClinicEndTime.setOnClickListener(v -> showStartTimeDialog(false));

        binding.btnClinicConfirm.setOnClickListener(v -> {
            setFieldsEnabled(false);

            String name = binding.etClinicName.getText().toString().trim();
            String location = binding.etClinicLocation.getText().toString().trim();
            int from = binding.spnrClinicStartDay.getSelectedItemPosition();
            int to = binding.spnrClinicEndDay.getSelectedItemPosition();
            String startTIme = DateUtil.getTime(
                    binding.tvClinicStartTimeTimeHour.getText().toString().trim(),
                    binding.tvClinicStartTimeTimeMinutes.getText().toString().trim(),
                    binding.tvClinicStartTimeTimePeriod.getText().toString().trim()
            );
            String endTime = DateUtil.getTime(
                    binding.tvClinicEndTimeTimeHour.getText().toString().trim(),
                    binding.tvClinicEndTimeMinutes.getText().toString().trim(),
                    binding.tvClinicEndTimePeriod.getText().toString().trim()
            );

            mViewModel.uploadClinic(
                    name,
                    location,
                    numbersAdapter.getList(),
                    from,
                    to,
                    startTIme,
                    endTime
            );
        });
    }

    private void initializeFields(Clinic clinic) {

        UIUtil.setText(clinic.getName(), binding.etClinicName);
        UIUtil.setText(clinic.getLocation(), binding.etClinicLocation);

        numbersAdapter.clear();
        if (clinic.getContact() != null) {
            for (String number : clinic.getContact()) {
                if (clinic.getContact().get(0).equals(BaseRepository.NEW_PATIENT)) {
                    break;
                }
                addMobileNumber(number);
            }
        }

        binding.spnrClinicStartDay.setSelection(clinic.getStartDay());
        binding.spnrClinicEndDay.setSelection(clinic.getEndDay());

        UIUtil.setTimeFields(
                DateUtil.getTime(DateUtil.getDate(clinic.getStartTime())),
                binding.tvClinicStartTimeTimeHour,
                binding.tvClinicStartTimeTimeMinutes,
                binding.tvClinicStartTimeTimePeriod
        );

        UIUtil.setTimeFields(
                DateUtil.getTime(DateUtil.getDate(clinic.getEndTime())),
                binding.tvClinicEndTimeTimeHour,
                binding.tvClinicEndTimeMinutes,
                binding.tvClinicEndTimePeriod
        );
    }

    private void addMobileNumber(String number) {
        if (number.trim().isEmpty()) {
            binding.etClinicNumber.setError(getString(R.string.invalid_empty_input));
            return;
        }

        numbersAdapter.add(number);
        numbersAdapter.notifyDataSetChanged();

        binding.etClinicNumber.setText("");
    }

    private void showStartTimeDialog(boolean isStart) {
        TimePickerFragment dialogFragment = new TimePickerFragment(
                isStart ? binding.tvClinicStartTimeTimeHour : binding.tvClinicEndTimeTimeHour,
                isStart ? binding.tvClinicStartTimeTimeMinutes : binding.tvClinicEndTimeMinutes,
                isStart ? binding.tvClinicStartTimeTimePeriod : binding.tvClinicEndTimePeriod);

        dialogFragment.showDatePickerDialog(dialogFragment, getChildFragmentManager(), "Select Time of Appointment");
    }

    private void setFieldsEnabled(boolean enabled) {
        binding.pbLoadingConfirm.setVisibility(!enabled ? View.VISIBLE : View.GONE);
        binding.etClinicName.setEnabled(enabled);
        binding.etClinicLocation.setEnabled(enabled);
        binding.spnrClinicNumberMode.setEnabled(enabled);
        binding.etClinicNumber.setEnabled(enabled);
        binding.btnClinicAddNumber.setEnabled(enabled);
        binding.spnrClinicStartDay.setEnabled(enabled);
        binding.spnrClinicEndDay.setEnabled(enabled);
        binding.layoutClinicStartTime.setEnabled(enabled);
        binding.ibClinicStartTimeTime.setEnabled(enabled);
        binding.layoutClinicEndTime.setEnabled(enabled);
        binding.ibClinicEndTime.setEnabled(enabled);
        binding.btnClinicConfirm.setEnabled(enabled);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        mViewModel.removeListeners();
    }
}