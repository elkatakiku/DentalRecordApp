package com.bsit_three_c.dentalrecordapp.util;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.bsit_three_c.dentalrecordapp.R;
import com.bsit_three_c.dentalrecordapp.data.model.Account;
import com.bsit_three_c.dentalrecordapp.data.model.DentalServiceOption;
import com.bsit_three_c.dentalrecordapp.ui.users.admin.MainAdminActivity;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

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
            Log.d(TAG, "getServiceTitle: selected title is default: " + selectedTitle.equals(title));
            if (selectedTitle.equals(title) && !(selectedTitle.equals(strings[0]))) {
                inTitle = true;

                Log.d(TAG, "getServiceTitle: title: " + title);

                //  This is wrong
                int titlePos = getPosition(strings, selectedTitle);
                if (titlePos != -1)
                    serviceOptions.get(titlePos).setSelected(false);
                continue;
            } 

            newTitle.append(title).append(" | ");
        }

        Log.d(TAG, "getServiceTitle: new title: " + newTitle.toString());
        Log.d(TAG, "getServiceTitle: new title length: " + newTitle.length());

        if (newTitle.length() == 0) {
            newTitle.append(strings[0]);
            serviceOptions.get(0).setSelected(false);
        }
        else if (!inTitle) {
            newTitle.append(selectedTitle);
        }
        else {
            int index = newTitle.lastIndexOf(" | ");
            newTitle.delete(index, newTitle.capacity());
        }

        Log.d(TAG, "getServiceTitle: new title: " + newTitle.toString());

        return newTitle.toString();
    }

    private static int getPosition(String[] titles, String selectedTitle) {
        for (int pos = 0; pos < titles.length; pos++) {
            if (titles[pos].equals(selectedTitle)) return pos;
        }

        return -1;
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

    //  Formats string to telephone number format
    public static String formatTelephoneNumber(final String number) {
        String firstPart = number.substring(0, 3);
        String contactNumber = number;

        if (number.length() == 10) {
            String secondPart = number.substring(3, 6);
            String lastPart = number.substring(5, 10);

            contactNumber = firstPart + "-" + secondPart + "-" + lastPart;
        }
        else if (number.length() == 7) {
            contactNumber =  firstPart + "-" + number.substring(3, 7);
        }

        return contactNumber;
    }

    //  Formats string to philippine mobile number format
    public static String formatMobileNumber(String code, String number) {
        String firstPart = number.substring(0, 3);
        String secondPart = number.substring(3, 6);
        String lastPart = number.substring(5, 10);

        return "(" + code + ") " + firstPart + "-" + secondPart + "-" + lastPart;
    }

    //  Get formatted contact number.
    public static String getFormattedContactNumber(EditText etNumber, Spinner spnrCode, Resources resources) {
        final int mode = spnrCode.getSelectedItemPosition();
        String inputNumber = etNumber.getText().toString().trim();

        switch (mode) {
            case 0:
                if (inputNumber.length() != 10 && inputNumber.length() != 7) {
                    etNumber.setError(resources.getString(R.string.invalid_tel_limit_number));
                    return null;
                }

                inputNumber = UIUtil.formatTelephoneNumber(inputNumber);
                break;

            case 1:
                if (inputNumber.length() != 10) {
                    etNumber.setError(resources.getString(R.string.invalid_cel_limit_number));
                    return null;
                }

                String code = spnrCode.getSelectedItem().toString();
                inputNumber = UIUtil.formatMobileNumber(code, inputNumber);

                break;
        }

        return inputNumber;
    }

    public static ByteArrayOutputStream getOutputStreamImage(ImageView imageView) {
        Drawable drawable = imageView.getDrawable();
        Bitmap capture;
        if (drawable instanceof VectorDrawable) {
            capture = convertVectorToBitmap(drawable);
        } else {
            capture = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        }

        ByteArrayOutputStream outputStream;
        if (capture != null) {
            outputStream = new ByteArrayOutputStream();
            capture.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        }
        else outputStream = null;

        return outputStream;
    }

    public static Bitmap convertVectorToBitmap(Drawable drawable) {
        try {
            Bitmap bitmap;

            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);

            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
            return bitmap;
        } catch (OutOfMemoryError e) {
            // Handle the error
            e.printStackTrace();
            return null;
        }
    }

    public static Intent redirectUser(Context context, final int type) {
        Intent redirectUser = null;
        switch (type) {
            case Account.TYPE_ADMIN:
                redirectUser = new Intent(context, MainAdminActivity.class);
                break;
            case Account.TYPE_EMPLOYEE:
                break;
            case Account.TYPE_PATIENT:
                break;
        }

        return redirectUser;
    }

    public static void setText(String data, TextView textView) {
        String notAvailable = "N/A";

        if (Checker.isDataAvailable(data))
            textView.setText(data);
        else
            textView.setText(notAvailable);
    }

    public static void setField(String data, TextView textView) {
        if (Checker.isDataAvailable(data)) {
            textView.setText(data);
        }
    }

}