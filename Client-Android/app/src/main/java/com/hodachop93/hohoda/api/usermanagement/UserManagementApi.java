package com.hodachop93.hohoda.api.usermanagement;

import com.hodachop93.hohoda.api.ApiClient;
import com.hodachop93.hohoda.api.profile.GetProfileRequestBody;
import com.hodachop93.hohoda.api.profile.UpdateUserProfileRequestBody;
import com.hodachop93.hohoda.model.HohodaResponse;
import com.hodachop93.hohoda.model.Profile;
import com.hodachop93.hohoda.model.User;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;


/**
 * Created by Hop on 07/03/2016.
 */
public class UserManagementApi {
    private static UserManagementApiInterface apiInterface;

    public static UserManagementApiInterface getInstance() {
        if (apiInterface == null) {
            apiInterface = ApiClient.getRetrofit().create(UserManagementApiInterface.class);
        }
        return apiInterface;
    }

    public interface UserManagementApiInterface {
        @POST("user/registration")
        Call<HohodaResponse<User>> register(@Body RegisterRequestBody body);

        @POST("user/signin")
        Call<HohodaResponse<User>> signIn(@Body SignInRequestBody body);

        @GET("secure/user/signout")
        Call<HohodaResponse<Object>> signOut();

        @POST("user/activate")
        Call<HohodaResponse<User>> activate(@Body ActivationUserRequestBody body);

        @POST("secure/user/profile")
        Call<HohodaResponse<Profile>> getUserProfile(@Body GetProfileRequestBody body);

        @GET("secure/user/saveDeviceToken")
        Call<HohodaResponse<Object>> saveDeviceToken();

        @POST("user/loginsocialnetwork")
        Call<HohodaResponse<User>> loginSocialNetwork(@Body LoginSocialRequestBody body);
    }


}
