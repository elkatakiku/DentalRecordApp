package com.bsit_three_c.dentalrecordapp.data.model;

public class DentalService {

    private String serviceUID;
    protected String title;
    protected String description;

    public DentalService(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public DentalService(String title) {
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

    public String getServiceUID() {
        return serviceUID;
    }

    @Override
    public String toString() {
        return "DentalService{" +
                "\nserviceUID='" + serviceUID + '\'' +
                "\ntitle='" + title + '\'' +
                "\ndescription='" + description + '\'' +
                '}';
    }
}
