package com.bsit_three_c.dentalrecordapp.data.model;

import java.util.List;

public class Account extends Person {

    private String email;
    private String username;
    private String password;
    private String userTypeCode;

    public Account() { }

    public Account(String uid,
                   String firstname,
                   String lastname,
                   String middlename,
                   String suffix,
                   List<String> phoneNumber,
                   String email,
                   String username,
                   String password) {
        super(uid, firstname, lastname, middlename, suffix, phoneNumber);
        this.email = email;
        this.username = username;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserTypeCode() {
        return userTypeCode;
    }

    public void setUserTypeCode(String userTypeCode) {
        this.userTypeCode = userTypeCode;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
