package com.bsit_three_c.dentalrecordapp.data.model;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.widget.ImageView;

import com.bsit_three_c.dentalrecordapp.R;
import com.bsit_three_c.dentalrecordapp.util.Checker;
import com.bsit_three_c.dentalrecordapp.util.UIUtil;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.Date;
import java.util.List;

public class Employee extends Person implements Parcelable {
    public static final String EMPLOYEE_KEY = "EMPLOYEE_KEY";

    private String displayImage;
    private int jobTitle;
    private String address2ndPart;
    private String accountUid;
    private String emergencyContactUid;

    public Employee() {
    }

    public Employee(String uid,
                    String displayImage,
                    String firstname,
                    String lastname,
                    String middleInitial,
                    String suffix,
                    int jobTitle,
                    String dateOfBirth,
                    int age,
                    List<String> phoneNumber,
                    String email,
                    String address1stPart,
                    String address2ndPart,
                    int civilStatus,
                    String accountUid,
                    String emergencyContactUid,
                    Date lastUpdated) {

        super(
                uid,
                firstname,
                lastname,
                middleInitial,
                suffix,
                dateOfBirth,
                phoneNumber,
                address1stPart,
                civilStatus,
                age,
                lastUpdated,
                email
        );

        this.displayImage = displayImage;
        this.jobTitle = jobTitle;
        this.address2ndPart = UIUtil.capitalize(address2ndPart);
        this.accountUid = accountUid;
        this.emergencyContactUid = emergencyContactUid;
    }

    public Employee(String uid,
                    String displayImage,
                    String firstname,
                    String lastname,
                    String middleInitial,
                    String suffix,
                    int jobTitle,
                    String dateOfBirth,
                    int age,
                    List<String> phoneNumber,
                    String email,
                    String address1stPart,
                    String address2ndPart,
                    int civilStatus,
                    Date lastUpdated) {

        this(
                uid,
                displayImage,
                firstname,
                lastname,
                middleInitial,
                suffix,
                jobTitle,
                dateOfBirth,
                age,
                phoneNumber,
                email,
                address1stPart,
                address2ndPart,
                civilStatus,
                "",
                "",
                lastUpdated
        );
    }

    public Employee(String displayImage,
                    String firstname,
                    String lastname,
                    String middleInitial,
                    String suffix,
                    int jobTitle,
                    String dateOfBirth,
                    int age,
                    List<String> phoneNumber,
                    String email,
                    String address1stPart,
                    String address2ndPart,
                    int civilStatus) {

        this(
                null,
                displayImage,
                firstname,
                lastname,
                middleInitial,
                suffix,
                jobTitle,
                dateOfBirth,
                age,
                phoneNumber,
                email,
                address1stPart,
                address2ndPart,
                civilStatus,
                "",
                "",
                new Date()
        );
    }

    public Employee(
                    String firstname,
                    String lastname,
                    String middleInitial,
                    String suffix,
                    int jobTitle,
                    String dateOfBirth,
                    int age,
                    List<String> phoneNumber,
                    String email,
                    String address1stPart,
                    String address2ndPart,
                    int civilStatus) {

        this(
                "",
                "",
                firstname,
                lastname,
                middleInitial,
                suffix,
                jobTitle,
                dateOfBirth,
                age,
                phoneNumber,
                email,
                address1stPart,
                address2ndPart,
                civilStatus,
                new Date()
        );
    }

    protected Employee(Parcel in) {
        uid = in.readString();
        firstname = in.readString();
        lastname = in.readString();
        middleInitial = in.readString();
        suffix = in.readString();
        dateOfBirth = in.readString();
        phoneNumber = in.createStringArrayList();
        address = in.readString();
        civilStatus = in.readInt();
        age = in.readInt();

        displayImage = in.readString();
        jobTitle = in.readInt();
        address2ndPart = in.readString();
        email = in.readString();
        accountUid = in.readString();
        emergencyContactUid = in.readString();
    }

    public static final Creator<Employee> CREATOR = new Creator<Employee>() {
        @Override
        public Employee createFromParcel(Parcel in) {
            return new Employee(in);
        }

        @Override
        public Employee[] newArray(int size) {
            return new Employee[size];
        }
    };

    public String getDisplayImage() {
        return displayImage;
    }

    public void setDisplayImage(String displayImage) {
        this.displayImage = displayImage;
    }

    public void loadDisplayImage(Context context, ImageView imageView) {
        Glide
                .with(context)
                .load(Uri.parse(displayImage))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imageView);

        imageView.setImageTintList(null);
        imageView.setBackgroundColor(Color.TRANSPARENT);
    }

    public int getJobTitle() {
        return jobTitle;
    }

    public String getJobTitle(Resources resources){
        if (Checker.isNotDefault(civilStatus)) {
            return resources.getStringArray(R.array.job_titles)[jobTitle];
        }

        return Checker.NOT_AVAILABLE;
    }

    public void setJobTitle(int jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getAddress2ndPart() {
        return address2ndPart;
    }

    public void setAddress2ndPart(String address2ndPart) {
        this.address2ndPart = address2ndPart;
    }

    public String getFullAddress() {
        return address + ' ' + address2ndPart;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAccountUid() {
        return accountUid;
    }

    public void setAccountUid(String accountUid) {
        this.accountUid = accountUid;
    }

    public String getEmergencyContactUid() {
        return emergencyContactUid;
    }

    public void setEmergencyContactUid(String emergencyContactUid) {
        this.emergencyContactUid = emergencyContactUid;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(uid);
        dest.writeString(firstname);
        dest.writeString(lastname);
        dest.writeString(middleInitial);
        dest.writeString(suffix);
        dest.writeString(dateOfBirth);
        dest.writeStringList(phoneNumber);
        dest.writeString(address);
        dest.writeInt(civilStatus);
        dest.writeInt(age);

        dest.writeString(displayImage);
        dest.writeInt(jobTitle);
        dest.writeString(address2ndPart);
        dest.writeString(email);
        dest.writeString(accountUid);
        dest.writeString(emergencyContactUid);
    }

    @Override
    public String toString() {
        return "\nEmployee{" +
                "\ndisplayImage='" + displayImage + '\'' +
                "\njobTitle='" + jobTitle + '\'' +
                "\naddress2ndPart='" + address2ndPart + '\'' +
                "\nemail='" + email + '\'' +
                "\naccountUid='" + accountUid + '\'' +
                "\nemergencyContactUid='" + emergencyContactUid + '\'' +
                "\nuid='" + uid + '\'' +
                "\nfirstname='" + firstname + '\'' +
                "\nlastname='" + lastname + '\'' +
                "\nmiddleInitial='" + middleInitial + '\'' +
                "\nsuffix='" + suffix + '\'' +
                "\ndateOfBirth='" + dateOfBirth + '\'' +
                "\nphoneNumber=" + phoneNumber +
                "\naddress='" + address + '\'' +
                "\ncivilStatus=" + civilStatus +
                "\nage=" + age +
                "\nlastUpdated=" + lastUpdated +
                "}\n";
    }
}
