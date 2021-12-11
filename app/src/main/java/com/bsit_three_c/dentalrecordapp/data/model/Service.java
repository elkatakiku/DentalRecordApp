package com.bsit_three_c.dentalrecordapp.data.model;

public class Service {

    protected String title;
    protected String description;

    public Service(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public Service(String title) {
        this(title, null);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
