package com.hodachop93.hohoda.api.notification;

import com.hodachop93.hohoda.api.ApiClient;
import com.hodachop93.hohoda.model.HohodaResponse;
import com.hodachop93.hohoda.model.Notification;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by hopho on 06/05/2016.
 */
public class NotificationApi {
    private static NotificationApiInterface apiInterface;

    public static NotificationApiInterface getInstance(){
        if (apiInterface == null){
            apiInterface = ApiClient.getRetrofit().create(NotificationApiInterface.class);
        }
        return apiInterface;
    }

    public interface NotificationApiInterface{
        @FormUrlEncoded
        @POST("secure/notification/getAllNotifications")
        Call<HohodaResponse<List<Notification>>> getAllNotifications(@Field("time_next") long timeNext);

        @FormUrlEncoded
        @POST("secure/notification/updateStatus")
        Call<HohodaResponse<Object>> updateNotificationStatus(@Field("notification_id") String notificationId,
                                                              @Field("status") int status);
    }
}