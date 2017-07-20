package com.srpago.library.common;


import com.srpago.library.api.WebServiceConnection;

/**
 * Created by Rodolfo on 22/06/2017.
 */

public class SPDefinitions {

    public final static String SP_API_VERSION = "v1";
    public final static String SP_URL_CREATE_TOKEN = "token";

    public final static int SP_ID_CREATE_TOKEN = 1;

    public static int getRESTType(int type) {
        switch (type) {
            case SP_ID_CREATE_TOKEN:
                return WebServiceConnection.POST;
            default:
                return WebServiceConnection.GET;
        }
    }

    public static String getMethod(int ws) {
        switch (ws) {
            case SP_ID_CREATE_TOKEN:
                return SP_URL_CREATE_TOKEN;
            default:
                return "";
        }
    }
}
