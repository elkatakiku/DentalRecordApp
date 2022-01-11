package com.bsit_three_c.dentalrecordapp.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

public class Appointment implements Parcelable {

    private String uid;
    private Person patient;
    private Procedure procedure;
    private ProgressNote progressNote;
    private Date dateTime;
    private String comments;
    private long timeStamp;
    private boolean isDone;
    private boolean isPassed;

    public Appointment() {
    }

    public Appointment(String uid, Person patient, Procedure procedure, Date dateTime, String comments) {
        this.uid = uid;
        this.patient = patient;
        this.procedure = procedure;
        this.dateTime = dateTime;
        this.comments = comments;
        this.timeStamp = dateTime.getTime();
    }

    protected Appointment(Parcel in) {
        uid = in.readString();
        patient = in.readParcelable(Person.class.getClassLoader());
        procedure = in.readParcelable(Procedure.class.getClassLoader());
        comments = in.readString();
        timeStamp = in.readLong();
        isDone = in.readByte() != 0;
        isPassed = in.readByte() != 0;
        dateTime = new Date(in.readLong());
    }

    public static final Creator<Appointment> CREATOR = new Creator<Appointment>() {
        @Override
        public Appointment createFromParcel(Parcel in) {
            return new Appointment(in);
        }

        @Override
        public Appointment[] newArray(int size) {
            return new Appointment[size];
        }
    };

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public Person getPatient() {
        return patient;
    }

    public void setPatient(Person patient) {
        this.patient = patient;
    }

    public Procedure getProcedure() {
        return procedure;
    }

    public void setProcedure(Procedure procedure) {
        this.procedure = procedure;
    }

    public Date getDateTime() {
        return dateTime;
    }

    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public boolean isDone() {
        return isDone;
    }

    public void setDone(boolean done) {
        isDone = done;
    }

    public boolean isPassed() {
        return isPassed;
    }

    public void setPassed(boolean passed) {
        isPassed = passed;
    }

    @Override
    public String toString() {
        return "\nAppointment{" +
                "\nuid='" + uid + '\'' +
                "\npatient=" + patient +
                "\nprocedure=" + procedure +
                "\ndateTime=" + dateTime +
                "\ncomments='" + comments + '\'' +
                "\ntimeStamp=" + timeStamp +
                "\nisDone=" + isDone +
                "\nisPassed=" + isPassed +
                "\n}";
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(uid);
        dest.writeParcelable(patient, flags);
        dest.writeParcelable(procedure, flags);
        dest.writeString(comments);
        dest.writeLong(timeStamp);
        dest.writeByte((byte) (isDone ? 1 : 0));
        dest.writeByte((byte) (isPassed ? 1 : 0));
        dest.writeLong(dateTime.getTime());
    }
}
