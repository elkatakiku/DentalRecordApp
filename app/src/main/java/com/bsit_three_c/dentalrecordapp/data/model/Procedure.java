package com.bsit_three_c.dentalrecordapp.data.model;

import java.util.ArrayList;

public class Procedure {

    private String uid;
    private String dentalDesc;
    private String dentalDate;
    private double dentalTotalAmount;
    private boolean isDownpayment;
    private double dentalBalance;
    private ArrayList<Integer> service;

    private ArrayList<String> paymentKeys;

    public Procedure() { }

    //  This constructor is used in getting dental operations
    public Procedure(String uid, ArrayList<Integer> service, String dentalDesc, String dentalDate, double dentalTotalAmount,
                     boolean isDownpayment, double dentalBalance, ArrayList<String> paymentKeys) {
        this(uid, service, dentalDesc, dentalDate, dentalTotalAmount, isDownpayment, dentalBalance);
        this.paymentKeys = paymentKeys;
    }

    //  This constructor is used in creating new dental operations
    public Procedure(String uid, ArrayList<Integer> service, String dentalDesc, String dentalDate, double dentalTotalAmount,
                     boolean isDownpayment, double dentalBalance) {
        this.uid = uid;
        this.service = service;
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

    public ArrayList<Integer> getService() {
        return service;
    }

    public void setService(ArrayList<Integer> service) {
        this.service = service;
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
        return "Procedure{\n" +
                "uid='" + uid +
                "\n, dentalDesc='" + dentalDesc +
                "\n, dentalDate='" + dentalDate +
                "\n, isDownpayment=" + isDownpayment +
                "\n, dentalTotalAmount=" + dentalTotalAmount +
                "\n, dentalBalance=" + dentalBalance +
                "\n}";
    }
}
