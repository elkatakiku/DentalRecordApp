package com.bsit_three_c.dentalrecordapp.util;

import android.content.res.Resources;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import androidx.lifecycle.Observer;

import com.bsit_three_c.dentalrecordapp.data.model.FormState;

public class CustomObserver implements Observer<FormState> {
    private static final String TAG = CustomObserver.class.getSimpleName();

    final Resources res;
    final EditText editText;

    public CustomObserver(EditText editText, Resources res) {
        this.editText = editText;
        this.res = res;
    }

    @Override
    public void onChanged(FormState formState) {
        if (formState == null) return;

        if (formState.getMsgError() != null) {
            editText.setError(res.getString(formState.getMsgError()));
        }
    }


    public static class ObserverButton implements Observer<FormState> {

        private final Button button;

        public ObserverButton(Button button) {
            this.button = button;
        }

        @Override
        public void onChanged(FormState formState) {
            Log.d(TAG, "onChanged: addpatientformstate: " + (formState != null));
            if (formState == null) return;

            Log.d(TAG, "onChanged: setting button enabled: " + formState.isDataValid());
            button.setEnabled(formState.isDataValid());
        }
    }
}
