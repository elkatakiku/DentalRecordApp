package com.bsit_three_c.dentalrecordapp.ui.base;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.bsit_three_c.dentalrecordapp.R;
import com.bsit_three_c.dentalrecordapp.data.model.Appointment;
import com.bsit_three_c.dentalrecordapp.data.model.DentalService;
import com.bsit_three_c.dentalrecordapp.data.model.Patient;
import com.bsit_three_c.dentalrecordapp.databinding.ActivityFormBinding;
import com.bsit_three_c.dentalrecordapp.ui.accounts.edit_account.EditAccountFragment;
import com.bsit_three_c.dentalrecordapp.ui.accounts.edit_account.delete_account.DeleteAccountFragment;
import com.bsit_three_c.dentalrecordapp.ui.accounts.edit_account.email.UpdateEmailFormFragment;
import com.bsit_three_c.dentalrecordapp.ui.accounts.edit_account.forgotten_password.ForgotPasswordFragment;
import com.bsit_three_c.dentalrecordapp.ui.accounts.edit_account.password.UpdatePasswordFormFragment;
import com.bsit_three_c.dentalrecordapp.ui.admin.AdminFormFragment;
import com.bsit_three_c.dentalrecordapp.ui.appointments.ui.appointment_form.AppointmentFormFragment;
import com.bsit_three_c.dentalrecordapp.ui.clinic.ClinicFormFragment;
import com.bsit_three_c.dentalrecordapp.ui.employees.EmployeesFragment;
import com.bsit_three_c.dentalrecordapp.ui.employees.employee_form.EmergencyContactFormFragment;
import com.bsit_three_c.dentalrecordapp.ui.employees.specialties_form.SpecialtiesFormFragment;
import com.bsit_three_c.dentalrecordapp.ui.patients.patient_form.PatientFormFragment;
import com.bsit_three_c.dentalrecordapp.ui.patients.procedure_form.ProcedureFormFragment;
import com.bsit_three_c.dentalrecordapp.ui.patients.registration_form.RegisterFormFragment;
import com.bsit_three_c.dentalrecordapp.ui.services.services_form.ServiceFormFragment;

public class BaseFormActivity extends AppCompatActivity {
    public static final String FORM_KEY = "ARG_PATIENT_ACTIVITY_KEY";

    public static final int FORM_PATIENT = 0x001ED819;
    public static final int FORM_PROCEDURE = 0x001ED81A;
    public static final int FORM_REGISTRATION = 0x001ED81B;
    public static final int FORM_SERVICE = 0x001ED81C;
    public static final int FORM_ADMIN = 0x001ED81D;
    public static final int FORM_CLINIC = 0x001ED81E;
    public static final int FORM_EMPLOYEE = 0x001ED81F;
    public static final int FORM_UPDATE_EMAIL = 0x001ED820;
    public static final int FORM_UPDATE_PASSWORD = 0x001ED821;
    private static final int FORM_APPOINTMENT = 0x001ED822;
    private static final int FORM_ACCOUNT = 0x001ED823;
    private static final int FORM_DELETE_ACCOUNT = 0x001ED824;
    private static final int FORM_FORGOT_PASSWORD = 0x001ED825;
    private static final int FORM_SPECIALTIES = 0x001ED826;
    private static final int FORM_EMERGENCY_CONTACT = 0x001ED827;

    public static final String USER_ID_KEY = "ARG_BF_USER_ID_KEY";
    public static final String PATIENT_ID_KEY = "ARG_BF_PATIENT_ID_KEY";
    public static final String PATIENT_KEY = "ARG_BF_PATIENT_KEY";
    public static final String EMPLOYEE_ID_KEY = "ARG_BF_EMPLOYEE_ID_KEY";
    public static final String EMERGENCY_ID_KEY = "ARG_BF_EMERGENCY_ID_KEY";
    public static final String APPOINTMENT_KEY = "ARG_BF_APPOINTMENT_KEY";
    public static final String SERVICE_KEY = "ARG_BF_SERVICE_KEY";
    public static final String ACCOUNT_KEY = "ARG_BF_ACCOUNT_KEY";

