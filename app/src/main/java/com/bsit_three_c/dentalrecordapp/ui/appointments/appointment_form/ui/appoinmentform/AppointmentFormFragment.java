package com.bsit_three_c.dentalrecordapp.ui.appointments.appointment_form.ui.appoinmentform;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
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
import com.bsit_three_c.dentalrecordapp.data.adapter.ServiceOptionsAdapter;
import com.bsit_three_c.dentalrecordapp.data.model.Appointment;
import com.bsit_three_c.dentalrecordapp.data.model.DentalServiceOption;
import com.bsit_three_c.dentalrecordapp.databinding.FragmentFormAppointmentBinding;
import com.bsit_three_c.dentalrecordapp.ui.appointments.AppointmentsFragment;
import com.bsit_three_c.dentalrecordapp.ui.dialog.DatePickerFragment;
import com.bsit_three_c.dentalrecordapp.ui.dialog.TimePickerFragment;
import com.bsit_three_c.dentalrecordapp.util.Checker;
import com.bsit_three_c.dentalrecordapp.util.CustomObserver;
import com.bsit_three_c.dentalrecordapp.util.CustomTextWatcher;
import com.bsit_three_c.dentalrecordapp.util.DateUtil;
import com.bsit_three_c.dentalrecordapp.util.UIUtil;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

public class AppointmentFormFragment extends Fragment {
    private static final String TAG = AppointmentFormFragment.class.getSimpleName();

    private static final String APPOINTMENT_KEY = "APPOINTMENT_KEY";

    private AppointmentFormViewModel mViewModel;
    private FragmentFormAppointmentBinding binding;
    private Appointment appointment;

    public static AppointmentFormFragment newInstance(Appointment appointment) {
        Log.d(TAG, "newInstance: passed appointment: " + appointment);
        AppointmentFormFragment instance = new AppointmentFormFragment();
        Bundle arguments = new Bundle();
        arguments.putParcelable(APPOINTMENT_KEY, appointment);
        instance.setArguments(arguments);
        return instance;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        if (getArguments() != null) {
            appointment = getArguments().getParcelable(APPOINTMENT_KEY);
            Log.d(TAG, "onCreateView: appointment sent: " + appointment);
        }

        binding = FragmentFormAppointmentBinding.inflate(inflater, container, false);
        mViewModel = new ViewModelProvider(requireActivity()).get(AppointmentFormViewModel.class);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initializeServices();

        binding.ibAppointmentCalendar.setOnClickListener(v -> {
            binding.appointmentDateError.setVisibility(View.GONE);
            showDatePickerDialog();
        });

        binding.ibAppointmentTime.setOnClickListener(v -> {
            binding.appointmentTimeError.setVisibility(View.GONE);
            showTimePickerDialog();
        });

        binding.spnrAppointmentServices.setOnItemSelectedListener(
                new ServiceOptionsAdapter.selectorListener(binding.appointmentServiceError));

        binding.btnAppointmentConfirm.setOnClickListener(v -> {
            createAppointment();
        });

        mViewModel.getmUploadResult().observe(getViewLifecycleOwner(), integer -> {
            if (Checker.VALID == integer) {
                Log.d(TAG, "onViewCreated: not returning appointment data");
                requireActivity().setResult(Activity.RESULT_OK, new Intent()
                        .putExtra(AppointmentsFragment.RESULT_KEY, mViewModel.getAppointment()));
                requireActivity().finish();
            }
            else {
                Snackbar
                        .make(binding.btnAppointmentConfirm, integer, Snackbar.LENGTH_SHORT)
                        .show();
            }
        });

        setListeners();
        setObservers();
    }

    private void setServicesAdapter() {
        Log.d(TAG, "setServicesAdapter: setting adapter");
        ServiceOptionsAdapter serviceOptionsAdapter = new ServiceOptionsAdapter(requireContext(), 0,
                mViewModel.getDentalServiceOptions(), binding.spnrAppointmentServices);
        serviceOptionsAdapter.initializeSpinner();
        binding.spnrAppointmentServices.setAdapter(serviceOptionsAdapter);
    }

