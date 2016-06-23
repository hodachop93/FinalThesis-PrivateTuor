package com.hodachop93.hohoda.model;

import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.SerializedName;
import com.google.maps.android.clustering.ClusterItem;
import com.hodachop93.hohoda.HohodaApplication;
import com.hodachop93.hohoda.R;

import java.util.List;

public class Profile implements Parcelable, ClusterItem {
    public static final int ACCOUNT_TYPE_NORMAL = 0;
    public static final int ACCOUNT_TYPE_FACEBOOK = 1;
    private static final int MALE = 0;
    private static final int FEMALE = 1;

    @SerializedName("user_id")
    private String userID;
    private String name;
    private String age;
    @SerializedName("rate_average")
    private float rateAverage;
    @SerializedName("avatar_url")
    private String avatarUrl;
    private String email;
    private String address;
    private String infor;
    private int gender;
    @SerializedName("created_at")
    private long createdAt;
    @SerializedName("updated_at")
    private long updatedAt;
    @SerializedName("user_name")
    private String userName;
    @SerializedName("phone")
    private String phoneNumber;
    @SerializedName("interested_hashtags")
    private List<HashTag> interestedHashtags;
    @SerializedName("total_unread_notification")
    private int totalUnreadNotification;
    @SerializedName("total_review")
    private int totalReview;
    @SerializedName("account_type")
    private int accountType;
    private double lat;
    private double lng;
    private transient Drawable avatar;

    protected Profile(Parcel in) {
        userID = in.readString();
        name = in.readString();
        age = in.readString();
        rateAverage = in.readFloat();
        avatarUrl = in.readString();
        email = in.readString();
        address = in.readString();
        infor = in.readString();
        gender = in.readInt();
        createdAt = in.readLong();
        updatedAt = in.readLong();
        userName = in.readString();
        phoneNumber = in.readString();
        interestedHashtags = in.createTypedArrayList(HashTag.CREATOR);
        totalUnreadNotification = in.readInt();
        totalReview = in.readInt();
        accountType = in.readInt();
        lat = in.readDouble();
        lng = in.readDouble();
    }

    public static final Creator<Profile> CREATOR = new Creator<Profile>() {
        @Override
        public Profile createFromParcel(Parcel in) {
            return new Profile(in);
        }

        @Override
        public Profile[] newArray(int size) {
            return new Profile[size];
        }
    };

    public String getUserID() {
        return userID;
    }

    public String getName() {
        return name;
    }

    public String getAge() {
        return age;
    }

    public float getRateAverage() {
        return (float) Math.round(rateAverage * 10) / 10;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public String getEmail() {
        return email;
    }

    public String getAddress() {
        return address;
    }

    public String getInfor() {
        return infor;
    }

    public String getUserName() {
        return userName;
    }

    public List<HashTag> getInterestedHashtags() {
        return interestedHashtags;
    }

    public int getTotalUnreadNotification() {
        return totalUnreadNotification;
    }

    public int getAccountType() {
        return accountType;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getGenderString() {
        if (gender == MALE)
            return HohodaApplication.getInstance().getString(R.string.male);
        else {
            return HohodaApplication.getInstance().getString(R.string.female);
        }
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public int getGenderInt() {
        return gender;
    }

    public int getTotalReview() {
        return totalReview;
    }

    public LatLng getPosition() {
        if (lat != 0 && lng != 0) {
            return new LatLng(lat, lng);
        } else {
            return null;
        }
    }

    public Drawable getAvatar() {
        return avatar;
    }

    public void setAvatar(Drawable avatar) {
        this.avatar = avatar;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(userID);
        dest.writeString(name);
        dest.writeString(age);
        dest.writeFloat(rateAverage);
        dest.writeString(avatarUrl);
        dest.writeString(email);
        dest.writeString(address);
        dest.writeString(infor);
        dest.writeInt(gender);
        dest.writeLong(createdAt);
        dest.writeLong(updatedAt);
        dest.writeString(userName);
        dest.writeString(phoneNumber);
        dest.writeTypedList(interestedHashtags);
        dest.writeInt(totalUnreadNotification);
        dest.writeInt(accountType);
        dest.writeInt(totalReview);
        dest.writeDouble(lat);
        dest.writeDouble(lng);
    }
}
