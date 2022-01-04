package com.bsit_three_c.dentalrecordapp.data.model;

import android.content.res.Resources;
import android.os.Parcel;
import android.os.Parcelable;

import com.bsit_three_c.dentalrecordapp.R;
import com.bsit_three_c.dentalrecordapp.data.repository.FirebaseHelper;
import com.bsit_three_c.dentalrecordapp.util.Checker;
import com.bsit_three_c.dentalrecordapp.util.DateUtil;

import java.util.Date;
import java.util.List;

public class Person implements Parcelable {
    public static final String NOT_AVAILABLE = "N/A";

    protected String uid;
    protected String firstname;
    protected String lastname;
    protected String middleInitial;
    protected String suffix;
    protected String dateOfBirth;
    protected List<String> phoneNumber;

    protected String address;
    protected int civilStatus;
    protected int age;
    protected Date lastUpdated;

    protected String email;

    public Person() {
    }

    //  Update constructor to initialize email
    public Person(String uid,
                  String firstname,
                  String lastname,
                  String middleInitial,
                  String suffix,
                  String dateOfBirth,
                  List<String> phoneNumber,
                  String address,
                  int civilStatus,
                  int age,
                  Date lastUpdated,
                  String email) {

        this.uid = uid;
        this.firstname = firstname;
        this.lastname = lastname;
        this.middleInitial = middleInitial;
        this.suffix = suffix;
        this.dateOfBirth = dateOfBirth;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.civilStatus = civilStatus;
        this.age = age;
        this.lastUpdated = lastUpdated;
        this.email = email;
    }

    public Person(String uid,
                  String firstname,
                  String lastname,
                  String middleInitial,
                  String suffix,
                  List<String> phoneNumber,
                  String address) {

        this(
                uid,
                firstname,
                lastname,
                middleInitial,
                suffix,
                null,
                phoneNumber,
                address,
                -1,
                -1,
                new Date(),
                null
        );
    }

    protected Person(Parcel in) {
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
        email = in.readString();
    }

    public static final Creator<Person> CREATOR = new Creator<Person>() {
        @Override
        public Person createFromParcel(Parcel in) {
            return new Person(in);
        }

        @Override
        public Person[] newArray(int size) {
            return new Person[size];
        }
    };

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
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

    public String getMiddleInitial() {
        return middleInitial;
    }

    public void setMiddleInitial(String middleInitial) {
        this.middleInitial = middleInitial;
    }

    public List<String> getPhoneNumber() {
        return phoneNumber;
    }

    public String getContactNumber() {
        if (phoneNumber != null && phoneNumber.size() > 0) {
            StringBuilder builder = new StringBuilder();

            if (phoneNumber.get(0).equals(FirebaseHelper.NEW_PATIENT)) {
                builder.append(Checker.NOT_AVAILABLE);
            } else {
                for (String number : phoneNumber) {
                    builder.append(number).append("\n");
                }
                builder.deleteCharAt(builder.length()-1);
            }

            return builder.toString();
        }
        else
            return NOT_AVAILABLE;
    }

    public void setPhoneNumber(List<String> phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public String getBirthdate() {
        if (Checker.isDataAvailable(dateOfBirth) && !dateOfBirth.equals(Checker.NOT_AVAILABLE)) {
            return DateUtil.getReadableDate(DateUtil.convertToDate(dateOfBirth));
        } else {
            return dateOfBirth;
        }
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getCivilStatus() {
        return civilStatus;
    }

    public void setCivilStatus(int civilStatus) {
        this.civilStatus = civilStatus;
    }

    public String getCivilStatus(Resources resources) {
        return resources.getStringArray(R.array.civil_status_array)[civilStatus];
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public String getFullName() {
        String fullname = lastname + ", " + firstname;
        if (Checker.isDataAvailable(middleInitial)) {
            fullname  += " " + middleInitial + ".";
        }

        if ((Checker.isDataAvailable(suffix))) {
            if (!suffix.contains(".")) {    //  Checks if the suffix already contained period.
                suffix += '.';
            }
            fullname += " " + suffix;
        }

        return fullname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "\nPerson{" +
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
        dest.writeString(email);
    }
}
