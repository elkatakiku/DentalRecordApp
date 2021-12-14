package com.bsit_three_c.dentalrecordapp.data.model;

import java.io.Serializable;

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */
public class LoggedInUser implements Serializable {

    private final String userId;
    private final String displayName;
    private final String email;
    private final String type;

    public LoggedInUser(String userId, String displayName, String email, String type) {
        this.userId = userId;
        this.displayName = displayName;
        this.email = email;
        this.type = type;
    }

    public String getUserId() {
        return userId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getEmail() {
        return email;
    }

    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        return "LoggedInUser{" +
                "\nuserId='" + userId + '\'' +
                "\ndisplayName='" + displayName + '\'' +
                "\nemail='" + email + '\'' +
                "\ntype='" + type + '\'' +
                '}';
    }
}