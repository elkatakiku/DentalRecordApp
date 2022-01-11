package com.bsit_three_c.dentalrecordapp.ui.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.DialogFragment;

import com.bsit_three_c.dentalrecordapp.R;
import com.bsit_three_c.dentalrecordapp.data.model.Appointment;
import com.bsit_three_c.dentalrecordapp.util.DateUtil;
import com.google.android.material.snackbar.Snackbar;

public class AppointmentDialog extends DialogFragment {

    private final View cardView;
    private final Appointment appointment;

    public AppointmentDialog(View cardView, Appointment appointment) {
        this.cardView = cardView;
        this.appointment = appointment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final View dialogView = LayoutInflater.from(cardView.getContext()).inflate(R.layout.dialog_appointment, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setCancelable(false);

        builder.setView(dialogView);

        final TextView name = dialogView.findViewById(R.id.tvAppointmentName);
        final TextView contact = dialogView.findViewById(R.id.tvAppointmentContact);
        final TextView date = dialogView.findViewById(R.id.tvAppointmentDate);
        final TextView time = dialogView.findViewById(R.id.tvAppointmentTime);
        final TextView service = dialogView.findViewById(R.id.tvAppointmentProcedure);
        final TextView note = dialogView.findViewById(R.id.tvAppointmentNotes);
        final CardView btnEdit = dialogView.findViewById(R.id.ibEditAppointment);
        final CardView btnDelete = dialogView.findViewById(R.id.ibAppointmentDone);

        name.setText(appointment.getPatient().getFullName());
        contact.setText(appointment.getPatient().getContactNumber());
        date.setText(DateUtil.getReadableDate(appointment.getDateTime()));
        time.setText(DateUtil.getReadableTime(appointment.getDateTime()));
//        service.setText(appointment.);
        note.setText(appointment.getComments());

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(v, "Edit appointment", Snackbar.LENGTH_SHORT)
                        .show();
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(v, "Delete appointment", Snackbar.LENGTH_SHORT)
                        .show();
            }
        });

        return builder.create();
    }
}
