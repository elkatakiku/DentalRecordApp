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

            // Get total height of all items.
            int totalItemsHeight = 0;
            for (int itemPos = 0; itemPos < numberOfItems; itemPos++) {
                View item = listAdapter.getView(itemPos, null, listView);
                item.measure(0, 0);
                totalItemsHeight += item.getMeasuredHeight();
            }

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
    
    public static String getMonthName(int month) {
        return DateFormatSymbols.getInstance().getMonths()[month-1];
    }

    public static String[] getDateUnits(Date date) {
        return dateFormat.format(date).split("/");
    }
    
    public static String getDateInString(Date date) {
        String[] units = getDateUnits(date);
        return getMonthName(Integer.parseInt(units[1])) + units[0] + units[2];
    }

    public static String getDate(Date date) {
        return dateFormat.format(date);
    }

    public static Date stringToDate(String dateString) throws ParseException {
        return dateFormat.parse(dateString);
    }


}
