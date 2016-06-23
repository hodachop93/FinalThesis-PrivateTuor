package com.hodachop93.hohoda.utils;

import android.app.ActivityManager;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.os.Build;

import com.hodachop93.hohoda.HohodaApplication;
import com.hodachop93.hohoda.api.notification.NotificationApi;
import com.hodachop93.hohoda.api.usermanagement.UserManagementApi;
import com.hodachop93.hohoda.common.AppReferences;
import com.hodachop93.hohoda.model.HohodaResponse;
import com.hodachop93.hohoda.model.Notification;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Hop Dac Ho on 02/05/2016.
 */
public class NotificationUtils {
    public static void saveDeviceToken() {
        Call<HohodaResponse<Object>> call = UserManagementApi.getInstance().saveDeviceToken();
        call.enqueue(new Callback<HohodaResponse<Object>>() {
            @Override
            public void onResponse(Call<HohodaResponse<Object>> call, Response<HohodaResponse<Object>> response) {
                AppReferences.markDeviceTokenSaved();
            }

            @Override
            public void onFailure(Call<HohodaResponse<Object>> call, Throwable t) {
                AppReferences.markDeviceTokenUnsaved();
            }
        });
    }

    public static boolean isAppIsInBackground(Context context) {
        boolean isInBackground = true;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (String activeProcess : processInfo.pkgList) {
                        if (activeProcess.equals(context.getPackageName())) {
                            isInBackground = false;
                        }
                    }
                }
            }
        } else {
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
            ComponentName componentInfo = taskInfo.get(0).topActivity;
            if (componentInfo.getPackageName().equals(context.getPackageName())) {
                isInBackground = false;
            }
        }

        return isInBackground;
    }

    public static void updateNotificationStatus(String notificationId) {
        Callback<HohodaResponse<Object>> callback = new Callback<HohodaResponse<Object>>() {
            @Override
            public void onResponse(Call<HohodaResponse<Object>> call, Response<HohodaResponse<Object>> response) {

            }

            @Override
            public void onFailure(Call<HohodaResponse<Object>> call, Throwable t) {

            }
        };

        Call<HohodaResponse<Object>> call = NotificationApi.getInstance().updateNotificationStatus(notificationId, Notification.STATUS_READ);
        call.enqueue(callback);
    }

    public static void clearAllNotifications() {
        Context context = HohodaApplication.getInstance();
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }
}
