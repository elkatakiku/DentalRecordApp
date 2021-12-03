package com.bsit_three_c.dentalrecordapp.data.model;

public class Payment {

    private String uid;
    private String operationUID;
    private Double amount;
    private String modeOfPayment;
    private String paymentDate;

    public Payment(String uid, String operationUID, Double amount, String modeOfPayment, String paymentDate) {
        this.uid = uid;
        this.operationUID = operationUID;
        this.amount = amount;
        this.modeOfPayment = modeOfPayment;
        this.paymentDate = paymentDate;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getOperationUID() {
        return operationUID;
    }

    public void setOperationUID(String operationUID) {
        this.operationUID = operationUID;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getModeOfPayment() {
        return modeOfPayment;
    }

    public void setModeOfPayment(String modeOfPayment) {
        this.modeOfPayment = modeOfPayment;
    }

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
                "\noperationUID='" + operationUID +
                "\namount=" + amount +
                "\nmodeOfPayment='" + modeOfPayment +
                "\npaymentDate='" + paymentDate +
                '}';
    }
}
