package com.hodachop93.hohoda.api.usermanagement;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Hop Dac Ho on 25/05/2016.
 */
public class LoginSocialRequestBody {
    @SerializedName("social_id")
    private String socialId;
    private String name;
    @SerializedName("avatar_url")
    private String avatarUrl;
    private String email;
    @SerializedName("account_type")
    private int accountType;
    private String gender;

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setSocialId(String socialId) {
        this.socialId = socialId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setAccountType(int accountType) {
        this.accountType = accountType;
    }
}
