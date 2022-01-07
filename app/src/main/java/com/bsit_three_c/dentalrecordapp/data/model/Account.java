package com.bsit_three_c.dentalrecordapp.data.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Account implements Parcelable {
    public static final String ACCOUNT_KEY = "ACCOUNT_KEY";
    public static final String LOGGED_ID = "LOGGED_IN";

    public static final int TYPE_ADMIN = 0x001ED751;
    public static final int TYPE_EMPLOYEE = 0x001ED752;
    public static final int TYPE_PATIENT = 0x001ED753;

    private String uid;
    private String userUid;
    private String email;
    private String password;
    private int userType;

    public Account() {}

    public Account(String email, String password, int userType, String userUid) {
        this("", email, password, userType, userUid);
    }

    public Account(String uid, String email, String password, int userType, String userUid) {
        this.uid = uid;
        this.email = email;
        this.password = password;
        this.userType = userType;
    }

    protected Account(Parcel in) {
        uid = in.readString();
        email = in.readString();
        password = in.readString();
        userType = in.readInt();
    }

    public static final Creator<Account> CREATOR = new Creator<Account>() {
        @Override
        public Account createFromParcel(Parcel in) {
            return new Account(in);
        }

        @Override
        public Account[] newArray(int size) {
            return new Account[size];
        }
    };

    public String getUserUid() {
        return userUid;
    }

    public void setUserUid(String userUid) {
        this.userUid = userUid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getUserType() {
        return userType;
    }

    public void setUserType(int userType) {
        this.userType = userType;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public static String getDefaultPassword(String name) {
        if (name == null) {
            return null;
        }

        return name + 123;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    @Override
    public String toString() {
        return "\nAccount{" +
                "\nuid='" + uid + '\'' +
                "\nemail='" + email + '\'' +
                "\npassword='" + password + '\'' +
                "\nuserType=" + userType +
                "\n}";
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(uid);
        dest.writeString(email);
        dest.writeString(password);
        dest.writeInt(userType);
    }
}
