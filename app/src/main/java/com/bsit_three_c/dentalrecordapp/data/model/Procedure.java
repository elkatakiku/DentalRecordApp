package com.bsit_three_c.dentalrecordapp.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class Procedure implements Parcelable {

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

    public Procedure(String uid, String dentalDate, List<String> serviceIds) {
        this(uid, serviceIds, null, dentalDate, -1, false, -1);
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

    protected Procedure(Parcel in) {
        uid = in.readString();
        dentalDesc = in.readString();
        dentalDate = in.readString();
        dentalTotalAmount = in.readDouble();
        isDownpayment = in.readByte() != 0;
        dentalBalance = in.readDouble();
        serviceIds = in.createStringArrayList();
        paymentKeys = in.createStringArrayList();
    }

    public static final Creator<Procedure> CREATOR = new Creator<Procedure>() {
        @Override
        public Procedure createFromParcel(Parcel in) {
            return new Procedure(in);
        }

        @Override
        public Procedure[] newArray(int size) {
            return new Procedure[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(uid);
        dest.writeString(dentalDesc);
        dest.writeString(dentalDate);
        dest.writeDouble(dentalTotalAmount);
        dest.writeByte((byte) (isDownpayment ? 1 : 0));
        dest.writeDouble(dentalBalance);
        dest.writeStringList(serviceIds);
        dest.writeStringList(paymentKeys);
    }
}
