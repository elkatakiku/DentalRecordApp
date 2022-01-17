package com.bsit_three_c.dentalrecordapp.util;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.bsit_three_c.dentalrecordapp.data.adapter.ServiceOptionsAdapter;
import com.bsit_three_c.dentalrecordapp.data.model.Account;
import com.bsit_three_c.dentalrecordapp.data.model.DentalService;
import com.bsit_three_c.dentalrecordapp.data.model.DentalServiceOption;
import com.bsit_three_c.dentalrecordapp.data.model.Person;
import com.bsit_three_c.dentalrecordapp.ui.main.MainAdminActivity;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class UIUtil {

    private static final String TAG = UIUtil.class.getSimpleName();

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

    public static void setMargins (View v, int l, int t, int r, int b) {
        if (v.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            p.setMargins(l, t, r, b);
            v.requestLayout();
        }
    }

    public static void setMargins (View v, int m) {
        setMargins(v, m, m, m, m);
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

    public static ArrayList<String> getServiceUids(List<DentalServiceOption> serviceOptions) {
        ArrayList<String> selected = new ArrayList<>();

        for (DentalServiceOption service : serviceOptions) {
            if (service.isSelected()) selected.add(service.getServiceUID());
        }

        Log.d(TAG, "getServices: service options size: " + selected.size());

        if (selected.size() == 0) {
            Log.e(TAG, "getServices: no services selected");
            selected.add(ServiceOptionsAdapter.DEFAULT_OPTION);
        }

        return selected;
    }

    public static boolean isServiceDefault(List<String> dentalServiceOptions) {
        return ServiceOptionsAdapter.DEFAULT_OPTION.equals(dentalServiceOptions.get(0));
    }

    public static String getServiceOptionsTitle(List<DentalServiceOption> dentalServiceOptions, String serviceUid) {
        String title = null;

        for (DentalServiceOption service : dentalServiceOptions) {
            if (service.getServiceUID().equals(serviceUid)){
                title = service.getTitle();
                break;
            }
        }

        return title;
    }

    public static String getServiceOptionsTitle(String serviceUid, List<DentalService> dentalServiceOptions) {
        String title = null;

        for (DentalService service : dentalServiceOptions) {
            if (service.getServiceUID().equals(serviceUid)){
                title = service.getTitle();
                break;
            }
        }

        return title;
    }

    public static boolean isServiceSelected(List<String> serviceUds, String serviceUid) {
        for (String uid : serviceUds) {
            if (uid.equals(serviceUid)) {
                return true;
            }
        }

        return false;
    }

    public static String getServiceOptionsTitle(List<String> servicesProcedure, List<DentalServiceOption> dentalServiceOptions) {
        Log.d(TAG, "getServiceTitle: services procedure: " + servicesProcedure);

        if (servicesProcedure == null || servicesProcedure.size() == 0) {
            return "Procedure";
        }

        StringBuilder services = new StringBuilder();

        for (String serviceUid : servicesProcedure) {
            String title = getServiceOptionsTitle(dentalServiceOptions, serviceUid);
            if (title != null) {
                services.append(title).append(" | ");
            }
        }

        Log.d(TAG, "getServiceTitle: capacity: " + services.capacity());
        Log.d(TAG, "getServiceTitle: last index: " + services.lastIndexOf(" | "));
        int indexLast = services.lastIndexOf(" | ");
        if (indexLast != -1) {
            services.delete(indexLast, services.capacity());
        }

        return services.toString();
    }

    public static String getServiceTitle(String serviceUid, List<DentalService> dentalServiceOptions) {
        String title = null;

        for (DentalService service : dentalServiceOptions) {
            if (service.getServiceUID().equals(serviceUid)){
                title = service.getTitle();
                break;
            }
        }

        return title;
    }

    public static List<String> getServicesList(List<DentalService> services, List<String> servicesUid) {
        ArrayList<String> servicesNames = new ArrayList<>();

        for (String serviceUid : servicesUid) {
            String serviceName = getServiceTitle(serviceUid, services);
            if (serviceName != null) {
                servicesNames.add(serviceName);
            }
        }

        return servicesNames;
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

    public static long convertToLong(String input) {
        try {
            return Long.parseLong(input);
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

    public static Bitmap convertVectorToBitmap(Drawable drawable) {
        try {
            Log.d(TAG, "convertVectorToBitmap: trying to convert drawable");
            Bitmap bitmap;

            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);

            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
            return bitmap;
        } catch (OutOfMemoryError e) {
            Log.d(TAG, "convertVectorToBitmap: error");
            // Handle the error
            e.printStackTrace();
            return null;
        }
    }

    public static byte[] getByteArray(Bitmap resource) {
        ByteArrayOutputStream outputStream;
        outputStream = new ByteArrayOutputStream();
        resource.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        return outputStream.toByteArray();
    }

    public static Intent redirectUser(Context context, final int type) {
        Intent redirectUser = null;
        switch (type) {
            case Account.TYPE_ADMIN:
            case Account.TYPE_EMPLOYEE:
                redirectUser = new Intent(context, MainAdminActivity.class);
                break;
            case Account.TYPE_PATIENT:
                break;
        }

        return redirectUser;
    }

    public static void setText(String data, TextView textView) {
        String notAvailable = "N/A";

        if (Checker.isDataAvailable(data))
            textView.setText(data.trim());
        else
            textView.setText(notAvailable);
    }

    public static void setText(int data, TextView textView) {
        String notAvailable = "N/A";

        if (data >= 0)
            textView.setText(String.valueOf(data));
        else
            textView.setText(notAvailable);
    }

    public static void setText(double data, TextView textView) {
        String notAvailable = "N/A";

        if (data >= 0)
            textView.setText(String.valueOf(data));
        else
            textView.setText(notAvailable);
    }

    public static void setField(String data, TextView textView) {
        if (Checker.isDataAvailable(data)) {
            textView.setText(data);
        }
    }

    public static String getPasswordText(String password) {
        StringBuilder pass = new StringBuilder();

        if (Checker.isDataAvailable(password)) {
            for (char c : password.toCharArray()) {
                pass.append('*');
            }
        }

        return pass.toString();
    }

    public static void loadDisplayImage(Context context, ImageView imageView, String stringUri, int drawable) {
        Glide
                .with(context)
                .load(Checker.isDataAvailable(stringUri) ? Uri.parse(stringUri) : drawable)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imageView);

        imageView.setImageTintList(null);
        imageView.setBackgroundColor(Color.TRANSPARENT);
    }



    public static boolean isValid(String defaultValue, String toBeChecked, View view) {
        if (defaultValue.equals(toBeChecked)) {
            view.setVisibility(View.VISIBLE);
            return false;
        }
        return true;
    }

    public static void setDateFields(String dateOfBirth, TextView day, TextView month, TextView year) {
        if (Checker.isDataAvailable(dateOfBirth) && !dateOfBirth.equals(Checker.NOT_AVAILABLE)) {
            String[] dateUnits = DateUtil.toStringArray(dateOfBirth);
            day.setText(dateUnits[0]);
            month.setText(DateUtil.getMonthName(UIUtil.convertToInteger(dateUnits[1])));
            year.setText(dateUnits[2]);
        }
    }

    public static void setTimeFields(String time, TextView hour, TextView minutes, TextView period) {
        if (Checker.isDataAvailable(time) && !time.equals(Checker.NOT_AVAILABLE)) {
            String[] timeUnits = DateUtil.toStringArray(time);

            int hourOfDay = convertToInteger(timeUnits[0]);
            boolean isAm = true;

            if (hourOfDay > 12) {
                Log.d(TAG, "getReadableTime: is pm");
                hourOfDay -= 12;
                isAm = false;
            }

            hour.setText(String.valueOf(hourOfDay));
            minutes.setText(timeUnits[1]);
            period.setText(isAm ? "AM" : "PM");
        }
    }

    public static String getDefaultPassword(Person person) {
        return person.getLastname() + 123456;
    }
}