package com.bsit_three_c.dentalrecordapp.ui.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.NavController;

import com.bsit_three_c.dentalrecordapp.R;
import com.bsit_three_c.dentalrecordapp.data.model.DentalService;
import com.bsit_three_c.dentalrecordapp.data.repository.ServiceRepository;
import com.bsit_three_c.dentalrecordapp.ui.users.admin.patients.add_patient.AddPatientActivity;
import com.bsit_three_c.dentalrecordapp.ui.users.admin.services.view_service.ViewServiceActivity;
import com.bsit_three_c.dentalrecordapp.util.LocalStorage;

public class CustomDialog extends Dialog implements View.OnClickListener {

    private static final String TAG = CustomDialog.class.getSimpleName();

    private final Context activity;
    private final NavController navController;

    private TextView tvTitle;
    private Button button1;
    private Button button2;
    private ImageView close;

    private String title;
    private Intent intent;
    private Object parcel;

    public CustomDialog(@NonNull Activity activity, NavController navController) {
        super(activity);

        Log.d(TAG, "CustomDialog: object created");

        this.activity = activity;
        this.navController = navController;
    }

    public CustomDialog(@NonNull Activity activity) {
        this(activity, null);
    }

    @Override
    public void setTitle(@Nullable CharSequence title) {
        super.setTitle(title);

        Log.d(TAG, "setTitle: set title called");
        if (title != null)
            this.title = title.toString();
        else
            this.title = "Options";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "onCreate: creating dialog");

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_admin_pop_up);


        tvTitle = findViewById(R.id.tvDialogTitle);
        button1 = findViewById(R.id.btnDialog1);
        button2 = findViewById(R.id.btnDialog2);
        close = findViewById(R.id.imgDialogClose);

        Log.d(TAG, "onCreate: finding: " + findViewById(R.id.btnDialog1).getId());

        Log.d(TAG, "onCreate: button 1: " + button1.getText().toString());
        Log.d(TAG, "onCreate: button 2: " + button2.getText().toString());

        tvTitle.setText(title);

        this.button1.setOnClickListener(this);
        this.button2.setOnClickListener(this);
        this.close.setOnClickListener(this);

        initializeButton();
    }

    @Override
    public void onClick(View v) {

        final int closeID = R.id.imgDialogClose;
        final int button1ID = R.id.btnDialog1;
        final int button2ID = R.id.btnDialog2;

        switch (v.getId()) {

            case button1ID:
                setButton1Action();
                break;

            case button2ID:
                setButton2Action();
                break;

            case closeID:
                dismiss();
                break;

        }

        dismiss();

    }

    private void initializeButton() {

        final String sTitle = this.tvTitle.getText().toString();

        switch (sTitle) {
            case LocalStorage.DIALOG_PATIENT:
                setButtonText(R.string.btn_add_patient, R.string.btn_patient_list);
                break;
            case LocalStorage.DIALOG_EMPLOYEE:
                setButtonText(R.string.btn_add_employee, R.string.btn_employee_list);
                break;
            case LocalStorage.DIALOG_APPOINTMENT:
                setButtonText(R.string.btn_create_appointment, R.string.btn_appointment_lsit);
                break;
            case LocalStorage.DIALOG_SERVICE:
                setButtonText(R.string.btn_view_service, R.string.btn_remove_service);
                break;
        }

    }

    private void setButtonText(int button1Text, int button2Text) {
        this.button1.setText(button1Text);
        this.button2.setText(button2Text);
    }

    private void setButton1Action() {

        final String sTitle = this.tvTitle.getText().toString();

        switch (sTitle) {
            case LocalStorage.DIALOG_PATIENT:
                intent = new Intent(activity, AddPatientActivity.class);
                break;
            case LocalStorage.DIALOG_EMPLOYEE:
                intent = new Intent(activity, AddPatientActivity.class);
                break;
            case LocalStorage.DIALOG_APPOINTMENT:
                intent = new Intent(activity, AddPatientActivity.class);
                break;
            case LocalStorage.DIALOG_SERVICE:
                intent = new Intent(activity, ViewServiceActivity.class);
                break;
        }

        if (parcel != null) intent.putExtra(LocalStorage.PARCEL_KEY, (Parcelable) parcel);
        activity.startActivity(intent);
    }


    private void setButton2Action() {

        final String sTitle = this.tvTitle.getText().toString();

        switch (sTitle) {
            case LocalStorage.DIALOG_PATIENT:
                navController.navigate(R.id.nav_patients);
                break;
            case LocalStorage.DIALOG_EMPLOYEE:
                navController.navigate(R.id.nav_employees);
                break;
            case LocalStorage.DIALOG_APPOINTMENT:
                navController.navigate(R.id.nav_appointments);
                break;
            case LocalStorage.DIALOG_SERVICE:
                ServiceRepository.getInstance().removeService((DentalService) parcel);
                break;
        }
    }

    public void setParcel(Object object) {
        this.parcel = object;
    }

}
