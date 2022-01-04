package com.bsit_three_c.dentalrecordapp.ui.users.admin.employees.employee_form;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.bsit_three_c.dentalrecordapp.data.model.Employee;
import com.bsit_three_c.dentalrecordapp.data.repository.EmployeeRepository;
import com.bsit_three_c.dentalrecordapp.interfaces.SpinnerState;
import com.bsit_three_c.dentalrecordapp.interfaces.TextChange;
import com.bsit_three_c.dentalrecordapp.util.LocalStorage;
import com.bsit_three_c.dentalrecordapp.util.UIUtil;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.List;

public class BasicInfoFormViewModel extends ViewModel implements TextChange, SpinnerState {
    private static final String TAG = BasicInfoFormViewModel.class.getSimpleName();

    private final EmployeeRepository employeeRepository;

    private final MutableLiveData<Employee> mEmployee = new MutableLiveData<>();
    private final MutableLiveData<Boolean> mEmployeeData = new MutableLiveData<>();
    private final MutableLiveData<ByteArrayOutputStream> mImage = new MutableLiveData<>();
    private final MutableLiveData<Uri> mImageUri = new MutableLiveData<>();

    private final Bundle arguments = new Bundle();

    public static final String IMAGE_DRAWABLE_KEY = "IMAGE_DRAWABLE_KEY";

    public BasicInfoFormViewModel(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    public Bundle createEmployeeData(
//            ImageView displayImage,
                                 String firstname,
                                 String lastname,
                                 String middleInitial,
                                 String suffix,
                                 int jobTitle,
                                 String dateOfBirth,
                                 String age,
                                 List<String> phoneNumber,
                                 String email,
                                 String address1stPart,
                                 String address2ndPart,
                                 int civilStatusIndex) {

        mEmployeeData.setValue(false);

//        ByteArrayOutputStream outputStream = UIUtil.getOutputStreamImage(displayImage);
//        if (outputStream == null) {
//            Snackbar.make(displayImage, "Out of Memory. Pleas clear some memory to proceed.", Snackbar.LENGTH_SHORT).show();
//            return null;
//        }

//        byte[] image = outputStream.toByteArray();


        Employee employee = new Employee(
                arguments.getString(LocalStorage.IMAGE_URI_KEY),
                firstname,
                lastname,
                middleInitial,
                suffix,
                jobTitle,
                dateOfBirth,
                UIUtil.convertToInteger(age),
                phoneNumber,
                email,
                address1stPart,
                address2ndPart,
                civilStatusIndex
        );

        Log.d(TAG, "onViewCreated: employee: " + employee);

//        arguments = new Bundle();
        arguments.putParcelable(LocalStorage.EMPLOYEE_KEY, employee);

        return arguments;
    }

    public void addImageUriToArguments(String imageUri) {
        arguments.putString(LocalStorage.IMAGE_URI_KEY, imageUri);
    }

    public void createByteImage() {
        if (mImage.getValue() != null) {
            byte[] image = mImage.getValue().toByteArray();
            Log.d(TAG, "onViewCreated: image array data: " + Arrays.toString(image));
            arguments.putByteArray(LocalStorage.IMAGE_BYTE_KEY, image);
        }
    }

    private Converter converter;

    public void convertImage(ImageView imageView) {
        Log.d(TAG, "convertImage: starting conversion");
        converter = new Converter();
        converter.execute(imageView);
    }

    public boolean isConvertingDone() {
        return converter.getStatus() == AsyncTask.Status.FINISHED;
    }

    public boolean isConvertingImage() {
        return converter.getStatus() == AsyncTask.Status.RUNNING;
    }

    public MutableLiveData<Employee> getmEmployee() {
        return mEmployee;
    }

    public void setEmployee(Employee employee) {
        mEmployee.setValue(employee);
    }

    public Bundle getArguments() {
        return arguments;
    }

    public LiveData<Uri> getmImageUri() {
        return mImageUri;
    }

    public void setmImageUri(Uri imageUri) {
        mImageUri.setValue(imageUri);
    }

    public LiveData<ByteArrayOutputStream> getmImage() {
        return mImage;
    }

//    public void resetVariables() {
//        mImage = null;
//    }

    @Override
    public void setSpinnerState(String label, int pos) {

    }

    @Override
    public void beforeDataChange(String input, int after, String s) {

    }

    @Override
    public void dataChanged(String input, String label) {

    }

    private class Converter extends AsyncTask<ImageView, Void, ByteArrayOutputStream> {

        @Override
        protected void onPostExecute(ByteArrayOutputStream byteArrayOutputStream) {
            mImage.setValue(byteArrayOutputStream);
        }

        @Override
        protected ByteArrayOutputStream doInBackground(ImageView... imageViews) {
            return UIUtil.getOutputStreamImage(imageViews[0]);
        }
    }
}
