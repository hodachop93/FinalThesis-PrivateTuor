package com.hodachop93.hohoda.utils;

import android.os.Handler;
import android.support.annotation.Nullable;

import com.hodachop93.hohoda.api.profile.GetProfileRequestBody;
import com.hodachop93.hohoda.api.usermanagement.UserManagementApi;
import com.hodachop93.hohoda.common.AppReferences;
import com.hodachop93.hohoda.eventbus.ProfileEventBus;
import com.hodachop93.hohoda.model.HohodaResponse;
import com.hodachop93.hohoda.model.Profile;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Hop on 27/03/2016.
 */
public class ProfileUtils {

    private static Profile profile;
    private static int TRY_AGAIN_TIME = 30;

    public static void fetchProfile() {
        if (AppReferences.isUserLoggedIn()) {
            GetProfileRequestBody body = new GetProfileRequestBody();
            body.setUserId(AppReferences.getUserID());

            Call<HohodaResponse<Profile>> call = UserManagementApi.getInstance().getUserProfile(body);
            call.enqueue(new Callback<HohodaResponse<Profile>>() {
                @Override
                public void onResponse(Call<HohodaResponse<Profile>> call, Response<HohodaResponse<Profile>> response) {
                    if (response.isSuccess()) {
                        profile = response.body().getBody();
                        ProfileEventBus.getInstance().post(new ProfileEventBus.UpdateProfileEvent(profile));
                    }
                }

                @Override
                public void onFailure(Call<HohodaResponse<Profile>> call, Throwable t) {
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            fetchProfile();
                        }
                    }, TRY_AGAIN_TIME * 1000);
                }
            });
        }
    }

    @Nullable
    public static Profile getCurrentProfile() {
        return profile;
    }

    public static boolean isEmptyInterestedHashtags() {
        return profile != null && (profile.getInterestedHashtags() == null || profile.getInterestedHashtags().isEmpty());
    }
}
