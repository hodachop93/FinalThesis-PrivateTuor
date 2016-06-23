package com.hodachop93.hohoda.model;

import com.google.gson.annotations.SerializedName;

public class HohodaResponseBody {

    @SerializedName("user_id")
    private String userID;

    public String getUserID() {
        return userID;
    }
}