    private void initializeFields(Appointment appointment) {
        UIUtil.setText(appointment.getPatient().getFirstname(), binding.etAppointmentFirstname);
        UIUtil.setText(appointment.getPatient().getLastname(), binding.etAppointmentLastname);
        UIUtil.setText(appointment.getPatient().getMiddleInitial(), binding.etAppointmentMiddleInitial);
        UIUtil.setText(appointment.getPatient().getSuffix(), binding.etAppointmentSuffix);
        UIUtil.setText(appointment.getPatient().getContactNumber(), binding.etAppointmentContact);
        UIUtil.setDateFields(
                DateUtil.getDate(appointment.getDateTime()),
                binding.tvAppointmentCalendarDay,
                binding.tvAppointmentCalendarMonth,
                binding.tvAppointmentCalendarYear
        );
        UIUtil.setTimeFields(
                DateUtil.getTime(appointment.getDateTime()),
                binding.tvAppointmentTimeHour,
                binding.tvAppointmentTimeMinutes,
                binding.tvAppointmentTimePeriod
        );
        UIUtil.setField(appointment.getComments(), binding.etAppointmentComments);
    }

    private void showDatePickerDialog() {
        DatePickerFragment datePickerFragment = new DatePickerFragment(
                binding.tvAppointmentCalendarMonth,
                binding.tvAppointmentCalendarDay,
                binding.tvAppointmentCalendarYear
        );

        datePickerFragment.setMinDateToday();
        datePickerFragment.showDatePickerDialog(
                datePickerFragment,
                DateUtil.getMonthNumber(binding.tvAppointmentCalendarMonth.getText().toString()),
                getChildFragmentManager(),
                "Select Date of Appointment"
        );
    }

    private void showTimePickerDialog() {
        TimePickerFragment dialogFragment = new TimePickerFragment(
                binding.tvAppointmentTimeHour,
                binding.tvAppointmentTimeMinutes,
                binding.tvAppointmentTimePeriod);

        dialogFragment.showDatePickerDialog(dialogFragment, getChildFragmentManager(), "Select Time of Appointment");
    }

    private void initializeServices() {
        List<DentalServiceOption> dentalServiceOptions = mViewModel.getDentalServiceOptions();

        if (dentalServiceOptions.size() == 1) {
            mViewModel.loadServices();
        } else {
            setServicesAdapter();
        }

        mViewModel.getmDentalServices().observe(getViewLifecycleOwner(), services -> {
            if (services != null) {
                mViewModel.setServicesOptions(services, appointment);
                setServicesAdapter();
            }

            if (appointment != null) {
                initializeFields(appointment);
            }
        });
    }

    private void setFieldsEnabled(boolean enabled) {
        binding.pbAppoitnmentLoading.setVisibility(!enabled ? View.VISIBLE : View.GONE);
        binding.etAppointmentFirstname.setEnabled(enabled);
        binding.etAppointmentLastname.setEnabled(enabled);
        binding.etAppointmentMiddleInitial.setEnabled(enabled);
        binding.etAppointmentSuffix.setEnabled(enabled);
        binding.etAppointmentComments.setEnabled(enabled);
        binding.layoutAppointmentDate.setEnabled(enabled);
        binding.ibAppointmentCalendar.setEnabled(enabled);
        binding.layoutAppointmentTime.setEnabled(enabled);
        binding.ibAppointmentTime.setEnabled(enabled);
        binding.spnrAppointmentServices.setEnabled(enabled);
        binding.etAppointmentComments.setEnabled(enabled);
        binding.btnAppointmentConfirm.setEnabled(enabled);
    }

    private void setListeners() {
        binding.etAppointmentFirstname.addTextChangedListener(new CustomTextWatcher(mViewModel, binding.etAppointmentFirstname.getHint().toString()));
        binding.etAppointmentLastname.addTextChangedListener(new CustomTextWatcher(mViewModel, binding.etAppointmentLastname.getHint().toString()));
        binding.etAppointmentMiddleInitial.addTextChangedListener(new CustomTextWatcher(mViewModel, binding.etAppointmentMiddleInitial.getHint().toString()));
        binding.etAppointmentSuffix.addTextChangedListener(new CustomTextWatcher(mViewModel, binding.etAppointmentSuffix.getHint().toString()));
        binding.etAppointmentContact.addTextChangedListener(new CustomTextWatcher(mViewModel, binding.etAppointmentContact.getHint().toString()));
    }

