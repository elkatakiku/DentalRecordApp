package com.bsit_three_c.dentalrecordapp.data.model;

public class Person {

    protected String uid;
    protected String firstname;
    protected String lastname;
    protected String middlename;
    protected String phoneNumber;

    public Person() {
    }

    public Person(String uid, String firstname, String lastname, String middlename, String phoneNumber) {
        this.uid = uid;
        this.firstname = firstname;
        this.lastname = lastname;
        this.middlename = middlename;
        this.phoneNumber = phoneNumber;
    }

    public Person(String firstname, String lastname, String middlename, String phoneNumber) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.middlename = middlename;
        this.phoneNumber = phoneNumber;
    }

    public String getUid() {
        return uid;
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

    public String getMiddlename() {
        return middlename;
    }

    public void setMiddlename(String middlename) {
        this.middlename = middlename;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
