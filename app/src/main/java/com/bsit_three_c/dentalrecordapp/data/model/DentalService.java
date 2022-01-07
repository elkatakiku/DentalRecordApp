package com.bsit_three_c.dentalrecordapp.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class DentalService implements Parcelable {

    private String serviceUID;
    private String displayImage;
    protected String title;
    protected String description;
    private ArrayList<String> categories;

    public DentalService() {
    }

    public DentalService(String serviceUID, String displayImage, String title, String description, ArrayList<String> categories) {
        this.serviceUID = serviceUID;
        this.displayImage = displayImage;
        this.title = title;
        this.description = description;
        this.categories = categories;
    }

    public DentalService(String title, String description, ArrayList<String> categories) {
        this.title = title;
        this.description = description;
        this.categories = categories;
    }

    public DentalService(String serviceUID, String title) {
        this(serviceUID, null, title, null, null);
    }

    protected DentalService(Parcel in) {
        serviceUID = in.readString();
        displayImage = in.readString();
        title = in.readString();
        description = in.readString();
        categories = in.createStringArrayList();
    }

    public static final Creator<DentalService> CREATOR = new Creator<DentalService>() {
        @Override
        public DentalService createFromParcel(Parcel in) {
            return new DentalService(in);
        }

        @Override
        public DentalService[] newArray(int size) {
            return new DentalService[size];
        }
    };

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

    public void setServiceUID(String serviceUID) {
        this.serviceUID = serviceUID;
    }

    public String getDisplayImage() {
        return displayImage;
    }

    public void setDisplayImage(String displayImage) {
        this.displayImage = displayImage;
    }

    public ArrayList<String> getCategories() {
        return categories;
    }

    public void setCategories(ArrayList<String> categories) {
        this.categories = categories;
    }

    @Override
    public String toString() {
        return "DentalService{" +
                "\nserviceUID='" + serviceUID + '\'' +
                "\ndisplayImage=" + displayImage +
                "\ntitle='" + title + '\'' +
                "\ndescription='" + description + '\'' +
                "\ncategories=" + categories +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(serviceUID);
        dest.writeString(displayImage);
        dest.writeString(title);
        dest.writeString(description);
        dest.writeStringList(categories);
    }
}
