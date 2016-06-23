package com.hodachop93.hohoda.model;

import android.os.Parcel;
import android.os.Parcelable;

public class HohodaNotification implements Parcelable {

    public static final int NOTIFICATION_TYPE_NEW_CANDIDATE = 1;
    public static final int NOTIFICATION_TYPE_NEW_JOB = 2;

    public static final Creator<HohodaNotification> CREATOR = new Creator<HohodaNotification>() {
        @Override
        public HohodaNotification createFromParcel(Parcel in) {
            return new HohodaNotification(in);
        }

        @Override
        public HohodaNotification[] newArray(int size) {
            return new HohodaNotification[size];
        }
    };

    private String userName;
    private String avatarUrl;
    private double timeInterval;
    private String content;
    private String hashTag;

    public HohodaNotification(String userName, String avatarUrl, double timeInterval, String content, String hashTag) {
        this.userName = userName;
        this.avatarUrl = avatarUrl;
        this.timeInterval = timeInterval;
        this.content = content;
        this.hashTag = hashTag;
    }

    protected HohodaNotification(Parcel in) {
        userName = in.readString();
        avatarUrl = in.readString();
        timeInterval = in.readDouble();
        content = in.readString();
        hashTag = in.readString();
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public double getTimeInterval() {
        return timeInterval;
    }

    public void setTimeInterval(double timeInterval) {
        this.timeInterval = timeInterval;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getHashTag() {
        return hashTag;
    }

    public void setHashTag(String hashTag) {
        this.hashTag = hashTag;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(userName);
        dest.writeString(avatarUrl);
        dest.writeDouble(timeInterval);
        dest.writeString(content);
        dest.writeString(hashTag);
    }
}
