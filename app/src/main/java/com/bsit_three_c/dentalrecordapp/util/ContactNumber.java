package com.bsit_three_c.dentalrecordapp.util;

import android.content.res.Resources;
import android.util.Log;
import android.widget.EditText;
import android.widget.Spinner;

import com.bsit_three_c.dentalrecordapp.R;
import com.bsit_three_c.dentalrecordapp.data.repository.BaseRepository;

import java.util.ArrayList;
import java.util.List;

public class ContactNumber {

    public String getContactNumber(List<String> contactNumber) {
        if (contactNumber != null && contactNumber.size() > 0) {
            StringBuilder builder = new StringBuilder();

            if (contactNumber.get(0).equals(BaseRepository.NEW_PATIENT)) {
                builder.append(Checker.NOT_AVAILABLE);
            } else {
                for (String number : contactNumber) {
                    builder.append(number).append("\n");
                }
                builder.deleteCharAt(builder.length()-1);
            }

            return builder.toString();
        }
        else
            return Checker.NOT_AVAILABLE;
    }

    public static List<String> createList(String contactNumber) {
        ArrayList<String> contact = new ArrayList<>(1);
        contact.add(contactNumber);

        return contact;
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

    public static String getFormattedContactNumber(String inputNumber, Resources resources) {
        if (inputNumber.length() == 7 || inputNumber.length() == 10) {
            inputNumber = formatTelephoneNumber(inputNumber);
        } else if (inputNumber.length() == 11) {
            inputNumber = formatMobileNumber("+63", inputNumber);
        }


        return inputNumber;
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

                inputNumber = formatTelephoneNumber(inputNumber);
                break;

            case 1:
                if (inputNumber.length() != 10) {
                    etNumber.setError(resources.getString(R.string.invalid_cel_limit_number));
                    return null;
                }

                String code = spnrCode.getSelectedItem().toString();
                inputNumber = formatMobileNumber(code, inputNumber);

                break;
        }

        return inputNumber;
    }

    public static String getFirstNumber(String contactNumbers) {
        return contactNumbers.split("\n")[0];
    }

    public static String convertToNumbers(String contactNumber) {
        Log.d("CONVERTER", "convertToNumbers: contact length: " + contactNumber.length());
        String contact = "";
        switch (contactNumber.length()) {
            case 8:
                contact = contactNumber.substring(0, 3) + contactNumber.substring(4, 8);
                break;
            case 12:
                contact = contactNumber.substring(0, 3) + contactNumber.substring(4, 7) + contactNumber.substring(8, 12);
                break;
            case 18:
                contact = 0 + contactNumber.substring(6,9) + contactNumber.substring(10, 13) + contactNumber.substring(14, 18);
                break;
        }
        return contact;
    }

}
