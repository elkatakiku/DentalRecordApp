package com.bsit_three_c.dentalrecordapp.data.model;

public class Payment {

    private String uid;
    private String paymentDate;
//    private int modeOfPayment;
    private Double amount;

    public Payment() {
    }

    public Payment(
            String uid,
            Double amount,
//            int modeOfPayment,
            String paymentDate) {
        this.uid = uid;
        this.amount = amount;
//        this.modeOfPayment = modeOfPayment;
        this.paymentDate = paymentDate;
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

//    public int getModeOfPayment() {
//        return modeOfPayment;
//    }

//    public void setModeOfPayment(int modeOfPayment) {
//        this.modeOfPayment = modeOfPayment;
//    }

    public String getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(String paymentDate) {
        this.paymentDate = paymentDate;
    }

    @Override
    public String toString() {
        return "Payment{" +
                "uid='" + uid +
                "\namount=" + amount +
//                "\nmodeOfPayment='" + modeOfPayment +
                "\npaymentDate='" + paymentDate +
                '}';
    }
}