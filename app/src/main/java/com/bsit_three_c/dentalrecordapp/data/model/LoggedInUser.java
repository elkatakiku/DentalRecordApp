package com.bsit_three_c.dentalrecordapp.data.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */
public class LoggedInUser implements Parcelable {

    private final Person person;
    private final Account account;
//    private final String userId;
//    private final String displayName;
//    private final String email;
//    private final String type;

//    public LoggedInUser(String userId, String displayName, String email, String type) {
//        this.userId = userId;
//        this.displayName = displayName;
//        this.email = email;
//        this.type = type;
//    }

    public LoggedInUser(Person person, Account account) {
        this.person = person;
        this.account = account;
//        this.userId = userId;
//        this.displayName = displayName;
//        this.email = email;
//        this.type = type;
    }

    protected LoggedInUser(Parcel in) {
        person = in.readParcelable(Person.class.getClassLoader());
        account = in.readParcelable(Account.class.getClassLoader());
    }

    public static final Creator<LoggedInUser> CREATOR = new Creator<LoggedInUser>() {
        @Override
        public LoggedInUser createFromParcel(Parcel in) {
            return new LoggedInUser(in);
        }

        @Override
        public LoggedInUser[] newArray(int size) {
            return new LoggedInUser[size];
        }
    };

    public String getUserId() {
        return account.getUid();
    }

    public String getDisplayName() {
        return person.getFullName();
    }

    public String getLastname() {
        return person.getLastname();
    }

    public String getEmail() {
        return person.getEmail();
    }

    public String getPassword() {
        return account.getPassword();
    }

    //  Error here
    public int getType() {
        return account.getUserType();
    }

//    public void logout() {
//
//    }


    public Person getPerson() {
        return person;
    }

    public Account getAccount() {
        return account;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(person, flags);
        dest.writeParcelable(account, flags);
    }

    @Override
    public String toString() {
        return "\nLoggedInUser{" +
                "\nperson=" + person +
                "\naccount=" + account +
                "\n}";
    }
}