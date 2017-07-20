package com.srpago.library.common;

import android.util.Log;

import java.util.ArrayList;


/**
 * Created by Rodolfo on 21/09/2015.
 */
public class Logger {
    public final static int DEBUG = 3;
    public final static int WARNING = 1;
    public final static int ERROR = 2;
    public final static int INFO = 0;
    public final static int ALLOWED = 3;
    public static boolean LOG = true;

    public static void logDebug(String title, String message){
        if(LOG && ALLOWED >= DEBUG){
            for(String toPrint : wrapString(message)) {
                Log.d("SrPagoFramework" + "_" + title, toPrint);
            }
        }
    }

    public static void logMessage(String title, String message){
        if(LOG && ALLOWED >= INFO){
            for(String toPrint : wrapString(message)) {
                Log.d("SrPagoFramework" + "_" + title, toPrint);
            }
        }
    }

    public static void logWarning(String title, String message){
        if(LOG && ALLOWED >= WARNING){
            for(String toPrint : wrapString(message)) {
                Log.e("SrPagoFramework" + "_" + title, toPrint);
            }
        }
    }

    public static void logError(Exception ex){
        if(LOG && ALLOWED >= ERROR){
            PixzelleErrors.printException(ex);
        }
    }

    private static ArrayList<String> wrapString(String message) {
        if(message == null){
            return new ArrayList<>();
        }

        int maxLogSize = 999;

        ArrayList<String> messages = new ArrayList<>();

        for (int i = 0; i <= message.length() / maxLogSize; i++) {
            int start = i * maxLogSize;
            int end = (i + 1) * maxLogSize;
            end = end > message.length() ? message.length() : end;
            messages.add(message.substring(start, end));
        }

        return messages;
    }
}
