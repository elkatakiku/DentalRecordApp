package com.bsit_three_c.dentalrecordapp.data.model;

public class DentalOperation {

    private String uid;
    private String dentalDesc;
    private String dentalDate;
    private String modeOfPayment;
    private double dentalAmount;
    private boolean isDownpayment;
    private double dentalTotalAmount;
    private double dentalBalance;

    public DentalOperation() { }

    public DentalOperation(String uid, String dentalDesc, String dentalDate, String modeOfPayment, double dentalAmount, boolean isDownpayment,
                           double dentalTotalAmount, double dentalBalance, String paymentUID) {
        this.uid = uid;
        this.dentalDesc = dentalDesc;
        this.dentalDate = dentalDate;
        this.modeOfPayment = modeOfPayment;
        this.dentalAmount = dentalAmount;
        this.isDownpayment = isDownpayment;
        this.dentalTotalAmount = dentalTotalAmount;
        this.dentalBalance = dentalBalance;
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

    public String getModeOfPayment() {
        return modeOfPayment;
    }

    public void setModeOfPayment(String modeOfPayment) {
        this.modeOfPayment = modeOfPayment;
    }

    public double getDentalAmount() {
        return dentalAmount;
    }

    public void setDentalAmount(double dentalAmount) {
        this.dentalAmount = dentalAmount;
    }

    public boolean isDownpayment() {
        return isDownpayment;
    }

    public void setDownpayment(boolean downpayment) {
        isDownpayment = downpayment;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public double getDentalTotalAmount() {
        return dentalTotalAmount;
    }

    public void setDentalTotalAmount(double dentalTotalAmount) {
        this.dentalTotalAmount = dentalTotalAmount;
    }

    public double getDentalBalance() {
        return dentalBalance;
    }

    public void setDentalBalance(double dentalBalance) {
        this.dentalBalance = dentalBalance;
    }

    @Override
    public String toString() {
        return "DentalOperation{" + '\n' +
                "dentalDesc='" + dentalDesc + '\n' +
                ", dentalDate='" + dentalDate + '\n' +
                ", dentalAmount=" + dentalAmount + '\n' +
                ", isFullyPaid=" + isDownpayment +
                '}';
    }
}
