package com.bsit_three_c.dentalrecordapp.ui.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.bsit_three_c.dentalrecordapp.R;
import com.bsit_three_c.dentalrecordapp.data.model.Appointment;
import com.bsit_three_c.dentalrecordapp.data.model.DentalService;
import com.bsit_three_c.dentalrecordapp.data.model.Patient;
import com.bsit_three_c.dentalrecordapp.data.repository.PatientRepository;
import com.bsit_three_c.dentalrecordapp.util.DateUtil;
import com.bsit_three_c.dentalrecordapp.util.UIUtil;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class AppointmentDialog extends DialogFragment {
    private static final String TAG = AppointmentDialog.class.getSimpleName();

    public static final String APPOINTMENT_KEY = "ARG_AD_APPOINTMENT_KEY";
    public static final String PROCEDURE_KEY = "ARG_AD_PROCEDURE_KEY";
    public static final String PROGRESS_NOTE_KEY = "ARG_AD_PROGRESS_NOTE_KEY";


    private final List<DentalService> services;
    private final Appointment appointment;
//    private final ActivityResultLauncher<Intent> toAddPatientResult;
    private OnGotResultListener gotResultListener;
    private OnDoneClickListener onDoneClickListener;

    public AppointmentDialog(List<DentalService> services, Appointment appointment) {
        this.services = services;
        this.appointment = appointment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final View dialogView = getLayoutInflater().inflate(R.layout.dialog_appointment, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setView(dialogView);

        AppointmentDialog.DialogHolder dialogHolder = new AppointmentDialog.DialogHolder(dialogView);

        dialogHolder.name.setText(appointment.getPatient().getFullName());
        dialogHolder.contact.setText(appointment.getPatient().getContactNumber());
        dialogHolder.date.setText(DateUtil.getReadableDate(appointment.getDateTime()));
        dialogHolder.time.setText(DateUtil.getReadableTime(appointment.getDateTime()));
        dialogHolder.service.setText(TextUtils.join("\n",
                UIUtil.getServicesList(services, appointment.getProcedure().getServiceIds())));
        dialogHolder.note.setText(appointment.getComments());
        dialogHolder.btnDone.setImageResource(appointment.isDone() ? R.drawable.ic_baseline_radio_button_unchecked_24 : R.drawable.ic_baseline_check_24);

        final AlertDialog appointmentDialog = builder.create();

        dialogHolder.btnClose.setOnClickListener(v -> {
            appointmentDialog.dismiss();
        });

        dialogHolder.btnDone.setOnClickListener(v -> {
            if (appointment.isPassed()) {
                Snackbar.make(v, "Appointment date has passed.", Snackbar.LENGTH_SHORT)
                        .show();
                return;
            }

            PatientRepository
                    .getInstance()
                    .getPath(appointment.getPatient().getUid())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Patient patient = snapshot.getValue(Patient.class);
                            if (patient == null) {
                                onDoneClickListener.onClick(appointment, true);
                            } else {
                                onDoneClickListener.onClick(appointment, false);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

            Log.d(TAG, "showAppointmentDialog: button done clicked");
//            if (appointment.isDone()) {
//                appointment.setDone(false);
//
//            } else {
//

//                if (appointment.getPatient().getUid().isEmpty()) {
//
//                }
//                appointment.setDone(true);
//            }

            //  TODO: remove or add patient accordingly
//            mViewModel.updateAppointment(appointment);
            appointmentDialog.dismiss();
        });

        return appointmentDialog;
    }

    public static class DialogHolder {

        public final TextView name;
        public final TextView contact;
        public final TextView date;
        public final TextView time;
        public final TextView service;
        public final TextView note;
        public final ImageButton btnClose;
        public final ImageButton btnDone;

        public DialogHolder(View dialogView) {
            this.name = dialogView.findViewById(R.id.tvAppointmentName);;
            this.contact = dialogView.findViewById(R.id.tvAppointmentContact);;
            this.date = dialogView.findViewById(R.id.tvAppointmentDate);;
            this.time = dialogView.findViewById(R.id.tvAppointmentTime);;
            this.service = dialogView.findViewById(R.id.tvAppointmentProcedure);;
            this.note = dialogView.findViewById(R.id.tvAppointmentNotes);;
            this.btnClose = dialogView.findViewById(R.id.ibEditAppointment);;
            this.btnDone = dialogView.findViewById(R.id.ibAppointmentDone);;
        }
    }

    public interface OnGotResultListener {
        void getResult(Intent intent);
    }

    public interface OnDoneClickListener {
        void onClick(Appointment appointment, boolean isNewPatient);
    }

    public void setOnDoneClickListener(OnDoneClickListener onDoneClickListener) {
        this.onDoneClickListener = onDoneClickListener;
    }

    public void setGotResultListener(OnGotResultListener gotResultListener) {
        this.gotResultListener = gotResultListener;
    }
}
