package com.bsit_three_c.dentalrecordapp.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.bsit_three_c.dentalrecordapp.util.UIUtil;

import java.util.List;

public class EmergencyContact extends Person implements Parcelable {
    public static final String EMERGENCY_CONTACT_KEY = "EMERGENCY_CONTACT_KEY";

    private String address2ndPart;

    public EmergencyContact() {
    }

    public EmergencyContact(String uid,
                            String firstname,
                            String lastname,
                            String middleInitial,
                            String suffix,
                            List<String> contactNumber,
                            String address1stPart,
                            String address2ndPart) {

        super(uid, UIUtil.capitalize(firstname), UIUtil.capitalize(lastname), UIUtil.capitalize(middleInitial), UIUtil.capitalize(suffix), contactNumber, UIUtil.capitalize(address1stPart));
        this.address2ndPart = UIUtil.capitalize(address2ndPart);
    }

    public EmergencyContact(String firstname,
                            String lastname,
                            String middleInitial,
                            String suffix,
                            List<String> contactNumber,
                            String address1stPart,
                            String address2ndPart) {
        this(null, UIUtil.capitalize(firstname), UIUtil.capitalize(lastname), UIUtil.capitalize(middleInitial), UIUtil.capitalize(suffix), contactNumber, UIUtil.capitalize(address1stPart), UIUtil.capitalize(address2ndPart));
    }

    protected EmergencyContact(Parcel in) {
        uid = in.readString();
        firstname = in.readString();
        lastname = in.readString();
        middleInitial = in.readString();
        suffix = in.readString();
        phoneNumber = in.createStringArrayList();
        address = in.readString();
        address2ndPart = in.readString();
    }

    public static final Creator<EmergencyContact> CREATOR = new Creator<EmergencyContact>() {
        @Override
        public EmergencyContact createFromParcel(Parcel in) {
            return new EmergencyContact(in);
        }

        @Override
        public EmergencyContact[] newArray(int size) {
            return new EmergencyContact[size];
        }
    };

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getAddress2ndPart() {
        return address2ndPart;
    }

    public void setAddress2ndPart(String address2ndPart) {
        this.address2ndPart = address2ndPart;
    }

    public String getFullAddress() {
        return address + " " + address2ndPart;
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
        dest.writeStringList(phoneNumber);
        dest.writeString(address);
        dest.writeString(address2ndPart);
    }

    @Override
    public String toString() {
        return "\nEmergencyContact{" +
                "\naddress2ndPart='" + address2ndPart + '\'' +
                "\nuid='" + uid + '\'' +
                "\nfirstname='" + firstname + '\'' +
                "\nlastname='" + lastname + '\'' +
                "\nmiddleInitial='" + middleInitial + '\'' +
                "\nsuffix='" + suffix + '\'' +
                "\ndateOfBirth='" + dateOfBirth + '\'' +
                "\nphoneNumber=" + phoneNumber +
                "\naddress='" + address + '\'' +
                "\ncivilStatus=" + civilStatus +
                "\nage=" + age +
                "\nlastUpdated=" + lastUpdated +
                "\nemail='" + email + '\'' +
                "\n}";
    }
}
