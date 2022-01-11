package com.bsit_three_c.dentalrecordapp.ui.dialog;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.bsit_three_c.dentalrecordapp.util.DateUtil;

import java.util.Calendar;
import java.util.Date;

public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
    private static final String TAG = DatePickerFragment.class.getSimpleName();

    public static final String DATE_PICKER_ID = "ID";
    public static final String DATE_PICKER_TITLE = "TITLE";
    public static final String DATE_PICKER_DATE = "DATE";

    public static final String BIRTH_DATE_TITLE =  "Select Date of Birth";

    private final TextView tvMonth;
    private final TextView tvDay;
    private final TextView tvYear;
    private final EditText etAge;

    DatePickerDialog datePickerDialog;
    boolean maxDate;
    boolean minDateToday;

    int mDialogId = 0;

    public DatePickerFragment(TextView tvMonth, TextView tvDay, TextView tvYear, EditText etAge) {
        this.tvMonth = tvMonth;
        this.tvDay = tvDay;
        this.tvYear = tvYear;
        this.etAge = etAge;
    }

    public DatePickerFragment(TextView tvMonth, TextView tvDay, TextView tvYear) {
        this(tvMonth, tvDay, tvYear, null);
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final Calendar calendar = Calendar.getInstance();
        String title = null;

        Bundle arguments = getArguments();

        if (arguments != null) {
            mDialogId = arguments.getInt(DATE_PICKER_ID);
            title = arguments.getString(DATE_PICKER_TITLE);

            Date givenDate = (Date) arguments.getSerializable(DATE_PICKER_DATE);
            if (givenDate != null) {
                calendar.setTime(givenDate);
                Log.d(TAG, "onCreateDialog: retrieved date: " + givenDate.toString());
            }
        }

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        datePickerDialog = new DatePickerDialog(
                requireContext(),
                AlertDialog.THEME_HOLO_LIGHT,
                this,
                year,
                month,
                day
        );

        if (minDateToday) {
            datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        }

        if (maxDate) {
            datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        }

        if (title != null) datePickerDialog.setTitle(title);


        return datePickerDialog;
    }

    public void setMaxDateToday() {
        maxDate = true;
    }

    public void setMinDateToday() {
        minDateToday = true;
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Log.d(TAG, "onDateSet: entering onDateSet");

        tvMonth.setText(DateUtil.getMonthName(month+1));
        tvDay.setText(String.valueOf(dayOfMonth));
        tvYear.setText(String.valueOf(year));

        if (etAge != null) {
            etAge.setText(DateUtil.getAge(year, month, dayOfMonth));
        }

        Log.d(TAG, "onDateSet: Exiting onDateSet");
    }

    public void showDatePickerDialog(DialogFragment dialogFragment, int month, FragmentManager fragmentManager, String title) {
        Log.d(TAG, "showDatePickerDialog: starting");

        Bundle arguments = new Bundle();

        if (month != -1) {
            String[] dateArray = {tvDay.getText().toString(), String.valueOf(month), tvYear.getText().toString()};
            Date date = DateUtil.convertToDate(TextUtils.join("/", dateArray));

            arguments.putSerializable(DatePickerFragment.DATE_PICKER_DATE, date);
        }

        arguments.putString(DatePickerFragment.DATE_PICKER_TITLE, title);

        dialogFragment.setArguments(arguments);
        dialogFragment.show(fragmentManager, "datePicker");
    }
}
