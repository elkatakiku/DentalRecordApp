package com.bsit_three_c.dentalrecordapp.data.model;

public class DentalOperation {

    private String uid;
    private String dentalDesc;
    private String dentalDate;
    private double dentalAmount;
    private boolean isFullyPaid;

    public DentalOperation(String dentalDesc, String dentalDate, double dentalAmount, boolean isFullyPaid) {
        this.dentalDesc = dentalDesc;
        this.dentalDate = dentalDate;
        this.dentalAmount = dentalAmount;
        this.isFullyPaid = isFullyPaid;
    }

    public String getDentalDesc() {
        return dentalDesc;
    }

    public void setDentalDesc(String dentalDesc) {
        this.dentalDesc = dentalDesc;
    }

    public String getDentalDate() {
        return dentalDate;
    }

    public void setDentalDate(String dentalDate) {
        this.dentalDate = dentalDate;
    }

    public double getDentalAmount() {
        return dentalAmount;
    }

    public void setDentalAmount(double dentalAmount) {
        this.dentalAmount = dentalAmount;
    }

    public boolean isFullyPaid() {
        return isFullyPaid;
    }

    public void setFullyPaid(boolean fullyPaid) {
        isFullyPaid = fullyPaid;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    @Override
    public String toString() {
        return "DentalOperation{" +
                "dentalDesc='" + dentalDesc + '\n' +
                ", dentalDate='" + dentalDate + '\n' +
                ", dentalAmount=" + dentalAmount + '\n' +
                ", isFullyPaid=" + isFullyPaid +
                '}';
    }
}
