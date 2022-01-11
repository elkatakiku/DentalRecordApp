package com.bsit_three_c.dentalrecordapp.ui.appointments.appointment_form.ui.appoinmentform;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.bsit_three_c.dentalrecordapp.R;
import com.bsit_three_c.dentalrecordapp.data.adapter.ServiceOptionsAdapter;
import com.bsit_three_c.dentalrecordapp.data.model.Appointment;
import com.bsit_three_c.dentalrecordapp.data.model.DentalService;
import com.bsit_three_c.dentalrecordapp.data.model.DentalServiceOption;
import com.bsit_three_c.dentalrecordapp.data.model.FormState;
import com.bsit_three_c.dentalrecordapp.data.model.Person;
import com.bsit_three_c.dentalrecordapp.data.model.Procedure;
import com.bsit_three_c.dentalrecordapp.data.repository.AppointmentRepository;
import com.bsit_three_c.dentalrecordapp.data.repository.ServiceRepository;
import com.bsit_three_c.dentalrecordapp.interfaces.TextChange;
import com.bsit_three_c.dentalrecordapp.util.Checker;
import com.bsit_three_c.dentalrecordapp.util.DateUtil;
import com.bsit_three_c.dentalrecordapp.util.UIUtil;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AppointmentFormViewModel extends ViewModel implements TextChange {
    private static final String TAG = AppointmentFormViewModel.class.getSimpleName();
    // TODO: Implement the ViewModel

    private final AppointmentRepository appointmentRepository;
    private final ServiceRepository serviceRepository;

    private final MutableLiveData<ArrayList<DentalService>> mDentalServices = new MutableLiveData<>();

    private final ArrayList<DentalServiceOption> serviceOptions = new ArrayList<>();
    private final ValueEventListener servicesEventListener;

    private final MutableLiveData<FormState> mFirstname = new MutableLiveData<>();
    private final MutableLiveData<FormState> mlastname = new MutableLiveData<>();
    private final MutableLiveData<FormState> mMiddleInitial = new MutableLiveData<>();
    private final MutableLiveData<FormState> mSuffix = new MutableLiveData<>();
    private final MutableLiveData<FormState> mContact = new MutableLiveData<>();

    private final MutableLiveData<Integer> mUploadResult = new MutableLiveData<>();
    private Appointment appointment;

    private static final String FIRSTNAME = "Firstname";
    private static final String LASTNAME = "Lastname";
    private static final String MIDDLE_INITIAL = "MI";
    private static final String SUFFIX = "Suffix";
    private static final String CONTACT = "Contact Number";


    public AppointmentFormViewModel() {
        this.appointmentRepository = AppointmentRepository.getInstance();
        this.serviceRepository = ServiceRepository.getInstance();
        this.servicesEventListener = new ServiceRepository.ServicesListener(mDentalServices);

        this.serviceOptions.add(new DentalServiceOption(ServiceOptionsAdapter.DEFAULT_OPTION, ServiceOptionsAdapter.DEFAULT_OPTION, false));
    }

    public void loadServices() {
        Log.d(TAG, "loadServices: called");
        serviceRepository.getServicesPath().addValueEventListener(servicesEventListener);
    }

    public void setServicesOptions(ArrayList<DentalService> dentalServices, Appointment appointment) {
        if (appointment == null) {
            serviceRepository.setServicesOptions(dentalServices, serviceOptions);
        } else {
            serviceRepository.setServicesOptions(dentalServices, serviceOptions, appointment.getProcedure().getServiceIds());
        }
    }

    public List<DentalServiceOption> getDentalServiceOptions() {
        return serviceOptions;
    }

    public LiveData<ArrayList<DentalService>> getmDentalServices() {
        return mDentalServices;
    }

    @Override
    public void beforeDataChange(String input, int after, String s) {
    }

    @Override
    public void dataChanged(String label, String input) {

        Log.d(TAG, "dataChanged: label: " + label);
        Log.d(TAG, "dataChanged: data changed");

        if (Checker.isDataAvailable(input)) {

            if (SUFFIX.equals(label)) {
                if (Checker.hasNumber(input)) {
                    Log.d(TAG, "dataChanged: has number");
                    setState(label, R.string.invalid_contains_number);
                }
                else {
                    Log.d(TAG, "dataChanged: data is valid");
                    setState(label, Checker.VALID);
                }
            }

            else if (Checker.containsSpecialCharacter(input)) {
                setState(label, R.string.invalid_contains_special_character);
            }

            else if (isLetterField(label)) {
                if (Checker.hasNumber(input)) {
                    Log.d(TAG, "dataChanged: has number");
                    setState(label, R.string.invalid_contains_number);
                }
                else if (MIDDLE_INITIAL.equals(label) && (input.length() > 1)) {
                    Log.d(TAG, "dataChanged: middle error");
                    setState(label, R.string.invalid_contains_more_than_one_character);
                }
                else {
                    Log.d(TAG, "dataChanged: data is valid");
                    setState(label, Checker.VALID);
                }
            } else if (!isLetterField(label)) {
                if (Checker.hasLetter(input)) {
                    setState(label, R.string.invalid_contains_letter);
                } else {
                    setState(label, Checker.VALID);
                }
            }
        }
    }

    private boolean isLetterField(final String s) {
        boolean result = false;
        switch (s) {
            case FIRSTNAME: case LASTNAME: case MIDDLE_INITIAL: case SUFFIX:
                result = true;
                break;
            case CONTACT:
                result = false;
                break;
        }

        return result;
    }

    private void setState(final String label, final Integer msg) {
        FormState field;

        if (msg == null) field = null;
        else if (msg == -1) field = new FormState(true);
        else field = new FormState(msg);

        switch (label) {
            case FIRSTNAME:
                mFirstname.setValue(field);
                break;
            case LASTNAME:
                mlastname.setValue(field);
                break;
            case MIDDLE_INITIAL:
                mMiddleInitial.setValue(field);
                break;
            case SUFFIX:
                mSuffix.setValue(field);
                break;
            case CONTACT:
                mContact.setValue(field);
                break;
        }
    }

    public LiveData<FormState> getmFirstname() {
        return mFirstname;
    }

    public LiveData<FormState> getMlastname() {
        return mlastname;
    }

    public LiveData<FormState> getmMiddleInitial() {
        return mMiddleInitial;
    }

    public LiveData<FormState> getmSuffix() {
        return mSuffix;
    }

    public LiveData<FormState> getmContact() {
        return mContact;
    }

    public void createAppointment(String firstname,
                                  String lastname,
                                  String middleInitial,
                                  String suffix,
                                  String contact,
                                  String date,
                                  String time,
                                  List<String> services,
                                  String comments) {

        Person person = new Person(
                appointmentRepository.getNewUid(),
                firstname,
                lastname,
                middleInitial,
                suffix,
                UIUtil.createList(contact)
        );

        Date appointmentDate = DateUtil.getDateTime(date+'/'+time);

        Procedure procedure = new Procedure(
                appointmentRepository.getNewUid(),
                DateUtil.getDate(appointmentDate),
                services
        );

        Appointment appointment = new Appointment(
                appointmentRepository.getNewUid(),
                person,
                procedure,
                appointmentDate,
                comments
        );

        appointmentRepository.upload(appointment).addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                mUploadResult.setValue(R.string.an_error_occurred);
                return;
            }

            AppointmentFormViewModel.this.appointment = appointment;
            mUploadResult.setValue(Checker.VALID);
        });

        Log.d(TAG, "createAppointment: appointment created: " + appointment);
    }

    public LiveData<Integer> getmUploadResult() {
        return mUploadResult;
    }

    public Appointment getAppointment() {
        return appointment;
    }
}