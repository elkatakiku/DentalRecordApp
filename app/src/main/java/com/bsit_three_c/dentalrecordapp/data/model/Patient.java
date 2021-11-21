package com.bsit_three_c.dentalrecordapp.data.model;

import androidx.annotation.NonNull;

public class Patient extends Person {

    private String address;
    private int age;
    private double balance;

    public Patient() {}

    public Patient(String uid, String firstname, String lastname, String middlename, String phoneNumber, String address, int age) {
        super(uid, firstname, lastname, middlename, phoneNumber);
        this.address = address;
        this.age = age;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    @NonNull
    @Override
    public String toString() {
        return "firstname: " + firstname + '\n' +
                "lastname: " + lastname + '\n' +
                "phoneNumber: " + phoneNumber + '\n' +
                "uid: " + uid;
    }
}
