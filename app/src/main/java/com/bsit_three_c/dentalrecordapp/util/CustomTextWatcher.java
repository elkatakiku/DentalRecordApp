package com.bsit_three_c.dentalrecordapp.util;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;

import com.bsit_three_c.dentalrecordapp.interfaces.TextChange;

public class CustomTextWatcher implements TextWatcher {
    private static final String TAG = CustomObserver.class.getSimpleName();

    private final TextChange viewModel;
    private final String label;

    public CustomTextWatcher(TextChange viewModel, String label) {
        this.viewModel = viewModel;
        this.label = label;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        Log.d(TAG, "beforeTextChanged called");
        viewModel.beforeDataChange(label, after, s.toString());
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        viewModel.dataChanged(label, s.toString());
    }
}
