package com.bsit_three_c.dentalrecordapp.data.model;

public class Account extends Person {

    private String email;
    private String username;
    private String password;
    private boolean isAdmin;

    public Account(String uid, String firstname, String lastname, String middlename, String phoneNumber, String email, String username, String password) {
        super(uid, firstname, lastname, middlename, phoneNumber);
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

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
