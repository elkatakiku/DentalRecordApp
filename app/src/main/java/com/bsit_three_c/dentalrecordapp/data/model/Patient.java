package com.bsit_three_c.dentalrecordapp.data.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;

public class Patient extends Person implements Parcelable {
    private static final String TAG = Parcelable.class.getSimpleName();

    private String address;
    private String civilStatus;
    private int age;
    private String occupation;
    private Date lastUpdated;

    private ArrayList<String> dentalProcedures;

    private static final String NEW_PATIENT = "New patient";

    public Patient() {}

//    public Patient(String firstname, String lastname, String middleInitial, String address, String phoneNumber, String civilStatus, int age, String occupation) {
//        super(firstname, lastname, middleInitial, phoneNumber);
//        this.address = address;
//        this.civilStatus = civilStatus;
//        this.age = age;
//        this.occupation = occupation;
//    }

    //  This constructor is used to retrive patient's detail
    public Patient(String uid, String firstname, String lastname, String middleInitial, String phoneNumber,
                   String address, String civilStatus, int age,
//                   double balance,
                   String occupation,
                   Date lastUpdated, ArrayList<String> dentalProcedures) {
        super(uid, firstname, lastname, middleInitial, phoneNumber);
        this.address = address;
        this.civilStatus = civilStatus;
        this.age = age;
//        this.balance = balance;
        this.occupation = occupation;
        this.lastUpdated = lastUpdated;
        if (dentalProcedures == null) Log.d(TAG, "Patient: dentalProcedures is null");
        this.dentalProcedures = dentalProcedures;
    }

//    //  This constructor is used to create new patient
//    public Patient(String uid, String firstname, String lastname, String middleInitial, String phoneNumber,
//                   String address, String civilStatus, int age,
////                   double balance,
//                   String occupation,
//                   Date lastUpdated) {
//        super(uid, firstname, lastname, middleInitial, phoneNumber);
//        this.address = address;
//        this.civilStatus = civilStatus;
//        this.age = age;
////        this.balance = balance;
//        this.occupation = occupation;
//        this.lastUpdated = lastUpdated;
//        this.dentalProcedures = new ArrayList<>();
//    }

    protected Patient(Parcel in) {
        uid = in.readString();
        firstname = in.readString();
        lastname = in.readString();
        middleInitial = in.readString();
        phoneNumber = in.readString();
        address = in.readString();
        civilStatus = in.readString();
        age = in.readInt();
//        balance = in.readDouble();
        occupation = in.readString();
//        if (dentalProcedures != null) {
//            in.readStringList(dentalProcedures);

        dentalProcedures = in.createStringArrayList();

        for (String data : dentalProcedures) {
            Log.d(TAG, "Patient: dentalProcedures: " + data);
        }
//        }
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

//    public double getBalance() {
//        return balance;
//    }
//
//    public void setBalance(double balance) {
//        this.balance = balance;
//    }

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

    public ArrayList<String> getDentalProcedures() {
        return dentalProcedures;
    }

    public void setDentalProcedures(ArrayList<String> dentalProcedures) {
        this.dentalProcedures = dentalProcedures;
    }

    public static Creator<Patient> getCREATOR() {
        return CREATOR;
    }

    public void addProcedure(String procedureKey) {
        if (dentalProcedures.get(0).equals(NEW_PATIENT)) dentalProcedures.remove(0);
        dentalProcedures.add(procedureKey);
    }

    @Override
    public String toString() {
        return "Patient{" +
                "\naddress='" + address + '\'' +
                "\ncivilStatus='" + civilStatus + '\'' +
                "\nage=" + age +
//                "\nbalance=" + balance +
                "\noccupation='" + occupation + '\'' +
                "\nlastUpdated=" + lastUpdated +
                "\ndentalProcedures=" + dentalProcedures +
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
//        dest.writeDouble(balance);
        dest.writeString(occupation);
//        if (dentalProcedures != null)
            dest.writeStringList(dentalProcedures);
    }
}
