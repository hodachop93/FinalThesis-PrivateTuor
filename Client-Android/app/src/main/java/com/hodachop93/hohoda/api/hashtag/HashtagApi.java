package com.hodachop93.hohoda.api.hashtag;

import com.hodachop93.hohoda.api.ApiClient;
import com.hodachop93.hohoda.model.HashTag;
import com.hodachop93.hohoda.model.HohodaResponse;
import com.hodachop93.hohoda.model.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by Hop on 08/03/2016.
 */
public class HashtagApi {
    private static HashtagApiInterface apiInterface;

    public static HashtagApiInterface getInstance() {
        if (apiInterface == null) {
            apiInterface = ApiClient.getRetrofit().create(HashtagApiInterface.class);
        }
        return apiInterface;
    }

    public interface HashtagApiInterface {
        @GET("hashtag/popular")
        Call<HohodaResponse<List<HashTag>>> getPopularHashtag();
    }
}
