package com.hodachop93.hohoda.api.profile;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Hop Dac Ho on 28/04/2016.
 */
public class GetProfileRequestBody {
    @SerializedName("user_id")
    private String userId;

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
