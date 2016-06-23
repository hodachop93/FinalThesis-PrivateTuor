package com.hodachop93.hohoda.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by hopho on 06/05/2016.
 */
public class Notification implements Parcelable{
    @SerializedName("notification_id")
    private String notificationId;
    @SerializedName("to_user_id")
    private String toUserId;
    @SerializedName("created_at")
    private long createdAt;
    private int status;
    @SerializedName("noti_type")
    private int notiType;
    private Course course;
    private Profile profile;

    public static final int NOTI_TYPE_POST_COURSE = 0;
    public static final int NOTI_TYPE_JOIN_COURSE = 1;

    public static final int STATUS_UNREAD = 0;
    public static final int STATUS_READ = 1;

    protected Notification(Parcel in) {
        notificationId = in.readString();
        toUserId = in.readString();
        createdAt = in.readLong();
        status = in.readInt();
        notiType = in.readInt();
        course = in.readParcelable(Course.class.getClassLoader());
        profile = in.readParcelable(Profile.class.getClassLoader());
    }

    public static final Creator<Notification> CREATOR = new Creator<Notification>() {
        @Override
        public Notification createFromParcel(Parcel in) {
            return new Notification(in);
        }

        @Override
        public Notification[] newArray(int size) {
            return new Notification[size];
        }
    };

    public String getNotificationId() {
        return notificationId;
    }

    public String getToUserId() {
        return toUserId;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public int getStatus() {
        return status;
    }

    public int getNotiType() {
        return notiType;
    }

    public Course getCourse() {
        return course;
    }

    public Profile getProfile() {
        return profile;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(notificationId);
        dest.writeString(toUserId);
        dest.writeLong(createdAt);
        dest.writeInt(status);
        dest.writeInt(notiType);
        dest.writeParcelable(course, flags);
        dest.writeParcelable(profile, flags);
    }
}
