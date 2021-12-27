package com.bsit_three_c.dentalrecordapp.data.model;

public class ProgressNote {

    private String uid;
    private String date;
    private String description;
    private Double amount;

    public ProgressNote() {
    }

    public ProgressNote(String uid, String date, String description, Double amount) {
        this.uid = uid;
        this.date = date;
        this.description = description;
        this.amount = amount;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "\nProgressNote{" +
                "\nuid='" + uid + '\'' +
                "\ndate='" + date + '\'' +
                "\ndescription='" + description + '\'' +
                "\namount=" + amount +
                "}\n";
    }
}
