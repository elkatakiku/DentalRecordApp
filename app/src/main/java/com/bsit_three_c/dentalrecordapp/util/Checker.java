package com.bsit_three_c.dentalrecordapp.util;

import android.util.Log;

import androidx.lifecycle.LiveData;

import com.bsit_three_c.dentalrecordapp.data.form_state.FormState;

public class Checker {

    private static final String TAG = Checker.class.getSimpleName();

    public static boolean isNull(LiveData<FormState> liveData) {
        return liveData.getValue() == null || liveData.getValue().getMsgError() != null;
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
    public static boolean isIncomplete(LiveData<FormState>... forms) {

        for (LiveData<FormState> state : forms) {
            if (isNull(state)) return false;
        }

        return true;
//
//        return !isNull(mFirstname) &&
//                !isNull(mlastname) &&
//                !isNull(mAge) &&
//                !isNull(mAddress) &&
//                !isNull(mPhoneNumber);
////                !isNull(mCivilStatus.getValue()) && !isNull(mCivilStatus.getValue().getMsgError());
    }
}
