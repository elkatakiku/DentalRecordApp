package com.bsit_three_c.dentalrecordapp.ui.patients.procedure_form;

import android.content.Intent;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.bsit_three_c.dentalrecordapp.R;
import com.bsit_three_c.dentalrecordapp.data.adapter.ServiceOptionsAdapter;
import com.bsit_three_c.dentalrecordapp.data.model.Appointment;
import com.bsit_three_c.dentalrecordapp.data.model.DentalService;
import com.bsit_three_c.dentalrecordapp.data.model.DentalServiceOption;
import com.bsit_three_c.dentalrecordapp.data.model.FormState;
import com.bsit_three_c.dentalrecordapp.data.model.Patient;
import com.bsit_three_c.dentalrecordapp.data.model.Person;
import com.bsit_three_c.dentalrecordapp.data.model.Procedure;
import com.bsit_three_c.dentalrecordapp.data.model.ProgressNote;
import com.bsit_three_c.dentalrecordapp.data.repository.PatientRepository;
import com.bsit_three_c.dentalrecordapp.data.repository.ProcedureRepository;
import com.bsit_three_c.dentalrecordapp.data.repository.ProgressNoteRepository;
import com.bsit_three_c.dentalrecordapp.data.repository.ServiceRepository;
import com.bsit_three_c.dentalrecordapp.interfaces.SpinnerState;
import com.bsit_three_c.dentalrecordapp.interfaces.TextChange;
import com.bsit_three_c.dentalrecordapp.ui.dialog.AppointmentDialog;
import com.bsit_three_c.dentalrecordapp.util.Checker;
import com.bsit_three_c.dentalrecordapp.util.DateUtil;
import com.bsit_three_c.dentalrecordapp.util.UIUtil;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ProcedureFormViewModel extends ViewModel implements TextChange, SpinnerState {
    private final ProcedureRepository procedureRepository;
    private final ServiceRepository serviceRepository;

    private final MutableLiveData<FormState> mAmount;
    private final MutableLiveData<FormState> mPayment;
    private final MutableLiveData<FormState> mBalance;
    private final MutableLiveData<FormState> mServices;
    private final MutableLiveData<List<DentalService>> mDentalServices;

    private final ArrayList<DentalServiceOption> serviceOptions;

    private final ValueEventListener servicesEventListener;

    private double amount;
    private double payment;
    private Double balance;

    private static final String AMOUNT = "Amount";
    private static final String PAYMENT = "Payment";
    private static final String BALANCE = "Balance";
    private static final String SERVICE = "Services";
    private static final int VALID = -1;

    public ProcedureFormViewModel(ProcedureRepository procedureRepository, ServiceRepository serviceRepository) {
        this.procedureRepository = procedureRepository;
        this.serviceRepository = serviceRepository;

        this.mAmount = new MutableLiveData<>();
        this.mPayment = new MutableLiveData<>();
        this.mBalance = new MutableLiveData<>();
        this.mServices = new MutableLiveData<>();
        this.mDentalServices = new MutableLiveData<>();

        this.serviceOptions = new ArrayList<>();

        this.serviceOptions.add(new DentalServiceOption(ServiceOptionsAdapter.DEFAULT_OPTION, ServiceOptionsAdapter.DEFAULT_OPTION, false));
        this.servicesEventListener = new ServiceRepository.ServicesListener(mDentalServices);
    }

    public void loadServices() {
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

    public LiveData<List<DentalService>> getmDentalServices() {
        return mDentalServices;
    }

    @Override
    public void dataChanged(String label, String input) {
        boolean isNull = input == null;

        if (isInputEmpty(isNull, input)) {
            setState(label, R.string.invalid_empty_input);
            if (PAYMENT.equals(label)) setBalance(label, input);
        }
        else if (isNumberField(label)) {

            if (Checker.isRepeated(input, "."))
                setState(label, R.string.invalid_contains_two_or_more_dots);

            else if (UIUtil.convertToDouble(input) == -1)
                setState(label, R.string.invalid_input);

            else if (Checker.hasLetter(input))
                setState(label, R.string.invalid_contains_letter);

            else {
                setState(label, VALID);
                setBalance(label, input);
            }
        }
        else setState(label, VALID);
    }

    private void setBalance(String label, String input) {
        boolean hasAmount = Checker.isNotNullAndValid(mAmount);
        boolean hasPayment = Checker.isNotNullAndValid(mPayment);

        if (AMOUNT.equals(label) && hasAmount) amount = Double.parseDouble(input);

        if (PAYMENT.equals(label)) {
            if (hasPayment) payment = Double.parseDouble(input);
            else setState(BALANCE, R.string.invalid_input);
        }

        if (hasAmount && hasPayment) computeBalance();
    }

    private void computeBalance() {

        balance = amount - payment;

        if (balance < 0) {
            setState(BALANCE, R.string.invalid_input);
        }
        else {
            setState(BALANCE, VALID);
        }
    }

    public boolean isDataValid(boolean downpayment, List<String> services) {
        boolean result;

        if (!downpayment) {
            result = Checker.isNotNullAndValid(mAmount);
        }
        else {
            result = Checker.isNotNullAndValid(mPayment);
        }

        return result && !UIUtil.isServiceDefault(services);
    }

    public Intent createResultIntent(
            Appointment appointment,
            String dentalDesc,
            String dentalAmount,
            boolean isDownpayment,
            String dentalPayment,
            String dentalBalance) {

        ProgressNote progressNote = createProgressNote(
                dentalDesc,
                appointment.getDateTime(),
                dentalPayment
        );

        Procedure procedure = appointment.getProcedure();

        procedure.setDentalDesc(dentalDesc);
        procedure.setDentalTotalAmount(UIUtil.convertToDouble(dentalAmount));
        procedure.setDownpayment(isDownpayment);
        procedure.setDentalBalance(UIUtil.convertToDouble(dentalBalance));

        appointment.setProcedure(procedure);
        procedure.addPaymentKey(progressNote.getUid());

        return new Intent()
                .putExtra(AppointmentDialog.PROCEDURE_KEY, procedure)
                .putExtra(AppointmentDialog.PROGRESS_NOTE_KEY, progressNote);
    }

    private final MutableLiveData<Integer> mError = new MutableLiveData<>();

    public void uploadProcedure(Appointment appointment,
                                String dentalDesc,
                                String dentalAmount,
                                boolean isDownpayment,
                                String dentalPayment,
                                String dentalBalance) {

        ProgressNote progressNote = createProgressNote(dentalDesc, appointment.getDateTime(), dentalPayment);

        Procedure procedure = appointment.getProcedure();

        procedure.setDentalDesc(dentalDesc);
        procedure.setDentalTotalAmount(UIUtil.convertToDouble(dentalAmount));
        procedure.setDownpayment(isDownpayment);
        procedure.setDentalBalance(UIUtil.convertToDouble(dentalBalance));

        appointment.setProcedure(procedure);
        procedure.addPaymentKey(progressNote.getUid());

        uploadProcedure(appointment.getPatient(), procedure, progressNote);
    }

    public void uploadProcedure(
            Appointment appointment,
            String dentalDesc,
            String dentalAmount,
            boolean isDownpayment) {

        ProgressNote progressNote = createProgressNote(dentalDesc, appointment.getDateTime(), dentalAmount);

        Procedure procedure = appointment.getProcedure();

        procedure.setDentalDesc(dentalDesc);
        procedure.setDentalTotalAmount(UIUtil.convertToDouble(dentalAmount));
        procedure.setDownpayment(isDownpayment);
        procedure.setDentalBalance(0);

        appointment.setProcedure(procedure);
        procedure.addPaymentKey(progressNote.getUid());

        uploadProcedure(appointment.getPatient(), procedure, progressNote);
    }

    private void uploadProcedure(Patient patient, Procedure procedure, ProgressNote progressNote) {
        procedure.addPaymentKey(progressNote.getUid());
        ProgressNoteRepository
                .getInstance()
                .upload(progressNote)
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        mError.setValue(R.string.unsuccessfull_update_data);
                        return;
                    }

                    procedureRepository
                            .upload(procedure)
                            .addOnCompleteListener(task1 -> {
                                if (!task1.isSuccessful()) {
                                    mError.setValue(R.string.unsuccessfull_update_data);
                                    return;
                                }

                                patient.addProcedure(procedure.getUid());
                                PatientRepository
                                        .getInstance()
                                        .upload(patient)
                                        .continueWith(task2 -> {
                                            if (!task2.isSuccessful()) {
                                                mError.setValue(R.string.an_error_occurred);
                                                return null;
                                            }

                                            mError.setValue(Checker.VALID);
                                            return null;
                                        });
                            });
                });
    }

    public void uploadProcedure(Patient patient,
                                List<String> service,
                                String dentalDesc,
                                Date dentalDate,
                                String dentalAmount,
                                boolean isDownpayment,
                                String dentalPayment,
                                String dentalBalance) {

        Procedure procedure = createProcedure(patient, service, dentalDesc, dentalDate, dentalAmount, isDownpayment, dentalBalance);
        ProgressNote progressNote = createProgressNote(dentalDesc, dentalDate, dentalPayment);

        uploadProcedure(patient, procedure, progressNote);
    }

    private Procedure createProcedure(Person patient,
                                      List<String> service,
                                      String dentalDesc,
                                      Date dentalDate,
                                      String dentalAmount,
                                      boolean isDownpayment,
                                      String dentalBalance) {
        return new Procedure(
                procedureRepository.getNewUid(),
                patient.getUid(),
                service,
                dentalDesc,
                DateUtil.getDate(dentalDate),
                UIUtil.convertToDouble(dentalAmount),
                isDownpayment,
                UIUtil.convertToDouble(dentalBalance)
        );
    }

    private ProgressNote createProgressNote(String dentalDesc,
                                            Date dentalDate,
                                            String dentalPayment) {

        return new ProgressNote(
                procedureRepository.getNewUid(),
                DateUtil.getDate(dentalDate),
                dentalDesc,
                UIUtil.convertToDouble(dentalPayment)
        );
    }

    public LiveData<Integer> getmError() {
        return mError;
    }

    public void uploadProcedure(Patient patient, List<String> service, String dentalDesc, Date dentalDate,
                                String dentalAmount, boolean isFullyPaid) {
        uploadProcedure(
                patient,
                service,
                dentalDesc,
                dentalDate,
                dentalAmount,
                isFullyPaid,
                dentalAmount,
                "0"
        );
    }

    public LiveData<FormState> getmAmount() {
        return mAmount;
    }

    public LiveData<FormState> getmPayment() {
        return mPayment;
    }

    public MutableLiveData<FormState> getmBalance() {
        return mBalance;
    }

    public Double getBalance() {
        return balance;
    }

    private boolean isInputEmpty(boolean isNull, String input) {
        return isNull || input.isEmpty();
    }

    private boolean isNumberField(String label) {
        return AMOUNT.equals(label) || PAYMENT.equals(label);
    }

    @Override
    public void beforeDataChange(String label, int after, String input) {
        // Ignore this
    }

    @Override
    public void setSpinnerState(final String label, int pos) {
        if (pos == 0) setState(label, pos);
        else setState(label, VALID);
    }

    private void setState(String label, int msg) {

        FormState field;

        if (msg == VALID) field = new FormState(true);
        else field = new FormState(msg);

        switch (label) {
            case AMOUNT:
                mAmount.setValue(field);
                break;
            case PAYMENT:
                mPayment.setValue(field);
                break;
            case BALANCE:
                mBalance.setValue(field);
                break;
            case SERVICE:
                mServices.setValue(field);
                break;
        }
    }

    public void removeListeners() {
        serviceRepository.getServicesPath().removeEventListener(servicesEventListener);
    }
}
