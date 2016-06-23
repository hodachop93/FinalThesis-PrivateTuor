package com.hodachop93.hohoda.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.SerializedName;
import com.google.maps.android.clustering.ClusterItem;

import java.util.List;

public class Course implements ClusterItem, Parcelable {
    private static double LAT_LNG_ERR = -1;
    public static final int COURSE_PENDING = 0;
    public static final int COURSE_OPENED = 1;
    public static final int COURSE_CLOSED = 2;
    public static final int COURSE_TYPE_PER_MONTH = 0;
    public static final int COURSE_TYPE_PER_A_PERIOD_TIME = 1;

    private String id;
    @SerializedName("owner_id")
    private String ownerId;
    private String price;
    private String title;
    private String description;
    @SerializedName("start_date")
    private long startDate;
    private String duration;
    private double lat = LAT_LNG_ERR;
    private double lng = LAT_LNG_ERR;
    private String address;
    private String city;
    private String country;
    @SerializedName("bid")
    private int bid;
    @SerializedName("created_at")
    private long createdAt;
    @SerializedName("updated_at")
    private long updatedAt;
    private int status;
    private List<HashTag> hashtags;
    @SerializedName("course_type")
    private int courseType;
    private String schedule;
    private Profile profile;
    @SerializedName("is_joined")
    private boolean isJoined;

    protected Course(Parcel in) {
        id = in.readString();
        ownerId = in.readString();
        price = in.readString();
        title = in.readString();
        description = in.readString();
        startDate = in.readLong();
        duration = in.readString();
        lat = in.readDouble();
        lng = in.readDouble();
        address = in.readString();
        city = in.readString();
        country = in.readString();
        bid = in.readInt();
        createdAt = in.readLong();
        updatedAt = in.readLong();
        status = in.readInt();
        hashtags = in.createTypedArrayList(HashTag.CREATOR);
        courseType = in.readInt();
        schedule = in.readString();
        profile = in.readParcelable(Profile.class.getClassLoader());
        isJoined = in.readByte() != 0;
    }

    public static final Creator<Course> CREATOR = new Creator<Course>() {
        @Override
        public Course createFromParcel(Parcel in) {
            return new Course(in);
        }

        @Override
        public Course[] newArray(int size) {
            return new Course[size];
        }
    };

    public String getId() {
        return id;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public String getPrice() {
        return price;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public long getStartDate() {
        return startDate;
    }

    public String getDuration() {
        return duration;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }

    public String getAddress() {
        return address;
    }

    public String getCity() {
        return city;
    }

    public String getCountry() {
        return country;
    }

    public int getBid() {
        return bid;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public List<HashTag> getHashtags() {
        return hashtags;
    }

    @Override
    public LatLng getPosition() {
        if (lat != LAT_LNG_ERR && lng != LAT_LNG_ERR) {
            return new LatLng(lat, lng);
        } else {
            return null;
        }
    }

    public int getStatusInt() {
        return this.status;
    }

    public void setJoined(boolean joined) {
        isJoined = joined;
    }

    public String getStatusString() {
        String result;
        switch (status) {
            case COURSE_PENDING:
                //pending
                result = "PENDING";
                break;
            case COURSE_OPENED:
                //opened
                result = "OPENED";
                break;
            case COURSE_CLOSED:
                //closed
                result = "CLOSED";
                break;
            default:
                result = null;
        }
        return result;
    }

    public String getSchedule() {
        return schedule;
    }

    public int getCourseType() {
        return courseType;
    }

    public Profile getProfile() {
        return profile;
    }

    public boolean isJoined() {
        return isJoined;
    }

    public void setCourseStatus(int status) {
        this.status = status;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(ownerId);
        dest.writeString(price);
        dest.writeString(title);
        dest.writeString(description);
        dest.writeLong(startDate);
        dest.writeString(duration);
        dest.writeDouble(lat);
        dest.writeDouble(lng);
        dest.writeString(address);
        dest.writeString(city);
        dest.writeString(country);
        dest.writeInt(bid);
        dest.writeLong(createdAt);
        dest.writeLong(updatedAt);
        dest.writeInt(status);
        dest.writeTypedList(hashtags);
        dest.writeInt(courseType);
        dest.writeString(schedule);
        dest.writeParcelable(profile, flags);
        dest.writeByte((byte) (isJoined ? 1 : 0));
    }
}
