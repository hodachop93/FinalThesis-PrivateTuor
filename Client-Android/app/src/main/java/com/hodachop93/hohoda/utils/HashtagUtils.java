package com.hodachop93.hohoda.utils;

import com.hodachop93.hohoda.api.hashtag.HashtagApi;
import com.hodachop93.hohoda.model.HashTag;
import com.hodachop93.hohoda.model.HohodaResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Hop Dac Ho on 07/05/2016.
 */
public class HashtagUtils {
    private static List<HashTag> mHashtagList;

    public static void fetchPopularHashtag() {
        Callback<HohodaResponse<List<HashTag>>> callback = new Callback<HohodaResponse<List<HashTag>>>() {
            @Override
            public void onResponse(Call<HohodaResponse<List<HashTag>>> call, Response<HohodaResponse<List<HashTag>>> response) {
                if (response.isSuccess()) {
                    String status = response.body().getStatus();
                    if ("0".equals(status)) {
                        if (mHashtagList == null) {
                            mHashtagList = response.body().getBody();
                        }
                        mHashtagList.clear();
                        mHashtagList.addAll(response.body().getBody());
                    } else {
                    }
                } else {
                }
            }

            @Override
            public void onFailure(Call<HohodaResponse<List<HashTag>>> call, Throwable t) {
            }
        };

        Call<HohodaResponse<List<HashTag>>> call = HashtagApi.getInstance().getPopularHashtag();
        call.enqueue(callback);
    }

    public static List<HashTag> getPopularHashtags() {
        return mHashtagList;
    }

    public static List<String> getNamePopularHashtags() {
        List<String> list = null;
        if (mHashtagList != null) {
            list = new ArrayList<>();
            for (HashTag item : mHashtagList) {
                list.add(item.getName());
            }
        }
        return list;
    }
}
