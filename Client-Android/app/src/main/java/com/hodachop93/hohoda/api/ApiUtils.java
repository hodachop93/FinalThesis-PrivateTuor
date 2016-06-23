package com.hodachop93.hohoda.api;

import android.os.Build;

import com.hodachop93.hohoda.HohodaApplication;
import com.hodachop93.hohoda.common.AppReferences;
import com.hodachop93.hohoda.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

public class ApiUtils {

    public enum RequestType {
        INITIAL_REQUEST(),
        AUTH_REQUIRED_REQUEST(),
        PUBLIC_REQUEST()
    }

    public static Map<String, String> getBaseRequestHeaders(RequestType requestType) {
        Map<String, String> headers = new HashMap<>();

        if (!(requestType == RequestType.PUBLIC_REQUEST)) {
            headers.put("x-access-token", AppReferences.getUserTokenID());
            headers.put("user_id", AppReferences.getUserID());
        }

        headers.put("sessionID", "");
        headers.put("productVersion", "");
        headers.put("applicationVersion", Build.VERSION.CODENAME);
        headers.put("deviceFingerprint", getDeviceFingerPrint());
        headers.put("timestamp", String.valueOf(System.currentTimeMillis()));
        headers.put("timezone", TimeZone.getDefault().getID());
        headers.put("locale", Utils.getUserCountry(HohodaApplication.getInstance()));
        headers.put("location", getRequestLocation());
        headers.put("applicationInstanceID", "");
        headers.put("messageID", "");
        headers.put("Content-Type", "application/json");
        headers.put("deviceTokenID", AppReferences.getDeviceToken());

        return headers;
    }

    private static String getDeviceFingerPrint() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("oem", "");
            jsonObject.put("modelName", "");
            jsonObject.put("modelNo", "");
            jsonObject.put("os", "Android");
            jsonObject.put("osVersion", Build.VERSION.CODENAME);
            jsonObject.put("handsetId", "");
            jsonObject.put("iccID", "");
            jsonObject.put("screenHeight", "");
            jsonObject.put("screenWidth", "");

            return jsonObject.toString();

        } catch (JSONException e) {
            return null;
        }
    }

    public static String getRequestLocation() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("longitude", "");
            jsonObject.put("latitude", "");
            jsonObject.put("altitude", "");

            return jsonObject.toString();

        } catch (JSONException e) {
            return null;
        }
    }
}
