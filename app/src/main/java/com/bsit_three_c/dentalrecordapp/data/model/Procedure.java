package com.bsit_three_c.dentalrecordapp.data.model;

import java.util.ArrayList;
import java.util.List;

public class Procedure {

    private String uid;
    private String dentalDesc;
    private String dentalDate;
    private double dentalTotalAmount;
    private boolean isDownpayment;
    private double dentalBalance;
    private List<String> serviceIds;

    private List<String> paymentKeys;

    public Procedure() { }

    //  This constructor is used in getting dental operations
    public Procedure(String uid,
                     List<String> serviceIds,
                     String dentalDesc,
                     String dentalDate,
                     double dentalTotalAmount,
                     boolean isDownpayment,
                     double dentalBalance,
                     List<String> paymentKeys) {

        this(uid, serviceIds, dentalDesc, dentalDate, dentalTotalAmount, isDownpayment, dentalBalance);
        this.paymentKeys = paymentKeys;
    }

    //  This constructor is used in creating new dental operations
    public Procedure(String uid, List<String> serviceIds, String dentalDesc, String dentalDate, double dentalTotalAmount,
                     boolean isDownpayment, double dentalBalance) {
        this.uid = uid;
        this.serviceIds = serviceIds;
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

    public List<String> getPaymentKeys() {
        return paymentKeys;
    }

    public List<String> getServiceIds() {
        return serviceIds;
    }

    public void setServiceIds(ArrayList<String> serviceIds) {
        this.serviceIds = serviceIds;
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
