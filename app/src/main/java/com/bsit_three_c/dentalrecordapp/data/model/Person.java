package com.bsit_three_c.dentalrecordapp.data.model;

public class Person {

    protected String uid;
    protected String firstname;
    protected String lastname;
    protected String middleInitial;
    protected String suffix;
    protected String phoneNumber;

    public Person() {
    }

    public Person(String uid,
                  String firstname,
                  String lastname,
                  String middleInitial,
                  String suffix,
                  String phoneNumber) {
        this.uid = uid;
        this.firstname = firstname;
        this.lastname = lastname;
        this.middleInitial = middleInitial;
        this.suffix = suffix;
        this.phoneNumber = phoneNumber;
    }

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

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    @Override
    public String toString() {
        return "Person{" +
                "\nuid='" + uid + '\'' +
                "\nfirstname='" + firstname + '\'' +
                "\nlastname='" + lastname + '\'' +
                "\nmiddleInitial='" + middleInitial + '\'' +
                "\nsuffix='" + suffix + '\'' +
                "\nphoneNumber='" + phoneNumber + '\'' +
                '}';
    }
}