    private ActivityFormBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityFormBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.formToolbar);
        ActionBar actionBar = getSupportActionBar();

        int form = getIntent().getIntExtra(FORM_KEY, -1);

        if (actionBar != null) {
            actionBar.setTitle(getTitle(form));
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainer,  getFragment(form))
                    .commitNow();
        }
    }

    private String getTitle(final int form) {
        String title;

        switch (form) {
            case FORM_PATIENT:
                title = "Patient Form";
                break;
            case FORM_PROCEDURE:
                title = "Procedure Form";
                break;
            case FORM_REGISTRATION:
                title = "Create Account";
                break;
            case FORM_SERVICE:
                title = "Service Form";
                break;
            case FORM_ADMIN:
                title = "Admin Form";
                break;
            case FORM_CLINIC:
                title = "Clinic Form";
                break;
            case FORM_EMPLOYEE:
                title = "Employee Form";
                break;
            case FORM_UPDATE_EMAIL:
                title = "Update Email Address";
                break;
            case FORM_UPDATE_PASSWORD:
                title = "Update Password";
                break;
            case FORM_APPOINTMENT:
                title = "Appointment Form";
                break;
            case FORM_ACCOUNT:
                title = "Edit Account";
                break;
            case FORM_DELETE_ACCOUNT:
                title = "Account Deletion";
                break;
            case FORM_FORGOT_PASSWORD:
                title = "Forgot Password";
                break;
            case FORM_SPECIALTIES:
                title = "Update Specialties";
                break;
            case FORM_EMERGENCY_CONTACT:
                title = "Emergency Contact Form";
                break;
            default:
                title = "Form";
                break;
        }

        return title;
    }

    private Fragment getFragment(final int form) {
        Fragment fragment = null;

        switch (form) {
            case FORM_PATIENT:
                fragment = PatientFormFragment.newInstance(
                        getIntent().getParcelableExtra(PATIENT_KEY),
                        getIntent().getParcelableExtra(APPOINTMENT_KEY)
                );
                break;
            case FORM_PROCEDURE:
                fragment = ProcedureFormFragment.newInstance(
                        getIntent().getParcelableExtra(PATIENT_KEY),
                        getIntent().getParcelableExtra(APPOINTMENT_KEY)
                );
                break;
            case FORM_REGISTRATION:
                fragment = RegisterFormFragment.newInstance();
                break;
            case FORM_SERVICE:
                fragment = ServiceFormFragment.newInstance(
                        getIntent().getParcelableExtra(SERVICE_KEY)
                );
                break;
            case FORM_ADMIN:
                fragment = AdminFormFragment.newInstance(
                        getIntent().getStringExtra(USER_ID_KEY)
                );
                break;
            case FORM_CLINIC:
                fragment = ClinicFormFragment.newInstance();
                break;
            case FORM_EMPLOYEE:
                fragment = EmployeesFragment.newInstance();
                break;
            case FORM_UPDATE_EMAIL:
                fragment = UpdateEmailFormFragment.newInstance(
                        getIntent().getStringExtra(ACCOUNT_KEY)
                );
                break;
            case FORM_UPDATE_PASSWORD:
                fragment = UpdatePasswordFormFragment.newInstance(
                        getIntent().getStringExtra(ACCOUNT_KEY)
                );
                break;
            case FORM_APPOINTMENT:
                fragment = AppointmentFormFragment.newInstance(
                        getIntent().getStringExtra(PATIENT_ID_KEY),
                        getIntent().getParcelableExtra(APPOINTMENT_KEY)
                );
                break;
            case FORM_ACCOUNT:
                fragment = EditAccountFragment.newInstance(
                        getIntent().getStringExtra(ACCOUNT_KEY)
                );
                break;
            case FORM_DELETE_ACCOUNT:
                fragment = DeleteAccountFragment.newInstance(
                        getIntent().getStringExtra(ACCOUNT_KEY)
                );
                break;
            case FORM_FORGOT_PASSWORD:
                fragment = ForgotPasswordFragment.newInstance();
                break;
            case FORM_SPECIALTIES:
                fragment = SpecialtiesFormFragment.newInstance(
                        getIntent().getStringExtra(EMPLOYEE_ID_KEY)
                );
                break;
            case FORM_EMERGENCY_CONTACT:
                fragment = EmergencyContactFormFragment.newInstance(
                        getIntent().getStringExtra(EMERGENCY_ID_KEY)
                );
                break;
            default:
                finish();
                break;
        }

        return fragment;
    }

    public static void showAppointmentForm(Context context, String patientUid, Appointment appointment) {
        context.startActivity(new Intent(context, BaseFormActivity.class)
                .putExtra(FORM_KEY, FORM_APPOINTMENT)
                .putExtra(PATIENT_ID_KEY, patientUid)
                .putExtra(APPOINTMENT_KEY, appointment));
    }

    public static void showAppointmentForm(Context context, Appointment appointment) {
        showAppointmentForm(context, null, appointment);
    }

    public static void showAppointmentForm(Context context, String patientUid) {
        showAppointmentForm(context, patientUid, null);
    }

    public static void showAppointmentForm(Context context) {
        showAppointmentForm(context, null, null);
    }

    public static Intent getAppointmentFormIntent(Context context) {
        return new Intent(context, BaseFormActivity.class)
                .putExtra(FORM_KEY, FORM_APPOINTMENT);
    }

    public static Intent getAppointmentFormIntent(Context context, String patientUid) {
        return new Intent(context, BaseFormActivity.class)
                .putExtra(FORM_KEY, FORM_APPOINTMENT)
                .putExtra(PATIENT_ID_KEY, patientUid);
    }

    public static Intent getPatientFormIntent(Context context, Patient patient, Appointment appointment) {
        return new Intent(context, BaseFormActivity.class)
                .putExtra(FORM_KEY, FORM_PATIENT)
                .putExtra(PATIENT_KEY, patient)
                .putExtra(APPOINTMENT_KEY, appointment);
    }

    public static Intent getPatientFormIntent(Context context, Appointment appointment) {
        return getPatientFormIntent(context, null, appointment);
    }

    public static Intent getPatientFormIntent(Context context, Patient patient) {
        return getPatientFormIntent(context, patient, null);
    }

    public static Intent getPatientFormIntent(Context context) {
        return getPatientFormIntent(context, null, null);
    }

    public static void showServiceForm(Context context, DentalService service) {
        context.startActivity(new Intent(context, BaseFormActivity.class)
                .putExtra(FORM_KEY, FORM_SERVICE)
                .putExtra(SERVICE_KEY, service));
    }

    public static void showServiceForm(Context context) {
        showServiceForm(context, null);
    }

    public static Intent getProcedureFormIntent(Context context, Appointment appointment) {
        return new Intent(context, BaseFormActivity.class)
                .putExtra(FORM_KEY, FORM_PROCEDURE)
                .putExtra(APPOINTMENT_KEY, appointment);
    }

    public static Intent getEditAccountForm(Context context, String accountUid) {
        return new Intent(context, BaseFormActivity.class)
                .putExtra(FORM_KEY, FORM_ACCOUNT)
                .putExtra(ACCOUNT_KEY, accountUid);
    }

    public static Intent getDeleteAccountForm(Context context, String accountUid) {
        return new Intent(context, BaseFormActivity.class)
                .putExtra(FORM_KEY, FORM_DELETE_ACCOUNT)
                .putExtra(ACCOUNT_KEY, accountUid);
    }

    public static Intent getUpdateEmailIntent(Context context, String accountUid) {
        return new Intent(context, BaseFormActivity.class)
                .putExtra(FORM_KEY, FORM_UPDATE_EMAIL)
                .putExtra(ACCOUNT_KEY, accountUid);
    }

    public static Intent getUpdatePasswordIntent(Context context, String accountUid) {
        return new Intent(context, BaseFormActivity.class)
                .putExtra(FORM_KEY, FORM_UPDATE_PASSWORD)
                .putExtra(ACCOUNT_KEY, accountUid);
    }

    public static Intent getForgotPasswordIntent(Context context) {
        return new Intent(context, BaseFormActivity.class)
                .putExtra(FORM_KEY, FORM_FORGOT_PASSWORD);
    }

    public static Intent getSpecialtiesFormIntent(Context context, String employeeUid) {
        return new Intent(context, BaseFormActivity.class)
                .putExtra(FORM_KEY, FORM_SPECIALTIES)
                .putExtra(EMPLOYEE_ID_KEY, employeeUid);
    }

    public static Intent getEmergencyContactFormIntent(Context context, String emergencyUid) {
        return new Intent(context, BaseFormActivity.class)
                .putExtra(FORM_KEY, FORM_EMERGENCY_CONTACT)
                .putExtra(EMERGENCY_ID_KEY, emergencyUid);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}