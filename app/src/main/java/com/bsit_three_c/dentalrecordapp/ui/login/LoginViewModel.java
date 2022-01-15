package com.bsit_three_c.dentalrecordapp.ui.login;

import android.os.AsyncTask;
import android.util.Log;
import android.util.Patterns;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.bsit_three_c.dentalrecordapp.R;
import com.bsit_three_c.dentalrecordapp.data.model.Account;
import com.bsit_three_c.dentalrecordapp.data.model.Employee;
import com.bsit_three_c.dentalrecordapp.data.model.LoggedInUser;
import com.bsit_three_c.dentalrecordapp.data.model.Patient;
import com.bsit_three_c.dentalrecordapp.data.model.Person;
import com.bsit_three_c.dentalrecordapp.data.repository.AccountRepository;
import com.bsit_three_c.dentalrecordapp.data.repository.AdminRepository;
import com.bsit_three_c.dentalrecordapp.data.repository.EmployeeRepository;
import com.bsit_three_c.dentalrecordapp.data.repository.LoginRepository;
import com.bsit_three_c.dentalrecordapp.data.repository.PatientRepository;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class LoginViewModel extends ViewModel {
    private static final String TAG = "LoginViewModel";

    private final MutableLiveData<LoginFormState> loginFormState = new MutableLiveData<>();
    private final MutableLiveData<LoginResult> loginResult = new MutableLiveData<>();
    private final LoginRepository loginRepository;
    private final AccountRepository accountRepository;
    private LoggedInUser loggedInUser;

    LoginViewModel(LoginRepository loginRepository) {
        this.loginRepository = loginRepository;
        this.accountRepository = AccountRepository.getInstance();
    }

    LiveData<LoginFormState> getLoginFormState() {
        return loginFormState;
    }

    LiveData<LoginResult> getLoginResult() {
        return loginResult;
    }

    public LoggedInUser getLoggedInUser() {
        return loggedInUser;
    }

    public void setLoggedInUser(LoggedInUser loggedInUser) {
        Log.d(TAG, "setLoggedInUser: setting logged in user");
        Log.d(TAG, "setLoggedInUser: logged in user: " + loggedInUser);
        this.loggedInUser = loggedInUser;
        loginResult.setValue(new LoginResult(createLoggedInUserView(loggedInUser)));
    }

    public void login(String username, String password) {
        // can be launched in a separate asynchronous job
        // Launched in async
        new LoginUser().execute(username, password);
    }

    // Get user details to be displayed in the UI
    private LoggedInUserView createLoggedInUserView(LoggedInUser loggedInUser) {
        return new LoggedInUserView(loggedInUser.getLastname());
    }

    public void loginDataChanged(String email, String password) {
        Log.d(TAG, "loginDataChanged: email: " + email);
        Log.d(TAG, "loginDataChanged: password: " + password);
        Log.d(TAG, "loginDataChanged: password valid: " + isPasswordValid(password));

        if (!isEmailValid(email)) {
            loginFormState.setValue(new LoginFormState(R.string.invalid_username, null));
        }

        if (!isPasswordValid(password)) {
            loginFormState.setValue(new LoginFormState(null, R.string.invalid_password));
        }

        if (isEmailValid(email) && isPasswordValid(password)){
            loginFormState.setValue(new LoginFormState(true));
        }
    }

    // A placeholder username validation check
    private boolean isEmailValid(String username) {
        if (username.contains("@")) {
            return Patterns.EMAIL_ADDRESS.matcher(username).matches();
        } else {
            return !username.trim().isEmpty();
        }
    }

    // A placeholder password validation check
    private boolean isPasswordValid(String password) {
        return password != null && password.trim().length() > 5;
    }

    private class LoginUser extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... strings) {
//
//            loginRepository
//                    .login(strings[0], strings[1])
//                    .addOnCompleteListener(task -> {
//                        if (task.isSuccessful()) {
//                            Account account = new Account(
//                                    task.getResult().getUser().getUid(),
//                                    strings[0],
//                                    strings[0],
//                                    Account.TYPE_EMPLOYEE,
//                                    "-MsnLGtoUoWBIVjKaXYt"
//                            );
//                            accountRepository
//                                    .addAccount(account);
//                        }
//                    });

            loginRepository
                    .login(strings[0], strings[1])
                    .addOnSuccessListener(authResult -> {
                        Log.d(TAG, "loginUser: logged in success");

                        FirebaseUser user = authResult.getUser();
                        if (user != null) {
                            accountRepository.getPath(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    Account account = snapshot.getValue(Account.class);

                                    if (account != null) {
                                        switch (account.getUserType()) {
                                            case Account.TYPE_ADMIN:
                                                AdminRepository
                                                        .getInstance()
                                                        .getDatabaseReference()
                                                        .addListenerForSingleValueEvent(new GetUser(account));
                                                break;

                                            case Account.TYPE_EMPLOYEE:

                                                EmployeeRepository
                                                        .getInstance()
                                                        .getEmployeePathThroughAccount(account.getUid())
                                                        .addListenerForSingleValueEvent(new GetUser(account));

                                                break;

                                            case Account.TYPE_PATIENT:
                                                PatientRepository
                                                        .getInstance()
                                                        .getPatientsByAccount(account.getUid())
                                                        .addListenerForSingleValueEvent(new GetUser(account));
                                                break;
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "loginUser: logged in error");
                        loginResult.setValue(new LoginResult(R.string.login_failed));
                    });
            return null;
        }
    }

    private void setLoggedInUser(Person person, Account account) {
        setLoggedInUser(new LoggedInUser(person, account));
    }

    private class GetUser implements ValueEventListener {
        private final Account account;

        public GetUser(Account account) {
            this.account = account;
        }

        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            Log.d(TAG, "onDataChange: employee uid: " + snapshot.getKey());
            Log.d(TAG, "onDataChange: data result count: " + snapshot.getChildrenCount());

            for (DataSnapshot data : snapshot.getChildren()) {
                Log.d(TAG, "onDataChange: data uid: " + data.getKey());

                switch (account.getUserType()) {
                    case Account.TYPE_ADMIN:
                        Person admin = snapshot.getValue(Person.class);

                        if (admin != null) {
                            Log.d(TAG, "onDataChange: account uid: " + account.getUid());
                            Log.d(TAG, "onDataChange: has admin: " + admin);
                            setLoggedInUser(admin, account);
                        }

                        break;
                    case Account.TYPE_EMPLOYEE:
                        Employee user = data.getValue(Employee.class);

                        if (user != null) {
                            Log.d(TAG, "onDataChange: account uid: " + account.getUid());
                            Log.d(TAG, "onDataChange: has employee: " + user);
                            setLoggedInUser(user, account);
                        }

                        break;
                    case Account.TYPE_PATIENT:

                        Patient patient = data.getValue(Patient.class);

                        if (patient != null) {
                            Log.d(TAG, "onDataChange: account uid: " + account.getUid());
                            Log.d(TAG, "onDataChange: has patient: " + patient);
                            setLoggedInUser(patient, account);
                        }

                        break;
                }
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    }

}