package com.bsit_three_c.dentalrecordapp.util;

import android.view.View;
import android.widget.AdapterView;

import com.bsit_three_c.dentalrecordapp.interfaces.SpinnerState;

public class CustomItemSelectedListener implements AdapterView.OnItemSelectedListener {

    String label;
    SpinnerState viewModel;

    public CustomItemSelectedListener(String label, SpinnerState viewModel) {
        this.label = label;
        this.viewModel = viewModel;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        viewModel.setSpinnerState(label, position);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
