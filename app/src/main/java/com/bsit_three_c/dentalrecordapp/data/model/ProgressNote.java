package com.bsit_three_c.dentalrecordapp.data.model;

import android.os.Parcel;
import android.os.Parcelable;

public class ProgressNote implements Parcelable {

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

    protected ProgressNote(Parcel in) {
        uid = in.readString();
        date = in.readString();
        description = in.readString();
        if (in.readByte() == 0) {
            amount = null;
        } else {
            amount = in.readDouble();
        }
    }

    public static final Creator<ProgressNote> CREATOR = new Creator<ProgressNote>() {
        @Override
        public ProgressNote createFromParcel(Parcel in) {
            return new ProgressNote(in);
        }

        @Override
        public ProgressNote[] newArray(int size) {
            return new ProgressNote[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(uid);
        dest.writeString(date);
        dest.writeString(description);
        if (amount == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(amount);
        }
    }
}
