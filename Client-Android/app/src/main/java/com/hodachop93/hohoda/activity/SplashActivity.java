package com.hodachop93.hohoda.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.hodachop93.hohoda.R;
import com.hodachop93.hohoda.common.AppReferences;
import com.hodachop93.hohoda.gcm.RegistrationIntentService;

public class SplashActivity extends BaseActivity {
    // Splash screen timer
    private static int SPLASH_TIME_OUT = 1000;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final String TAG = SplashActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        if (checkPlayServices()) {
            registerGCM();
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i;
                if (AppReferences.isUserLoggedIn()) {
                    i = HohodaActivity.getIntent(SplashActivity.this);
                } else {
                    i = SignInActivity.getIntent(SplashActivity.this);
                }
                startActivity(i);
                finish();
            }
        }, SPLASH_TIME_OUT);
    }

    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i(TAG, "This device is not supported. Google Play Services not installed!");
                Toast.makeText(getApplicationContext(), "This device is not supported. Google Play Services not installed!", Toast.LENGTH_LONG).show();
                finish();
            }
            return false;
        }
        return true;
    }

    private void registerGCM() {
        Intent intent = new Intent(this, RegistrationIntentService.class);
        startService(intent);
    }


}
