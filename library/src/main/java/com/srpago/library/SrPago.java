package com.srpago.library;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.srpago.library.model.Card;
import com.srpago.library.services.LocationUpdatePlayServicesService;
import com.srpago.library.services.SPTokenService;


/**
 * Created by Rodolfo on 22/06/2017.
 */

public class SrPago {
    private Context context;
    private static SrPago instance;
    private boolean liveMode = false;
    private String publishableKey;

    public static SrPago getInstance(Activity context) throws Exception {

        if (instance == null && getPermissionStatus(context, Manifest.permission.ACCESS_FINE_LOCATION) == GRANTED) {
            if (instance == null) {
                instance = new SrPago();
                instance.context = context;
                context.startService(new Intent(context, LocationUpdatePlayServicesService.class));

            }

            return instance;
        }else{
            if(instance != null){
                return instance;
            }
        }

        throw new Exception("Permission for location has not been granted");
    }

    public boolean isLiveMode() {
        return liveMode;
    }

    public void setLiveMode(boolean liveMode) {
        this.liveMode = liveMode;
    }

    public String getPublishableKey() {
        return publishableKey;
    }

    public void setPublishableKey(String publishableKey) {
        this.publishableKey = publishableKey;
    }

    private static final int GRANTED = 0;
    private static final int DENIED = 1;
    private static final int BLOCKED_OR_NEVER_ASKED = 2;

    private static int getPermissionStatus(Activity activity, String androidPermissionName) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(activity, androidPermissionName) != PackageManager.PERMISSION_GRANTED) {
                if (!ActivityCompat.shouldShowRequestPermissionRationale(activity, androidPermissionName)) {
                    return BLOCKED_OR_NEVER_ASKED;
                }
                return DENIED;
            }
            return GRANTED;
        } else {
            return GRANTED;
        }
    }

    public static SrPago getInstance(@NonNull Activity activity, boolean liveMode) throws Exception {
        instance = getInstance(activity);
        instance.liveMode = liveMode;

        return instance;
    }

    public static SrPago getInstance(@NonNull Activity activity, boolean liveMode, String publishableKey) throws Exception {
        instance = getInstance(activity, liveMode);
        instance.publishableKey = publishableKey;

        return instance;
    }

    public void createToken(@NonNull Card spCard, @NonNull SpTokenListener spTokenListener) throws Exception {
        if (publishableKey == null || publishableKey.equals("")) {
            throw new Exception("The publishable key is empty or null");
        } else {
            SPTokenService.requestToken(context, spCard, spTokenListener);
        }
    }
}
