package com.hodachop93.hohoda.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class User implements Parcelable {
    public static final int ACCOUNT_TYPE_NORMAL = 0;
    public static final int ACCOUNT_TYPE_FACEBOOK = 1;
    @SerializedName("user_id")
    private String userID;
    @SerializedName("token_id")
    private String tokenID;

    protected User(Parcel in) {
        userID = in.readString();
        tokenID = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public String getUserID() {
        return userID;
    }

    public String getTokenId() {
        return tokenID;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(userID);
        dest.writeString(tokenID);
    }
}
