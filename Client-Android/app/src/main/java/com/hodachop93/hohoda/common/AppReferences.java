package com.hodachop93.hohoda.common;

import android.content.Context;
import android.content.SharedPreferences;

import com.hodachop93.hohoda.HohodaApplication;

public class AppReferences {

    private static final String KEY_FIRST_LOAD = "APP_FIRST_LOAD";
    private static final String KEY_USER_ID = "USER_ID";
    private static final String KEY_USER_TOKEN_ID = "USER_TOKEN_ID";
    private static final String KEY_DEVICE_TOKEN = "USER_DEVICE_TOKEN";
    private static final String KEY_DEVICE_TOKEN_SAVED = "USER_DEVICE_TOKEN_SAVED";

    private static Context context = HohodaApplication.getInstance();
    private static SharedPreferences mSharedPreferences = context.getSharedPreferences("APP_REFERENCES", Context.MODE_PRIVATE);

    /**
     * Check if app is loaded for the first time
     *
     * @return true if app is first loaded, otherwise it returns false
     */
    public static boolean isAppFirstLoad() {
        boolean isAppFirstLoad = mSharedPreferences.getBoolean(KEY_FIRST_LOAD, true);
        mSharedPreferences.edit().putBoolean(KEY_FIRST_LOAD, false).apply();
        return isAppFirstLoad;
    }

    public static String getUserID() {
        return mSharedPreferences.getString(KEY_USER_ID, "");
    }

    public static void setUserID(String userID) {
        mSharedPreferences.edit().putString(KEY_USER_ID, userID).apply();
    }

    public static void setUserTokenID(String tokenID) {
        mSharedPreferences.edit().putString(KEY_USER_TOKEN_ID, tokenID).apply();
    }

    public static String getUserTokenID() {
        return mSharedPreferences.getString(KEY_USER_TOKEN_ID, "");
    }

    public static boolean isUserLoggedIn() {
        return !getUserID().isEmpty() && !getUserTokenID().isEmpty();
    }

    public static void setDeviceToken(String deviceToken) {
        mSharedPreferences.edit().putString(KEY_DEVICE_TOKEN, deviceToken).apply();
    }

    public static String getDeviceToken() {
        return mSharedPreferences.getString(KEY_DEVICE_TOKEN, "");
    }

    public static boolean isDeviceTokenSaved() {
        return mSharedPreferences.getBoolean(KEY_DEVICE_TOKEN_SAVED, false);
    }

    public static void markDeviceTokenSaved() {
        mSharedPreferences.edit().putBoolean(KEY_DEVICE_TOKEN_SAVED, true).apply();
    }

    public static void markDeviceTokenUnsaved() {
        mSharedPreferences.edit().putBoolean(KEY_DEVICE_TOKEN_SAVED, false).apply();
    }
}
