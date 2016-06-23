package com.hodachop93.hohoda.api.usermanagement;

import com.google.gson.annotations.SerializedName;
import com.hodachop93.hohoda.utils.EncodingUtils;

/**
 * Created by Hop on 08/03/2016.
 */
public class SignInRequestBody {
    @SerializedName("account_id")
    private String accountId;
    private String password;

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public void setPassword(String password) {
        this.password = EncodingUtils.encodePassword(password);
    }
}
