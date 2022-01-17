package com.bsit_three_c.dentalrecordapp.ui.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.bsit_three_c.dentalrecordapp.R;
import com.bsit_three_c.dentalrecordapp.interfaces.OnStuffClickListener;

public class SuccessDialogFragment extends DialogFragment {

    public static final String TITLE_KEY = "ARG_SD_TITLE_KEY";
    public static final String ICON_KEY = "ARG_SD_ICON_KEY";
    public static final String MESSAGE_KEY = "ARG_SD_MESSAGE_KEY";

    private int icon;
    private String title;
    private String message;
    private OnStuffClickListener onStuffClickListener;

    public static SuccessDialogFragment newInstance(int icon,
                                             String title,
                                             String message) {
        SuccessDialogFragment dialogFragment = new SuccessDialogFragment();
        Bundle arguments = new Bundle();
        arguments.putInt(ICON_KEY, icon);
        arguments.putString(TITLE_KEY, title);
        arguments.putString(MESSAGE_KEY, message);
        dialogFragment.setArguments(arguments);
        return dialogFragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        Bundle arguments = getArguments();

        if (arguments != null) {
            icon = arguments.getInt(ICON_KEY);
            title = arguments.getString(TITLE_KEY);
            message = arguments.getString(MESSAGE_KEY);
        }

        final View view = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_success, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setView(view);

        ViewHolder viewHolder = new ViewHolder(view);

        viewHolder.icon.setImageResource(icon);
        viewHolder.title.setText(title);
        viewHolder.message.setText(message);

        final AlertDialog alertDialog = builder.create();

//        alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);'

        alertDialog.setOnDismissListener(dialog -> {
            Log.d("DISMISS", "onCreateDialog: dialog dismissed");
            if (onStuffClickListener != null) {
                Log.d("DISMISS", "onCreateDialog: calling onclick");
                onStuffClickListener.onCLick();
            }
        });

        viewHolder.btn.setOnClickListener(v -> alertDialog.dismiss());

        return alertDialog;
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);

        if (onStuffClickListener != null) {
            onStuffClickListener.onCLick();
        }
    }

    public SuccessDialogFragment setOnStuffClickListener(OnStuffClickListener onStuffClickListener) {
        this.onStuffClickListener = onStuffClickListener;
        return this;
    }

    private static class ViewHolder {
        final ImageView icon;
        final TextView title;
        final TextView message;
        final Button btn;

        public ViewHolder(View view) {
            this.icon = view.findViewById(R.id.ivSuccessIcon);
            this.title = view.findViewById(R.id.tvSuccessTitle);
            this.message = view.findViewById(R.id.tvSuccessMessage);
            this.btn = view.findViewById(R.id.btnSuccess);
        }
    }
}
