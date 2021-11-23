package com.bsit_three_c.dentalrecordapp.data.model;

import androidx.annotation.NonNull;

public class Patient extends Person {

    private String address;
    private String civilStatus;
    private int age;
    private double balance;
    private String occupation;

    public Patient() {}

    public Patient(String uid, String firstname, String lastname, String middleInitial, String phoneNumber, String address, int age, double balance) {
        super(uid, firstname, lastname, middleInitial, phoneNumber);
        this.address = address;
        this.age = age;
        this.balance = balance;
    }

    public Patient(String firstname, String lastname, String middleInitial, String address, String phoneNumber, String civilStatus, int age, String occupation) {
        super(firstname, lastname, middleInitial, phoneNumber);
        this.address = address;
        this.civilStatus = civilStatus;
        this.age = age;
        this.occupation = occupation;
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

    public String getCivilStatus() {
        return civilStatus;
    }

    public void setCivilStatus(String civilStatus) {
        this.civilStatus = civilStatus;
    }

    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
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
