package com.bsit_three_c.dentalrecordapp.ui.users.admin.employees.employee_form;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.bsit_three_c.dentalrecordapp.R;
import com.bsit_three_c.dentalrecordapp.databinding.FragmentAdminEmployeeForm1Binding;
import com.bsit_three_c.dentalrecordapp.ui.dialog.DatePickerFragment;
import com.bsit_three_c.dentalrecordapp.util.LocalStorage;
import com.bsit_three_c.dentalrecordapp.util.UIUtil;

import java.util.Calendar;

public class BasicInfoFormFragment extends Fragment {
    private static final String TAG = BasicInfoFormFragment.class.getSimpleName();

    private FragmentAdminEmployeeForm1Binding binding;
    private final Calendar mCalendar = Calendar.getInstance();
    private boolean isImageChanged;

    private final ActivityResultLauncher<Intent> selectImage = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {

            if (result.getResultCode() == RESULT_OK && result.getData() != null) {

                isImageChanged = true;
                // Get the url of the image from data
                Uri selectedImageUri = result.getData().getData();
                if (null != selectedImageUri) {
                    // update the preview image in the layout
                    binding.ivEmployeeDisplay.setImageTintList(null);
                    binding.ivEmployeeDisplay.setImageURI(selectedImageUri);
                }
            }
        }
    });

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentAdminEmployeeForm1Binding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.btnEmployeeUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LocalStorage.imageChooser(selectImage);
            }
        });

        binding.btnEmployeeNext.setOnClickListener(view1 ->
                NavHostFragment.findNavController(BasicInfoFormFragment.this)
                .navigate(R.id.action_FirstFragment_to_Second2Fragment));

        binding.ibEmployeeCalendar.setOnClickListener(v -> showDatePickerDialog());
    }

    private void showDatePickerDialog() {
        DatePickerFragment datePickerFragment = new DatePickerFragment(
                binding.tvEmployeeMonth,
                binding.tvEmployeeDay,
                binding.tvEmployeeYear,
                binding.etEmployeeAge
        );

        datePickerFragment.showDatePickerDialog(
                datePickerFragment,
                UIUtil.getMonthNumber(binding.tvEmployeeMonth.getText().toString()),
                getChildFragmentManager()
        );
    }

//    private void showDatePickerDialog(String title, int month) {
//        Log.d(TAG, "showDatePickerDialog: starting");
//        DialogFragment dialogFragment = new DatePickerFragment(
//                binding.tvEmployeeMonth,
//                binding.tvEmployeeDay,
//                binding.tvEmployeeYear,
//                binding.etEmployeeAge
//        );
//
//        Bundle arguments = new Bundle();
////        arguments.putInt(DatePickerFragment.DATE_PICKER_ID, dialogId);
//
//        if (month != -1) {
//            String[] dateArray = {binding.tvEmployeeDay.getText().toString(), String.valueOf(month), binding.tvEmployeeYear.getText().toString()};
//            Date date = UIUtil.stringToDate(TextUtils.join("/", dateArray));
//
//            arguments.putSerializable(DatePickerFragment.DATE_PICKER_DATE, date);
//        }
//
//        arguments.putString(DatePickerFragment.DATE_PICKER_TITLE, title);
//
//        dialogFragment.setArguments(arguments);
//        dialogFragment.show(getChildFragmentManager(), "datePicker");
//    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}