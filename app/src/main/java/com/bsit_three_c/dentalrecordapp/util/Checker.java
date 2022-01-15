package com.bsit_three_c.dentalrecordapp.util;

import android.util.Log;
import android.util.Patterns;

import androidx.lifecycle.LiveData;

import com.bsit_three_c.dentalrecordapp.data.model.FormState;

import java.util.regex.Pattern;

public class Checker {

    private static final String TAG = Checker.class.getSimpleName();

    public static final String NOT_AVAILABLE = "N/A";
    public static final int VALID = -1;

    private static boolean isNullOrError(LiveData<FormState> liveData) {
        return liveData.getValue() == null || liveData.getValue().getMsgError() != null;
    }

    public static boolean isNotNullAndValid(LiveData<FormState> liveData) {
        return liveData.getValue() != null && liveData.getValue().isDataValid();
    }

    public static boolean hasNumber(String s) {
        boolean result = false;
        Log.d(TAG, "hasNumber: checking letters if contains number");
        for (char c : s.toCharArray()) {
            Log.d(TAG, "hasNumber: c: " + c);
            if (Character.isDigit(c)) result = true;
        }
        return result;
    }

    public static boolean hasLetter(String s) {
        boolean result = false;

        for (char c : s.toCharArray())
            if (Character.isLetter(c)) result = true;

        return result;
    }

    @SafeVarargs
    public static boolean isComplete(LiveData<FormState>... forms) {

        for (LiveData<FormState> state : forms) {
            if (isNullOrError(state)) return false;
        }

        return true;
    }

    public static boolean containsSpecialCharacter(String s) {
        return Pattern.compile("[^a-zA-Z0-9 ñ]").matcher(s).find();
    }

    public static boolean isDataAvailable(String data) {
        return !(data == null || data.trim().isEmpty() || NOT_AVAILABLE.equals(data.trim()));
    }

    public static boolean isRepeated(String word, String character) {
        int initialIndex = word.indexOf(character);
        int finalIndex = initialIndex;
        int temp = initialIndex;

        while (temp >= 0) {
            temp = word.indexOf(character, temp + 1);

            if (temp <= 0) break;
            finalIndex = temp;
        }
        return initialIndex != finalIndex;
    }

    public static boolean isNotDefault(int pos) {
        return pos > 0;
    }

    public static boolean isFullyPaid(String input, double balance) {
        return balance - UIUtil.convertToDouble(input) < 0;
    }

    public static boolean isNotAvailable(String data) {
        return NOT_AVAILABLE.equals(data);
    }

    public static boolean isEmailValid(String username) {
        if (username == null) {
            return false;
        }

        if (username.contains("@")) {
            Log.d(TAG, "isEmailValid: is email: " + Patterns.EMAIL_ADDRESS.matcher(username).matches());
            return Patterns.EMAIL_ADDRESS.matcher(username).matches();
        } else {
            Log.d(TAG, "isEmailValid: is valid: " + (!username.trim().isEmpty()));
            return !username.trim().isEmpty();
        }
    }
}
