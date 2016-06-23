package com.hodachop93.hohoda.api.usermanagement;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Hop on 08/03/2016.
 */
public class ActivationUserRequestBody {
    @SerializedName("user_id")
    private String userId;
    private String otp;

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }
}
