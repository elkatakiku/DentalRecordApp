package com.bsit_three_c.dentalrecordapp.data.model;

import android.content.res.Resources;
import android.os.Parcel;
import android.os.Parcelable;

import com.bsit_three_c.dentalrecordapp.R;
import com.bsit_three_c.dentalrecordapp.data.repository.BaseRepository;
import com.bsit_three_c.dentalrecordapp.util.Checker;

import java.util.List;

public class Clinic implements Parcelable {

    private String uid;
    private String name;
    private String location;
    private List<String> contact;
    private int startDay;
    private int endDay;
    private long startTime;
    private long endTime;

    public Clinic() {
    }

    public Clinic(String uid, String name, String location, List<String> contact, int startDay, int endDay, long startTime, long endTime) {
        this.uid = uid;
        this.name = name;
        this.location = location;
        this.contact = contact;
        this.startDay = startDay;
        this.endDay = endDay;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    protected Clinic(Parcel in) {
        uid = in.readString();
        name = in.readString();
        location = in.readString();
        contact = in.createStringArrayList();
        startDay = in.readInt();
        endDay = in.readInt();
        startTime = in.readLong();
        endTime = in.readLong();
    }

    public static final Creator<Clinic> CREATOR = new Creator<Clinic>() {
        @Override
        public Clinic createFromParcel(Parcel in) {
            return new Clinic(in);
        }

        @Override
        public Clinic[] newArray(int size) {
            return new Clinic[size];
        }
    };

    public String getUid() {
        return uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public List<String> getContact() {
        return contact;
    }

    public String getContactNumber() {
        if (contact != null && contact.size() > 0) {
            StringBuilder builder = new StringBuilder();

            if (contact.get(0).equals(BaseRepository.NEW_PATIENT)) {
                builder.append(Checker.NOT_AVAILABLE);
            } else {
                for (String number : contact) {
                    builder.append(number).append("\n");
                }
                builder.deleteCharAt(builder.length()-1);
            }

            return builder.toString();
        }
        else
            return Checker.NOT_AVAILABLE;
    }


    public void setContact(List<String> contact) {
        this.contact = contact;
    }

    public int getStartDay() {
        return startDay;
    }

    public String getStartDay(Resources resources) {
        if (Checker.isNotDefault(startDay)) {
            return resources.getStringArray(R.array.days_in_week)[startDay];
        }
        return Checker.NOT_AVAILABLE;
    }

    public void setStartDay(int startDay) {
        this.startDay = startDay;
    }

    public int getEndDay() {
        return endDay;
    }

    public String getEndDay(Resources resources) {
        if (Checker.isNotDefault(endDay)) {
            return resources.getStringArray(R.array.days_in_week)[endDay];
        }
        return Checker.NOT_AVAILABLE;
    }

    public void setEndDay(int endDay) {
        this.endDay = endDay;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(uid);
        dest.writeString(name);
        dest.writeString(location);
        dest.writeStringList(contact);
        dest.writeInt(startDay);
        dest.writeInt(endDay);
        dest.writeLong(startTime);
        dest.writeLong(endTime);
    }
}
