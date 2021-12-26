package com.bsit_three_c.dentalrecordapp.ui.users.admin.employees.employee_form;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.bsit_three_c.dentalrecordapp.R;
import com.bsit_three_c.dentalrecordapp.databinding.FragmentAdminAddEmployee1Binding;
import com.bsit_three_c.dentalrecordapp.ui.dialog.DatePickerFragment;
import com.bsit_three_c.dentalrecordapp.util.UIUtil;

import java.util.Calendar;

public class BasicInfoFormFragment extends Fragment {
    private static final String TAG = BasicInfoFormFragment.class.getSimpleName();

    private FragmentAdminAddEmployee1Binding binding;
    private final Calendar mCalendar = Calendar.getInstance();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentAdminAddEmployee1Binding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.btnEmployeeNext.setOnClickListener(view1 ->
                NavHostFragment.findNavController(BasicInfoFormFragment.this)
                .navigate(R.id.action_FirstFragment_to_Second2Fragment));

//        int year = mCalendar.get(Calendar.YEAR);
//        int month = mCalendar.get(Calendar.MONTH);
//        int dayOfMonth = mCalendar.get(Calendar.DAY_OF_MONTH);
//
//        DatePickerDialog datePickerDialog = new DatePickerDialog(
//                requireContext(),
//                R.style.MySpinnerDatePickerStyle,
//                new DatePickerDialog.OnDateSetListener() {
//                    @Override
//                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
//                        binding.tvEmployeeMonth.setText(UIUtil.getMonthName(month+1));
//                        binding.tvEmployeeDay.setText(String.valueOf(day));
//                        binding.tvEmployeeYear.setText(String.valueOf(year));
//
//                        binding.etEmployeeAge.setText(UIUtil.getAge(year, month, day));
//                    }
//                },
//                year, month, dayOfMonth
//        );
//
//        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());

        binding.ibEmployeeCalendar.setOnClickListener(v ->
//                showDatePickerDialog("Select Date of Birth",
//                UIUtil.getMonthNumber(binding.tvEmployeeMonth.getText().toString()))
                showDatePickerDialog());
    }

    private void showDatePickerDialog() {
        DatePickerFragment datePickerFragment = new DatePickerFragment(
                binding.tvEmployeeMonth,
                binding.tvEmployeeDay,
                binding.tvEmployeeYear,
                binding.etEmployeeAge
        );

        datePickerFragment.showDatePickerDialog(
                datePickerFragment,
                UIUtil.getMonthNumber(binding.tvEmployeeMonth.getText().toString()),
                getChildFragmentManager()
        );
    }

//    private void showDatePickerDialog(String title, int month) {
//        Log.d(TAG, "showDatePickerDialog: starting");
//        DialogFragment dialogFragment = new DatePickerFragment(
//                binding.tvEmployeeMonth,
//                binding.tvEmployeeDay,
//                binding.tvEmployeeYear,
//                binding.etEmployeeAge
//        );
//
//        Bundle arguments = new Bundle();
////        arguments.putInt(DatePickerFragment.DATE_PICKER_ID, dialogId);
//
//        if (month != -1) {
//            String[] dateArray = {binding.tvEmployeeDay.getText().toString(), String.valueOf(month), binding.tvEmployeeYear.getText().toString()};
//            Date date = UIUtil.stringToDate(TextUtils.join("/", dateArray));
//
//            arguments.putSerializable(DatePickerFragment.DATE_PICKER_DATE, date);
//        }
//
//        arguments.putString(DatePickerFragment.DATE_PICKER_TITLE, title);
//
//        dialogFragment.setArguments(arguments);
//        dialogFragment.show(getChildFragmentManager(), "datePicker");
//    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}