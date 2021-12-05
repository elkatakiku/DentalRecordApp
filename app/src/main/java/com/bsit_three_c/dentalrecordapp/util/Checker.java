package com.bsit_three_c.dentalrecordapp.util;

import android.util.Log;

import androidx.lifecycle.LiveData;

import com.bsit_three_c.dentalrecordapp.data.model.FormState;

import java.util.regex.Pattern;

public class Checker {

    private static final String TAG = Checker.class.getSimpleName();

    public static boolean isNullOrError(LiveData<FormState> liveData) {
        return liveData.getValue() == null || liveData.getValue().getMsgError() != null;
    }

    public static boolean isNotNullOrValid(LiveData<FormState> liveData) {
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

//        return !isNull(mFirstname) &&
//                !isNull(mlastname) &&
//                !isNull(mAge) &&
//                !isNull(mAddress) &&
//                !isNull(mPhoneNumber);
////                !isNull(mCivilStatus.getValue()) && !isNull(mCivilStatus.getValue().getMsgError());
    }

    public static boolean containsSpecialCharacter(String s) {
        return Pattern.compile("[^a-zA-Z0-9 ]").matcher(s).find();
    }

    public static boolean isDataAvailable(String data) {
        return !(data == null || data.isEmpty());
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

    public static double convertToDouble(String input) {
        try {
            return Double.parseDouble(input);
        } catch (Exception e) {
            return -1;
        }
    }
}
