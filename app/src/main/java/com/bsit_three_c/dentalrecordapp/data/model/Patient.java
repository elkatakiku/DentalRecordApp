package com.bsit_three_c.dentalrecordapp.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.bsit_three_c.dentalrecordapp.util.UIUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Patient extends Person implements Parcelable {
    private static final String TAG = Patient.class.getSimpleName();

    private String occupation;
    private ArrayList<String> dentalProcedures;

    private static final String NEW_PATIENT = "New patient";

    public Patient() {}

    //  This constructor is used to retrieve patient's detail
    public Patient(String uid,
                   String firstname,
                   String lastname,
                   String middleInitial,
                   String suffix,
                   String dateOfBirth,
                   List<String> phoneNumber,
                   String address,
                   int civilStatus,
                   int age,
                   String occupation,
                   Date lastUpdated,
                   ArrayList<String> dentalProcedures,
                   String email) {

        super(
                uid,
                firstname,
                lastname,
                middleInitial,
                suffix,
                dateOfBirth,
                phoneNumber,
                address,
                civilStatus,
                age,
                lastUpdated,
                email
        );

        this.occupation = UIUtil.capitalize(occupation);
        this.dentalProcedures = dentalProcedures;
    }

    protected Patient(Parcel in) {
        uid = in.readString();
        firstname = in.readString();
        lastname = in.readString();
        middleInitial = in.readString();
        suffix = in.readString();
        dateOfBirth = in.readString();
        phoneNumber = in.createStringArrayList();
        address = in.readString();
        civilStatus = in.readInt();
        age = in.readInt();
        occupation = in.readString();
        dentalProcedures = in.createStringArrayList();
        email = in.readString();
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

    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    public ArrayList<String> getDentalProcedures() {
        return dentalProcedures;
    }

    public void setDentalProcedures(ArrayList<String> dentalProcedures) {
        this.dentalProcedures = dentalProcedures;
    }

    public void addProcedure(String procedureKey) {
        if (dentalProcedures != null ) dentalProcedures.add(procedureKey);
        if (dentalProcedures.get(0).equals(NEW_PATIENT)) dentalProcedures.remove(0);
    }

    @Override
    public String toString() {
        return "Patient{" +
                "\nuid='" + uid + '\'' +
                "\nfirstname='" + firstname + '\'' +
                "\nlastname='" + lastname + '\'' +
                "\nmiddleInitial='" + middleInitial + '\'' +
                "\nsuffix='" + suffix + '\'' +
                "\ndateOfBirth='" + dateOfBirth + '\'' +
                "\nphoneNumber=" + phoneNumber +
                "\naddress='" + address + '\'' +
                "\ncivilStatus='" + civilStatus + '\'' +
                "\nage=" + age +
                "\noccupation='" + occupation + '\'' +
                "\nlastUpdated=" + lastUpdated +
                "\ndentalProcedures=" + dentalProcedures +
                "\nemail= " + email +
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
        dest.writeString(suffix);
        dest.writeString(dateOfBirth);
        dest.writeStringList(phoneNumber);
        dest.writeString(address);
        dest.writeInt(civilStatus);
        dest.writeInt(age);
        dest.writeString(occupation);
        dest.writeStringList(dentalProcedures);
        dest.writeString(email);
    }
}
