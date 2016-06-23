package com.hodachop93.hohoda.model;


import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class Candidate implements Parcelable {

    public static final Creator<Candidate> CREATOR = new Creator<Candidate>() {
        @Override
        public Candidate createFromParcel(Parcel in) {
            return new Candidate(in);
        }

        @Override
        public Candidate[] newArray(int size) {
            return new Candidate[size];
        }
    };
    private String id;
    @SerializedName("user_id")
    private String userId;
    private String description;
    @SerializedName("create_at")
    private long createdAt;
    @SerializedName("update_at")
    private long updateAt;
    private Profile profile;

    protected Candidate(Parcel in) {
        id = in.readString();
        description = in.readString();
        createdAt = in.readLong();
        updateAt = in.readLong();
        userId = in.readString();
        profile = in.readParcelable(Profile.class.getClassLoader());
    }

    public String getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public long getUpdateAt() {
        return updateAt;
    }

    public Profile getProfile() {
        return profile;
    }

    public String getUserId() {
        return userId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(description);
        dest.writeLong(createdAt);
        dest.writeLong(updateAt);
        dest.writeString(userId);
        dest.writeParcelable(profile, flags);
    }
}
