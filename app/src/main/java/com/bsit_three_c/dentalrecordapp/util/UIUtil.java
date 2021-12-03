package com.bsit_three_c.dentalrecordapp.util;

import android.content.res.ColorStateList;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class UIUtil {

    private static final String TAG = UIUtil.class.getSimpleName();
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);

    /**
     * Sets ListView height dynamically based on the height of the items.
     *
     * @param listView to be resized
     * @return true if the listView is successfully resized, false otherwise
     */
    public static boolean setListViewHeightBasedOnItems(ListView listView) {

        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter != null) {

            int numberOfItems = listAdapter.getCount();
            Log.d(TAG, "setListViewHeightBasedOnItems: numberOfItems: " + numberOfItems);

            // Get total height of all items.
            int totalItemsHeight = 0;
            Log.d(TAG, "setListViewHeightBasedOnItems: initial totalHeight: " + totalItemsHeight);
            for (int itemPos = 0; itemPos < numberOfItems; itemPos++) {
                View item = listAdapter.getView(itemPos, null, listView);

                item.measure(0, 0);
                totalItemsHeight += item.getMeasuredHeight();
            }
            Log.d(TAG, "setListViewHeightBasedOnItems: final totalHieght: " + totalItemsHeight);

            // Get total height of all item dividers.
            int totalDividersHeight = listView.getDividerHeight() *
                    (numberOfItems - 1);

            // Set list height.
            ViewGroup.LayoutParams params = listView.getLayoutParams();
            params.height = totalItemsHeight + totalDividersHeight;
            listView.setLayoutParams(params);
            listView.requestLayout();

            return true;

        } else {
            return false;
        }
    }

    public static ColorStateList getCheckBoxColor(boolean isFullyPaid) {
        return isFullyPaid ? ColorStateList.valueOf(0xFF01bb64) : ColorStateList.valueOf(0xFF6E6E6E);
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

    // Return month name
    public static String getMonthName(int month) {
        return DateFormatSymbols.getInstance().getMonths()[month-1];
    }

    // Return the arrays of units in strings type
    public static String[] getDateUnits(Date date) {
        return dateFormat.format(date).split("/");
    }

    // Return date in a readable format (Month ddd, yyyy)
    public static String getReadableDate(Date date) {
        String[] units = getDateUnits(date);
        return getMonthName(Integer.parseInt(units[1])) + " " + units[0] + ", " + units[2];
    }

    // Return date in numbers
    public static String getDate(Date date) {
        return dateFormat.format(date);
    }

    // Converts date in string type to Date type
    public static Date stringToDate(String dateString) throws ParseException {
        return dateFormat.parse(dateString);
    }

    // Get the current date in String type
    public static String getCurrentDate() {
        return dateFormat.format(new Date());
    }


}
