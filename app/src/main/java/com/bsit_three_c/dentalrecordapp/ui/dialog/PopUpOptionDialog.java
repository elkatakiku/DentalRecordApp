package com.bsit_three_c.dentalrecordapp.ui.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.DialogFragment;

import com.bsit_three_c.dentalrecordapp.R;
import com.bsit_three_c.dentalrecordapp.util.UIUtil;

public class PopUpOptionDialog extends DialogFragment {

    public static final String POP_UP_KEY = "ARG_PUO_POP_UP_KEY";

    public static final String TITLE_KEY = "ARG_PUO_TITLE_KEY";
    public static final String BUTTON1_TITLE_KEY = "ARG_PUO_BUTTON1_TITLE_KEY";
    public static final String BUTTON2_TITLE_KEY = "ARG_PUO_BUTTON2_TITLE_KEY";
    public static final String BUTTON1_IMAGE_KEY = "ARG_PUO_BUTTON1_IMAGE_KEY";
    public static final String BUTTON2_IMAGE_KEY = "ARG_PUO_BUTTON2_IMAGE_KEY";

    private OnButtonsClickListener onButtonsClickListener;

    private PopUpOptionDialog() {}

    public static PopUpOptionDialog newInstance(
            String title,
            String bn1Title,
            String btn2Title,
            int btn1Image,
            int btn2Image) {

        Bundle arguments = new Bundle();
        arguments.putString(TITLE_KEY, title);
        arguments.putString(BUTTON1_TITLE_KEY, bn1Title);
        arguments.putString(BUTTON2_TITLE_KEY, btn2Title);
        arguments.putInt(BUTTON1_IMAGE_KEY, btn1Image);
        arguments.putInt(BUTTON2_IMAGE_KEY, btn2Image);

        PopUpOptionDialog optionDialog = new PopUpOptionDialog();
        optionDialog.setArguments(arguments);
        return optionDialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        final View view = getLayoutInflater().inflate(R.layout.dialog_pop_up, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setView(view);

        ViewHolder viewHolder = new ViewHolder(view);

        if (getArguments() != null) {
            UIUtil.setText(getArguments().getString(TITLE_KEY), viewHolder.title);
            UIUtil.setText(getArguments().getString(BUTTON1_TITLE_KEY), viewHolder.btnTitle1);
            UIUtil.setText(getArguments().getString(BUTTON2_TITLE_KEY), viewHolder.btnTitle2);
            viewHolder.btn1Image.setImageResource(getArguments().getInt(BUTTON1_IMAGE_KEY));
            viewHolder.btn2Image.setImageResource(getArguments().getInt(BUTTON2_IMAGE_KEY));
        }


        final AlertDialog dialog = builder.create();

        if (onButtonsClickListener != null) {
            viewHolder.btn1.setOnClickListener(v -> {
                dialog.dismiss();
                onButtonsClickListener.onButton1Click();
            });
            viewHolder.btn2.setOnClickListener(v -> {
                dialog.dismiss();
                onButtonsClickListener.onButton2Click();
            });
        }
        viewHolder.close.setOnClickListener(v -> dialog.dismiss());

        return dialog;
    }

    public void setOnButtonsClickListener(OnButtonsClickListener onButtonsClickListener) {
        this.onButtonsClickListener = onButtonsClickListener;
    }

    private static class ViewHolder {
        final ImageView close;
        final TextView title;
        final CardView btn1;
        final TextView btnTitle1;
        final ImageView btn1Image;
        final CardView btn2;
        final TextView btnTitle2;
        final ImageView btn2Image;

        public ViewHolder(View view) {
            this.close = view.findViewById(R.id.imgPopUpClose);
            this.title = view.findViewById(R.id.tvPopUpTitle);

            this.btn1 = view.findViewById(R.id.btnPopUp1);
            this.btn1Image = view.findViewById(R.id.ivPopUpButton1);
            this.btnTitle1 = view.findViewById(R.id.tvPopUpButton1Title);

            this.btn2 = view.findViewById(R.id.buttonPopUp2);
            this.btn2Image = view.findViewById(R.id.ivPopUpButton2);
            this.btnTitle2 = view.findViewById(R.id.tvPopUpButton2Title);
        }
    }

    public static interface OnButtonsClickListener {
        void onButton1Click();
        void onButton2Click();
    }
}
