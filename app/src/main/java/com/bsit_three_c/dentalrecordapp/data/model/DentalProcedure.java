package com.bsit_three_c.dentalrecordapp.data.model;

import java.util.ArrayList;

public class DentalProcedure {

    private String uid;
    private String dentalDesc;
    private String dentalDate;
    private double dentalTotalAmount;
    private boolean isDownpayment;
    private double dentalBalance;

    private ArrayList<String> paymentKeys;

    public DentalProcedure() { }

    //  This constructor is used in getting dental operations
    public DentalProcedure(String uid, String dentalDesc, String dentalDate, double dentalTotalAmount,
                           boolean isDownpayment, double dentalBalance, ArrayList<String> paymentKeys) {
        this(uid, dentalDesc, dentalDate, dentalTotalAmount, isDownpayment, dentalBalance);
        this.paymentKeys = paymentKeys;
    }

    //  This constructor is used in creating new dental operations
    public DentalProcedure(String uid, String dentalDesc, String dentalDate, double dentalTotalAmount,
                           boolean isDownpayment, double dentalBalance) {
        this.uid = uid;
        this.dentalDesc = dentalDesc;
        this.dentalDate = dentalDate;
        this.dentalTotalAmount = dentalTotalAmount;
        this.isDownpayment = isDownpayment;
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

    public ArrayList<String> getPaymentKeys() {
        return paymentKeys;
    }

    public void setPaymentKeys(ArrayList<String> paymentKeys) {
        this.paymentKeys = paymentKeys;
    }

    public void addPaymentKey(String paymentUID) {
        if (paymentKeys == null) this.paymentKeys = new ArrayList<>();
        this.paymentKeys.add(paymentUID);
    }

    @Override
    public String toString() {
        return "DentalProcedure{\n" +
                "uid='" + uid +
                "\n, dentalDesc='" + dentalDesc +
                "\n, dentalDate='" + dentalDate +
                "\n, isDownpayment=" + isDownpayment +
                "\n, dentalTotalAmount=" + dentalTotalAmount +
                "\n, dentalBalance=" + dentalBalance +
                "\n}";
    }
}
