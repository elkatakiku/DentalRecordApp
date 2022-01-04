package com.bsit_three_c.dentalrecordapp.ui.dialog;

import android.app.Activity;
import android.app.AlertDialog;
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
import com.bsit_three_c.dentalrecordapp.ui.users.admin.employees.employee_form.EmployeeFormActivity;
import com.bsit_three_c.dentalrecordapp.ui.users.admin.patients.patient_form.AddPatientActivity;
import com.bsit_three_c.dentalrecordapp.ui.users.admin.services.view_service.ViewServiceActivity;
import com.bsit_three_c.dentalrecordapp.util.LocalStorage;

public class CustomDialog extends Dialog implements View.OnClickListener {

    private static final String TAG = CustomDialog.class.getSimpleName();

    public static final int DIALOG_PATIENT = 0x001ED7B5;
    public static final int DIALOG_EMPLOYEE = 0x001ED7B6;
    public static final int DIALOG_APPOINTMENT = 0x001ED7B7;
    public static final int DIALOG_SERVICE = 0x001ED7B8;

    private final Context context;
    private final NavController navController;

    private TextView tvTitle;
    private Button button1;
    private Button button2;
    private ImageView close;

    private String title;
    private Intent intent;
    private Object parcel;

    AlertDialog alertDialog;

    public CustomDialog(@NonNull Activity context, NavController navController) {
        super(context);

        Log.d(TAG, "CustomDialog: object created");

        this.context = context;
        this.navController = navController;
    }

    public CustomDialog(@NonNull Activity context) {
        this(context, null);
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
            case LocalStorage.PATIENT_KEY:
                setButtonText(R.string.btn_add_patient, R.string.btn_patient_list);
                break;
            case LocalStorage.EMPLOYEE_KEY:
                setButtonText(R.string.btn_add_employee, R.string.btn_employee_list);
                break;
            case LocalStorage.APPOINTMENT_KEY:
                setButtonText(R.string.btn_create_appointment, R.string.btn_appointment_lsit);
                break;
            case LocalStorage.SERVICE_KEY:
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
            case LocalStorage.PATIENT_KEY:
                intent = new Intent(context, AddPatientActivity.class);
                break;
            case LocalStorage.EMPLOYEE_KEY:
                intent = new Intent(context, EmployeeFormActivity.class);
                break;
            case LocalStorage.APPOINTMENT_KEY:
                intent = new Intent(context, AddPatientActivity.class);
                break;
            case LocalStorage.SERVICE_KEY:
                intent = new Intent(context, ViewServiceActivity.class);
                break;
        }

        if (parcel != null) intent.putExtra(LocalStorage.PARCEL_KEY, (Parcelable) parcel);
        context.startActivity(intent);
    }


    private void setButton2Action() {

        final String sTitle = this.tvTitle.getText().toString();

        switch (sTitle) {
            case LocalStorage.PATIENT_KEY:
                navController.navigate(R.id.nav_patients);
                break;
            case LocalStorage.EMPLOYEE_KEY:
                navController.navigate(R.id.nav_employees);
                break;
            case LocalStorage.APPOINTMENT_KEY:
                navController.navigate(R.id.nav_appointments);
                break;
            case LocalStorage.SERVICE_KEY:

                AlertDialog.Builder builder = new AlertDialog.Builder(context);

                builder
                        .setTitle(R.string.delete_title)
                        .setMessage(context.getString(R.string.delete_message) + " this service?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            ServiceRepository.getInstance().removeService((DentalService) parcel);
                        })
                        .setNegativeButton("No", (dialog, which) -> alertDialog.dismiss());
                alertDialog = builder.create();
                alertDialog.show();

                break;
        }
    }

    public void setParcel(Object object) {
        this.parcel = object;
    }

}
