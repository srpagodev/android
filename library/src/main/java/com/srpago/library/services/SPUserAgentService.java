package com.srpago.library.services;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;

import com.srpago.library.BuildConfig;
import com.srpago.library.common.SPDefinitions;
import com.srpago.library.model.SpUserAgentModel;

import org.json.JSONObject;

import java.util.Locale;


/**
 * Created by Rodolfo on 22/06/2017.
 */

public class SPUserAgentService {
    public static SpUserAgentModel getUserAgent(final Context context) {
        String appVersion;
        String packageName;
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(
                    context.getPackageName(), 0);
            appVersion = info.versionName;
            packageName = info.packageName;
        } catch (Exception ex) {
            appVersion = "";
            packageName = "";
        }

        SpUserAgentModel spUserAgentModel = new SpUserAgentModel();
        spUserAgentModel.setLanguage(Locale.getDefault().toString());
        spUserAgentModel.setSdkVersion(BuildConfig.VERSION_NAME);
        spUserAgentModel.setVersion(appVersion);
        spUserAgentModel.setApiVersion(SPDefinitions.SP_API_VERSION);
        spUserAgentModel.setOsVersion(Build.VERSION.RELEASE);
        spUserAgentModel.setName(packageName);
        spUserAgentModel.setOsName(getOSName());
        spUserAgentModel.setDeviceModel(getDeviceName());
        try {
            spUserAgentModel.setDeviceId(Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID));
        }catch (Exception ex){
            spUserAgentModel.setDeviceId("SrPagoError");
        }

        spUserAgentModel.setLatitude("");
        spUserAgentModel.setLongitude("");
        if(LocationUpdatePlayServicesService.lastLocation != null){
            spUserAgentModel.setLatitude(String.valueOf(LocationUpdatePlayServicesService.lastLocation.getLatitude()));
            spUserAgentModel.setLongitude(String.valueOf(LocationUpdatePlayServicesService.lastLocation.getLongitude()));
        }

        return spUserAgentModel;
    }

    public static String parseUserAgent(final SpUserAgentModel spUserAgentModel){
        String json;

        try{
            JSONObject root = new JSONObject();

            root.put("language", spUserAgentModel.getLanguage());
            root.put("version", spUserAgentModel.getVersion());
            root.put("app_name", spUserAgentModel.getName());
            root.put("sdk_version", spUserAgentModel.getSdkVersion());
            root.put("os_version", spUserAgentModel.getOsVersion());
            root.put("os_name", spUserAgentModel.getOsName());
            root.put("device_model", spUserAgentModel.getDeviceModel());
            root.put("api_version", spUserAgentModel.getApiVersion());
            root.put("latitude", spUserAgentModel.getLatitude());
            root.put("longitude", spUserAgentModel.getLongitude());
            root.put("device_id", spUserAgentModel.getDeviceId());

            json = root.toString();
        }catch (Exception ex){
            json = "";
        }

        return json;
    }

    private static String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        } else {
            return capitalize(manufacturer) + " " + model;
        }
    }


    private static String capitalize(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        char first = s.charAt(0);
        if (Character.isUpperCase(first)) {
            return s;
        } else {
            return Character.toUpperCase(first) + s.substring(1);
        }
    }

    private static String getOSName() {
        final int sdkInt = Build.VERSION.SDK_INT;

        switch (sdkInt) {
            case Build.VERSION_CODES.GINGERBREAD:
            case Build.VERSION_CODES.GINGERBREAD_MR1:
                return "Gingerbread";

            case Build.VERSION_CODES.ICE_CREAM_SANDWICH:
            case Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1:
                return "Ice Cream Sandwich";

            case Build.VERSION_CODES.JELLY_BEAN:
            case Build.VERSION_CODES.JELLY_BEAN_MR1:
            case Build.VERSION_CODES.JELLY_BEAN_MR2:
                return "Jelly Bean";

            case Build.VERSION_CODES.KITKAT:
            case Build.VERSION_CODES.KITKAT_WATCH:
                return "KitKat";

            case Build.VERSION_CODES.LOLLIPOP:
            case Build.VERSION_CODES.LOLLIPOP_MR1:
                return "Lollipop";

            case Build.VERSION_CODES.M:
                return "Marshmallow";

            case Build.VERSION_CODES.N:
            case Build.VERSION_CODES.N_MR1:
                return "Nougat";

            default:
                return "O";
        }
    }
}
