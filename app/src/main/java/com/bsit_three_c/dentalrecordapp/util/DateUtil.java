package com.bsit_three_c.dentalrecordapp.util;

import android.util.Log;
import android.widget.DatePicker;

import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateUtil {

    private static final String TAG = DateUtil.class.getSimpleName();
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
    public final static String DEFAULT_DATE = "dd/mm/yyyy";

    // Get the current date in String type
    public static String getCurrentDate() {
        return dateFormat.format(new Date());
    }

    public static boolean isDefaultDate(String inputDate) {
        return DEFAULT_DATE.equals(inputDate);
    }

    // Return Date format from date picker
    public static Date getDate(DatePicker datePicker) {
        Date date = null;
        if (datePicker != null) {
            int day = datePicker.getDayOfMonth();
            int month = datePicker.getMonth() + 1;
            int year = datePicker.getYear();

            Log.d(TAG, "getDate: day: " + day);
            Log.d(TAG, "getDate: month: " + month);
            Log.d(TAG, "getDate: year: " + year);

            try {
                Log.d(TAG, "getDate: parsing date");
                Log.d(TAG, "getDate: date format: " + ("" + day + "/" + month + "/" + year));
                date = dateFormat.parse("" + day + "/" + month + "/" + year);
            } catch (ParseException e) {
                Log.d(TAG, "getDate: error in parsing");
                Log.d(TAG, "getDate: setting date to null");
                date = null;
            }
        }

        return date;
    }

    // Return date in numbers: dd/mm/yyyy
    public static String getDate(Date date) {
        return dateFormat.format(date);
    }

    public static String getDate(String date) {
        final String DEFAULT_DATE = "Day/Month/Year";
        return DEFAULT_DATE.equals(date) ? Checker.NOT_AVAILABLE : date;
    }

    public static int getMonthNumber(String monthName) {
        final String[] months = {"January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"};

        for (int pos = 0; pos < months.length; pos++) {
            if (months[pos].equalsIgnoreCase(monthName)) return pos + 1;
        }

        return -1;
    }

    // Return month name
    public static String getMonthName(int month) {
        return DateFormatSymbols.getInstance().getMonths()[month-1];
    }

    // Return the arrays of units in strings type: dd/mm/yyyy
    public static String[] toStringArray(Date date) {
        return toStringArray(dateFormat.format(date));
    }

    public static String[] toStringArray(String date) {
        return date.split("/");
    }

    // Converts date in string type to Date type: dd/mm/yyyy
    public static Date convertToDate(String dateString) {
        Date resultDate = new Date();
        try {
            resultDate = dateFormat.parse(dateString);
        } catch (ParseException e) {
            Log.e(TAG, "stringToDate: Error parsing", e);
        }

        return resultDate;
    }

    // Return date in a readable format (Month ddd, yyyy)
    public static String getReadableDate(Date date) {
        String[] units = toStringArray(date);
        return getMonthName(Integer.parseInt(units[1])) + " " + units[0] + ", " + units[2];
    }


    public static String getAge(int year, int month, int day){
        Calendar dob = Calendar.getInstance();
        Calendar today = Calendar.getInstance();

        int currentYear = today.get(Calendar.YEAR);

        dob.set(year, month, day);

        int age = currentYear - dob.get(Calendar.YEAR);

        int dobDayOfYear = dob.get(Calendar.DAY_OF_YEAR);

        if (!isLeapYear(currentYear) && isLeapYear(year)) dobDayOfYear --;

        if (today.get(Calendar.DAY_OF_YEAR) < dobDayOfYear) age--;

        return Integer.toString(age);
    }

    private static boolean isLeapYear(int year) {
        return year % 4 == 0;
    }

    public static String getDate(String day, String month, String year) {
        if (day.isEmpty()) {
            day = "dd";
        }

        if (month.isEmpty()){
            month = "mm";
        }

        if (year.isEmpty()) {
            year = "yyyy";
        }

        String dateOfBirth = day + "/" + month + "/" + year;
        if (!DateUtil.isDefaultDate(dateOfBirth)) {
            dateOfBirth = day + "/" + DateUtil.getMonthNumber(month) + "/" + year;
        } else {
            dateOfBirth = Checker.NOT_AVAILABLE;
        }

        return dateOfBirth;
    }
}
