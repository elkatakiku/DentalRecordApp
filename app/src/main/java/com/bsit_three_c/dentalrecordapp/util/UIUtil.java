package com.bsit_three_c.dentalrecordapp.util;

import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.bsit_three_c.dentalrecordapp.R;
import com.bsit_three_c.dentalrecordapp.data.model.DentalServiceOption;

import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
    public static String[] getDateUnits(Date date) {
        return dateFormat.format(date).split("/");
    }

    // Return date in a readable format (Month ddd, yyyy)
    public static String getReadableDate(Date date) {
        String[] units = getDateUnits(date);
        return getMonthName(Integer.parseInt(units[1])) + " " + units[0] + ", " + units[2];
    }

    // Return date in numbers: dd/mm/yyyy
    public static String getDate(Date date) {
        return dateFormat.format(date);
    }

    // Converts date in string type to Date type: dd/mm/yyyy
    public static Date stringToDate(String dateString) {
        try {
            return dateFormat.parse(dateString);
        } catch (ParseException e) {
            Log.e(TAG, "stringToDate: Error parsing", e);
            return null;
        }
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

    // Get the current date in String type
    public static String getCurrentDate() {
        return dateFormat.format(new Date());
    }

    public static String getPaymentStatus(boolean isDownpayment) {
        return isDownpayment ? "Incomplete" : "Fully Paid";
    }

    public static String getPaymentStatus(double balance) {
        Log.d(TAG, "getPaymentStatus: balance == 0d " + (balance == 0d));
        return balance <= 0d ? "Fully Paid"  : "Incomplete";
    }

    public static ColorStateList getCheckBoxColor(boolean isFullyPaid) {
        return isFullyPaid ? ColorStateList.valueOf(0xFF01bb64) : ColorStateList.valueOf(0xFFFF5252);
    }

    public static ColorStateList getCheckBoxColor(double balance) {
        return balance <= 0d ? ColorStateList.valueOf(0xFF01bb64) : ColorStateList.valueOf(0xFFFF5252);
    }

    public static ArrayList<Integer> getServices(ArrayList<DentalServiceOption> serviceOptions) {
        ArrayList<Integer> selected = new ArrayList<>();

        for (DentalServiceOption service : serviceOptions) {
            if (service.isSelected()) selected.add(service.getServicePosition());
        }

        Log.d(TAG, "getServices: service options size: " + selected.size());

        if (selected.size() == 0) {
            Log.e(TAG, "getServices: no services selected");
            selected.add(0);
        }

        return selected;
    }

    public static String getService(Resources resources, int position) {
        return resources.getStringArray(R.array.services_array)[position];
    }

    public static String getServiceTitle(Resources resources, ArrayList<Integer> servicesIndex) {
        StringBuilder services = new StringBuilder();

        for (Integer serviceIndex : servicesIndex) {
            services.append(getService(resources, serviceIndex)).append(" | ");
        }

        services.delete(services.lastIndexOf(" | "), services.capacity());

        return services.toString();
    }

    public static String getServiceTitle(String selectedTitles, String selectedTitle, ArrayList<DentalServiceOption> serviceOptions, String defaultTitle, String[] strings) {
        if (selectedTitles.equals(defaultTitle)) {
            Log.d(TAG, "getServiceTitle: this is true");
            return selectedTitle;
        }

        Log.d(TAG, "getServiceTitle: selected titles: " + selectedTitles);
        Log.d(TAG, "getServiceTitle: selected title: " + selectedTitle);

        StringBuilder newTitle = new StringBuilder();

        String[] titles = selectedTitles.split(" \\| ");
        boolean inTitle = false;

        for (String title : titles) {
            if (selectedTitle.equals(title)) {
                inTitle = true;

                //  This is wrong
                int titlePos = getPosition(strings, selectedTitle);
                if (titlePos != -1)
                    serviceOptions.get(titlePos).setSelected(false);
                continue;
            }

            newTitle.append(title).append(" | ");
        }

        if (!inTitle) {
            newTitle.append(selectedTitle);
        }
        else {
            int index = newTitle.lastIndexOf(" | ");
            newTitle.delete(index, newTitle.capacity());
        }

        return newTitle.toString();
    }

    private static int getPosition(String[] titles, String selectedTitle) {
        for (int pos = 0; pos < titles.length; pos++) {
            if (titles[pos].equals(selectedTitle)) return pos;
        }

        return -1;
    }

    public static String getModeOfPayment(Resources resources, int position) {
        return resources.getStringArray(R.array.mop_array)[position];
    }

    public static String getCivilStatus(Resources resources, int position) {
        return resources.getStringArray(R.array.civil_status_array)[position];
    }

    public static double convertToDouble(String input) {
        try {
            return Double.parseDouble(input);
        } catch (NumberFormatException | NullPointerException e) {
            return -1;
        }
    }

    public static int convertToInteger(String input) {
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    public static String capitalize(String s) {
        StringBuilder capitalize = new StringBuilder(s.length());
        boolean nextTitleCase = true;

        for (char c : s.toCharArray()) {
            if (Character.isSpaceChar(c))
                nextTitleCase = true;
            else if (nextTitleCase) {
                c = Character.toTitleCase(c);
                nextTitleCase = false;
            }

            capitalize.append(c);
        }

        return capitalize.toString();
    }

}
