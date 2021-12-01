package com.bsit_three_c.dentalrecordapp.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

public class Patient extends Person implements Parcelable {

    private String address;
    private String civilStatus;
    private int age;
    private double balance;
    private String occupation;
    private Date lastUpdated;

    private String dentalHistoryUID;

    public Patient() {}

//    public Patient(String uid, String firstname, String lastname, String middleInitial, String phoneNumber, String address, int age, double balance) {
//        super(uid, firstname, lastname, middleInitial, phoneNumber);
//        this.address = address;
//        this.age = age;
//        this.balance = balance;
//    }

    public Patient(String firstname, String lastname, String middleInitial, String address, String phoneNumber, String civilStatus, int age, String occupation) {
        super(firstname, lastname, middleInitial, phoneNumber);
        this.address = address;
        this.civilStatus = civilStatus;
        this.age = age;
        this.occupation = occupation;
    }

    protected Patient(Parcel in) {
        uid = in.readString();
        firstname = in.readString();
        lastname = in.readString();
        middleInitial = in.readString();
        phoneNumber = in.readString();
        address = in.readString();
        civilStatus = in.readString();
        age = in.readInt();
        balance = in.readDouble();
        occupation = in.readString();
        dentalHistoryUID = in.readString();
    }

    public static final Creator<Patient> CREATOR = new Creator<Patient>() {
        @Override
        public Patient createFromParcel(Parcel in) {
            return new Patient(in);
        }

        @Override
        public Patient[] newArray(int size) {
            return new Patient[size];
        }
    };

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

    public Date getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public String getDentalHistoryUID() {
        return dentalHistoryUID;
    }

    public void setDentalHistoryUID(String dentalHistoryUID) {
        this.dentalHistoryUID = dentalHistoryUID;
    }

    public static Creator<Patient> getCREATOR() {
        return CREATOR;
    }

    @Override
    public String toString() {
        return "Patient{" + super.toString() +
                "address='" + address + '\'' +
                ", civilStatus='" + civilStatus + '\'' +
                ", age=" + age +
                ", balance=" + balance +
                ", occupation='" + occupation + '\'' +
                ", lastUpdated=" + lastUpdated +
                ", dentalHistoryUID='" + dentalHistoryUID + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(uid);
        dest.writeString(firstname);
        dest.writeString(lastname);
        dest.writeString(middleInitial);
        dest.writeString(phoneNumber);
        dest.writeString(address);
        dest.writeString(civilStatus);
        dest.writeInt(age);
        dest.writeDouble(balance);
        dest.writeString(occupation);
        dest.writeString(dentalHistoryUID);
    }
}
