package com.bsit_three_c.dentalrecordapp.util;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import com.bsit_three_c.dentalrecordapp.R;
import com.bsit_three_c.dentalrecordapp.data.adapter.ServiceOptionsAdapter;
import com.bsit_three_c.dentalrecordapp.data.model.Account;
import com.bsit_three_c.dentalrecordapp.data.model.DentalServiceOption;
import com.bsit_three_c.dentalrecordapp.ui.main.MainAdminActivity;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

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

    public static ArrayList<String> getServices(List<DentalServiceOption> serviceOptions) {
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

    public static String getServiceTitle(List<DentalServiceOption> dentalServiceOptions, String serviceUid) {
        String title = null;

        for (DentalServiceOption service : dentalServiceOptions) {
            if (service.getServiceUID().equals(serviceUid)){
                title = service.getTitle();
                break;
            }
        }

        return title;
    }

    public static String getServiceTitle(List<String> servicesProcedure, List<DentalServiceOption> dentalServiceOptions) {
        Log.d(TAG, "getServiceTitle: services prrocedure: " + servicesProcedure);

        if (servicesProcedure == null || servicesProcedure.size() == 0) {
            return "Procedure";
        }

        StringBuilder services = new StringBuilder();

        for (String service : servicesProcedure) {
            String title = getServiceTitle(dentalServiceOptions, service);
            if (title != null) {
                services.append(title).append(" | ");
            }
        }

        Log.d(TAG, "getServiceTitle: capacity: " + services.capacity());
        Log.d(TAG, "getServiceTitle: last index: " + services.lastIndexOf(" | "));
        services.delete(services.lastIndexOf(" | "), services.capacity());

        return services.toString();
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
            String lastPart = number.substring(6, 10);

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
        String lastPart = number.substring(6, 10);

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
            capture.compress(Bitmap.CompressFormat.JPEG, 50, outputStream);
        }
        else outputStream = null;

        return outputStream;
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

    public static Bitmap getBitmapFromResource(Context context, int path) {
        final MutableLiveData<Bitmap> result = new MutableLiveData<>();
        Glide.with(context)
                .asBitmap()
                .load(path)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        result.setValue(resource);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                    }
                });

        return result.getValue();
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
                redirectUser = new Intent(context, MainAdminActivity.class);
                break;
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

        if (Checker.isDataAvailable(data.trim()))
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

}