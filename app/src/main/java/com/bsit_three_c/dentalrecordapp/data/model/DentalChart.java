package com.bsit_three_c.dentalrecordapp.data.model;

import java.util.Date;

public class DentalChart {

    private String uid;
    private String uri;
    private Date date;

    public DentalChart() {
    }

    public DentalChart(String uid, String uri, Date date) {
        this.uid = uid;
        this.uri = uri;
        this.date = date;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    @Override
    public String toString() {
        return "\nDentalChart{" +
                "\nuid='" + uid + '\'' +
                "\nuri='" + uri + '\'' +
                "\ndate=" + date +
                "\n}";
    }
}
