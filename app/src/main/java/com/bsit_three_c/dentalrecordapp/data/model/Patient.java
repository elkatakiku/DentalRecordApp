package com.bsit_three_c.dentalrecordapp.data.model;

import androidx.annotation.NonNull;

public class Patient {

    private final String uid;
    private String firstname;
    private String lastname;
    private String middlename;
    private String email;
    private String phoneNumber;
    private String address;
    private int age;
    private double balance;

    public Patient(String firstname, String lastname, String email, String phoneNumber, String uid) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.uid = uid;
    }

    public Patient(String firstname, String lastname, String email, String phoneNumber) {
        this(firstname, lastname, email, phoneNumber, null);
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getUid() {
        return uid;
    }

    @NonNull
    @Override
    public String toString() {
        return "firstname: " + firstname + '\n' +
                "lastname: " + lastname + '\n' +
                "email: " + email + '\n' +
                "phoneNumber: " + phoneNumber + '\n' +
                "uid: " + uid;
    }
}
