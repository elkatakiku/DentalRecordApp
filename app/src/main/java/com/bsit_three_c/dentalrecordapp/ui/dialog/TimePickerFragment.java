package com.bsit_three_c.dentalrecordapp.ui.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.bsit_three_c.dentalrecordapp.util.DateUtil;
import com.bsit_three_c.dentalrecordapp.util.UIUtil;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {
    private static final String TAG = TimePickerFragment.class.getSimpleName();

    public static final String TIME_PICKER_TITLE = "TITLE";
    public static final String TIME_PICKER_TIME = "DATE";

    private final TextView tvHour;
    private final TextView tvMinutes;
    private final TextView tvTime;

    private int minTime;
    private int maxTime;

    public TimePickerFragment(TextView tvHour, TextView tvMinutes, TextView tvTime) {
        this.tvHour = tvHour;
        this.tvMinutes = tvMinutes;
        this.tvTime = tvTime;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final Calendar calendar = Calendar.getInstance();
        String title = null;

        Bundle arguments = getArguments();

        if (arguments != null) {
            title = arguments.getString(TIME_PICKER_TITLE);

            Date givenDate = (Date) arguments.getSerializable(TIME_PICKER_TIME);
            if (givenDate != null) {
                calendar.setTime(givenDate);
                Log.d(TAG, "onCreateDialog: retrieved date: " + givenDate.toString());
            }
        }

        int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
        int minutes = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                requireContext(),
                AlertDialog.THEME_HOLO_LIGHT,
                this,
                hourOfDay,
                minutes,
                false
        );

        if (title != null) {
            timePickerDialog.setTitle(title);
        }

        return timePickerDialog;
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        Log.d(TAG, "onTimeSet: hour selected: " + hourOfDay);

        boolean isAM = hourOfDay < 12;

        if (!isAM) {
            if (hourOfDay > 19) {
                hourOfDay = 18;
            }

            if (hourOfDay != 12) {
                hourOfDay -= 12;
            }

        } else {
            if (hourOfDay < 10) {
                hourOfDay = 10;
                minute = 0;
            }
        }

        String minutes = String.format(Locale.ENGLISH, "%02d", minute);

        tvHour.setText(String.valueOf(hourOfDay));
        tvMinutes.setText(minutes);
        tvTime.setText(isAM ? "AM" : "PM");
    }

    public void showDatePickerDialog(DialogFragment dialogFragment, FragmentManager fragmentManager, String title) {
        Log.d(TAG, "showDatePickerDialog: starting");

        Bundle arguments = new Bundle();


        if (!tvHour.getText().toString().trim().isEmpty()) {
            Log.d(TAG, "showDatePickerDialog: has time");
            int hourOfDay = UIUtil.convertToInteger(tvHour.getText().toString().trim());
            if ("PM".equals(tvTime.getText().toString().trim())) {
                Log.d(TAG, "showDatePickerDialog: is pm");
                if (hourOfDay != 12) {
                    hourOfDay += 12;
                }
            }

            String[] timeArray = {String.valueOf(hourOfDay), tvMinutes.getText().toString().trim()};
            arguments.putSerializable(TimePickerFragment.TIME_PICKER_TIME, DateUtil.convertToTime(TextUtils.join("/", timeArray)));
        }

        arguments.putString(TimePickerFragment.TIME_PICKER_TITLE, title);

        dialogFragment.setArguments(arguments);
        dialogFragment.show(fragmentManager, "datePicker");
    }
}