    private void setObservers() {
        Resources resources = getResources();
        mViewModel.getmFirstname().observe(getViewLifecycleOwner(), new CustomObserver(binding.etAppointmentFirstname, resources));
        mViewModel.getMlastname().observe(getViewLifecycleOwner(), new CustomObserver(binding.etAppointmentLastname, resources));
        mViewModel.getmMiddleInitial().observe(getViewLifecycleOwner(), new CustomObserver(binding.etAppointmentMiddleInitial, resources));
        mViewModel.getmSuffix().observe(getViewLifecycleOwner(), new CustomObserver(binding.etAppointmentSuffix, resources));
        mViewModel.getmContact().observe(getViewLifecycleOwner(), new CustomObserver(binding.etAppointmentContact, resources));
    }

    private void createAppointment() {
        String firstname = binding.etAppointmentFirstname.getText().toString().trim();
        String lastname = binding.etAppointmentLastname.getText().toString().trim();
        String middleInitial = binding.etAppointmentMiddleInitial.getText().toString().trim();
        String suffix = binding.etAppointmentSuffix.getText().toString().trim();
        String contactNumber = binding.etAppointmentContact.getText().toString().trim();

        String appointmentDate = DateUtil.getDate(
                binding.tvAppointmentCalendarDay.getText().toString().trim(),
                binding.tvAppointmentCalendarMonth.getText().toString().trim(),
                binding.tvAppointmentCalendarYear.getText().toString().trim()
        );

        String appointmentTime = DateUtil.getTime(
                binding.tvAppointmentTimeHour.getText().toString().trim(),
                binding.tvAppointmentTimeMinutes.getText().toString().trim(),
                binding.tvAppointmentTimePeriod.getText().toString().trim()
        );

        List<String> service = UIUtil.getServiceUids(mViewModel.getDentalServiceOptions());
        String comment = binding.etAppointmentComments.getText().toString().trim();

        Log.d(TAG, "onClick: data inputted: " +
                "\nfirstname: " + firstname +
                "\nlastname: " + lastname +
                "\nmiddleInitial: " + middleInitial +
                "\nsuffix: " + suffix +
                "\ncontactNumber: " + contactNumber +
                "\nappointmentDate: " + appointmentDate +
                "\nappointmentTime: " + appointmentTime +
                "\nservice: " + service +
                "\ncomment: " + comment
        );

        if (!isInputValid(firstname, lastname, contactNumber, appointmentDate, appointmentTime, service)) {
            return;
        }

        setFieldsEnabled(false);

        mViewModel.createAppointment(
                firstname,
                lastname,
                middleInitial,
                suffix,
                contactNumber,
                appointmentDate,
                appointmentTime,
                service,
                comment
        );
    }

    private boolean isInputValid(String firstname, String lastname, String contactNumber,
                                 String appointmentDate, String appointmentTime, List<String> service) {
        boolean isValid = true;

        if (firstname.isEmpty()) {
            binding.etAppointmentFirstname.setError(getString(R.string.invalid_empty_input));
            isValid = false;
        }

        if (lastname.isEmpty()) {
            binding.etAppointmentLastname.setError(getString(R.string.invalid_empty_input));
            isValid = false;
        }

        if (contactNumber.isEmpty()) {
            binding.etAppointmentContact.setError(getString(R.string.invalid_empty_input));
            isValid = false;
        } else if (contactNumber.length() != 11 && contactNumber.length() != 7) {
            binding.etAppointmentContact.setError(getString(R.string.invalid_contact_number));
            isValid = false;
        }

        if (Checker.NOT_AVAILABLE.equals(appointmentDate)) {
            binding.appointmentDateError.setVisibility(View.VISIBLE);
            isValid = false;
        }

        if (Checker.NOT_AVAILABLE.equals(appointmentTime)) {
            binding.appointmentTimeError.setVisibility(View.VISIBLE);
            isValid = false;
        }

        if (ServiceOptionsAdapter.DEFAULT_OPTION.equals(service.get(0))) {
            binding.appointmentServiceError.setVisibility(View.VISIBLE);
            isValid = false;
        }

        return isValid;
    }

}