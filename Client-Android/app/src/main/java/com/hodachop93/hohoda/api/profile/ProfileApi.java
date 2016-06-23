package com.hodachop93.hohoda.api.profile;

import com.hodachop93.hohoda.api.ApiClient;
import com.hodachop93.hohoda.model.HohodaResponse;
import com.hodachop93.hohoda.model.Profile;
import com.hodachop93.hohoda.model.Review;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by hopho on 27/04/2016.
 */
public class ProfileApi {
    private static ProfileApiInterface apiInterface;

    public static ProfileApiInterface getInstance() {
        if (apiInterface == null) {
            apiInterface = ApiClient.getRetrofit().create(ProfileApiInterface.class);
        }
        return apiInterface;
    }

    public interface ProfileApiInterface {
        @POST("secure/user/updateProfile")
        Call<HohodaResponse<Object>> updateUserProfile(@Body UpdateUserProfileRequestBody body);

        @POST("secure/user/profile")
        Call<HohodaResponse<Profile>> getUserProfile(@Body GetProfileRequestBody body);

        @POST("secure/tutor/mylocal")
        Call<HohodaResponse<List<Profile>>> getLocalTutor(@Body TutorRequestBody body);

        @POST("secure/tutor/myinterest")
        Call<HohodaResponse<List<Profile>>> getTutorByUserInterest(@Body TutorRequestBody body);

        @FormUrlEncoded
        @POST("secure/tutor/searchByHashtag")
        Call<HohodaResponse<List<Profile>>> searchTutorsByHashtags(@Field("hashtags") List<String> hashtags,
                                                                   @Field("time_next") long timeNext);

        @FormUrlEncoded
        @POST("secure/user/getAllReview")
        Call<HohodaResponse<List<Review>>> getProfileReview(@Field("user_id") String userId,
                                                            @Field("time_next") long timeNext);

        @FormUrlEncoded
        @POST("secure/review/add")
        Call<HohodaResponse<Object>> addReview(@Field("to_user_id") String toUserId,
                                               @Field("rate") float rate,
                                               @Field("comment") String comment);
    }
}
