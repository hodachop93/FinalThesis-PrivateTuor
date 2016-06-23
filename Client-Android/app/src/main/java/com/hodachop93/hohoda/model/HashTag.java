package com.hodachop93.hohoda.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class HashTag implements Parcelable {
//    private static final int DEFAULT_COLOR = ContextCompat.getColor(HohodaApplication.getInstance(), R.color.defaultColorHashtag);
    public static final Creator<HashTag> CREATOR = new Creator<HashTag>() {
        @Override
        public HashTag createFromParcel(Parcel in) {
            return new HashTag(in);
        }

        @Override
        public HashTag[] newArray(int size) {
            return new HashTag[size];
        }
    };
    private String id;
    private String name;
    private String description;
    private String background;
    @SerializedName("created_at")
    private long createdAt;

    public HashTag(String name) {
        this.name = name;
    }

    protected HashTag(Parcel in) {
        id = in.readString();
        name = in.readString();
        description = in.readString();
        background = in.readString();
        createdAt = in.readLong();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBackground() {
        return background;
    }

    public void setBackground(String background) {
        this.background = background;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof HashTag && ((HashTag) o).getName().equalsIgnoreCase(name);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(description);
        dest.writeString(background);
        dest.writeLong(createdAt);
    }
}
