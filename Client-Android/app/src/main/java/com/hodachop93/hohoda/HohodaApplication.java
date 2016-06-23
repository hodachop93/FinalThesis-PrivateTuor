package com.hodachop93.hohoda;

import android.app.Application;

import com.facebook.FacebookSdk;
import com.hodachop93.hohoda.utils.Utils;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by Hop on 03/03/2016.
 */
public class HohodaApplication extends Application {
    private static HohodaApplication instance;

    public static HohodaApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                        .setDefaultFontPath("fonts/Roboto-Regular.ttf")
                        .setFontAttrId(R.attr.fontPath)
                        .build()
        );

        FacebookSdk.sdkInitialize(this);
        Utils.printKeyHashes(this);
    }
}
