package com.bsit_three_c.dentalrecordapp.ui.dialog;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.WindowManager;

import com.google.android.material.bottomsheet.BottomSheetDialog;

public class BottomDialog {

//    private void setDialogDismissListener(BottomSheetDialog dialog, PatientInfoFragment patientInfo) {
//        dialog.setOnDismissListener(dialog1 -> {
//            patientInfo.loadProcedures();
//
//            BottomOperationsDialog operationsDialog = new BottomOperationsDialog(layoutInflater, context, lifecycleOwner);
//            operationsDialog.setPatient(patient);
//            operationsDialog.createOperationDialog(operation);
//            operationsDialog.showDialog();
//        });
//    }

    public static void showDialog(BottomSheetDialog dialog) {
        dialog.show();
        dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
    }

    public static void setBackgroundColorTransparent(BottomSheetDialog dialog) {
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }
}
